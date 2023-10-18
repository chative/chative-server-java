package org.whispersystems.textsecuregcm.storage;

public class RecallMsgInfo {
    public static final int MEMCACHE_VERION = 1;
    private long id;
    private String conversation;
    private long msgSequenceId;
    private long msgNotifySequenceId;
    private long sourceSequenceId;
    private long sourceNotifySequenceId;

    public RecallMsgInfo(){}
    public RecallMsgInfo(long id, String conversation, long msgSequenceId, long msgNotifySequenceId, long sourceSequenceId, long sourceNotifySequenceId){
        this.id=id;
        this.conversation=conversation;
        this.msgSequenceId=msgSequenceId;
        this.msgNotifySequenceId=msgNotifySequenceId;
        this.sourceSequenceId=sourceSequenceId;
        this.sourceNotifySequenceId=sourceNotifySequenceId;
    }
    public RecallMsgInfo(String conversation, long msgSequenceId, long msgNotifySequenceId, long sourceSequenceId, long sourceNotifySequenceId){
        this.conversation=conversation;
        this.msgSequenceId=msgSequenceId;
        this.msgNotifySequenceId=msgNotifySequenceId;
        this.sourceSequenceId=sourceSequenceId;
        this.sourceNotifySequenceId=sourceNotifySequenceId;
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

    public long getMsgSequenceId() {
        return msgSequenceId;
    }

    public void setMsgSequenceId(long msgSequenceId) {
        this.msgSequenceId = msgSequenceId;
    }

    public long getMsgNotifySequenceId() {
        return msgNotifySequenceId;
    }

    public void setMsgNotifySequenceId(long msgNotifySequenceId) {
        this.msgNotifySequenceId = msgNotifySequenceId;
    }

    public long getSourceSequenceId() {
        return sourceSequenceId;
    }

    public void setSourceSequenceId(long sourceSequenceId) {
        this.sourceSequenceId = sourceSequenceId;
    }

    public long getSourceNotifySequenceId() {
        return sourceNotifySequenceId;
    }

    public void setSourceNotifySequenceId(long sourceNotifySequenceId) {
        this.sourceNotifySequenceId = sourceNotifySequenceId;
    }
}
