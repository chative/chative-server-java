package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class MeetingConfiguration {
  @JsonProperty
  @NotNull
  @Valid
  private MeetingAuthConfiguration auth;


  public int getGroupMaxMeetingVersion() {
    return groupMaxMeetingVersion;
  }

  public int getAccMaxMeetingVersion() {
    return accMaxMeetingVersion;
  }

  @JsonProperty
  private int groupMaxMeetingVersion = 1;

  @JsonProperty
  private int accMaxMeetingVersion = 255;

  public MeetingAuthConfiguration getAuth() {
    return auth;
  }
}
