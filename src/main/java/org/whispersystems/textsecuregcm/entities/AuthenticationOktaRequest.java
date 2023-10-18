package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationOktaRequest {

  @JsonProperty
  String accessToken;

  @JsonProperty
  String idToken;

  @JsonProperty
  String nonce;

  public String getAccessToken() {
    return accessToken;
  }

  public String getIdToken() {
    return idToken;
  }

  public String getNonce() {
    return nonce;
  }
}
