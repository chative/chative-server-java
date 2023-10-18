package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class IncomingMessageForDestinations extends IncomingMessage {

  @JsonProperty
  private boolean silent;
  @NotNull
  @JsonProperty
  @Valid
  private List<String> destinations;

  public boolean isSilent() {
    return silent;
  }

  public void setSilent(boolean silent) {
    this.silent = silent;
  }

  public List<String> getDestinations() {
    return destinations;
  }

  public void setDestinations(List<String> destinations) {
    this.destinations = destinations;
  }
}
