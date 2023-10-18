package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.whispersystems.textsecuregcm.util.StringUtil;

public class AuthenticationDomainCheckRequest {

  @JsonProperty
  String domain;

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public boolean validate() {
    if (StringUtil.isEmpty(domain) || domain.length() > 128) {
      return false;
    }

    return true;
  }
}
