package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SendMessageResponse {

  @JsonProperty
  private boolean needsSync;

  @JsonProperty
  private Long sequenceId;

  @JsonProperty
  private long systemShowTimestamp;

  @JsonProperty
  private Long notifySequenceId;


  @JsonProperty
  private Object unavailableUsers;

  public SendMessageResponse() {}

  public SendMessageResponse(boolean needsSync) {
    this.needsSync = needsSync;
  }

  public SendMessageResponse(boolean needsSync, Long sequenceId) {
    this.needsSync = needsSync;
    this.sequenceId = sequenceId;
  }

  public SendMessageResponse(boolean needsSync, Long sequenceId, long systemShowTimestamp) {
    this.needsSync = needsSync;
    this.sequenceId = sequenceId;
    this.systemShowTimestamp = systemShowTimestamp;
  }

  public SendMessageResponse(boolean needsSync, Long sequenceId, long systemShowTimestamp,long notifySequenceId) {
    this.needsSync = needsSync;
    this.sequenceId = sequenceId;
    this.systemShowTimestamp = systemShowTimestamp;
    this.notifySequenceId=notifySequenceId;
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

  public void setUnavailableUsers(Object unavailableUsers) {
    this.unavailableUsers = unavailableUsers;
  }

}
