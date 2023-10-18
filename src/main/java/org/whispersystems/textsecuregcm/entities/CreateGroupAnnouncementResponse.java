package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateGroupAnnouncementResponse {

  @JsonProperty
  private String id;

  public CreateGroupAnnouncementResponse(String id) {
    this.id = id;
  }
}
