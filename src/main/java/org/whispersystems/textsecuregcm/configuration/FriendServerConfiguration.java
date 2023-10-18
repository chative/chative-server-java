package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class FriendServerConfiguration {

  @NotEmpty
  @JsonProperty
  private String grpcHost;

  @NotNull
  @JsonProperty
  private int grpcPort;

  //@JsonProperty
  //private long timeout;

  public String getGrpcHost() {
    return grpcHost;
  }

  public void setGrpcHost(String grpcHost) {
    this.grpcHost = grpcHost;
  }

  public int getGrpcPort() {
    return grpcPort;
  }

  public void setGrpcPort(int grpcPort) {
    this.grpcPort = grpcPort;
  }

  //public long getTimeout() {
  //  return timeout;
  //}
  //
  //public void setTimeout(long timeout) {
  //  this.timeout = timeout;
  //}
}
