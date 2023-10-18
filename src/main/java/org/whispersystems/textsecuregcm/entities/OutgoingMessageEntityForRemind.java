package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OutgoingMessageEntityForRemind {

  @JsonProperty
  private String destination;


  @JsonProperty
  private String source;



  public OutgoingMessageEntityForRemind() {}

  public OutgoingMessageEntityForRemind(String source, String destination)
  {
    this.source       = source;
    this.destination=destination;
  }

  public String getDestination() {
    return destination;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }
}
