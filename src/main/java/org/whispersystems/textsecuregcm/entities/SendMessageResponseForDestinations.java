package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SendMessageResponseForDestinations {
  private List<SendMessageResponseWithDestination> responses;

  private long systemShowTimestamp;

  public SendMessageResponseForDestinations(List<SendMessageResponseWithDestination> responses ,long systemShowTimestamp){
    this.responses=responses;
    this.systemShowTimestamp=systemShowTimestamp;
  }
  public List<SendMessageResponseWithDestination> getResponses() {
    return responses;
  }

  public void setResponses(List<SendMessageResponseWithDestination> responses) {
    this.responses = responses;
  }

  public long getSystemShowTimestamp() {
    return systemShowTimestamp;
  }

  public void setSystemShowTimestamp(long systemShowTimestamp) {
    this.systemShowTimestamp = systemShowTimestamp;
  }

  public static class SendMessageResponseWithDestination{
    @JsonProperty
    private String destination;
    @JsonProperty
    private int status;
    @JsonProperty
    private Long sequenceId;
    @JsonProperty
    private Long notifySequenceId;
    @JsonProperty
    private String reason;

    public SendMessageResponseWithDestination(String destination,int status,Long sequenceId,String reason){
      this.destination=destination;
      this.status=status;
      this.sequenceId=sequenceId;
      this.reason=reason;
    }

    public SendMessageResponseWithDestination(String destination,int status,Long sequenceId,String reason,Long notifySequenceId){
      this.destination=destination;
      this.status=status;
      this.sequenceId=sequenceId;
      this.reason=reason;
      this.notifySequenceId=notifySequenceId;
    }

    public String getDestination() {
      return destination;
    }

    public void setDestination(String destination) {
      this.destination = destination;
    }

    public Long getSequenceId() {
      return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
      this.sequenceId = sequenceId;
    }

    public int getStatus() {
      return status;
    }

    public void setStatus(int status) {
      this.status = status;
    }

    public String getReason() {
      return reason;
    }

    public void setReason(String reason) {
      this.reason = reason;
    }
  }
}
