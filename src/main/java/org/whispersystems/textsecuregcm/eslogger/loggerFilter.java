package org.whispersystems.textsecuregcm.eslogger;

import javax.servlet.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingDeque;

import com.github.difftim.eslogger.ESLogger;
import com.google.common.io.BaseEncoding;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.storage.ClientVersionTable;
import org.whispersystems.textsecuregcm.storage.MemCache;


public class loggerFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(loggerFilter.class);
    private static ClientVersionTable versionTable;
    private static MemCache memCache;
    LinkedBlockingDeque<LoginInfo> loginDeque = new LinkedBlockingDeque<>(10000);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        new Thread(this::updateCliVersionFun).start();
    }

    private String setUID(ESLogger logger, Request sReq) {
        String authorization = sReq.getHeader("Authorization");
        String[] login = new String[0];
        String l = sReq.getParameter("login"); // 有login参数
        if (l != null) {
            login = l.split("\\.");
            logger.withURI(sReq.getHttpURI().getPath());
        } else if (authorization != null) {
            authorization = authorization.substring(6);
            String decodedAuth =new String(BaseEncoding.base64().decode(authorization), StandardCharsets.UTF_8);

            String[] arr = decodedAuth.split(":");
            if (arr.length == 0) return l;
            login = arr[0].split("\\.");
        }

        if (login.length == 0) return l;
        logger.withUID(login[0]);

        if (login.length > 1) {
            logger.withDeviceID(login[1]);
        } else {
            logger.withDeviceID("1");
        }
        return l;
    }

    private void upsertVersion(String login, String ua) {
        if (ua == null || login == null) return;
        if (!ua.contains("Chative/") && !ua.contains("ChativeTest/") && !ua.contains("cc/")) return; //忽略非Chative\cc的version统计。
        String device, os, dftVersion = null;
        String[] uidDevice = login.split("\\.");
        if (uidDevice.length == 0) return;
        device = uidDevice.length == 1 ? "1" : uidDevice[1];

        if (ua.contains("macOS") || ua.contains("Darwin")) os = "Darwin";
        else if (ua.contains("Android")) os = "Android";
        else if (ua.contains("Windows")) os = "Windows";
        else if (ua.contains("Linux")) os = "Linux";
        else if (ua.contains("iOS"))
            os = "iOS";
        else os = "other";
        String[] arr = ua.split(" ");
        if (arr.length > 0) {
            arr = arr[0].split("/");
            if (arr.length > 1) dftVersion = arr[1];
        }
        //final String v = memCache.get(login);
        //if (v != null && v.equals(dftVersion)) return;
        versionTable.update(login, uidDevice[0], device, ua, dftVersion, os);
        memCache.set(cliVersionCacheKey(login),dftVersion);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String login = null;
        HttpURI uri = null;
        String ua = null;
        if (request instanceof Request) {
            Request sReq = (Request) request;
            ua = sReq.getHeader("User-Agent");
            uri = sReq.getHttpURI();
            login = sReq.getParameter("login"); // 有login参数
            if (login != null && login.length() > 2 &&
                    login.endsWith(".1")) { // 去掉末尾的.1
                login = login.substring(0, login.length()-2);
            }
        }

        chain.doFilter(request, response);
        try {
            if (response instanceof Response) {
                Response sRes = (Response) response;
                if (sRes.getStatus() == 101 && uri != null
                        && uri.getPath().equals("/v1/websocket/")) // 双重校验，防止恶意添加login参数
                    loginDeque.add(new LoginInfo(login, ua));

            }
        } catch (Exception e) {
            this.logger.error("loggerFilter Exception", e);
        }
    }

    @Override
    public void destroy() {

    }

    static public void initVersionTable(ClientVersionTable table, MemCache cache) {
        versionTable = table;
        memCache = cache;
    }

    private void updateCliVersionFun() {
        while (true) {
            try {
                final LoginInfo loginInfo = loginDeque.take();
                upsertVersion(loginInfo.login, loginInfo.ua);
                logger.warn("get loginInfo {},{}", loginInfo.login, loginInfo.ua);
            } catch (Exception e) {
                logger.error("updateCliVersionFun Exception:", e);
            }
        }

    }

    static public String cliVersionCacheKey(String login) {
        return "cli_v:" + login;
    }

    static class LoginInfo {
        public String getLogin() {
            return login;
        }

        public String getUa() {
            return ua;
        }

        String login;
        String ua;

        public LoginInfo(String login, String ua) {
            this.login = login;
            this.ua = ua;
        }
    }
}
