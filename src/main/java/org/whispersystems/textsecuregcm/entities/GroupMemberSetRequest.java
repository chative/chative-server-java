package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupMemberSetRequest {

  @JsonProperty
  String displayName;

  @JsonProperty
  String remark;

  @JsonProperty
  int notification;

  public String getDisplayName() {
    return displayName;
  }

  public String getRemark() {
    return remark;
  }

  public int getNotification() {
    return notification;
  }
}
