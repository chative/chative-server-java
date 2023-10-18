package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class MeetingAuthConfiguration {

  @JsonProperty
  @NotEmpty
  private String tokenSecret;

  public String getTokenSecret() {
    return tokenSecret;
  }
}
