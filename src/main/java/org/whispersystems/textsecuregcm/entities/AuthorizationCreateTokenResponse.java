package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthorizationCreateTokenResponse {
  @JsonProperty
  private String token;

  public AuthorizationCreateTokenResponse(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }
}
