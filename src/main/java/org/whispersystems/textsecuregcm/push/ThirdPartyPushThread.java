package org.whispersystems.textsecuregcm.push;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.LinkedBlockingQueue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.whispersystems.textsecuregcm.storage.MemCache;

public class ThirdPartyPushThread implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(PushSender.class);
    private final MemCache memCache;
    private Thread mThread;
    private String mThreadName;
    private LinkedBlockingQueue<ArrayList<String>> mQueue;
    private String mHuaweiAppId;
    private String mHuaweiSecKey;
    private String mHuaweiAccessToken;
    private long mHuaweiTime;
    private long mHuaweiExpires;
    private String mXiaomiSecKey;

    public ThirdPartyPushThread(String threadName, LinkedBlockingQueue<ArrayList<String>> queue, MemCache memCache) {
        this.memCache = memCache;
        mThreadName = threadName;
        mQueue = queue;

        init_huawei();
        init_xiaomi();
    }

    private boolean init_huawei() {
        mHuaweiAppId = memCache.get("config_push_huawei_appid");
        mHuaweiSecKey = memCache.get("config_push_huawei_seckey");

        mHuaweiTime = 0;
        mHuaweiExpires = 0;

        return true;
    }

    private boolean init_xiaomi() {
        mXiaomiSecKey = memCache.get("config_push_xiaomi_seckey");

        return true;
    }

    private long getTimeInSeconds() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        return cal.getTimeInMillis() / 1000;
    }

    private String getHuaweiAccessToken(String appId, String secKey) {
        long now = getTimeInSeconds();
        if (0 != mHuaweiTime && now - mHuaweiTime < mHuaweiExpires / 2) {
            return mHuaweiAccessToken;
        }

        mHuaweiTime = now;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        String respText = httpPost(
                "https://login.cloud.huawei.com/oauth2/v2/token",
                headers,
                "grant_type=client_credentials&client_secret=" + secKey + "&client_id=" + appId
        );

        Type type = new TypeToken<Map>() {
        }.getType();
        Gson gson = new Gson();
        Map respJson = gson.fromJson(respText, type);
        if (respJson.containsKey("error")) {
            logger.debug("request huawei push token error: " + respJson.get("error"));
            return null;
        }

        mHuaweiExpires = ((Double)respJson.get("expires_in")).longValue();
        mHuaweiAccessToken = (String) respJson.get("access_token");

        logger.debug("got new huawei push token: " + mHuaweiAccessToken);

        return mHuaweiAccessToken;
    }

    public void run() {
        while (true) {
            try {
                ArrayList<String> msg = mQueue.take();
                String type = msg.get(0);
                String token = msg.get(1);
                String title = msg.get(2);
                String content = msg.get(3);

                if (null == type || type.isEmpty() || null == token || token.isEmpty()) {
                } else if (type.equals("huawei")) {
                    huaweiPush(token, title, content);
                } else if (type.equals("xiaomi")) {
                    xiaomiPush(token, title, content);
                } else {
                    logger.debug("wrong push type: " + type);
                }
            } catch (Exception e) {
            }
        }
    }

    public void start() {
        if (mThread == null) {
            mThread = new Thread(this, mThreadName);
            mThread.start();
        }
    }

    private String httpPost(String url, Map<String, String> headers, String data) {
        HttpURLConnection conn = null;
        try {
            //Create connection
            URL _url = new URL(url);
            conn = (HttpURLConnection) _url.openConnection();
            conn.setRequestMethod("POST");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            conn.setUseCaches(false);
            conn.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    conn.getOutputStream());
            wr.write(data.getBytes("UTF-8"));
            wr.close();

            //Get Response
            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return null;
    }

    private boolean huaweiPush(String token, String title, String content) {
        String accessToken = getHuaweiAccessToken(mHuaweiAppId, mHuaweiSecKey);

        /*PushManager.requestToken为客户端申请token的方法，可以调用多次以防止申请token失败*/
        /*PushToken不支持手动编写，需使用客户端的onToken方法获取*/
        JSONArray deviceTokens = new JSONArray();//目标设备Token
        deviceTokens.add(token);
        JSONObject body = new JSONObject();//仅通知栏消息需要设置标题和内容，透传消息key和value为用户自定义
        body.put("title", title);//消息标题
        body.put("content", content);//消息内容体

        JSONObject param = new JSONObject();
        param.put("appPkgName", "org.whosyourdaddy.sx");//定义需要打开的appPkgName

        JSONObject action = new JSONObject();
        action.put("type", 3);//类型3为打开APP，其他行为请参考接口文档设置
        action.put("param", param);//消息点击动作参数

        JSONObject msg = new JSONObject();
        msg.put("type", 3);//3: 通知栏消息，异步透传消息请根据接口文档设置
        msg.put("action", action);//消息点击动作
        msg.put("body", body);//通知栏消息body内容

        // JSONObject ext = new JSONObject();//扩展信息，含BI消息统计，特定展示风格，消息折叠。
        // ext.put("biTag", "Trump");//设置消息标签，如果带了这个标签，会在回执中推送给CP用于检测某种类型消息的到达率和状态
        // ext.put("icon", "http://pic.qiantucdn.com/58pic/12/38/18/13758PIC4GV.jpg");//自定义推送消息在通知栏的图标,value为一个公网可以访问的URL

        JSONObject hps = new JSONObject();//华为PUSH消息总结构体
        hps.put("msg", msg);
        // hps.put("ext", ext);

        JSONObject payload = new JSONObject();
        payload.put("hps", hps);

        String postUrl;
        String postBody;
        try {
            postBody = MessageFormat.format(
                    "access_token={0}&nsp_svc={1}&nsp_ts={2}&device_token_list={3}&payload={4}",
                    URLEncoder.encode(accessToken, "UTF-8"),
                    URLEncoder.encode("openpush.message.api.send", "UTF-8"),
                    URLEncoder.encode(String.valueOf(getTimeInSeconds()), "UTF-8"),
                    URLEncoder.encode(deviceTokens.toString(), "UTF-8"),
                    URLEncoder.encode(payload.toString(), "UTF-8"));

            postUrl = "https://api.push.hicloud.com/pushsend.do?nsp_ctx=" + URLEncoder.encode("{\"ver\":\"1\", \"appId\":\"" + mHuaweiAppId + "\"}", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        String respText = httpPost(postUrl, headers, postBody);

        logger.debug("huawei push " + token + " result: " + respText);

        Type type = new TypeToken<Map>() {
        }.getType();
        Gson gson = new Gson();
        Map resp = gson.fromJson(respText, type);
        int code = (int) resp.get("code");

        return code == 80000000;
    }

    private boolean xiaomiPush(String token, String title, String content) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "key=" + mXiaomiSecKey);

        String postBody;
        try {
            postBody = MessageFormat.format(
                    "alias={0}&description={1}&title={2}&payload={3}&notify_type={4}&pass_through={5}&extra.notify_effect={6}",
                    URLEncoder.encode(token, "UTF-8"),
                    URLEncoder.encode(content, "UTF-8"),
                    URLEncoder.encode(title, "UTF-8"),
                    URLEncoder.encode(content, "UTF-8"),
                    -1,
                    0,
                    1
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }

        String respText = httpPost("https://api.xmpush.xiaomi.com/v2/message/alias", headers, postBody);

        logger.debug("xiaomi push " + token + " result: " + respText);

        Type type = new TypeToken<Map>() {
        }.getType();
        Gson gson = new Gson();
        Map resp = gson.fromJson(respText, type);
        String result = (String) resp.get("result");

        return result.equals("ok");
    }
}
