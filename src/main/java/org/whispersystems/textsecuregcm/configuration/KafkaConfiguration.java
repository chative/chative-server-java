package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;


public class KafkaConfiguration {

  @JsonProperty
  @NotEmpty
  private String servers;

  @JsonProperty
  private String acks="all";

  @JsonProperty
  private int retries=0;

  @JsonProperty
  private int batchSize=16384;

  @JsonProperty
  private long lingerMs=20;

  @JsonProperty
  private long bufferMemory=33554432;

  @JsonProperty
  private String keySerializer="org.apache.kafka.common.serialization.StringSerializer";

  @JsonProperty
  private String valueSerializer="org.apache.kafka.common.serialization.StringSerializer";

  @JsonProperty
  private String securityProtocol="PLAINTEXT";

  public String getServers() {
    return servers;
  }

  public String getAcks() {
    return acks;
  }

  public int getRetries() {
    return retries;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public long getLingerMs() {
    return lingerMs;
  }

  public long getBufferMemory() {
    return bufferMemory;
  }

  public String getKeySerializer() {
    return keySerializer;
  }

  public String getValueSerializer() {
    return valueSerializer;
  }

  public String getSecurityProtocol() {
    return securityProtocol;
  }

}
