package org.whispersystems.textsecuregcm.filter;

import com.google.common.io.BaseEncoding;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.DefaultUnauthorizedHandler;
import io.dropwizard.auth.UnauthorizedHandler;
import io.dropwizard.auth.basic.BasicCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.auth.AccountAuthenticator;
import org.whispersystems.textsecuregcm.configuration.ForcedUpgradeConfiguration;
import org.whispersystems.textsecuregcm.push.APNSender;
import org.whispersystems.textsecuregcm.push.ApnMessage;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.Device;
import org.whispersystems.textsecuregcm.storage.MemCache;
import org.whispersystems.textsecuregcm.util.StringUtil;

import javax.annotation.Priority;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Priority(Priorities.AUTHENTICATION - 1)
public class UserAgentFilter implements ContainerRequestFilter,javax.servlet.Filter {
    private static final Logger logger = LoggerFactory.getLogger(UserAgentFilter.class);
    private MemCache memCache;
    private AccountsManager accountsManager;
    private AccountAuthenticator accountAuthenticator;
    public static String oldVersionUsersIOSKey = "OldVersionUsers_IOS";
    public static String userVersionsIOSKey = "userVersions_IOS";
    public static String oldVersionUsersAndroidKey = "OldVersionUsers_ANDROID";
    public static String userVersionsAndroidKey = "userVersions_ANDROID";
    public static String oldVersionUsersMacKey = "OldVersionUsers_MAC";
    public static String oldVersionUsersLinuxKey = "OldVersionUsers_Linux";
    public static String userVersionsMacKey = "userVersions_MAC";
    public static String userVersionsLinuxKey = "userVersions_Linux";
    public static String undefineVersionUsersKey = "OldVersionUsers_undefine";
    public static boolean upgradeSwitch;
    public static String uaPatternStrForIos = "^Chative/([\\d.]+) \\(.*; iOS ([\\d.]+); Scale(/[\\d.]+)\\)";
    //ChativeTest/1.0.0 (Android 31; Xiaomi 2106118C)
    //解释：产品名称/APP版本号 （Android SDK版本; 手机名称和型号）
    public static String uaPatternStrForAndroid = "";
    public static String uaPatternStrForMac = "^Chative/([\\d.]+) \\((?:macOS|Darwin);([\\d.]+);.*\\)";
    public static String uaPatternStrForLinux = "^Chative/([\\d.]+) \\(Linux;.*\\)";
    private String realm = "realm";
    private String prefix = "Basic";
    public static String iOSVersion;
    public static String androidVersion;
    public static String macVersion;

    public static String linuxVersion;

    protected UnauthorizedHandler unauthorizedHandler = new DefaultUnauthorizedHandler();
    private static APNSender apnSender;
    private static String payload = String.format("{\"aps\":{\"sound\":\"default\",\"badge\":%d,\"alert\":{\"title\":\"\",\"body\":\"Your app is outdated. Please update it first.\"}}}", 0);
    private static String payloadForMac = String.format("{\"aps\":{\"sound\":\"default\",\"badge\":%d,\"alert\":{\"title\":\"\",\"body\":\"Your app is outdated. Please update it first.\n\"}}}", 0);

