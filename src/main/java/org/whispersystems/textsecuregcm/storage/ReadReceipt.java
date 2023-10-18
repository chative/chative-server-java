package org.whispersystems.textsecuregcm.storage;

public class ReadReceipt {
    public static final int MEMCACHE_VERION = 1;
    private long id;
    private String source;
    private long sourceDevice;
    private long maxServerTimestamp;
    private long readAt;
    private long maxNotifySequenceId;
    private String conversation;
    private long uploadTime;

    public ReadReceipt(){}
    public ReadReceipt(String conversation, String source, long sourceDevice, long maxServerTimestamp, long readAt, long maxNotifySequenceId, long uploadTime){
        this.conversation=conversation;
        this.source=source;
        this.sourceDevice=sourceDevice;
        this.maxServerTimestamp=maxServerTimestamp;
        this.readAt=readAt;
        this.maxNotifySequenceId=maxNotifySequenceId;
        this.uploadTime=uploadTime;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getSourceDevice() {
        return sourceDevice;
    }

    public void setSourceDevice(long sourceDevice) {
        this.sourceDevice = sourceDevice;
    }

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public long getMaxServerTimestamp() {
        return maxServerTimestamp;
    }

    public void setMaxServerTimestamp(long maxServerTimestamp) {
        this.maxServerTimestamp = maxServerTimestamp;
    }

    public long getReadAt() {
        return readAt;
    }

    public void setReadAt(long readAt) {
        this.readAt = readAt;
    }

    public long getMaxNotifySequenceId() {
        return maxNotifySequenceId;
    }

    public void setMaxNotifySequenceId(long maxNotifySequenceId) {
        this.maxNotifySequenceId = maxNotifySequenceId;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }
}
