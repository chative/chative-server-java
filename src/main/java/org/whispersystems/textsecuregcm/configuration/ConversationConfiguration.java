package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class ConversationConfiguration {

  @JsonProperty
  @NotEmpty
  private String blockRegex;

  public String getBlockRegex() {
    return blockRegex;
  }

  public void setBlockRegex(String blockRegex) {
    this.blockRegex = blockRegex;
  }
}
