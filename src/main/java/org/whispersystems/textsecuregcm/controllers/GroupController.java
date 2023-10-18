//package org.whispersystems.textsecuregcm.controllers;
//
//import com.codahale.metrics.annotation.Timed;
//import io.dropwizard.auth.Auth;
//import org.skife.jdbi.v2.TransactionIsolationLevel;
//import org.skife.jdbi.v2.sqlobject.Transaction;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.whispersystems.textsecuregcm.InternalAccount.InternalAccountManager;
//import org.whispersystems.textsecuregcm.entities.*;
//import org.whispersystems.textsecuregcm.exceptions.*;
//import org.whispersystems.textsecuregcm.storage.*;
//import org.whispersystems.textsecuregcm.util.StringUtil;
//
//import javax.validation.Valid;
//import javax.ws.rs.*;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import java.util.*;
//import java.util.concurrent.LinkedBlockingDeque;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
////Change to GroupControllerWithTransaction and discard
//@Path("/v1/groups1")
//public class GroupController {
//
//  enum STATUS {
//    OK,
//    INVALID_PARAMETER,
//    NO_PERMISSION,
//    NO_SUCH_GROUP,
//    NO_SUCH_GROUP_MEMBER,
//    NO_SUCH_GROUP_ANNOUNCEMENT,
//  }
//
//  private final Logger logger = LoggerFactory.getLogger(DeviceController.class);
//
//  private final AccountsManager accountsManager;
//  private final GroupManager groupManager;
//  private final NotifyManager notifyManager;
//  private final InternalAccountManager internalAccountManager;
//
//  public GroupController(AccountsManager accountsManager, GroupManager groupManager,NotifyManager notifyManager,InternalAccountManager internalAccountManager) {
//    this.accountsManager = accountsManager;
//    this.groupManager = groupManager;
//    this.notifyManager=notifyManager;
//    this.internalAccountManager=internalAccountManager;
//  }
//  ThreadPoolExecutor executor=new ThreadPoolExecutor(20,20,0, TimeUnit.SECONDS,new LinkedBlockingDeque<>(), new ThreadPoolExecutor.CallerRunsPolicy());
//
//  @Timed
//  @PUT
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Produces(MediaType.APPLICATION_JSON)
//  public BaseResponse create(@Auth Account account, @Valid CreateGroupRequest request) {
//    Group group = null;
//    try {
//      group = groupManager.createGroupWithPermissionCheck(account, new Group(request.getName(), account.getNumber(), GroupsTable.STATUS.ACTIVE.ordinal(), request.getMessageExpiry(), request.getAvatar(),-1));
//      logger.info(account.getNumber() + " create group: " + group.getId());
//    } catch (InvalidParameterException e) {
//      err(STATUS.INVALID_PARAMETER, e.getMessage());
//    } catch (NoPermissionException e) {
//      err(STATUS.NO_PERMISSION, e.getMessage());
//    }
//    sendGroupNotify(Notify.ChangeType.BASIC.ordinal(),account,group,GroupNotify.ActionType.ADD.ordinal(), GroupNotify.GroupNotifyType.CREATE_GROUP.ordinal(),null,GroupNotify.ActionType.ADD.ordinal(), null,-1,Notify.Display.YES.ordinal(),null);
//    return ok(new CreateGroupResponse(group.getId()));
//  }
//
//  @Timed
//  @DELETE
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/{gid}")
//  public BaseResponse delete(@Auth Account account, @PathParam("gid") String gid) {
//    Group group = getGroup(account, gid);
//
//    try {
//      group.setStatus(GroupsTable.STATUS.DISMISSED.ordinal());
//      group=groupManager.setGroupWithPermissionCheck(account, group);
//      logger.info(account.getNumber() + " delete group: " + group.getId());
//    } catch (InvalidParameterException e) {
//      err(STATUS.INVALID_PARAMETER, e.getMessage());
//    } catch (NoPermissionException e) {
//      err(STATUS.NO_PERMISSION, e.getMessage());
//    } catch (NoSuchGroupException e) {
//      err(STATUS.NO_SUCH_GROUP, e.getMessage());
//    }
//    sendGroupNotify(Notify.ChangeType.BASIC.ordinal(),account,group,GroupNotify.ActionType.DELETE.ordinal(), GroupNotify.GroupNotifyType.DISMISS_GROUP.ordinal(), null,-1,null,-1,Notify.Display.YES.ordinal(),null);
//    return ok();
//  }
//
//  @Timed
//  @GET
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/{gid}")
//  public BaseResponse get(@Auth Account account, @PathParam("gid") String gid) {
//    Group group = getGroup(account, gid);
//
//    // Members
//    List<GetGroupResponse.GroupMember> members = new LinkedList<>();
//    try {
//      for (GroupMember member : groupManager.getGroupMembersWithPermissionCheck(account, group)) {
//        members.add(new GetGroupResponse.GroupMember(member.getUid(), member.getRole(), member.getDisplayName()));
//      }
//    } catch (NoPermissionException e) {
//      err(STATUS.NO_PERMISSION, e.getMessage());
//    } catch (NoSuchGroupException e) {
//      err(STATUS.NO_SUCH_GROUP, e.getMessage());
//    }
//
//    return ok(new GetGroupResponse(group.getName(), group.getMessageExpiry(), group.getAvatar(),  members));
//  }
//
//  @Timed
//  @POST
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/{gid}")
//  public BaseResponse set(@Auth Account account, @PathParam("gid") String gid, @Valid SetGroupRequest request) {
//    Group group = getGroup(account, gid);
//    int changeType=Notify.ChangeType.BASIC.ordinal();
//    int display=Notify.Display.NO.ordinal();
//    int groupMemeberAction=-1;
//    int notifyType;
//    Object operatee=null;
//    try {
//      // Get current owner
//      GroupMember owner = null;
//      for (GroupMember member: groupManager.getGroupMembersWithPermissionCheck(account, group)) {
//        if (member.getRole() == GroupMembersTable.ROLE.OWNER.ordinal()) {
//          owner = member;
//          break;
//        }
//      }
//
//      if (!owner.getUid().equals( request.getOwner())) {
//        // Transfer group
//        Account newOnwer = getAccount(account, request.getOwner());
//        if (!request.getOwner().equals(owner.getUid())) {
//          groupManager.transferGroupWithPermissionCheck(account, group, newOnwer);
//        }
//        changeType=Notify.ChangeType.PERSONNEL.ordinal();
//        display=Notify.Display.YES.ordinal();
//        notifyType=GroupNotify.GroupNotifyType.GROUP_CHANGE_OWNER.ordinal();
//        operatee=newOnwer.getNumber();
//        groupMemeberAction=GroupNotify.ActionType.UPDATE.ordinal();
//        group=groupManager.addGroupVersion(group);
//      } else {
//        // Change settings
//        changeType=Notify.ChangeType.BASIC.ordinal();
//        if(!group.getName().equals(request.getName())) {
//          display=Notify.Display.YES.ordinal();
//          notifyType=GroupNotify.GroupNotifyType.GROUP_NAME_CHANGE.ordinal();
//          operatee=request.getName();
//        }else if(!group.getAvatar().equals(request.getAvatar())){
//          display=Notify.Display.NO.ordinal();
//          notifyType=GroupNotify.GroupNotifyType.GROUP_AVATAR_CHANGE.ordinal();
//        }else{
//          display=Notify.Display.NO.ordinal();
//          notifyType=GroupNotify.GroupNotifyType.GROUP_MSG_EXPIRY_CHANGE.ordinal();
//        }
//        group.setName(request.getName());
//        group.setMessageExpiry(request.getMessageExpiry());
//        group.setAvatar(request.getAvatar());
//        group=groupManager.setGroupWithPermissionCheck(account, group);
//        logger.info(account.getNumber() + " set group: " + group.getId() + " name: " + request.getName() + ", messageExpiry: " + request.getMessageExpiry() + ", avatar: " + request.getAvatar() );
//      }
//      sendGroupNotify(changeType,account,group,GroupNotify.ActionType.UPDATE.ordinal(), notifyType, null,groupMemeberAction,null,-1,display,operatee);
//    } catch (InvalidParameterException e) {
//      err(STATUS.INVALID_PARAMETER, e.getMessage());
//    } catch (NoPermissionException e) {
//      err(STATUS.NO_PERMISSION, e.getMessage());
//    } catch (NoSuchGroupException e) {
//      err(STATUS.NO_SUCH_GROUP, e.getMessage());
//    } catch (NoSuchGroupMemberException e) {
//      err(STATUS.NO_SUCH_GROUP_MEMBER, e.getMessage());
//    }
//    return ok();
//  }
//
//  @Timed
//  @GET
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Produces(MediaType.APPLICATION_JSON)
//  public BaseResponse getMyGroups(@Auth Account account) {
//    List<Group> groups = null;
//    try {
//      groups = groupManager.getMemberGroupsWithPermissionCheck(account);
//    } catch (NoPermissionException e) {
//      err(STATUS.NO_PERMISSION, e.getMessage());
//    }
//
//    List<GetMyGroupsResponse.Group> responseGroups = new LinkedList<>();
//    for (Group group : groups) {
//      responseGroups.add(new GetMyGroupsResponse.Group(group.getId(), group.getName(), group.getMessageExpiry(), group.getAvatar(),group.getStatus()));
//    }
//
//    return ok(new GetMyGroupsResponse(responseGroups));
//  }
//
//  @Timed
//  @PUT
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/{gid}/members")
//  @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
//  public BaseResponse join(@Auth Account account, @PathParam("gid") String gid, @Valid List<String> request) {
//    Group group = getGroup(account, gid);
//
//    // check if accounts exist
//    for (String uid : request) {
//      if (!accountsManager.get(uid).isPresent()) {
//        err(STATUS.NO_SUCH_GROUP_MEMBER, "No such account: " + uid);
//      }
//      InternalAccountsRow internalAccountsRow=internalAccountManager.get(uid);
//      if(internalAccountsRow==null){
//        err(STATUS.NO_SUCH_GROUP_MEMBER, "No such account: " + uid);
//      }
//      if(internalAccountsRow!=null&&internalAccountsRow.isDisabled()){
//        err(STATUS.NO_SUCH_GROUP_MEMBER, "account is disabled: " + uid);
//      }
//    }
//
//    List<GroupMember> groupMembers=new ArrayList<GroupMember>();
//    // join members
//    for (String uid: request) {
//      try {
//        GroupMember groupMember=groupManager.addMemberWithPermissionCheck(account, group, accountsManager.get(uid).get());
//        if(groupMember!=null) {
//          groupMembers.add(groupMember);
//        }
//      } catch (InvalidParameterException e) {
//      err(STATUS.INVALID_PARAMETER, e.getMessage());
//      } catch (NoPermissionException e) {
//        err(STATUS.NO_PERMISSION, e.getMessage());
//      } catch (NoSuchGroupException e) {
//        err(STATUS.NO_SUCH_GROUP, e.getMessage());
//      }
//    }
//    if(groupMembers.size()>0) {
//      group=groupManager.addGroupVersion(group);
//      sendGroupNotify(Notify.ChangeType.MEMBER.ordinal(), account, group, GroupNotify.ActionType.NONE.ordinal(), GroupNotify.GroupNotifyType.INVITE_JOIN_GROUP.ordinal(), groupMembers, GroupNotify.ActionType.ADD.ordinal(), null, -1, Notify.Display.YES.ordinal(), groupMembers);
//    }
//    return ok();
//  }
//
//  @Timed
//  @DELETE
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/{gid}/members")
//  public BaseResponse leave(@Auth Account account, @PathParam("gid") String gid, @Valid List<String> request) {
//    Group group = getGroup(account, gid);
//    int notifyType;
//    // Check if accounts exist
//    for (String uid : request) {
//      if (!accountsManager.get(uid).isPresent()) {
//        err(STATUS.NO_SUCH_GROUP_MEMBER, "No such account: " + uid);
//      }
//    }
//    boolean isKickout=false;
//    List<GroupMember> groupMembers=new ArrayList<GroupMember>();
//    // Leave
//    for (String uid: request) {
//      try {
//        if(!account.getNumber().equals(uid)){
//          isKickout=true;
//        }
//        GroupMember targetMember=groupManager.removeMemberWithPermissionCheck(account, group, accountsManager.get(uid).get());
//        groupMembers.add(targetMember);
//      } catch (NoPermissionException e) {
//        err(STATUS.NO_PERMISSION, e.getMessage());
//      } catch (NoSuchGroupException e) {
//        err(STATUS.NO_SUCH_GROUP, e.getMessage());
//      } catch (NoSuchGroupMemberException e) {
//        err(STATUS.NO_SUCH_GROUP_MEMBER, e.getMessage());
//      }
//    }
//    if(isKickout) {
//      notifyType=GroupNotify.GroupNotifyType.KICKOUT_GROUP.ordinal();
//    }else{
//      notifyType=GroupNotify.GroupNotifyType.LEAVE_GROUP.ordinal();
//    }
//    group=groupManager.addGroupVersion(group);
//    sendGroupNotify(Notify.ChangeType.MEMBER.ordinal(),account,group,GroupNotify.ActionType.NONE.ordinal(), notifyType,groupMembers,GroupNotify.ActionType.LEAVE.ordinal(),null,-1,Notify.Display.YES.ordinal(),groupMembers);
//
//    // TODO: Change encryption keys which should only be shared within the group if any
//
//    return ok();
//  }
//
//  @Timed
//  @POST
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/{gid}/members/{uid}")
//  public BaseResponse set(@Auth Account account, @PathParam("gid") String gid, @PathParam("uid") String uid, @Valid SetGroupMemberRequest request) {
//    Group group = getGroup(account, gid);
//    Account accountToSet = getAccount(account, uid);
//    List<GroupMember> groupMembers=new ArrayList<GroupMember>();
//    String content=null;
//    int display=Notify.Display.NO.ordinal();
//    int notifyType;
//    Object operatee=null;
//    try {
//      GroupMember member = groupManager.getMemberWithPermissionCheck(account, group, accountToSet);
////      member.setRole(request.getRole());
////      member.setDisplayName(request.getDisplayName());
////      member.setRemark(request.getRemark());
////      member.setNotification(request.getNotification());
//      int chageType=groupManager.setMemberSettingsWithPermissionCheck(account, group, member);
//      if(chageType==GroupManager.MemberChangeType.BASIC.ordinal()) {
//        notifyType=GroupNotify.GroupNotifyType.GROUP_MEMBERINFO_CHANGE.ordinal();
//      }else if(chageType==GroupManager.MemberChangeType.SETADMIN.ordinal()){
//        display=Notify.Display.YES.ordinal();
//        notifyType=GroupNotify.GroupNotifyType.GROUP_ADD_ADMIN.ordinal();
//        operatee=member.getUid();
//      }else {
//        display=Notify.Display.YES.ordinal();
//        notifyType=GroupNotify.GroupNotifyType.GROUP_DEL_ADMIN.ordinal();
//        operatee=member.getUid();
//      }
//      groupMembers.add(member);
//      group=groupManager.addGroupVersion(group);
//      sendGroupNotify(Notify.ChangeType.PERSONNEL.ordinal(),account,group,GroupNotify.ActionType.NONE.ordinal(), notifyType,groupMembers,GroupNotify.ActionType.UPDATE.ordinal(),null,-1,display,operatee);
//    } catch (InvalidParameterException e) {
//      err(STATUS.INVALID_PARAMETER, e.getMessage());
//    } catch (NoPermissionException e) {
//      err(STATUS.NO_PERMISSION, e.getMessage());
//    } catch (NoSuchGroupException e) {
//      err(STATUS.NO_SUCH_GROUP, e.getMessage());
//    } catch (NoSuchGroupMemberException e) {
//      err(STATUS.NO_SUCH_GROUP_MEMBER, e.getMessage());
//    }
//    return ok();
//  }
//
//  @Timed
//  @PUT
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/{gid}/announcement")
//  public BaseResponse addAnnouncement(@Auth Account account,  @PathParam("gid") String gid,@Valid SetGroupAnnouncementRequest request) {
//    GroupAnnouncement groupAnnouncement = null;
//    List<GroupAnnouncement> groupAnnouncements=null;
//    try {
//      groupAnnouncement=groupManager.addAnnouncementPermissionCheck(account,gid,request);
//      groupAnnouncements=new ArrayList<GroupAnnouncement>();
//      groupAnnouncements.add(groupAnnouncement);
//      logger.info(account.getNumber() + " create groupAnnouncement: " + groupAnnouncement.getId());
//    } catch (InvalidParameterException e) {
//      err(STATUS.INVALID_PARAMETER, e.getMessage());
//    } catch (NoPermissionException e) {
//      err(STATUS.NO_PERMISSION, e.getMessage());
//    } catch (NoSuchGroupException e) {
//      err(STATUS.NO_SUCH_GROUP, e.getMessage());
//    }
//    Group group=groupManager.addGroupVersion(gid);
//    sendGroupNotify(Notify.ChangeType.ANNOUNCEMENT.ordinal(),account,group,GroupNotify.ActionType.NONE.ordinal(), GroupNotify.GroupNotifyType.GROUP_ADD_ANNOUNCEMENT.ordinal(),null,-1,groupAnnouncements,GroupNotify.ActionType.ADD.ordinal(),Notify.Display.YES.ordinal(),null);
//    return ok(new CreateGroupAnnouncementResponse(groupAnnouncement.getId()));
//  }
//
//  @Timed
//  @POST
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/{gid}/announcement/{gaid}")
//  public BaseResponse setAnnouncement(@Auth Account account, @PathParam("gid") String gid,  @PathParam("gaid") String gaid, @Valid SetGroupAnnouncementRequest request) {
//    List<GroupAnnouncement> groupAnnouncements=null;
//    GroupAnnouncement groupAnnouncement = null;
//    try {
//      groupAnnouncement=groupManager.setAnnouncementPermissionCheck(account,gid,gaid,request);
//      groupAnnouncements=new ArrayList<GroupAnnouncement>();
//      groupAnnouncements.add(groupAnnouncement);
//    } catch (InvalidParameterException e) {
//      err(STATUS.INVALID_PARAMETER, e.getMessage());
//    } catch (NoPermissionException e) {
//      err(STATUS.NO_PERMISSION, e.getMessage());
//    } catch (NoSuchGroupException e) {
//      err(STATUS.NO_SUCH_GROUP, e.getMessage());
//    } catch (NoSuchGroupAnnouncementException e) {
//      err(STATUS.NO_SUCH_GROUP_ANNOUNCEMENT, e.getMessage());
//    }
//    Group group=groupManager.addGroupVersion(gid);
//    sendGroupNotify(Notify.ChangeType.ANNOUNCEMENT.ordinal(),account,group,GroupNotify.ActionType.NONE.ordinal(), GroupNotify.GroupNotifyType.GROUP_UPDATE_ANNOUNCEMENT.ordinal(),null,-1,groupAnnouncements,GroupNotify.ActionType.UPDATE.ordinal(),Notify.Display.YES.ordinal(),null);
//    return ok();
//  }
//
//  @Timed
//  @DELETE
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/{gid}/announcement/{gaid}")
//  public BaseResponse delAnnouncement(@Auth Account account, @PathParam("gid") String gid,  @PathParam("gaid") String gaid) {
//    List<GroupAnnouncement> groupAnnouncements=null;
//    GroupAnnouncement groupAnnouncement = null;
//    try {
//      groupManager.delAnnouncementPermissionCheck(account,gid,gaid);
//      groupAnnouncement=new GroupAnnouncement(gaid,gid,null,0,null,0,GroupAnnouncementTable.STATUS.DISMISSED.ordinal(),0,null);
//      groupAnnouncements=new ArrayList<GroupAnnouncement>();
//      groupAnnouncements.add(groupAnnouncement);
//    } catch (NoPermissionException e) {
//      err(STATUS.NO_PERMISSION, e.getMessage());
//    } catch (NoSuchGroupException e) {
//      err(STATUS.NO_SUCH_GROUP, e.getMessage());
//    } catch (NoSuchGroupAnnouncementException e) {
//      err(STATUS.NO_SUCH_GROUP_ANNOUNCEMENT, e.getMessage());
//    }
//    Group group=groupManager.addGroupVersion(gid);
//    sendGroupNotify(Notify.ChangeType.ANNOUNCEMENT.ordinal(),account,group,GroupNotify.ActionType.NONE.ordinal(), GroupNotify.GroupNotifyType.GROUP_DEL_ANNOUNCEMENT.ordinal(),null,-1,groupAnnouncements,GroupNotify.ActionType.DELETE.ordinal(),Notify.Display.YES.ordinal(),null);
//    return ok();
//  }
//
//  @Timed
//  @GET
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/{gid}/announcement/")
//  public BaseResponse getGroupAnnouncement(@Auth Account account, @PathParam("gid") String gid) {
//    List<GetGroupAnnouncementResponse.GroupAnnouncement> groupAnnouncementList=new ArrayList<GetGroupAnnouncementResponse.GroupAnnouncement>();
//    try {
//      List<GroupAnnouncement> groupAnnouncements=groupManager.getGroupAnnouncementPermissionCheck(account,gid);
//      if(groupAnnouncements!=null&&groupAnnouncements.size()>0){
//        for(GroupAnnouncement groupAnnouncement:groupAnnouncements){
//          groupAnnouncementList.add(new GetGroupAnnouncementResponse.GroupAnnouncement(groupAnnouncement.getId(), groupAnnouncement.getAnnouncementExpiry(), groupAnnouncement.getContent(),groupAnnouncement.getReviseTime()));
//        }
//      }
//    } catch (InvalidParameterException e) {
//      err(STATUS.INVALID_PARAMETER, e.getMessage());
//    } catch (NoPermissionException e) {
//      err(STATUS.NO_PERMISSION, e.getMessage());
//    } catch (NoSuchGroupException e) {
//      err(STATUS.NO_SUCH_GROUP, e.getMessage());
//    } catch (NoSuchGroupAnnouncementException e) {
//      err(STATUS.NO_SUCH_GROUP_ANNOUNCEMENT, e.getMessage());
//    }
//    return ok(new GetGroupAnnouncementResponse(gid, groupAnnouncementList));
//  }
//
//  private void sendGroupNotify(int changeType,Account operator, Group group,int groupAction,int notifyType, List<GroupMember> members,int groupMemeberAction , List<GroupAnnouncement> groupAnnouncements ,int groupAnnouncementAction,int display,final Object operatee){
//    executor.submit(new Runnable() {
//      @Override
//      public void run() {
//        String content=null;
//        try {
//          List<GroupNotify.GroupMember> notifyMembers = null;
//          List<GroupMember> needNotifymembers =members;
//          if(notifyType== GroupNotify.GroupNotifyType.CREATE_GROUP.ordinal()){
//            needNotifymembers= groupManager.getGroupMembersWithPermissionCheck(operator,group);
//          }
//          if(notifyType== GroupNotify.GroupNotifyType.GROUP_CHANGE_OWNER.ordinal()){
//            needNotifymembers=new ArrayList<GroupMember>();
//            GroupMember operatorMem=groupManager.getMemberWithPermissionCheck(operator,group.getId(),operator.getNumber());
//            GroupMember operateeMem=groupManager.getMemberWithPermissionCheck(operator,group.getId(),(String) operatee);
//            needNotifymembers.add(operatorMem);
//            needNotifymembers.add(operateeMem);
//          }
//          if(notifyType== GroupNotify.GroupNotifyType.GROUP_ADD_ADMIN.ordinal()||notifyType== GroupNotify.GroupNotifyType.GROUP_DEL_ADMIN.ordinal()){
//            List<GroupMember> groupMembers=new ArrayList<GroupMember>();
//            for(GroupMember groupMember:needNotifymembers){
//              String uid=groupMember.getUid();
//              GroupMember groupMemberN=groupManager.getMemberWithPermissionCheck(operator,group.getId(),uid);
//              if(groupMemberN!=null) {
//                groupMembers.add(groupMemberN);
//              }
//            }
//            needNotifymembers=groupMembers;
//          }
//
//          if (needNotifymembers != null && needNotifymembers.size() > 0) {
//            notifyMembers = new ArrayList<GroupNotify.GroupMember>();
//            for (GroupMember groupMember : needNotifymembers) {
//              GroupNotify.GroupMember notifyMember = new GroupNotify.GroupMember(groupMember.getUid(), groupMember.getRole(), groupMember.getDisplayName(), groupMemeberAction);
//              notifyMembers.add(notifyMember);
//            }
//          }
//          List<GroupNotify.GroupAnnouncement> notifyAnnouncements = null;
//          if (groupAnnouncements != null && groupAnnouncements.size() > 0) {
//            notifyAnnouncements = new ArrayList<GroupNotify.GroupAnnouncement>();
//            for (GroupAnnouncement groupAnnouncement : groupAnnouncements) {
//              GroupNotify.GroupAnnouncement notifyAnnouncement = new GroupNotify.GroupAnnouncement(groupAnnouncement.getId(), groupAnnouncement.getAnnouncementExpiry(), groupAnnouncement.getContent(), groupAnnouncement.getReviseTime(), groupAnnouncementAction);
//              notifyAnnouncements.add(notifyAnnouncement);
//            }
//          }
//          GroupNotify.Group nogifyGroup= new GroupNotify.Group(group.getName(), group.getMessageExpiry(), group.getAvatar(),groupAction,group.getVersion());
//          GroupNotify.NodifyData nodifyData = new GroupNotify.NodifyData(nogifyGroup, notifyMembers, notifyAnnouncements);
//          content=createGroupNotifyContent(operator,notifyType,operatee);
//          GroupNotify groupNotify = new GroupNotify(group.getId(), GroupNotify.VER, changeType, content, System.currentTimeMillis(), nodifyData, display);
//          sendNotify(group, operator, groupNotify);
//        }catch (Exception e){
//          logger.error(String.format("sendGroupNotify error! changeType:d%,operator:s%,group:s%,content:s%",changeType,operator.getNumber(),group.getId(),content));
//        }
//      }
//    });
//  }
//  private void sendNotify(Group group,Account operator,GroupNotify notify){
//    List<GroupMember> groupMembers=groupManager.getGroupMembers(group.getId());
//    for(GroupMember groupMember:groupMembers){
//      String uid=groupMember.getUid();
//      Account memAccount=accountsManager.get(uid).get();
//      InternalAccountsRow internalAccountsRow=internalAccountManager.get(memAccount.getNumber());
//      if(memAccount!=null&&internalAccountsRow!=null&&!internalAccountsRow.isDisabled()){
//        notifyManager.sendNotify(memAccount,notify,null);
//      }
//    }
//  }
//
//  private String createGroupNotifyContent(Account operator,int notifyType,Object operatee){
//    String content=null;
//    InternalAccountsRow opertorIAR=internalAccountManager.get(operator.getNumber());
//    String operatorName=operator.getNumber();
//    if(opertorIAR!=null&& !StringUtil.isEmpty(opertorIAR.getName())){
//      operatorName=opertorIAR.getName();
//    }
//    switch (GroupNotify.GroupNotifyType.values()[notifyType]){
//      case CREATE_GROUP:
//        content=String.format(GroupNotify.ContentTermTemplate.CREATE_GROUP,operatorName);
//        break;
//      case JOIN_GROUP:
//        break;
//      case LEAVE_GROUP:
//        StringBuffer leaveNames=new StringBuffer();
//        for(GroupMember groupMember:(List<GroupMember>)operatee) {
//          String uid=groupMember.getUid();
//          InternalAccountsRow leaveMemberRow=internalAccountManager.get(uid);
//          String leaveMemberName=uid;
//          if(leaveMemberRow!=null&& !StringUtil.isEmpty(leaveMemberRow.getName())){
//            leaveMemberName=leaveMemberRow.getName();
//          }
//          leaveNames.append(leaveMemberName).append(",");
//        }
//        if(leaveNames.length()>0){
//          leaveNames=leaveNames.delete(leaveNames.length()-1,leaveNames.length());
//        }
//        content = String.format(GroupNotify.ContentTermTemplate.LEAVE_GROUP, leaveNames.toString());
//        break;
//      case INVITE_JOIN_GROUP:
//        StringBuffer joinNames=new StringBuffer();
//        for(GroupMember groupMember:(List<GroupMember>)operatee) {
//          String uid=groupMember.getUid();
//          InternalAccountsRow joinMemberRow=internalAccountManager.get(uid);
//          String joinMemberName=uid;
//          if(joinMemberRow!=null&& !StringUtil.isEmpty(joinMemberRow.getName())){
//            joinMemberName=joinMemberRow.getName();
//          }
//          joinNames.append(joinMemberName).append(",");
//        }
//        content=String.format(GroupNotify.ContentTermTemplate.INVITE_JOIN_GROUP,operatorName,joinNames.toString());
//        break;
//      case KICKOUT_GROUP:
//        StringBuffer kickoutNames=new StringBuffer();
//        for(GroupMember groupMember:(List<GroupMember>)operatee) {
//          String uid=groupMember.getUid();
//          InternalAccountsRow kickoutMemberRow=internalAccountManager.get(uid);
//          String kickoutMemberName=uid;
//          if(kickoutMemberRow!=null&& !StringUtil.isEmpty(kickoutMemberRow.getName())){
//            kickoutMemberName=kickoutMemberRow.getName();
//          }
//          kickoutNames.append(kickoutMemberName).append(",");
//        }
//        content = String.format(GroupNotify.ContentTermTemplate.KICKOUT_GROUP,operatorName, kickoutNames.toString());
//        break;
//      case DISMISS_GROUP:
//        content=String.format(GroupNotify.ContentTermTemplate.DISMISS_GROUP,operatorName);
//        break;
//      case GROUP_NAME_CHANGE:
//        content = String.format(GroupNotify.ContentTermTemplate.GROUP_NAME_CHANGE, operatorName,operatee);
//        break;
//      case GROUP_AVATAR_CHANGE:
//        content = String.format(GroupNotify.ContentTermTemplate.GROUP_AVATAR_CHANGE, operatorName,operatee);
//        break;
//      case GROUP_MSG_EXPIRY_CHANGE:
//        content = String.format(GroupNotify.ContentTermTemplate.GROUP_MSG_EXPIRY_CHANGE, operatorName,operatee);
//        break;
//      case GROUP_ADD_ADMIN:
//        InternalAccountsRow addAdminIAR=internalAccountManager.get((String)operatee);
//        String addAdminName=(String)operatee;
//        if(addAdminIAR!=null&& !StringUtil.isEmpty(addAdminIAR.getName())){
//          addAdminName=addAdminIAR.getName();
//        }
//        content = String.format(GroupNotify.ContentTermTemplate.GROUP_ADD_ADMIN, operatorName,addAdminName);
//        break;
//      case GROUP_DEL_ADMIN:
//        InternalAccountsRow delAdminIAR=internalAccountManager.get((String)operatee);
//        String delAdminName=(String)operatee;
//        if(delAdminIAR!=null&& !StringUtil.isEmpty(delAdminIAR.getName())){
//          delAdminName=delAdminIAR.getName();
//        }
//        content = String.format(GroupNotify.ContentTermTemplate.GROUP_DEL_ADMIN, operatorName,delAdminName);
//        break;
//      case GROUP_MEMBERINFO_CHANGE:
//        content = String.format(GroupNotify.ContentTermTemplate.GROUP_MEMBERINFO_CHANGE, operatorName);
//        break;
//      case GROUP_CHANGE_OWNER:
//        InternalAccountsRow changeOwerIAR=internalAccountManager.get((String)operatee);
//        String changeOwerName=(String)operatee;
//        if(changeOwerIAR!=null&& !StringUtil.isEmpty(changeOwerIAR.getName())){
//          changeOwerName=changeOwerIAR.getName();
//        }
//        content=String.format(GroupNotify.ContentTermTemplate.GROUP_CHANGE_OWNER,operatorName,changeOwerName);
//        break;
//      case GROUP_ADD_ANNOUNCEMENT:
//        content=String.format(GroupNotify.ContentTermTemplate.GROUP_ADD_ANNOUNCEMENT,operatorName);
//        break;
//      case GROUP_UPDATE_ANNOUNCEMENT:
//        content=String.format(GroupNotify.ContentTermTemplate.GROUP_UPDATE_ANNOUNCEMENT,operatorName);
//        break;
//      case GROUP_DEL_ANNOUNCEMENT:
//        content=String.format(GroupNotify.ContentTermTemplate.GROUP_DEL_ANNOUNCEMENT,operatorName);
//        break;
//      default:
//    }
//    return content;
//  }
//  private Group getGroup(Account operator, String gid) {
//    try {
//      return groupManager.getGroupWithPermissionCheck(operator, gid);
//    } catch (NoPermissionException e) {
//      err(STATUS.NO_PERMISSION, e.getMessage());
//    } catch (NoSuchGroupException e) {
//      err(STATUS.NO_SUCH_GROUP, e.getMessage());
//    }
//    return null;
//  }
//
//  private Account getAccount(Account operator, String uid) {
//    Optional<Account> account = accountsManager.get(uid);
//    if (!account.isPresent()) {
//      err(STATUS.NO_SUCH_GROUP_MEMBER, "No such account: " + uid);
//    }
//    return account.get();
//  }
//
//  private void err(STATUS code, String description) {
//    logger.error(description);
//    throw new WebApplicationException(Response.status(403).entity(new BaseResponse(1, code.ordinal(), description, null)).build());
//  }
//
//  private BaseResponse ok() {
//    return ok(null);
//  }
//
//  private BaseResponse ok(Object data) {
//    return new BaseResponse(1, 0, "OK", data);
//  }
//}
