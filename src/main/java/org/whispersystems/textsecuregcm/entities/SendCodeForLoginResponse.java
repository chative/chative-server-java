package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SendCodeForLoginResponse {

  @JsonProperty
  String once;

  public SendCodeForLoginResponse(String once){
    this.once=once;
  }
  public String getOnce() {
    return once;
  }

  public void setOnce(String once) {
    this.once = once;
  }
}
