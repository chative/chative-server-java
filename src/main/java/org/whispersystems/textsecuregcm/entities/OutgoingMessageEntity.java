package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OutgoingMessageEntity {

  @JsonIgnore
  private long id;

  @JsonIgnore
  private boolean cached;

  @JsonProperty
  private int type;

  @JsonProperty
  private String relay;

  @JsonProperty
  private long timestamp;

  @JsonProperty
  private String source;

  @JsonProperty
  private int sourceDevice;

  @JsonProperty
  private byte[] message;

  @JsonProperty
  private byte[] content;

  @JsonIgnore
  private boolean notify;

  @JsonProperty
  private Long systemShowTimestamp;

  @JsonProperty
  private Long sequenceId;

  @JsonProperty
  private Long notifySequenceId;

  @JsonProperty
  private int msgType;

  @JsonProperty
  private String conversation;

  @JsonProperty
  private String sourceIK;

  @JsonProperty
  private String peerContext;

  public String getSourceIK() {
    return sourceIK;
  }

  public boolean hasSourceIK() {
    return sourceIK != null;
  }

  public void setSourceIK(String sourceIK) {
    this.sourceIK = sourceIK;
  }

  public boolean hasPeerContext() {
    return peerContext != null;
  }
  public String getPeerContext() {
    return peerContext;
  }

  public void setPeerContext(String peerContext) {
    this.peerContext = peerContext;
  }

  public OutgoingMessageEntity() {}

  public OutgoingMessageEntity(long id, boolean cached, int type, String relay, long timestamp,
                               String source, int sourceDevice, byte[] message,
                               byte[] content,boolean notify,Long systemShowTimestamp,Long sequenceId,Long notifySequenceId,int msgType,String conversation,
                               String sourceIK,String peerContext)
  {
    this.id           = id;
    this.cached       = cached;
    this.type         = type;
    this.relay        = relay;
    this.timestamp    = timestamp;
    this.source       = source;
    this.sourceDevice = sourceDevice;
    this.message      = message;
    this.content      = content;
    this.notify  =notify;
    this.systemShowTimestamp=systemShowTimestamp;
    this.sequenceId=sequenceId;
    this.notifySequenceId=notifySequenceId;
    this.msgType=msgType;
    this.conversation=conversation;
    this.sourceIK=sourceIK;
    this.peerContext=peerContext;
  }

  public int getType() {
    return type;
  }

  public String getRelay() {
    return relay;
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

  public byte[] getMessage() {
    return message;
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

  public Long getSystemShowTimestamp() {
    return systemShowTimestamp;
  }

  public Long getSequenceId() {
    return sequenceId;
  }

  public Long getNotifySequenceId() {
    return notifySequenceId;
  }

  public int getMsgType() {
    return msgType;
  }

  public void setMsgType(int msgType) {
    this.msgType = msgType;
  }

  public String getConversation() {
    return conversation;
  }

  public void setConversation(String conversation) {
    this.conversation = conversation;
  }
}
