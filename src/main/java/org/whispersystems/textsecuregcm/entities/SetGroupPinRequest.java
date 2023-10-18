package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class SetGroupPinRequest extends SetGroupAnnouncementRequest{

  @JsonProperty
  private String conversationId;

  public Optional<String> getConversationId() {
    return Optional.ofNullable(conversationId);
  }

}
