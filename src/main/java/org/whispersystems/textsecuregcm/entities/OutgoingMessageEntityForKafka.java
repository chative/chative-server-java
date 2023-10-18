package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OutgoingMessageEntityForKafka extends OutgoingMessageEntity{
  @JsonProperty
  private String destination;
  @JsonProperty
  private long destinationDevice;

  public OutgoingMessageEntityForKafka() {}

  public OutgoingMessageEntityForKafka(long id, boolean cached, int type, String relay, long timestamp,
                                       String source, int sourceDevice, byte[] message,
                                       byte[] content, boolean notify,String destination,long destinationDevice,Long systemShowTimestamp,Long sequenceId,Long notifySequenceId ,int msgType,String conversation)
  {
   super(id,cached,type,relay,timestamp,source,sourceDevice,message,content,notify,systemShowTimestamp,sequenceId,notifySequenceId,msgType,conversation,
           null,null);
   this.destination=destination;
   this.destinationDevice=destinationDevice;
  }

  public String getDestination() {
    return destination;
  }

  public long getDestinationDevice() {
    return destinationDevice;
  }
}
