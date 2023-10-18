package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdentityKeyState {
  @JsonProperty
  private String identityKey;

  public IdentityKeyState() {}

  public IdentityKeyState(String identityKey) {
    this.identityKey   = identityKey;

  }

  public String getIdentityKey() {
    return identityKey;
  }

}
