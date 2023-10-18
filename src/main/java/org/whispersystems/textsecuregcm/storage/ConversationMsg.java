package org.whispersystems.textsecuregcm.storage;

public class ConversationMsg {
    public static final int MEMCACHE_VERION = 1;
    private long id;
    private String conversation;
    private long lastMsgId;
    private long lastMsgServerTime;
    private long lastMsgSequenceId;
    private long lastVisibleMsgId;
    private long lastVisibleMsgServerTime;
    private long lastVisibleMsgSequenceId;
    private long lastVisibleMsgNotifySequenceId;
    private int pushType;
    private int priority;

    public ConversationMsg(){}
    public ConversationMsg(long id, String conversation, long lastMsgId, long lastVisibleMsgId, long lastVisibleMsgSequenceId, long lastVisibleMsgNotifySequenceId,long lastMsgServerTime,long lastVisibleMsgServerTime,int pushType,int priority,long lastMsgSequenceId){
        this.id=id;
        this.conversation=conversation;
        this.lastVisibleMsgId=lastVisibleMsgId;
        this.lastVisibleMsgSequenceId=lastVisibleMsgSequenceId;
        this.lastVisibleMsgNotifySequenceId=lastVisibleMsgNotifySequenceId;
        this.lastMsgId=lastMsgId;
        this.lastMsgServerTime=lastMsgServerTime;
        this.lastVisibleMsgServerTime=lastVisibleMsgServerTime;
        this.pushType=pushType;
        this.priority=priority;
        this.lastMsgSequenceId=lastMsgSequenceId;
    }
    public ConversationMsg(String conversation, long lastMsgId, long lastVisibleMsgId, long lastVisibleMsgSequenceId, long lastVisibleMsgNotifySequenceId,long lastMsgServerTime,long lastVisibleMsgServerTime,int pushType,int priority,long lastMsgSequenceId){
        this.conversation=conversation;
        this.lastVisibleMsgId=lastVisibleMsgId;
        this.lastVisibleMsgSequenceId=lastVisibleMsgSequenceId;
        this.lastVisibleMsgNotifySequenceId=lastVisibleMsgNotifySequenceId;
        this.lastMsgId=lastMsgId;
        this.lastMsgServerTime=lastMsgServerTime;
        this.lastVisibleMsgServerTime=lastVisibleMsgServerTime;
        this.pushType=pushType;
        this.priority=priority;
        this.lastMsgSequenceId=lastMsgSequenceId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public long getLastMsgId() {
        return lastMsgId;
    }

    public void setLastMsgId(long lastMsgId) {
        this.lastMsgId = lastMsgId;
    }

    public long getLastVisibleMsgId() {
        return lastVisibleMsgId;
    }

    public void setLastVisibleMsgId(long lastVisibleMsgId) {
        this.lastVisibleMsgId = lastVisibleMsgId;
    }

    public long getLastVisibleMsgSequenceId() {
        return lastVisibleMsgSequenceId;
    }

    public void setLastVisibleMsgSequenceId(long lastVisibleMsgSequenceId) {
        this.lastVisibleMsgSequenceId = lastVisibleMsgSequenceId;
    }

    public long getLastVisibleMsgNotifySequenceId() {
        return lastVisibleMsgNotifySequenceId;
    }

    public void setLastVisibleMsgNotifySequenceId(long lastVisibleMsgNotifySequenceId) {
        this.lastVisibleMsgNotifySequenceId = lastVisibleMsgNotifySequenceId;
    }

    public long getLastMsgServerTime() {
        return lastMsgServerTime;
    }

    public void setLastMsgServerTime(long lastMsgServerTime) {
        this.lastMsgServerTime = lastMsgServerTime;
    }

    public long getLastVisibleMsgServerTime() {
        return lastVisibleMsgServerTime;
    }

    public void setLastVisibleMsgServerTime(long lastVisibleMsgServerTime) {
        this.lastVisibleMsgServerTime = lastVisibleMsgServerTime;
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

    public long getLastMsgSequenceId() {
        return lastMsgSequenceId;
    }

    public void setLastMsgSequenceId(long lastMsgSequenceId) {
        this.lastMsgSequenceId = lastMsgSequenceId;
    }
}