    public UserAgentFilter(MemCache memCache, AccountsManager accountsManage, APNSender apnSender, AccountAuthenticator accountAuthenticator, ForcedUpgradeConfiguration forcedUpgradeConfiguration) {
        this.memCache = memCache;
        this.accountsManager = accountsManage;
        this.apnSender = apnSender;
        this.accountAuthenticator = accountAuthenticator;
        this.iOSVersion = forcedUpgradeConfiguration.getiOSversion();
        this.androidVersion = forcedUpgradeConfiguration.getAndroidVersion();
        this.macVersion = forcedUpgradeConfiguration.getMacVersion();
        this.linuxVersion = forcedUpgradeConfiguration.getLinuxVersion();
        this.upgradeSwitch = forcedUpgradeConfiguration.isUpgradeSwitch();
        if (!StringUtil.isEmpty(forcedUpgradeConfiguration.getiOSUserAgentPatternStr())) {
            this.uaPatternStrForIos = forcedUpgradeConfiguration.getiOSUserAgentPatternStr();
        }
        if (!StringUtil.isEmpty(forcedUpgradeConfiguration.getAndroidUserAgentPatternStr())) {
            this.uaPatternStrForAndroid = forcedUpgradeConfiguration.getAndroidUserAgentPatternStr();
        }
        if (!StringUtil.isEmpty(forcedUpgradeConfiguration.getMacUserAgentPatternStr())) {
            this.uaPatternStrForMac = forcedUpgradeConfiguration.getMacUserAgentPatternStr();
        }
        if (!StringUtil.isEmpty(forcedUpgradeConfiguration.getLinuxUserAgentPatternStr())) {
            this.uaPatternStrForLinux = forcedUpgradeConfiguration.getLinuxUserAgentPatternStr();
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (!upgradeSwitch) {
            return;
        }
        String path=requestContext.getUriInfo().getPath();
        if(!StringUtil.isEmpty(path)){
            if(path.equals("v1/health")){
                return;
            }
        }
        String userAgent = requestContext.getHeaderString("User-Agent");
        String header = requestContext.getHeaders().getFirst("Authorization");
        if (userAgent != null) {
            //Difft/1.8.1 (iPhone; iOS 14.1; Scale/2.00)
            if (Pattern.matches(uaPatternStrForIos, userAgent)) {
                handler(header, userAgent, iOSVersion, uaPatternStrForIos, userVersionsIOSKey, oldVersionUsersIOSKey);
            } else if (Pattern.matches(uaPatternStrForAndroid, userAgent)) {//ChativeTest/1.0.0 (Android 31; Xiaomi 2106118C)
                handler(header, userAgent, androidVersion, uaPatternStrForAndroid, userVersionsAndroidKey, oldVersionUsersAndroidKey);
            } else if (Pattern.matches(uaPatternStrForMac, userAgent)) {//Difft/1.0.1090801 (macOS;20.6.0;node-fetch/1.0)
                handler(header, userAgent, macVersion, uaPatternStrForMac, userVersionsMacKey, oldVersionUsersMacKey);
            } else if (Pattern.matches(uaPatternStrForLinux, userAgent)) {//Difft/1.0.1090801 (macOS;20.6.0;node-fetch/1.0)
                handler(header, userAgent, macVersion, uaPatternStrForLinux, userVersionsMacKey, oldVersionUsersMacKey);
            } else {
                handlerUndefined(header, userAgent);
            }
        } else {
            handlerUndefined(header, userAgent);
        }
    }

    public static int compareVersion(String version1, String version2) {
        String[] arr1 = version1.split("\\."); // 将版本号按照 . 分割成数组
        String[] arr2 = version2.split("\\.");

        int i = 0;
        while (i < arr1.length || i < arr2.length) {
            if (i < arr1.length && i < arr2.length) {
                final int v1 = Integer.parseInt(arr1[i]);
                final int v2 = Integer.parseInt(arr2[i]);
                if (v1 < v2) {
                    return -1; // 如果当前位的数值小于另一个版本号对应位的数值，则返回 -1
                }
                else if (v1 > v2) {
                    return 1; // 如果当前位的数值大于另一个版本号对应位的数值，则返回 1
                }
            }
            else if (i < arr1.length) {
                if (Integer.parseInt(arr1[i]) != 0) {
                    return 1; // 如果版本号1比版本号2多了一位，并且这一位不是 0，则返回 1
                }
            }

            i++;
        }

        return 0; // 如果两个版本号是完全相同的，则返回 0
    }

    public void handler(String header, String userAgent, String versionLimit, String uaPatternStr, String userVersionsKey, String oldVersionUsersKey) {
        if (StringUtil.isEmpty(versionLimit)) {
            return;
        }
        Optional<Account> principal = authenticate(header);

        Pattern pattern = Pattern.compile(uaPatternStr);
        Matcher matcher = pattern.matcher(userAgent);
        String number=getNumber(header);
        Device device=null;
        Device authDevice=null;
        if(principal.isPresent()) {
            number = principal.get().getNumber();
            device = principal.get().getMasterDevice().get();
            authDevice = principal.get().getAuthenticatedDevice().get();
        }
        if (matcher.find()) {
            String difftVersion = matcher.group(1);
            if (compareVersion(difftVersion,versionLimit) < 0) {
                if (principal.isPresent()) {
                    memCache.hset(userVersionsKey, number, difftVersion);
                    memCache.srem(undefineVersionUsersKey, number);
                    accountsManager.kickOffDevice(number, principal.get().getAuthenticatedDevice().get().getId());
                    memCache.sadd(oldVersionUsersKey, number);
                    sendApn(memCache, device, number, device.equals(authDevice));
                    logger.info(String.format("reject request，number:%s , device:%d , User-Agent:%s", number, authDevice.getId(), userAgent));
                }else{
                    logger.info(String.format("reject request，number:%s , device:%d , User-Agent:%s", number, null, userAgent));
                }
                throw new WebApplicationException(Response.status(450).type(MediaType.TEXT_PLAIN_TYPE).entity("Your app is outdated. Please update it first.").build());
            } else {
                if(principal.isPresent()) {
                    memCache.srem(oldVersionUsersKey, number);
                }
            }
        } else {
            if (principal.isPresent()) {
                memCache.sadd(undefineVersionUsersKey, number);
                logger.info(String.format("reject request，number:%s , device:%d , User-Agent:%s", number, authDevice.getId(), userAgent));
                sendApn(memCache, device, number, device.equals(authDevice));
            }else{
                logger.info(String.format("reject request，number:%s , device:%d , User-Agent:%s", number, null, userAgent));
            }
            throw new WebApplicationException(Response.status(450).type(MediaType.TEXT_PLAIN_TYPE).entity("Your app is outdated. Please update it first.").build());
        }
    }

    public void handlerUndefined(String header, String userAgent) {
        Optional<Account> principal = authenticate(header);
        if (principal.isPresent()) {
            String number = principal.get().getNumber();
            accountsManager.kickOffDevice(number, principal.get().getAuthenticatedDevice().get().getId());
            memCache.sadd(undefineVersionUsersKey, number);
            Device device = principal.get().getMasterDevice().get();
            Device authDevice = principal.get().getAuthenticatedDevice().get();
            sendApn(memCache, device, number, device.equals(authDevice));
            logger.info(String.format("reject request，number:%s , device:%d , User-Agent:%s", number, authDevice.getId(), userAgent));
        }else {
            logger.info(String.format("reject request，number:%s , device:%d , User-Agent:%s", getNumber(header), null, userAgent));
        }
        throw new WebApplicationException(Response.status(450).type(MediaType.TEXT_PLAIN_TYPE).entity("Your app is outdated. Please update it first.").build());
    }

    private String getNumber(String header) {
        try {
            if (header != null) {
                int space = header.indexOf(32);
                if (space > 0) {
                    String method = header.substring(0, space);
                    if (this.prefix.equalsIgnoreCase(method)) {
                        String decoded = new String(BaseEncoding.base64().decode(header.substring(space + 1)), StandardCharsets.UTF_8);
                        int i = decoded.indexOf(58);
                        if (i > 0) {
                            String username = decoded.substring(0, i);
                            return username;
                        }
                    }
                }
            }
        } catch (IllegalArgumentException var12) {
            logger.warn("Error decoding credentials", var12);
        }
        return null;
    }

    private Optional<Account> authenticate(String header) {
        try {
            if (header != null) {
                int space = header.indexOf(32);
                if (space > 0) {
                    String method = header.substring(0, space);
                    if (this.prefix.equalsIgnoreCase(method)) {
                        String decoded = new String(BaseEncoding.base64().decode(header.substring(space + 1)), StandardCharsets.UTF_8);
                        int i = decoded.indexOf(58);
                        if (i > 0) {
                            String username = decoded.substring(0, i);
                            String password = decoded.substring(i + 1);
                            BasicCredentials credentials = new BasicCredentials(username, password);
                            try {
                                Optional<Account> principal = accountAuthenticator.authenticate(credentials);
                                return principal;
                            } catch (AuthenticationException var11) {
                                logger.warn("Error authenticating credentials", var11);
                            }
                        }
                    }
                }
            }
        } catch (IllegalArgumentException var12) {
            logger.warn("Error decoding credentials", var12);
        }
        return Optional.empty();
    }

    public static void sendApn(MemCache memCache, Device device, String number, boolean isMasterDevice) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!StringUtil.isEmpty(device.getApnId())) {
                    if (memCache.exists("forcedUpgrade_" + number + "_" + device.getId())) {
                        return;
                    }
                    String sendPayload = payload;
                    if (!isMasterDevice) {
                        sendPayload = payloadForMac;
                    }
                    ApnMessage apnMessage = new ApnMessage(device.getApnId(), number, device.getId(), sendPayload, false, "Difft111111111111");
                    apnSender.sendMessage(device.getUserAgent(), apnMessage);
                    memCache.setex("forcedUpgrade_" + number + "_" + device.getId(), 60, "1");
                }
            }
        }).start();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!upgradeSwitch) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String path=request.getRequestURI();
        if(!StringUtil.isEmpty(path)){
            if(path.equals("/")||path.equals("/v1/health")||path.equals("/metrics")){
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }
        String userAgent = request.getHeader("User-Agent");
        String header =null;
        if(request.getHeaders("Authorization").hasMoreElements()){
            header=request.getHeaders("Authorization").nextElement();
        }
        try{
            if (userAgent != null) {
                //Difft/1.8.1 (iPhone; iOS 14.1; Scale/2.00)
                if (Pattern.matches(uaPatternStrForIos, userAgent)) {
                    handler(header, userAgent, iOSVersion, uaPatternStrForIos, userVersionsIOSKey, oldVersionUsersIOSKey);
                } else if (Pattern.matches(uaPatternStrForAndroid, userAgent)) {// ChativeTest/1.0.0 (Android 31; Xiaomi 2106118C)
                    handler(header, userAgent, androidVersion, uaPatternStrForAndroid, userVersionsAndroidKey, oldVersionUsersAndroidKey);
                } else if (Pattern.matches(uaPatternStrForMac, userAgent)) {//Difft/1.0.1090801 (macOS;20.6.0;node-fetch/1.0)
                    handler(header, userAgent, macVersion, uaPatternStrForMac, userVersionsMacKey, oldVersionUsersMacKey);
                } else if (Pattern.matches(uaPatternStrForLinux, userAgent)) {//Difft/1.0.1090801 (macOS;20.6.0;node-fetch/1.0)
                    handler(header, userAgent, linuxVersion, uaPatternStrForLinux, userVersionsLinuxKey, oldVersionUsersLinuxKey);
                } else {
                    handlerUndefined(header, userAgent);
                }
            } else {
                handlerUndefined(header, userAgent);
            }
        }catch (WebApplicationException e){
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setStatus(e.getResponse().getStatus());
            httpResponse.getWriter().print(e.getResponse().getEntity());
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
