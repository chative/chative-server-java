package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetGroupResponse {

  public static class GroupMember {
    @JsonProperty
    private String uid;

    @JsonProperty
    private int role;

    @JsonProperty
    private String displayName;

    @JsonProperty
    private int rapidRole;

    @JsonProperty
    private long extId;

    public GroupMember(String uid, int role, String displayName,int rapidRole,long extId) {
      this.uid = uid;
      this.role = role;
      this.displayName = displayName;
      this.rapidRole=rapidRole;
      this.extId=extId;
    }
  }

  public static class GroupMemberWithSelf extends GroupMember {

    @JsonProperty
    private int notification;

    @JsonProperty
    private boolean useGlobal;

    @JsonProperty
    private String remark;

    public GroupMemberWithSelf(String uid, int role, String displayName,int notification,String remark,boolean useGlobal,int rapidRole,long extId) {
      super(uid,role,displayName,rapidRole,extId);
      this.notification=notification;
      this.remark=remark;
      this.useGlobal=useGlobal;
    }
  }

  @JsonProperty
  private String name;

  @JsonProperty
  private long messageExpiry;

  @JsonProperty
  private String avatar;

  @JsonProperty
  private int invitationRule;

  @JsonProperty
  private int version;

  @JsonProperty
  private List<GroupMember> members;

  @JsonProperty
  private int membersCount;


  @JsonProperty
  private String gid;

  @JsonProperty
  private String remindCycle;

  @JsonProperty
  private boolean anyoneRemove;

  @JsonProperty
  private boolean rejoin;

  @JsonProperty
  private boolean ext;

  @JsonProperty
  private int publishRule;

  @JsonProperty
  private boolean linkInviteSwitch;

  public GetGroupResponse(String name, long messageExpiry, String avatar, int invitationRule,int version,List<GroupMember> members, String remindCycle,
                          boolean anyoneRemove,boolean rejoin,boolean ext,int publishRule, boolean linkInviteSwitch) {
    this.name = name;
    this.messageExpiry = messageExpiry;
    this.avatar = avatar;
    this.invitationRule=invitationRule;
    this.version=version;
    this.members = members;
    this.remindCycle=remindCycle;
    this.anyoneRemove=anyoneRemove;
    this.rejoin=rejoin;
    this.ext=ext;
    this.publishRule=publishRule;
    this.linkInviteSwitch = linkInviteSwitch;
  }
  public GetGroupResponse(String gid,String name, long messageExpiry, String avatar, int invitationRule,int version,List<GroupMember> members, String remindCycle,boolean anyoneRemove,boolean rejoin,boolean ext,int publishRule) {
    this.name = name;
    this.messageExpiry = messageExpiry;
    this.avatar = avatar;
    this.invitationRule=invitationRule;
    this.version=version;
    this.members = members;
    this.gid=gid;
    this.remindCycle=remindCycle;
    this.anyoneRemove=anyoneRemove;
    this.rejoin=rejoin;
    this.ext=ext;
    this.publishRule=publishRule;
  }

  public GetGroupResponse(String name, long messageExpiry, String avatar, int invitationRule,int version,int membersCount) {
    this.name = name;
    this.messageExpiry = messageExpiry;
    this.avatar = avatar;
    this.invitationRule=invitationRule;
    this.version=version;
    this.membersCount = membersCount;
  }

}
