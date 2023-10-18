package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountExtInfoResponse {
  @JsonProperty
  private long extId;

  public AccountExtInfoResponse(long extId){
    this.extId=extId;
  }
  public long getExtId() {
    return extId;
  }

  public void setExtId(long extId) {
    this.extId = extId;
  }
}
