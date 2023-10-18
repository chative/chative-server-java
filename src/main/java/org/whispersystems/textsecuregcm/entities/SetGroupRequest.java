package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class SetGroupRequest {

  @JsonProperty
  private String name;

  @JsonProperty
  private String owner;

  @JsonProperty
  private Long messageExpiry;

  @JsonProperty
  private Integer invitationRule;

  @JsonProperty
  private String avatar;

  @JsonProperty
  private String remindCycle;

  @JsonProperty
  private Boolean anyoneRemove;

  @JsonProperty
  private Boolean rejoin;

  @JsonProperty
  private Integer publishRule;

  @JsonProperty
  private Boolean linkInviteSwitch;

  public Optional<String> getName() {
    return Optional.ofNullable(name);
  }

  public Optional<String> getOwner() {
    return Optional.ofNullable(owner);
  }

  public Optional<Long> getMessageExpiry() {
    return Optional.ofNullable(messageExpiry);
  }

  public Optional<Integer> getInvitationRule() {
    return Optional.ofNullable(invitationRule);
  }

  public Optional<String> getAvatar() {
    return Optional.ofNullable(avatar);
  }

  public Optional<String> getRemindCycle() {
    return Optional.ofNullable(remindCycle);
  }

  public Optional<Boolean> getAnyoneRemove() {
    return Optional.ofNullable(anyoneRemove);
  }

  public Optional<Boolean> getLinkInviteSwitch() {
    return Optional.ofNullable(linkInviteSwitch);
  }
  public Optional<Boolean> getRejoin() {
    return Optional.ofNullable(rejoin);
  }

  public Optional<Integer> getPublishRule() {
    return Optional.ofNullable(publishRule);
  }
}
