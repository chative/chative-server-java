package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

public class CreateGroupRequest {

  @JsonProperty
  private String name;

  @JsonProperty
  private Long messageExpiry;

  @JsonProperty
  private String avatar;

  @JsonProperty
  private Integer invitationRule;
  @JsonProperty
  private Integer notification;


  @JsonProperty
  private List<String> numbers;

  public Optional<String> getName() {
    return Optional.ofNullable(name);
  }

  public Optional<Long> getMessageExpiry() {
    return Optional.ofNullable(messageExpiry);
  }

  public Optional<String> getAvatar() {
    return Optional.ofNullable(avatar);
  }

  public Optional<Integer> getInvitationRule() {
    return Optional.ofNullable(invitationRule);
  }

  public Optional<List<String>> getNumbers() {
    return Optional.ofNullable(numbers);
  }

  public Integer getNotification() {
    return notification;
  }

  public void setNotification(Integer notification) {
    this.notification = notification;
  }
}
