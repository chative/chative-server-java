package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetConversationRequest {
  @JsonProperty
  List<String> conversations;

  public String getSourceQueryType() {
    return sourceQueryType;
  }

  @JsonProperty
  String sourceQueryType = "met";

  public List<String> getConversations() {
    return conversations;
  }

  public void setConversations(List<String> conversations) {
    this.conversations = conversations;
  }
}
