package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class AuthorizationConfiguration {

  @JsonProperty
  @NotEmpty
  private String pubkey;

  @JsonProperty
  @NotEmpty
  private String privkey;

  @JsonProperty
  private long effectiveDuration;

  public String getPubkey() {
    return pubkey;
  }

  public String getPrivkey() {
    return privkey;
  }

  public long getEffectiveDuration() {
    return effectiveDuration;
  }
}
