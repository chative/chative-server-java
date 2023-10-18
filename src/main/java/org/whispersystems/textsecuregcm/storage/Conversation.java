package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Conversation {
    public static final int MEMCACHE_VERION = 1;
    @JsonProperty
    private long id;
    @JsonProperty
    private String number;

    public Conversation(String conversation) {
        this.conversation = conversation;
    }

    @JsonProperty
    private String conversation;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @JsonProperty
    private String remark;
    @JsonProperty
    private long lastUpdateTime;
    @JsonProperty
    private Integer muteStatus;
    @JsonProperty
    private Long muteBeginTime;
    @JsonProperty
    private Long muteEndTime;
    @JsonProperty
    private Integer blockStatus;

    @JsonProperty
    private Integer confidentialMode;
    @JsonProperty
    private int version;

    public Conversation() {
    }

    public Conversation(long id,String number,String conversation,String remark,long lastUpdateTime,Integer muteStatus,Long muteBeginTime,Long muteEndTime,
                        Integer blockStatus,Integer confidentialMode,int version) {
        this.id=id;
        this.number=number;
        this.conversation=conversation;
        this.remark=remark;
        this.lastUpdateTime=lastUpdateTime;
        this.muteStatus=muteStatus;
        this.muteBeginTime=muteBeginTime;
        this.muteEndTime=muteEndTime;
        this.blockStatus=blockStatus;
        this.confidentialMode = confidentialMode != null ? confidentialMode : 0;
        this.version=version;
    }
    public Conversation(String number,String conversation,String remark,long lastUpdateTime,Integer muteStatus,Long muteBeginTime,
                        Long muteEndTime,Integer blockStatus,Integer confidentialMode,int version) {
        this.id=id;
        this.number=number;
        this.conversation=conversation;
        this.remark=remark;
        this.lastUpdateTime=lastUpdateTime;
        this.muteStatus=muteStatus;
        this.muteBeginTime=muteBeginTime;
        this.muteEndTime=muteEndTime;
        this.blockStatus=blockStatus;
        this.confidentialMode = confidentialMode != null ? confidentialMode : 0;
        this.version=version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Integer getMuteStatus() {
        if (muteStatus == null) {
            return 0;
        }
        return muteStatus;
    }

    public void setMuteStatus(Integer muteStatus) {
        this.muteStatus = muteStatus;
    }

    public Long getMuteBeginTime() {
        return muteBeginTime;
    }

    public void setMuteBeginTime(Long muteBeginTime) {
        this.muteBeginTime = muteBeginTime;
    }

    public Long getMuteEndTime() {
        return muteEndTime;
    }

    public void setMuteEndTime(Long muteEndTime) {
        this.muteEndTime = muteEndTime;
    }

    public Integer getBlockStatus() {
        if (blockStatus == null) {
            return 0;
        }
        return blockStatus;
    }

    public void setBlockStatus(Integer blockStatus) {
        this.blockStatus = blockStatus;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Integer getConfidentialMode() {
        return confidentialMode;
    }

    public void setConfidentialMode(Integer confidentialMode) {
        this.confidentialMode = confidentialMode;
    }

}
