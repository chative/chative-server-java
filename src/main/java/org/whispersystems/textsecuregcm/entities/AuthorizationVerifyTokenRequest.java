package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class AuthorizationVerifyTokenRequest {
  @JsonProperty
  private String token;


  @JsonProperty
  private Set<String> mustTeams;

  public String getToken() {
    return token;
  }

  public Set<String> getMustTeams() {
    return mustTeams;
  }

}
