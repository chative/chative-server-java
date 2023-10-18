package org.whispersystems.textsecuregcm.push;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.commons.lang.Objects;
import com.tencent.xinge.XingeApp;
import com.tencent.xinge.bean.AudienceType;
import com.tencent.xinge.bean.Message;
import com.tencent.xinge.bean.MessageAndroid;
import com.tencent.xinge.bean.MessageType;
import com.tencent.xinge.push.app.PushAppRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.configuration.TpnConfiguration;
import org.whispersystems.textsecuregcm.entities.MessageProtos;
import org.whispersystems.textsecuregcm.entities.Notification;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.Device;

import java.util.ArrayList;

public class TpnSender {
    private final Logger logger = LoggerFactory.getLogger(TpnSender.class);
    static private final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    String title;
    String defaultContent;
    String callContent = "You received a CALL.";
    XingeApp xingeApp;

    public TpnSender(TpnConfiguration tpnConfiguration) {
        //pString accessId, String secretKey, String domainUrl
        xingeApp = new XingeApp.Builder()
                .appId(tpnConfiguration.getAppId())
                .secretKey(tpnConfiguration.getSecretKey())
                .domainUrl(tpnConfiguration.getDomainUrl())
                //.domainUrl("https://api.tpns.sgp.tencent.com/")
                .build();
        defaultContent = tpnConfiguration.getDefaultContent();
        callContent = tpnConfiguration.getCallContent();
        title = tpnConfiguration.getTitle();
    }

