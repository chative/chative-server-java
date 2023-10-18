package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupMember {

  @JsonProperty
  private String gid;

  @JsonProperty
  private String uid;

  @JsonProperty
  private int role;

  @JsonProperty
  private long create_time;

  @JsonProperty
  private String inviter;

  @JsonProperty
  private String displayName;

  @JsonProperty
  private String remark;

  @JsonProperty
  private int notification;

  @JsonProperty
  private boolean useGlobal;

  @JsonProperty
  private int rapidRole;

    public GroupMember(String id, String number, int ordinal, long currentTimeMillis, String number1, String s, String s1, int globalNotification, boolean b, int ordinal1) {
        this(id, number, ordinal, currentTimeMillis, number1, s, s1, globalNotification, b, ordinal1, "");
    }

  public int getMeetingVersion() {
    return meetingVersion;
  }

  public void setMeetingVersion(int meetingVersion) {
    this.meetingVersion = meetingVersion;
  }

  @JsonProperty
  private int meetingVersion = 1;

  public int getMsgEncVersion() {
    return msgEncVersion;
  }

  public void setMsgEncVersion(int msgEncVersion) {
    this.msgEncVersion = msgEncVersion;
  }

  @JsonProperty
  private int msgEncVersion = 1;

  public int getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId(int registrationId) {
    this.registrationId = registrationId;
  }

  @JsonProperty
  private int registrationId;

  public String getIdentityKey() {
    return identityKey;
  }

  public void setIdentityKey(String identityKey) {
    this.identityKey = identityKey;
  }

  @JsonProperty
  private String identityKey;

  public String getInviteCode() {
    return inviteCode;
  }

  public void setInviteCode(String inviteCode) {
    this.inviteCode = inviteCode;
  }

  @JsonProperty
  private String inviteCode; // for group invitation

  public GroupMember(){

  }
  public GroupMember(String gid, String uid, int role, long create_time, String inviter, String displayName, String remark, int notification,boolean useGlobal,int rapidRole,
                     String inviteCode) {
    this.gid = gid;
    this.uid = uid;
    this.role = role;
    this.create_time = create_time;
    this.inviter = inviter;
    this.displayName = displayName;
    this.remark = remark;
    this.notification = notification;
    this.useGlobal=useGlobal;
    this.rapidRole=rapidRole;
    this.inviteCode=inviteCode;
  }

  public String getGid() {
    return gid;
  }

  public String getUid() {
    return uid;
  }

  public int getRole() {
    return role;
  }

  public long getCreate_time() {
    return create_time;
  }

  public String getInviter() {
    return inviter;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getRemark() {
    return remark;
  }

  public int getNotification() {
    return notification;
  }

  public void setGid(String gid) {
    this.gid = gid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public void setRole(int role) {
    this.role = role;
  }

  public void setCreate_time(long create_time) {
    this.create_time = create_time;
  }

  public void setInviter(String inviter) {
    this.inviter = inviter;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public void setNotification(int notification) {
    this.notification = notification;
  }

  public boolean isUseGlobal() {
    return useGlobal;
  }

  public void setUseGlobal(boolean useGlobal) {
    this.useGlobal = useGlobal;
  }

  public int getRapidRole() {
    return rapidRole;
  }

  public void setRapidRole(int rapidRole) {
    this.rapidRole = rapidRole;
  }
}
