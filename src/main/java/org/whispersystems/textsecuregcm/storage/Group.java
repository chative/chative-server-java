package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Group {

  public static final int MEMCACHE_VERION = 1;

  @JsonProperty
  private String id;

  @JsonProperty
  private String name;

  @JsonProperty
  private String creator;

  @JsonProperty
  private long createTime;

  @JsonProperty
  private int status;

  @JsonProperty
  private long messageExpiry=-1;

  @JsonProperty
  private String avatar;

  @JsonProperty
  private int version;

  @JsonProperty
  private int invitationRule;

  @JsonProperty
  private long lastActiveTime;

  @JsonProperty
  private String remindCycle;

  @JsonProperty
  private boolean anyoneRemove;

  @JsonProperty
  private boolean rejoin;

  @JsonProperty
  private boolean ext = false;

  @JsonProperty
  private int publishRule=2;

  @JsonProperty
  private boolean linkInviteSwitch = true;

  public Group() {
  }

  public Group(String name, String creator, int status, long messageExpiry, String avatar,int invitationRule,String remindCycle,boolean anyoneRemove,boolean rejoin,boolean ext,int publishRule) {
    this(null, name, creator, System.currentTimeMillis(), status, messageExpiry, avatar,invitationRule,1,System.currentTimeMillis(),remindCycle,anyoneRemove,rejoin,ext,publishRule,true);
  }

  public Group(String id, String name, String creator, long createTime, int status, long messageExpiry, String avatar,int invitationRule,int version,String remindCycle,boolean anyoneRemove,boolean rejoin,boolean ext,int publishRule) {
    this(id, name, creator, createTime, status, messageExpiry, avatar,invitationRule,version,System.currentTimeMillis(),remindCycle,anyoneRemove,rejoin,ext,publishRule,true);
  }

  public Group(String id, String name, String creator, long createTime, int status, long messageExpiry, String avatar,int invitationRule,int version,long lastActiveTime,String remindCycle,boolean anyoneRemove,boolean rejoin,boolean ext,int publishRule,
               boolean linkInviteSwitch) {
    this.id = id;
    this.name = name;
    this.creator = creator;
    this.createTime = createTime;
    this.status = status;
    this.messageExpiry = messageExpiry;
    this.avatar = avatar;
    this.version=version;
    this.invitationRule=invitationRule;
    this.lastActiveTime=lastActiveTime;
    this.remindCycle=remindCycle;
    this.anyoneRemove=anyoneRemove;
    this.rejoin=rejoin;
    this.ext=ext;
    this.publishRule=publishRule;
    this.linkInviteSwitch = linkInviteSwitch;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
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

  public long getMessageExpiry() {
    return messageExpiry;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
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

  public void setMessageExpiry(long messageExpiry) {
    this.messageExpiry = messageExpiry;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public int getInvitationRule() {
    return invitationRule;
  }

  public void setInvitationRule(int invitationRule) {
    this.invitationRule = invitationRule;
  }

  public long getLastActiveTime() {
    return lastActiveTime;
  }

  public void setLastActiveTime(long lastActiveTime) {
    this.lastActiveTime = lastActiveTime;
  }

  public String getRemindCycle() {
    return remindCycle;
  }

  public void setRemindCycle(String remindCycle) {
    this.remindCycle = remindCycle;
  }

  public boolean isAnyoneRemove() {
    return false;//anyoneRemove;
  }

  public void setAnyoneRemove(boolean anyoneRemove) {
    this.anyoneRemove = anyoneRemove;
  }

  public boolean isRejoin() {
    return rejoin;
  }

  public void setRejoin(boolean rejoin) {
    this.rejoin = rejoin;
  }

  public boolean isExt() {
    return false;
    //return ext;
  }

  public void setExt(boolean ext) {
    this.ext = false;
    //this.ext = ext;
  }

  public int getPublishRule() {
    return publishRule;
  }

  public void setPublishRule(int publishRule) {
    this.publishRule = publishRule;
  }


  public boolean linkInviteSwitchOn() {
    return linkInviteSwitch;
  }

  public void setLinkInviteSwitch(boolean linkInviteSwitch) {
    this.linkInviteSwitch = linkInviteSwitch;
  }

}
