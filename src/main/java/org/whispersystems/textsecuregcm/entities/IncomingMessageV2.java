package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class IncomingMessageV2 implements IncomingMessageBase {
    public String getSenderPubIK() {
        return senderPubIK;
    }

    public int getSenderRegistrationId() {
        return senderRegistrationId;
    }

    @JsonProperty
    private String senderPubIK;

    @JsonProperty
    private int senderRegistrationId;

    @JsonProperty
    private int    type;

    @JsonProperty
    private String content;

    @JsonProperty
    private String legacyContent;

    @JsonProperty
    private String relay;

    @JsonProperty
    private long   timestamp; // deprecated

    @JsonProperty
    private Notification notification;

    @JsonProperty
    private boolean readReceipt;

    @JsonProperty
    private Long sequenceId;

    @JsonProperty
    private long systemShowTimestamp;

    @JsonProperty
    private Long notifySequenceId;

    @JsonProperty
    private int msgType;

    @JsonProperty
    private IncomingMessage.Conversation conversation;

    @JsonProperty
    private List<IncomingMessage.ReadPosition> readPositions;

    @JsonProperty
    private IncomingMessage.RealSource realSource;
    @JsonProperty
    private int detailMessageType;


    @JsonProperty
    private List<Recipient> recipients;


    public int getType() {
        return type;
    }

    public String getRelay() {
        return relay;
    }


    public String getContent() {
        return content;
    }

    public String getLegacyContent() {
        return legacyContent;
    }

    public boolean hasLegacyContent(){
        return legacyContent != null && !legacyContent.isEmpty();
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public boolean isReadReceipt() {
        return readReceipt;
    }

    public void setReadReceipt(boolean readReceipt) {
        this.readReceipt = readReceipt;
    }

    public Long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public long getSystemShowTimestamp() {
        return systemShowTimestamp;
    }

    public void setSystemShowTimestamp(long systemShowTimestamp) {
        this.systemShowTimestamp = systemShowTimestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getNotifySequenceId() {
        return notifySequenceId;
    }

    public void setNotifySequenceId(Long notifySequenceId) {
        this.notifySequenceId = notifySequenceId;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public IncomingMessage.Conversation getConversation() {
        return conversation;
    }

    public void setConversation(IncomingMessage.Conversation conversation) {
        this.conversation = conversation;
    }

    public List<IncomingMessage.ReadPosition> getReadPositions() {
        return readPositions;
    }

    public void setReadPositions(List<IncomingMessage.ReadPosition> readPositions) {
        this.readPositions = readPositions;
    }

    public IncomingMessage.RealSource getRealSource() {
        return realSource;
    }

    public void setRealSource(IncomingMessage.RealSource realSource) {
        this.realSource = realSource;
    }

    public int getDetailMessageType() {
        return detailMessageType;
    }

    public void setDetailMessageType(int detailMessageType) {
        this.detailMessageType = detailMessageType;
    }

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public static class Recipient {
        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public int getRegistrationId() {
            return registrationId;
        }

        public void setRegistrationId(int registrationId) {
            this.registrationId = registrationId;
        }

        public String getPeerContext() {
            return peerContext;
        }

        public void setPeerContext(String peerContext) {
            this.peerContext = peerContext;
        }

        @JsonProperty
        private String uid; // 群的时候非空

        @JsonProperty
        private int registrationId; // 主设备的注册ID

        @JsonProperty
        private String peerContext;
    }

}
