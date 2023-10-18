package org.whispersystems.textsecuregcm.entities;


import com.fasterxml.jackson.annotation.JsonProperty;

public class GetGroupInvitationCodeResponse {
  @JsonProperty
  private String inviteCode;
  public GetGroupInvitationCodeResponse(String inviteCode){
    this.inviteCode=inviteCode;
  }
}
