package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.difftim.eslogger.ESLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.util.JsonPropertyAnnotationExclusionStrategy;
import org.whispersystems.websocket.messages.WebSocketResponseMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SendMessageLogHandler {
    private static final Logger logger = LoggerFactory.getLogger(SendMessageLogHandler.class);

    static ThreadPoolExecutor executor=new ThreadPoolExecutor(20,20,0, TimeUnit.SECONDS,new LinkedBlockingDeque<>(), new ThreadPoolExecutor.CallerRunsPolicy());
    private static String indexAlias="sendmessagelogs";

    public static long getTaskCount(){
        return executor.getTaskCount();
    }
    public static void send(SendMessageLog sendMessageLog) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                if(sendMessageLog==null||sendMessageLog.toJsonObject().size()==0){
                    logger.error("sendMessageLog is null!");
                    System.out.println("log is null!");
                    return;
                }
                ESLogger esLogger = new ESLogger(indexAlias);
                esLogger.withCustom("sendMessageLog",sendMessageLog.toJsonObject());
                logger.info("index:"+indexAlias+" body:"+sendMessageLog.toJsonObject());
                esLogger.send();
            }
        });
    }

    public enum SendMessageAction {
        REVICE(0,"revice"),INSERT(1,"insert"),SEND(2,"send"),RESPONSE(3,"response"),REQUEUE(4,"requeue");
        private String name;
        private int code;

        private SendMessageAction(int code,String name) {
            this.name = name;
            this.code = code;
        }

        public static String getName(int code) {
            for (SendMessageAction c : SendMessageAction.values()) {
                if (c.getCode() == code) {
                    return c.name;
                }
            }
            return null;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
    public static class SendMessageLog{
        @JsonProperty
        private String action;
        @JsonProperty
        private String destination;
        @JsonProperty
        private long  destinationDeviceId;
        @JsonProperty
        private long   timestamp;
        @JsonProperty
        private String source;
        @JsonProperty
        private long sourceDeviceId;
        private boolean notify;
        private boolean silent;

        MessageProtos.Envelope message;

        @JsonProperty
        private int envelopeType;

        @JsonProperty
        private JsonObject messageJsonObj;
        @JsonProperty
        private Boolean isSuccess;
        private WebSocketResponseMessage response;
        @JsonProperty
        private String type;
        @JsonProperty
        private String conversationLogId;
        ConversationPreviews.ConversationPreview conversationPreview;
        @JsonProperty
        LogResponse logResponse;

        public SendMessageLog(String action,String destination,long  destinationDeviceId,boolean notify,boolean silent, MessageProtos.Envelope message){
            this.action=action;
            this.destination=destination;
            this.destinationDeviceId=destinationDeviceId;
            this.notify=notify;
            this.silent=silent;
            this.message=message;
            type="message";
        }

        public SendMessageLog(String action,String destination,long  destinationDeviceId, MessageProtos.Envelope message){
            this.action=action;
            this.destination=destination;
            this.destinationDeviceId=destinationDeviceId;
            this.message=message;
            type="message";
        }
        public SendMessageLog(String action,String destination,long  destinationDeviceId, MessageProtos.Envelope message,Boolean isSuccess, WebSocketResponseMessage response){
            this.action=action;
            this.destination=destination;
            this.destinationDeviceId=destinationDeviceId;
            this.message=message;
            this.isSuccess=isSuccess;
            this.response=response;
            type="message";
        }

        public SendMessageLog(String action,String destination,long  destinationDeviceId, ConversationPreviews.ConversationPreview conversationPreview,String conversationLogId){
            this.action=action;
            this.destination=destination;
            this.destinationDeviceId=destinationDeviceId;
            this.conversationPreview=conversationPreview;
            type="conversation";
            this.conversationLogId=conversationLogId;
        }

        public SendMessageLog(String action,String destination,long  destinationDeviceId, ConversationPreviews.ConversationPreview conversationPreview ,Boolean isSuccess, WebSocketResponseMessage response,String conversationLogId){
            this.action=action;
            this.destination=destination;
            this.destinationDeviceId=destinationDeviceId;
            this.conversationPreview=conversationPreview;
            this.isSuccess=isSuccess;
            this.response=response;
            type="conversation";
            this.conversationLogId=conversationLogId;
        }


        public JsonObject toJsonObject(){
            Gson gson= new GsonBuilder().setExclusionStrategies(new JsonPropertyAnnotationExclusionStrategy()).create();
            if(message!=null) {
                if (message.hasSource()){
                   this.source=message.getSource();
                }
                if ( message.hasSourceDevice()) {
                   this.sourceDeviceId=message.getSourceDevice();
                }
                if ( message.hasTimestamp()) {
                    this.timestamp=message.getTimestamp();
                }
                this.envelopeType = message.getType().getNumber();
                if(SendMessageAction.REVICE.getName().equals(this.action)) {
                    try {
                        MessageProtos.Envelope.Builder builder =message.toBuilder();
                        builder.clearSource();
                        builder.clearSourceDevice();
                        builder.clearContent();
                        builder.clearTimestamp();
                        String messageStr = JsonFormat.printer()
                                .preservingProtoFieldNames()
                                .print(builder.build());
                        JsonObject messageJsonObj = new Gson().fromJson(messageStr, JsonObject.class);
                        if (messageJsonObj != null) {
                            messageJsonObj.addProperty("notify", this.notify);
                            messageJsonObj.addProperty("silent", this.silent);
                            this.messageJsonObj=messageJsonObj;
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(conversationPreview!=null){
                ConversationPreviews.ConversationPreview.Builder builder= conversationPreview.toBuilder();
                try {
                    if (conversationPreview.hasLastestMsg()){
                        MessageProtos.Envelope.Builder envelopeBuilder=conversationPreview.getLastestMsg().toBuilder();
                        envelopeBuilder.clearContent();
                        builder.setLastestMsg(envelopeBuilder.build());
                    }
                    String converdationStr = JsonFormat.printer()
                            .preservingProtoFieldNames()
                            .print(builder.build());
                    JsonObject converdationJsonObj = new Gson().fromJson(converdationStr, JsonObject.class);
                    if (converdationJsonObj != null) {
                        this.messageJsonObj=converdationJsonObj;
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
            if(type.equals("conversation")){
                this.timestamp=System.currentTimeMillis();
            }
            if(response!=null){
                LogResponse logResponse=new LogResponse(response.getStatus(),response.getRequestId(),response.getMessage());
                this.logResponse=logResponse;
            }
            JsonObject jsonObject=gson.toJsonTree(this).getAsJsonObject();
            return jsonObject;
        }

        private long getTimestamp() {
            return timestamp;
        }
    }
    static class LogResponse{
        @JsonProperty
        private int status;
        @JsonProperty
        private long requestId;
        @JsonProperty
        private String message;
        public LogResponse(int status,long requestId,String message){
            this.status=status;
            this.requestId=requestId;
            this.message=message;
        }
    }

}
