package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MeetingCreateTokenResponse {
  @JsonProperty
  private String token;

  public MeetingCreateTokenResponse(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }
}
