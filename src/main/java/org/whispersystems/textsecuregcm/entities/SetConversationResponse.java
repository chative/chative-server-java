package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SetConversationResponse {

  @JsonProperty
  String conversation;

  @JsonProperty
  String remark;

  @JsonProperty
  Integer muteStatus;

  @JsonProperty
  Integer blockStatus;

  @JsonProperty
  Integer confidentialMode;

  @JsonProperty
  Integer version;

  @JsonProperty
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  String sourceDescribe;

  @JsonProperty
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  String findyouDescribe;

  public SetConversationResponse(String conversation,String remark, Integer muteStatus,Integer blockStatus,Integer confidentialMode,Integer version){
    this.conversation=conversation;
    this.remark = remark;
    this.muteStatus=muteStatus;
    this.blockStatus=blockStatus;
    this.confidentialMode=confidentialMode != null ? confidentialMode : 0;
    this.version=version;
  }

  public String getConversation() {
    return conversation;
  }

  public void setConversation(String conversation) {
    this.conversation = conversation;
  }

  public Integer getMuteStatus() {
    return muteStatus;
  }

  public void setMuteStatus(Integer muteStatus) {
    this.muteStatus = muteStatus;
  }

  public Integer getBlockStatus() {
    return blockStatus;
  }

  public void setBlockStatus(Integer blockStatus) {
    this.blockStatus = blockStatus;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public void setSourceDescribe(String sourceDescribe) {
    this.sourceDescribe = sourceDescribe;
  }

  public void setFindyouDescribe(String findyouDescribe) {
    this.findyouDescribe = findyouDescribe;
  }

}
