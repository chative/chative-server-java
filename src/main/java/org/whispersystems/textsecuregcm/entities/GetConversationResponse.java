package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.whispersystems.textsecuregcm.storage.Conversation;

import java.util.List;

public class GetConversationResponse {
  @JsonProperty
  List<SetConversationResponse> conversations;
  public GetConversationResponse(List<SetConversationResponse> conversations){
    this.conversations=conversations;
  }
  public List<SetConversationResponse> getConversations() {
    return conversations;
  }

  public void setConversations(List<SetConversationResponse> conversations) {
    this.conversations = conversations;
  }
}
