package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class ConversationNotify extends Notify{

  public enum ChangeType {
    MUTE,
    BLOCK,
    REMARK,
    CONFIDENTIAL_MODE,
  }

  public static class Conversation{
    @JsonProperty
    String conversation;

    @JsonProperty
    Integer muteStatus;

    @JsonProperty
    Integer blockStatus;

    @JsonProperty
    Integer confidentialMode;

    @JsonProperty
    String remark;

    @JsonProperty
    Integer version;

    public Conversation(String conversation, Integer muteStatus,  Integer blockStatus,Integer confidentialMode, String remark, Integer version) {
      this.conversation = conversation;
      this.muteStatus = muteStatus;
      this.blockStatus = blockStatus;
      this.confidentialMode = confidentialMode;
      this.remark = remark;
      this.version = version;
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
  }

  public static class NodifyData extends Notify.NodifyData{
    @JsonProperty
    private String operator;
    @JsonProperty
    private long operatorDeviceId;
    @JsonProperty
    private int ver=1;
    @JsonProperty
    private int changeType;
    @JsonProperty
    private Conversation conversation;

    public NodifyData(String operator, long operatorDeviceId,int changeType,Conversation conversation) {
      this.operator=operator;
      this.operatorDeviceId=operatorDeviceId;
      this.changeType=changeType;
      this.conversation=conversation;
    }

    public String getOperator() {
      return operator;
    }

    public void setOperator(String operator) {
      this.operator = operator;
    }

    public long getOperatorDeviceId() {
      return operatorDeviceId;
    }

    public void setOperatorDeviceId(long operatorDeviceId) {
      this.operatorDeviceId = operatorDeviceId;
    }

    public int getVer() {
      return ver;
    }

    public void setVer(int ver) {
      this.ver = ver;
    }

    public int getChangeType() {
      return changeType;
    }

    public void setChangeType(int changeType) {
      this.changeType = changeType;
    }

    public Conversation getConversation() {
      return conversation;
    }

    public void setConversation(Conversation conversation) {
      this.conversation = conversation;
    }
  }

  public ConversationNotify(long notifyTime, NodifyData data) {
   super(NotifyType.CONVERSATION.ordinal(),notifyTime,null,data, Display.NO.ordinal());
  }

}