    public void sendNotification(Account account, Device device, int depth, boolean newOnly, boolean silent,
                                 MessageProtos.Envelope outgoingMessage, Notification notification) {
        final Device.AndroidNotify androidNotify = device.getAndroidNotify();
        if (notification == null) {
            logger.error("No notification for uid:{},device ua:{} ,androidNotify:{}" ,account.getNumber(), device.getUserAgent(), androidNotify);
            return;
        }
        if (androidNotify == null || Objects.isEmpty( androidNotify.getTpnID())) {
            logger.error("No android notify for uid:{},device ua:{} ,androidNotify:{}" ,account.getNumber(), device.getUserAgent(), androidNotify);
            return;
        }
        PushAppRequest pushAppRequest = new PushAppRequest();
        pushAppRequest.setAudience_type(AudienceType.token);
        pushAppRequest.setMessage_type(MessageType.notify);
        Message message = new Message();
        message.setTitle(title);
        message.setContent(defaultContent);
        if( notification.getType() == Notification.Type.PERSONAL_CALL.getCode() ||
                notification.getType() == Notification.Type.GROUP_CALL.getCode())
            message.setContent(callContent);
        message.setShowType(1);
        pushAppRequest.setMessage(message);
        MessageAndroid messageAndroid = new MessageAndroid();
        final CustomContent customContent = new CustomContent();
        customContent.uid = outgoingMessage.getSource();//account.getNumber();
        customContent.gid =notification.getArgs() != null ? notification.getArgs().getGid() : null;
        customContent.notifyType = notification.getType();
        customContent.locKey = getLocKey(customContent.notifyType);
        customContent.mutableContent = 1;
        customContent.passthrough = notification.getArgs() != null ? notification.getArgs().getPassthrough() : null;
        if ( !outgoingMessage.getType().equals(MessageProtos.Envelope.Type.NOTIFY) &&
                !outgoingMessage.getType().equals(MessageProtos.Envelope.Type.PLAINTEXT)){
            String msg = PushSender.getEncPushMsg(outgoingMessage, device);
            if(msg.length() <= 3*1024) customContent.msg = msg;
        }
        String customContentStr = null;
        try {
            customContentStr = objectMapper.writeValueAsString(customContent);
            messageAndroid.setCustom_content(customContentStr);
            messageAndroid.setSmall_icon("notification_icon");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        //messageAndroid.set
        message.setAndroid(messageAndroid);
        ArrayList<String> tokenList = new ArrayList<>();
        tokenList.add(androidNotify.getTpnID());
        pushAppRequest.setToken_list(tokenList);
        final JSONObject jsonObject = xingeApp.pushApp(pushAppRequest);
        logger.info("tpn push to {} ,custom content :{}, result:{}",account.getNumber(), customContentStr, jsonObject.toString());
    }
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static public class CustomContent {
        @JsonProperty
        private String uid;
        @JsonProperty
        private String gid;
        @JsonProperty
        private String locKey;
        @JsonProperty
        private int mutableContent;
        @JsonProperty
        int notifyType;
        @JsonProperty
        private String passthrough;
        @JsonProperty
        private String msg;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public String getLocKey() {
            return locKey;
        }

        public void setLocKey(String locKey) {
            this.locKey = locKey;
        }

        public int getMutableContent() {
            return mutableContent;
        }

        public void setMutableContent(int mutableContent) {
            this.mutableContent = mutableContent;
        }

        public int getNotifyType() {
            return notifyType;
        }

        public void setNotifyType(int notifyType) {
            this.notifyType = notifyType;
        }

        public String getPassthrough() {
            return passthrough;
        }

        public void setPassthrough(String passthrough) {
            this.passthrough = passthrough;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public String getLocKey(int type){
        if (type == Notification.Type.PERSONAL_NORMAL.getCode()){ return  Notification.Type.PERSONAL_NORMAL.name();}
        if (type == Notification.Type.PERSONAL_FILE.getCode()){ return  Notification.Type.PERSONAL_FILE.name();}
        if (type == Notification.Type.PERSONAL_REPLY.getCode()){ return  Notification.Type.PERSONAL_REPLY.name();}
        if (type == Notification.Type.PERSONAL_CALL.getCode()){ return  Notification.Type.PERSONAL_CALL.name();}
        if (type == Notification.Type.PERSONAL_CALL_CANCEL.getCode()){ return  Notification.Type.PERSONAL_CALL_CANCEL.name();}
        if (type == Notification.Type.PERSONAL_CALL_TIMEOUT.getCode()){ return  Notification.Type.PERSONAL_CALL_TIMEOUT.name();}
        if (type == Notification.Type.GROUP_NORMAL.getCode()){ return  Notification.Type.GROUP_NORMAL.name();}
        if (type == Notification.Type.GROUP_FILE.getCode()){ return  Notification.Type.GROUP_FILE.name();}
        if (type == Notification.Type.GROUP_MENTIONS_DESTINATION.getCode()){ return  Notification.Type.GROUP_MENTIONS_DESTINATION.name();}
        if (type == Notification.Type.GROUP_MENTIONS_OTHER.getCode()){ return  Notification.Type.GROUP_MENTIONS_OTHER.name();}
        if (type == Notification.Type.GROUP_MENTIONS_ALL.getCode()){ return  Notification.Type.GROUP_MENTIONS_ALL.name();}
        if (type == Notification.Type.GROUP_REPLY_DESTINATION.getCode()){ return  Notification.Type.GROUP_REPLY_DESTINATION.name();}
        if (type == Notification.Type.GROUP_REPLY_OTHER.getCode()){ return  Notification.Type.GROUP_REPLY_OTHER.name();}
        if (type == Notification.Type.GROUP_CALL.getCode()){ return  Notification.Type.GROUP_CALL.name();}
        if (type == Notification.Type.GROUP_CALL_COLSE.getCode()){ return  Notification.Type.GROUP_CALL_COLSE.name();}
        if (type == Notification.Type.GROUP_CALL_OVER.getCode()){ return  Notification.Type.GROUP_CALL_OVER.name();}
        if (type == Notification.Type.GROUP_ADD_ANNOUNCEMENT.getCode()){ return  Notification.Type.GROUP_ADD_ANNOUNCEMENT.name();}
        if (type == Notification.Type.GROUP_UPDATE_ANNOUNCEMENT.getCode()){ return  Notification.Type.GROUP_UPDATE_ANNOUNCEMENT.name();}
        if (type == Notification.Type.RECALL_MSG.getCode()){ return  Notification.Type.RECALL_MSG.name();}
        if (type == Notification.Type.RECALL_MENTIONS_MSG.getCode()){ return  Notification.Type.RECALL_MENTIONS_MSG.name();}
        if (type == Notification.Type.TASK_MSG.getCode()){ return  Notification.Type.TASK_MSG.name();}
        if (type == Notification.Type.GROUP_ADD_PIN.getCode()){ return  Notification.Type.GROUP_ADD_PIN.name();}

        return null;
    }
}
