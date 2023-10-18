package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OutgoingMessageEntityForSharding {

  @JsonIgnore
  private long id;

  @JsonIgnore
  private boolean cached;

  @JsonProperty
  private int type;

  @JsonProperty
  private long timestamp;

  @JsonProperty
  private String source;

  @JsonProperty
  private int sourceDevice;

  @JsonProperty
  private byte[] content;

  @JsonIgnore
  private boolean notify;

  @JsonProperty
  private int msgType;

  @JsonProperty
  private int receiveType;

  @JsonProperty
  private String conversation;

  @JsonProperty
  private Long sequenceId;
  @JsonProperty
  private int pushType;

  @JsonProperty
  private Long notifySequenceId;

  @JsonProperty
  private Long systemShowTimestamp;

  public OutgoingMessageEntityForSharding() {}

  public OutgoingMessageEntityForSharding(long id, boolean cached, int type, long timestamp,
                                          String source, int sourceDevice,
                                          byte[] content, boolean notify,int msgType,int receiveType,String conversation,long sequenceId,int pushType,long notifySequenceId, long systemShowTimestamp)
  {
    this.id           = id;
    this.cached       = cached;
    this.type         = type;
    this.timestamp    = timestamp;
    this.source       = source;
    this.sourceDevice = sourceDevice;
    this.content      = content;
    this.notify  =notify;
    this.msgType=msgType;
    this.receiveType=receiveType;
    this.conversation=conversation;
    this.sequenceId=sequenceId;
    this.pushType=pushType;
    this.notifySequenceId=notifySequenceId;
    this.systemShowTimestamp=systemShowTimestamp;
  }

  public int getType() {
    return type;
  }


  public long getTimestamp() {
    return timestamp;
  }

  public String getSource() {
    return source;
  }

  public int getSourceDevice() {
    return sourceDevice;
  }


  public byte[] getContent() {
    return content;
  }

  @JsonIgnore
  public long getId() {
    return id;
  }

  @JsonIgnore
  public boolean isCached() {
    return cached;
  }

  public boolean isNotify() {
    return notify;
  }

  public int getMsgType() {
    return msgType;
  }

  public void setMsgType(int msgType) {
    this.msgType = msgType;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setCached(boolean cached) {
    this.cached = cached;
  }

  public void setType(int type) {
    this.type = type;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public void setSourceDevice(int sourceDevice) {
    this.sourceDevice = sourceDevice;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  public void setNotify(boolean notify) {
    this.notify = notify;
  }

  public int getReceiveType() {
    return receiveType;
  }

  public void setReceiveType(int receiveType) {
    this.receiveType = receiveType;
  }

  public String getConversation() {
    return conversation;
  }

  public void setConversation(String conversation) {
    this.conversation = conversation;
  }

  public int getPushType() {
    return pushType;
  }

  public void setPushType(int pushType) {
    this.pushType = pushType;
  }

  public Long getSequenceId() {
    return sequenceId;
  }

  public void setSequenceId(long sequenceId) {
    this.sequenceId = sequenceId;
  }

  public Long getNotifySequenceId() {
    return notifySequenceId;
  }

  public void setNotifySequenceId(long notifySequenceId) {
    this.notifySequenceId = notifySequenceId;
  }

  public Long getSystemShowTimestamp() {
    return systemShowTimestamp;
  }

  public void setSystemShowTimestamp(Long systemShowTimestamp) {
    this.systemShowTimestamp = systemShowTimestamp;
  }
}
