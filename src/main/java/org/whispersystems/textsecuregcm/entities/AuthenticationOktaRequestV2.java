package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationOktaRequestV2 extends AuthenticationOktaRequest{

  @JsonProperty
  String domain;

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }
}
