package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupPin {

  public static final int MEMCACHE_VERION = 1;

  @JsonProperty
  private String id;

  @JsonProperty
  private String gid;

  @JsonProperty
  private String creator;

  @JsonProperty
  private long createTime;

  @JsonProperty
  private int status;

  @JsonProperty
  private String content;

  @JsonProperty
  private String conversationId;

  @JsonProperty
  private GroupPin groupPin;

  public GroupPin() {
  }

  public GroupPin(String gid, String creator, int status, String content, String conversationId) {
    this(null,gid,creator, System.currentTimeMillis(), status, content, conversationId);
  }

  public GroupPin(String id, String gid, String creator, long createTime, int status, String content, String conversationId) {
    this.id = id;
    this.gid=gid;
    this.creator = creator;
    this.createTime = createTime;
    this.status = status;
    this.content = content;
    this.conversationId = conversationId;
  }

  public String getId() {
    return id;
  }


  public String getCreator() {
    return creator;
  }

  public long getCreateTime() {
    return createTime;
  }

  public int getStatus() {
    return status;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public void setCreateTime(long createTime) {
    this.createTime = createTime;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getGid() {
    return gid;
  }

  public void setGid(String gid) {
    this.gid = gid;
  }

  public GroupPin getGroupPin() {
    return groupPin;
  }

  public void setGroupPin(GroupPin groupPin) {
    this.groupPin = groupPin;
  }

  public String getConversationId() {
    return conversationId;
  }

  public void setConversationId(String conversationId) {
    this.conversationId = conversationId;
  }
}
