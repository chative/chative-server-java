package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;

public class PushConfiguration {

  @JsonProperty
  @Min(0)
  private int queueSize = 200;

  public String getApnRecallMinVer() {
    return apnRecallMinVer;
  }

  @JsonProperty
  private String apnRecallMinVer = "2.8.6";

  public int getQueueSize() {
    return queueSize;
  }
}
