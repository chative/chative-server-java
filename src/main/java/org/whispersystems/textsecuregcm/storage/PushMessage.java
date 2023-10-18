package org.whispersystems.textsecuregcm.storage;

public class PushMessage {
    public static final int MEMCACHE_VERION = 1;
    private long id;
    private String pushConversationId;
    private long msgId;
    private long sequenceId;
    private int pushType;
    private int priority;

    public PushMessage(long id,String pushConversationId,long msgId,int pushType,int priority,long sequenceId){
        this.id=id;
        this.pushConversationId=pushConversationId;
        this.msgId=msgId;
        this.pushType=pushType;
        this.priority=priority;
        this.sequenceId=sequenceId;
    }

    public PushMessage(String pushConversationId,long msgId,int pushType,int priority,long sequenceId){
        this.id=id;
        this.pushConversationId=pushConversationId;
        this.msgId=msgId;
        this.pushType=pushType;
        this.priority=priority;
        this.sequenceId=sequenceId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPushConversationId() {
        return pushConversationId;
    }

    public void setPushConversationId(String pushConversationId) {
        this.pushConversationId = pushConversationId;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public int getPushType() {
        return pushType;
    }

    public void setPushType(int pushType) {
        this.pushType = pushType;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(long sequenceId) {
        this.sequenceId = sequenceId;
    }
}
