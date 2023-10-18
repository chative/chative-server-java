package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OutgoingMessageEntityListForRemind {

  @JsonProperty
  private List<OutgoingMessageEntityForRemind> messages;

  @JsonProperty
  private boolean more;

  public OutgoingMessageEntityListForRemind() {}

  public OutgoingMessageEntityListForRemind(List<OutgoingMessageEntityForRemind> messages, boolean more) {
    this.messages = messages;
    this.more     = more;
  }

  public List<OutgoingMessageEntityForRemind> getMessages() {
    return messages;
  }

  public boolean hasMore() {
    return more;
  }
}
