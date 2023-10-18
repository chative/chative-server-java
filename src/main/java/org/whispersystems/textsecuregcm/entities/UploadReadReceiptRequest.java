package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class UploadReadReceiptRequest {

  @JsonProperty
  @NotNull
  String conversation;

  @JsonProperty
  @NotNull
  private Integer conversationType;

  @JsonProperty
  @NotNull
  Long maxServerTimestamp;

  @JsonProperty
  @NotNull
  Long readAt;

  @JsonProperty
  @NotNull
  Long notifySequenceId;


  public String getConversation() {
    return conversation;
  }

  public void setConversation(String conversation) {
    this.conversation = conversation;
  }

  public Long getMaxServerTimestamp() {
    return maxServerTimestamp;
  }

  public void setMaxServerTimestamp(Long maxServerTimestamp) {
    this.maxServerTimestamp = maxServerTimestamp;
  }

  public Long getReadAt() {
    return readAt;
  }

  public void setReadAt(Long readAt) {
    this.readAt = readAt;
  }

  public Long getNotifySequenceId() {
    return notifySequenceId;
  }

  public void setNotifySequenceId(Long notifySequenceId) {
    this.notifySequenceId = notifySequenceId;
  }

  public Integer getConversationType() {
    return conversationType;
  }

  public void setConversationType(Integer conversationType) {
    this.conversationType = conversationType;
  }
}
