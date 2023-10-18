package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GroupPinListRequest {

  @JsonProperty
  private List<String> pins;

  public List<String> getPins() {
    return pins;
  }

  public void setPins(List<String> pins) {
    this.pins = pins;
  }
}
