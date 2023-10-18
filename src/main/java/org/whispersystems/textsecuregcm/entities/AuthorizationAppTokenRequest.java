package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthorizationAppTokenRequest {
  @JsonProperty
  String appid;

  @JsonProperty
  String scope;

  @JsonProperty
  String nonce;

  public String getAppid() {
    return appid;
  }

  public String getScope() {
    return scope;
  }
}
