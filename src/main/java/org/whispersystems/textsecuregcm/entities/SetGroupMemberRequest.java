package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class SetGroupMemberRequest {

  @JsonProperty
  private Integer role;

  @JsonProperty
  private String displayName;

  @JsonProperty
  private String remark;

  @JsonProperty
  private Integer notification;
  @JsonProperty
  private Boolean useGlobal;
  @JsonProperty
  private Integer rapidRole;


  public Optional<Integer> getRole() {
    return Optional.ofNullable(role);
  }

  public Optional<String> getDisplayName() {
    return Optional.ofNullable(displayName);
  }

  public Optional<String> getRemark() {
    return Optional.ofNullable(remark);
  }

  public Optional<Integer> getNotification() {
    return Optional.ofNullable(notification);
  }

  public Optional<Boolean> getUseGlobal() {
    return Optional.ofNullable(useGlobal);
  }

  public void setNotification(Integer notification) {
    this.notification = notification;
  }
  public Optional<Integer> getRapidRole() {
    return Optional.ofNullable(rapidRole);
  }
}
