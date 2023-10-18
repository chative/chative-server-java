package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateGroupResponse {

  @JsonProperty
  private String gid;
  @JsonProperty
  private String number;

  public CreateGroupResponse(String gid) {
    this.gid = gid;
  }
  public CreateGroupResponse(String gid,String number) {
    this.gid=gid;
    this.number = number;
  }
}
