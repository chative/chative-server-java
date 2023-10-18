package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GroupNotify extends Notify{
  public enum ChangeType {
    //0:基础信息变更（创建，解散及群基础信息）<br>1:成员信息变更（成员增删）<br>2:人员基本信息变更（群成员的基础信息，角色变更）
    // <br> 3:公告变更（新增、变更）4:群成员配置信息变更（无需通知其他人，groupVersion不增加） 5:pin 6:会议反馈(会议销毁) 7:用户入会/离会
    // 8:群周期性提醒 //9:会议提醒 //100:RAPID_ROLE变更
    BASIC(0),
    MEMBER(1),
    PERSONNEL(2),
    ANNOUNCEMENT(3),
    PERSONNEL_PRIVATE(4),
    PIN(5),
    MEETING_FEEDBACK(6),
    MEETING_JOIN_LEAVE(7),
    REMIND(8),
    MEETING_REMIND(9),
    CHANGE_RAPID_ROLE(100);
    int code;
    ChangeType(int code){
      this.code=code;
    }

    public int getCode() {
      return code;
    }
  }

  public enum ActionType {
    ADD,
    UPDATE,
    LEAVE,
    DELETE,
    NONE
  }

  public enum GroupNotifyDetailedType {
    CREATE_GROUP(0),
    JOIN_GROUP(1),
    LEAVE_GROUP(2),
    INVITE_JOIN_GROUP(3),
    KICKOUT_GROUP(4),
    DISMISS_GROUP(5),
    GROUP_NAME_CHANGE(6),
    GROUP_AVATAR_CHANGE(7),
    GROUP_MSG_EXPIRY_CHANGE(8),
    GROUP_ADD_ADMIN(9),
    GROUP_DEL_ADMIN(10),
    GROUP_MEMBERINFO_CHANGE(11),
    GROUP_CHANGE_OWNER(12),
    GROUP_ADD_ANNOUNCEMENT(13),
    GROUP_UPDATE_ANNOUNCEMENT(14),
    GROUP_DEL_ANNOUNCEMENT(15),
    GROUP_OTHER_CHANGE(16),
    GROUP_MEMBERINFO_CHANGE_PRIVATE(17),
    GROUP_ADD_PIN(18),
    GROUP_DEL_PIN(19),
    GROUP_INVITATION_RULE_CHANGE(20),
    GROUP_REMIND_CHANGE(21),
    GROUP_REMIND(22),
    GROUP_CHANGE_RAPID_ROLE(23),
    GROUP_ANYONE_REMOVE_CHANGE(24),
    GROUP_REJOIN_CHANGE(25),
    GROUP_EXT_CHANGE_ACCOUNT(26),
    GROUP_PUBLISH_RULE_CHANGE(27),
    GROUP_DESTROY(35),
    WEBLINK_INVITE_JOIN(37),
    WEBLINK_INVITE_SWITCH_CHANGE(38),
    KICKOUT_GROUP_ACCOUNT_INVALID(999);
    int code;
    GroupNotifyDetailedType(int code){
      this.code=code;
    }

    public int getCode() {
      return code;
    }

    public static GroupNotifyDetailedType fromCode(int n) {
      for(GroupNotifyDetailedType type: GroupNotifyDetailedType.values()){
        if(type.getCode()==n){
          return type;
        }
      }
      return null;
    }

  }

  public static class ContentTermTemplate{
    public static String CREATE_GROUP="%s created a group chat";
    public static String JOIN_GROUP="%s joined the group chat";
    public static String LEAVE_GROUP="%s left the group chat";
    public static String INVITE_JOIN_GROUP="%s invited %s to join the group chat";
    public static String KICKOUT_GROUP="%s kicked %s out of the group chat";
    public static String DISMISS_GROUP="%s disbanded the group chat";
    public static String GROUP_NAME_CHANGE="%s changed the group name to %s";
    public static String GROUP_AVATAR_CHANGE="%s changed the group profile picture";
    public static String GROUP_OTHER_CHANGE="%s changed the group setting";
    public static String GROUP_MSG_EXPIRY_CHANGE="%s changed the group message expiry";
    public static String GROUP_ADD_ADMIN="%s sets %s as administrator";
    public static String GROUP_DEL_ADMIN="%s canceled %s as an administrator";
    public static String GROUP_MEMBERINFO_CHANGE="%s changed his message";
    public static String GROUP_CHANGE_OWNER="%s transferred the group to %s";
    public static String GROUP_ADD_ANNOUNCEMENT="%s added a group announcement";
    public static String GROUP_UPDATE_ANNOUNCEMENT="%s updated the group announcement";
    public static String GROUP_DEL_ANNOUNCEMENT="%s deleted the group announcement";
    public static String GROUP_ADD_PIN="%s has pinned a message";
    public static String GROUP_DEL_PIN="%s has unpinned a message";
    public static String GROUP_INVITATION_RULE_CHANGE="%s changed the group invitation rule ";
    public static String GROUP_REMIND_CHANGE="%s changed the group remind ";
    public static String GROUP_CHANGE_RAPID_ROLE="%s sets %s as %s";
    public static String GROUP_CHANGE_RAPID_ROLE_SELF="%s sets himself as %s";
    public static String GROUP_ANYONE_REMOVE_CHANGE="%s changed the group anyone remove setting";
    public static String GROUP_REJOIN_CHANGE="%s changed the group rejoin setting";
    public static String GROUP_PUBLISH_RULE_CHANGE="%s changed the group publish setting";

    public static String GROUP_WEBLINK_INVITE_JOIN ="%s joined the group chat by invitation link";

  }

  public static class GroupMember {
    @JsonProperty
    private String uid;

    @JsonProperty
    private int role;

    @JsonProperty
    private String displayName;

    @JsonProperty
    private int action;

    @JsonProperty
    private int rapidRole;

    @JsonProperty
    private String inviteCode;

    @JsonProperty
    private long extId;

    public GroupMember(String uid, int role, String displayName,int action,int rapidRole,String inviteCode,long extId) {
      this.uid = uid;
      this.role = role;
      this.displayName = displayName;
      this.action = action;
      this.rapidRole=rapidRole;
      //this.inviteCode=inviteCode;
      this.extId=extId;
    }

    public String getUid() {
      return uid;
    }
  }

  public static class GroupMemberWithSelf extends  GroupMember {

    @JsonProperty
    private int notification;
    @JsonProperty
    private boolean useGlobal;
    @JsonProperty
    private String remark;

    public GroupMemberWithSelf(String uid, int role, String displayName,int action,int notification,String remark,boolean useGlobal,int rapidRole,String inviteCode,long extId) {
      super(uid,role,displayName,action,rapidRole,inviteCode,extId);
      this.notification=notification;
      this.remark=remark;
      this.useGlobal=useGlobal;
    }
  }

  public static class GroupAnnouncement {
    @JsonProperty
    private String id;

    @JsonProperty
    private long announcementExpiry;

    @JsonProperty
    private String content;

    @JsonProperty
    private long reviseTime;

    @JsonProperty
    private int action;

    public GroupAnnouncement(String id, long announcementExpiry, String content,long reviseTime,int action) {
      this.id = id;
      this.announcementExpiry = announcementExpiry;
      this.content = content;
      this.reviseTime=reviseTime;
      this.action = action;
    }
  }

  public static class GroupPin{
    @JsonProperty
    private String id;

    @JsonProperty
    private String content;

    @JsonProperty
    private String conversationId;

    @JsonProperty
    private String creator;

    @JsonProperty
    private long createTime;

    @JsonProperty
    private int action;

    public GroupPin(String id, String conversationId, String content, String creator, long createTime, int action) {
      this.id = id;
      this.conversationId = conversationId;
      this.content = content;
      this.creator = creator;
      this.createTime = createTime;
      this.action = action;
    }
  }
  public static class GroupRemind{
    @JsonProperty
    private String remindCycle;

    public GroupRemind(String remindCycle) {
      this.remindCycle = remindCycle;
    }
  }
  public static class Group{
    @JsonProperty
    private String name;

    @JsonProperty
    private long messageExpiry;

    @JsonProperty
    private String avatar;

    @JsonProperty
    private int action;

    @JsonProperty
    private int invitationRule;

    @JsonProperty
    private String remindCycle;
    @JsonProperty
    private boolean anyoneRemove;

    @JsonProperty
    private boolean linkInviteSwitch;

    @JsonProperty
    private boolean rejoin;

    @JsonProperty
    private boolean ext;

    @JsonProperty
    private int publishRule;

    public Group(String name, long messageExpiry, String avatar,int action,int invitationRule,String remindCycle,boolean anyoneRemove, boolean linkInviteSwitch,boolean rejoin,boolean ext, int publishRule){
      this.name = name;
      this.messageExpiry = messageExpiry;
      this.avatar = avatar;
      this.action=action;
      this.invitationRule=invitationRule;
      this.remindCycle=remindCycle;
      this.anyoneRemove=anyoneRemove;
      this.linkInviteSwitch = linkInviteSwitch;
      this.rejoin=rejoin;
      this.ext=ext;
      this.publishRule=publishRule;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class NodifyData extends Notify.NodifyData{
    @JsonProperty
    private String operator;
    @JsonProperty
    private String inviter; //邀请人,只有weblink邀请入群时才有
    @JsonProperty
    private Long operatorDeviceId;
    @JsonProperty
    private String gid;
    @JsonProperty
    private int ver=1;
    @JsonProperty
    private int groupNotifyType;
    @JsonProperty
    private int groupNotifyDetailedType;
    @JsonProperty
    private Integer groupVersion;
    @JsonProperty
    private Group group;
    @JsonProperty
    private List<GroupMember> members;
    @JsonProperty
    private List<GroupAnnouncement> groupAnnouncements;
    @JsonProperty
    private List<GroupPin> groupPins;
    @JsonProperty
    private GroupRemind groupRemind;

    public NodifyData(String operator,long operatorDeviceId,String gid,int groupNotifyType,int groupNotifyDetailedType,int groupVersion,Group group,List<GroupMember> members,List<GroupAnnouncement> groupAnnouncements, List<GroupPin> groupPins,GroupRemind remind) {
      this.operator=operator;
      this.operatorDeviceId=operatorDeviceId;
      this.gid=gid;
      this.groupNotifyType=groupNotifyType;
      this.groupNotifyDetailedType=groupNotifyDetailedType;
      this.group=group;
      this.groupVersion=groupVersion;
      this.members = members;
      this.groupAnnouncements = groupAnnouncements;
      this.groupPins = groupPins;
      this.groupRemind=remind;
    }

    public NodifyData(String gid,int groupNotifyType,int groupNotifyDetailedType,GroupRemind remind) {
      this.gid=gid;
      this.groupNotifyType=groupNotifyType;
      this.groupNotifyDetailedType=groupNotifyDetailedType;
      this.groupRemind=remind;
    }

    public List<GroupMember> getMembers() {
      return members;
    }

    public void setMembers(List<GroupMember> members) {
      this.members = members;
    }

    public String getGid() {
      return gid;
    }

    public int getVer() {
      return ver;
    }

    public int getGroupNotifyType() {
      return groupNotifyType;
    }

    public int getGroupVersion() {
      return groupVersion;
    }

    public Group getGroup() {
      return group;
    }

    public List<GroupAnnouncement> getGroupAnnouncements() {
      return groupAnnouncements;
    }

    public String getOperator() {
      return operator;
    }

    public long getOperatorDeviceId() {
      return operatorDeviceId;
    }

    public GroupRemind getGroupRemind() {
      return groupRemind;
    }

    public void setGroupRemind(GroupRemind groupRemind) {
      this.groupRemind = groupRemind;
    }

    public List<GroupPin> getGroupPins() {
      return groupPins;
    }

    public void setGroupPins(List<GroupPin> groupPins) {
      this.groupPins = groupPins;
    }

    public int getGroupNotifyDetailedType() {
      return groupNotifyDetailedType;
    }

    public void setGroupNotifyDetailedType(int groupNotifyDetailedType) {
      this.groupNotifyDetailedType = groupNotifyDetailedType;
    }

    public String getInviter() {
      return inviter;
    }

    public void setInviter(String inviter) {
      this.inviter = inviter;
    }

  }

  public GroupNotify( String content, long notifyTime,NodifyData data,int display) {
   super(NotifyType.GROUP.ordinal(),notifyTime,content,data,display);
  }

  public static void main(String[] args) {
    GroupAnnouncement groupAnnouncement=new GroupAnnouncement("id",111,"aaa",1111,ActionType.ADD.ordinal());
  }
}
