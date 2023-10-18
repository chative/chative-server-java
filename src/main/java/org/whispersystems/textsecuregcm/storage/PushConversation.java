package org.whispersystems.textsecuregcm.storage;

public class PushConversation {
    public static final int MEMCACHE_VERION = 1;
    private String id;
    private String destination;
    private long destinationDevice;
    private int pushType;
    private int priority;
    private String conversation;
    private long maxReadNotifySequenceId;
    private long lastSelfNotifySequenceId;
    private boolean isSend;
    private long lastMsgId;
    private long lastVisibleMsgId;
    private long lastVisibleMsgSequenceId;
    private long lastVisibleMsgNotifySequenceId;
    private long lastShardMsgId;
    private long lastShardMsgServerTime;


    public PushConversation(){}
    public PushConversation(String id,String conversation,String destination,long destinationDevice,int pushType,int priority,long maxReadNotifySequenceId,long lastSelfNotifySequenceId,boolean isSend,
                            long lastMsgId,long lastVisibleMsgId,long lastVisibleMsgSequenceId,long lastVisibleMsgNotifySequenceId,long lastShardMsgId,long lastShardMsgServerTime){
        this.id=id;
        this.conversation=conversation;
        this.destination=destination;
        this.destinationDevice=destinationDevice;
        this.pushType=pushType;
        this.priority=priority;
        this.maxReadNotifySequenceId=maxReadNotifySequenceId;
        this.lastSelfNotifySequenceId=lastSelfNotifySequenceId;
        this.isSend=isSend;
        this.lastMsgId=lastMsgId;
        this.lastVisibleMsgId=lastVisibleMsgId;
        this.lastVisibleMsgSequenceId=lastVisibleMsgSequenceId;
        this.lastVisibleMsgNotifySequenceId=lastVisibleMsgNotifySequenceId;
        this.lastShardMsgId=lastShardMsgId;
        this.lastShardMsgServerTime=lastShardMsgServerTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public long getDestinationDevice() {
        return destinationDevice;
    }

    public void setDestinationDevice(long destinationDevice) {
        this.destinationDevice = destinationDevice;
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

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public long getMaxReadNotifySequenceId() {
        return maxReadNotifySequenceId;
    }

    public void setMaxReadNotifySequenceId(long maxReadNotifySequenceId) {
        this.maxReadNotifySequenceId = maxReadNotifySequenceId;
    }

    public long getLastSelfNotifySequenceId() {
        return lastSelfNotifySequenceId;
    }

    public void setLastSelfNotifySequenceId(long lastSelfNotifySequenceId) {
        this.lastSelfNotifySequenceId = lastSelfNotifySequenceId;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
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

    public long getLastShardMsgId() {
        return lastShardMsgId;
    }

    public void setLastShardMsgId(long lastShardMsgId) {
        this.lastShardMsgId = lastShardMsgId;
    }

    public long getLastShardMsgServerTime() {
        return lastShardMsgServerTime;
    }

    public void setLastShardMsgServerTime(long lastShardMsgServerTime) {
        this.lastShardMsgServerTime = lastShardMsgServerTime;
    }
}
