package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhoneLoginRequest {
  @JsonProperty
  String phone;
  @JsonProperty
  String verificationCode;

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

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }


}
