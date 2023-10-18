package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailLoginRequest {
  @JsonProperty
  String email;
  @JsonProperty
  String verificationCode;
  @JsonProperty
  String once;

  @JsonProperty
  Integer supportTransfer;

  public boolean getSupportTransfer() {
    return supportTransfer != null && supportTransfer != 0;
  }


  public String getVerificationCode() {
    return verificationCode;
  }

  public void setVerificationCode(String verificationCode) {
    this.verificationCode = verificationCode;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getOnce() {
    return once;
  }

  public void setOnce(String once) {
    this.once = once;
  }
}
