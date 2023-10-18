package com.github.difftim.eslogger;

import com.google.gson.Gson;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

public class ESLogger {
    // ---------------------------------------------------- Log Level Constants

    /** "Trace" level logging. */
    public static final int LOG_LEVEL_TRACE  = 1;
    /** "Debug" level logging. */
    public static final int LOG_LEVEL_DEBUG  = 2;
    /** "Info" level logging. */
    public static final int LOG_LEVEL_INFO   = 3;
    /** "Warn" level logging. */
    public static final int LOG_LEVEL_WARN   = 4;
    /** "Error" level logging. */
    public static final int LOG_LEVEL_ERROR  = 5;
    /** "Fatal" level logging. */
    public static final int LOG_LEVEL_FATAL  = 6;

    public ESLogger(String indexName) {
        this();
        withIndexName(indexName);
    }

    private String indexName;

    public ESLogger withLevel(Integer level) {
        jsonMap.put(FieldName.Level.name(),level );
        return  this;
    }

    public ESLogger withCost(long cost) {
        jsonMap.put(FieldName.Cost.name(),cost );
        return  this;
    }


    public ESLogger withCustom(String key,Object value) {
        jsonMap.put(key,value );
        return  this;
    }

    public ESLogger withSrcIP(String srcIP) {
        jsonMap.put(FieldName.SrcIP.name(),srcIP );
        return  this;
    }

    public ESLogger withUID(String uid) {
        jsonMap.put(FieldName.UID.name(),uid );
        return  this;
    }
    public ESLogger withMethod(String Method) {
        jsonMap.put(FieldName.Method.name(),Method );
        return  this;
    }
    public ESLogger withURI(String uri) {
        jsonMap.put(FieldName.URI.name(),uri );
        return  this;
    }
    public ESLogger withURL(String url) {
        jsonMap.put(FieldName.URL.name(),url );
        return  this;
    }

    public ESLogger withReqData(String ReqData) {
        jsonMap.put(FieldName.ReqData.name(),ReqData );
        return  this;
    }
    public ESLogger withResponseCode(Integer code) {
        jsonMap.put(FieldName.ResponseCode.name(),code );
        return  this;
    }
    public ESLogger withResData(String ResData) {
        jsonMap.put(FieldName.ResData.name(),ResData );
        return  this;
    }
    public ESLogger withModule(String module) {
        jsonMap.put(FieldName.Module.name(),module );
        return  this;
    }
    public ESLogger withFunction(String function) {
        jsonMap.put(FieldName.Function.name(),function );
        return  this;
    }




    public ESLogger withIndexName(String indexName) {
        this.indexName = indexName.toLowerCase();
        return this;
    }

    public ESLogger withAccNumber(String number) {
        jsonMap.put(FieldName.AccountNumber.name(),number );
        return  this;
    }
    public ESLogger withDeviceID(String id) {
        jsonMap.put(FieldName.DeviceID.name(),id );
        return  this;
    }


    // 发送到日志服务器
    public void send(){
        Gson json = new Gson();
        IndexRequest indexRequest = new IndexRequest(getIndexName()).source(json.toJson(jsonMap), XContentType.JSON);
        ESClient.getInstance().sendLog(indexRequest);
    }

    public ESLogger() {
        jsonMap = new HashMap<>();

        setDateTime();
        jsonMap.put(FieldName.ServerIP.name(), ServerInfo.getServerIP());
        jsonMap.put(FieldName.ServiceName.name(), ServerInfo.getServiceName());
        jsonMap.put(FieldName.ProcessID.name(), ServerInfo.getProcessID());
    }

    private void setDateTime() {
        jsonMap.put(FieldName.DateTime.name(),
                ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now(zoneId)));
    }


    public String getIndexName() {
        if (indexName == null || indexName.equals(""))
            return defaultIndexName;
        return indexName;
    }

    protected Map<String, Object> jsonMap;

    // 初始化服务器信息
    static public void InitServerInfo(String myServerIP, String serviceName,
                                      String userName, String password, String endpoint) {
        ServerInfo.Init(myServerIP, serviceName);
        ESClient.initClient(userName,password,endpoint);
    }

    public static void setDefaultIndexName(String defaultIndexName) {
        ESLogger.defaultIndexName = defaultIndexName;
    }


    // UTC 时区
    private static final java.time.ZoneId zoneId = TimeZone.getTimeZone("UTC").toZoneId();

    private static String defaultIndexName = "default_logs";
}
