package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MeetingVerifyTokenRequest {
  @JsonProperty
  private String token;

  public String getToken() {
    return token;
  }
}
