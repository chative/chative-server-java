package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.whispersystems.textsecuregcm.util.StringUtil;

public class AuthenticationEmailCheckRequest {

  @JsonProperty
  String email;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean validate() {
    if (StringUtil.isEmpty(email) || email.length() > 128) {
      return false;
    }

    return true;
  }
}
