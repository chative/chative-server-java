package org.whispersystems.textsecuregcm.controllers;

import com.auth0.jwt.interfaces.Claim;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.auth.Auth;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.WhisperServerConfigurationApollo;
import org.whispersystems.textsecuregcm.distributedlock.DistributedLock;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.exceptions.*;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.StringUtil;
import org.whispersystems.textsecuregcm.util.TokenUtil;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.concurrent.locks.Lock;

@Path("/v1/groups")
public class GroupControllerWithTransaction {


  private final Logger logger = LoggerFactory.getLogger(GroupControllerWithTransaction.class);

  private final RateLimiters rateLimiters;
  private final AccountsManager accountsManager;
  private final GroupManagerWithTransaction groupManager;
  private final TokenUtil tokenUtil;

  public GroupControllerWithTransaction(RateLimiters rateLimiters, AccountsManager accountsManager, GroupManagerWithTransaction groupManager, TokenUtil tokenUtil) {
    this.rateLimiters = rateLimiters;
    this.accountsManager = accountsManager;
    this.groupManager = groupManager;
    this.tokenUtil=tokenUtil;
  }

  @Timed
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public BaseResponse create(@Auth Account account, @Valid CreateGroupRequest request)
          throws RateLimitExceededException {
    if(request==null){
     BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param ",logger);
    }
    rateLimiters.getCreateGroupLimiter().validate(account.getNumber());
    Group group = null;
    List<Account> operatees=new ArrayList<Account>();
    //Set<Long> extIds=new HashSet<>();
    //if(!accountsManager.isBootAccount(account.getNumber())){
    //  extIds.add(account.getExtId());
    //}
    // check messageExpiry
    if (request!=null&&request.getMessageExpiry().isPresent()&&!validGroupExpireTime(request.getMessageExpiry().get())) {
      logger.error("Invalid param messageExpiry, value:{} ", request.getMessageExpiry().get());
      return new BaseResponse(1, BaseResponse.STATUS.INVALID_PARAMETER.getState(), "Invalid param messageExpiry ",null);
    }
    // check if accounts exist
    if(request!=null&&request.getNumbers().isPresent()&&request.getNumbers().get().size()>0) {
      boolean isInContactList = accountsManager.isFriend(account.getNumber(), request.getNumbers().get());
      if (!isInContactList) {
        logger.error("accountsManager.isFriend check failed, uid:{}, list: {} ", account.getNumber(), request.getNumbers().get());
        return new BaseResponse(1, BaseResponse.STATUS.INVALID_PARAMETER.getState(), "No such account ", null);
      }
      for (String uid : request.getNumbers().get()) {
        Account operatee = getAccount(uid);
        if (null == operatee) {
          BaseResponse.err(200,BaseResponse.STATUS.NO_SUCH_GROUP_MEMBER, "No such account: " + uid,logger,new CreateGroupResponse(null,uid));
        }
        operatees.add(operatee);
        if(accountsManager.isBootAccount(uid)){
          continue;
        }
        //extIds.add(operatee.getExtId());
      }
    }
    boolean internal=false;
    List<GroupMember> groupMembers=new ArrayList<GroupMember>();
    try {
      group = groupManager.createGroupWithPermissionCheck(account, new Group(request.getName().isPresent()?request.getName().get():null, account.getNumber(), GroupsTable.STATUS.ACTIVE.ordinal(), request.getMessageExpiry().isPresent()?request.getMessageExpiry().get():-1, request.getAvatar().isPresent()?request.getAvatar().get():null,request.getInvitationRule().isPresent()?request.getInvitationRule().get():-1, GroupsTable.RemindCycle.NONE.getValue(),internal,internal,false,GroupMembersTable.ROLE.MEMBER.ordinal()),operatees,groupMembers,request.getNotification());
      logger.info(account.getNumber() + " create group: " + group.getId());
    } catch (InvalidParameterException e) {
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, e.getMessage(),logger);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    }catch (ExceedingGroupMemberSizeException e) {
      BaseResponse.err(BaseResponse.STATUS.GROUP_IS_FULL_OR_EXCEEDS, e.getMessage(),logger);
    }
    groupManager.sendGroupNotify(GroupNotify.ChangeType.BASIC.ordinal(),account,group,GroupNotify.ActionType.ADD.ordinal(), GroupNotify.GroupNotifyDetailedType.CREATE_GROUP.ordinal(),groupMembers,GroupNotify.ActionType.ADD.ordinal(), null, null,-1,GroupNotify.Display.YES.ordinal(),null);
    return BaseResponse.ok(new CreateGroupResponse(group.getId()));
  }


  @Timed
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{gid}")
  public BaseResponse upgradeCreate(@Auth Account account, @PathParam("gid") String gid, @Valid CreateGroupRequest request) {
    if(request==null){
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param ",logger);
    }
    Group group = null;
    List<Account> operatees=new ArrayList<Account>();
    //Set<Long> extIds=new HashSet<>();
    //if(!accountsManager.isBootAccount(account.getNumber())){
    //  extIds.add(account.getExtId());
    //}
    // check if accounts exist
    if(request!=null&&request.getNumbers().isPresent()&&request.getNumbers().get().size()>0) {
      for (String uid : request.getNumbers().get()) {
        Account operatee = getAccount(uid);
        if (null == operatee) {
          BaseResponse.err(200,BaseResponse.STATUS.NO_SUCH_GROUP_MEMBER, "No such account: " +uid,logger,new CreateGroupResponse(null,uid));
        }
        operatees.add(operatee);
        if(accountsManager.isBootAccount(uid)){
          continue;
        }
        //extIds.add(operatee.getExtId());
      }
    }
    boolean internal=false;
    List<GroupMember> groupMembers=new ArrayList<GroupMember>();
    try {
      group = groupManager.upgradeCreateGroupWithPermissionCheck(account, new Group(gid,request.getName().isPresent()?request.getName().get():null, account.getNumber(),  System.currentTimeMillis(),GroupsTable.STATUS.ACTIVE.ordinal(), request.getMessageExpiry().isPresent()?request.getMessageExpiry().get():-1, request.getAvatar().isPresent()?request.getAvatar().get():null,request.getInvitationRule().isPresent()?request.getInvitationRule().get():-1,1, GroupsTable.RemindCycle.NONE.getValue(),internal,internal,false,GroupMembersTable.ROLE.MEMBER.ordinal()),operatees,groupMembers);
      logger.info(account.getNumber() + " create group: " + group.getId());
    } catch (InvalidParameterException e) {
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, e.getMessage(),logger);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (UnableToExecuteStatementException e){
      if(e.getMessage().indexOf("duplicate key value violates unique constraint \"pk_groups\"")!=-1) {
        BaseResponse.err(BaseResponse.STATUS.GROUP_EXISTS, "group is exists",logger);
      }else{
        BaseResponse.err(BaseResponse.STATUS.OTHER_ERROR, e.getMessage(),logger);
      }
    }
    groupManager.sendGroupNotify(GroupNotify.ChangeType.BASIC.ordinal(),account,group,GroupNotify.ActionType.ADD.ordinal(), GroupNotify.GroupNotifyDetailedType.CREATE_GROUP.ordinal(),groupMembers,GroupNotify.ActionType.ADD.ordinal(), null, null,-1,GroupNotify.Display.YES.ordinal(),null);
    return BaseResponse.ok(new CreateGroupResponse(group.getId()));
  }


  @Timed
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{gid}")
  public BaseResponse delete(@Auth Account account, @PathParam("gid") String gid) {
    Group group = getGroup(account, gid);

    try {
      group.setStatus(GroupsTable.STATUS.DISMISSED.ordinal());
      group=groupManager.setGroupWithPermissionCheck(account, group,-1);
      groupManager.clearMemberGroupCacheForGroup(gid);
      logger.info(account.getNumber() + " delete group: " + group.getId());
    } catch (InvalidParameterException e) {
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, e.getMessage(),logger);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    }
    groupManager.sendGroupNotify(GroupNotify.ChangeType.BASIC.ordinal(),account,group,GroupNotify.ActionType.DELETE.ordinal(), GroupNotify.GroupNotifyDetailedType.DISMISS_GROUP.ordinal(), null,-1,null,null,-1,GroupNotify.Display.YES.ordinal(),null);
    return BaseResponse.ok();
  }

  @Timed
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/msgencinfo/{gid}")
  public BaseResponse getMemberMessageEncInfo(@Auth Account account, @PathParam("gid") String gid) {
    Group group = getGroup(account, gid);

    // Members

    int minMsgEncVersion = 0;
    List<GroupMessageEncInfoRes.GroupMessageEncInfo> memberInfos = null;
    try {
      final List<GroupMember> groupMembers = groupManager.getGroupMembersWithPermissionCheck(account, group);
      memberInfos = new ArrayList<>(groupMembers.size());
      for ( GroupMember groupMember : groupMembers) {
        final int msgEncVersion = groupMember.getMsgEncVersion();
        if (msgEncVersion > 0 && (minMsgEncVersion == 0 || msgEncVersion < minMsgEncVersion)) {
          minMsgEncVersion = msgEncVersion;
        }
        memberInfos.add(new GroupMessageEncInfoRes.GroupMessageEncInfo(groupMember.getUid(),
                groupMember.getIdentityKey(),groupMember.getRegistrationId()));
      }

    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    }

    return BaseResponse.ok(new GroupMessageEncInfoRes(memberInfos, minMsgEncVersion));
  }
  @Timed
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/meetingencinfo/{gid}")
  public BaseResponse getMemberMeetingEncInfo(@Auth Account account, @PathParam("gid") String gid) {
    Group group = getGroup(account, gid);

    // Members

    int minMeetingVersion = 0;
    List<GroupMeetingEncInfoRes.GroupMeetingEncInfo> memberInfos = null;
    try {
      final List<GroupMember> groupMembers = groupManager.getGroupMembersWithPermissionCheck(account, group);
      memberInfos = new ArrayList<>(groupMembers.size());
      for ( GroupMember groupMember : groupMembers) {
        final int meetingVersion = groupMember.getMeetingVersion();
        if (meetingVersion > 0 && (minMeetingVersion == 0 || meetingVersion < minMeetingVersion)) {
          minMeetingVersion = meetingVersion;
        }
        memberInfos.add(new GroupMeetingEncInfoRes.GroupMeetingEncInfo(groupMember.getUid(), groupMember.getIdentityKey()));
      }

    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    }

    final int groupMaxMeetingVersion = groupManager.getGroupMaxMeetingVersion();
    if (minMeetingVersion > groupMaxMeetingVersion)
      minMeetingVersion = groupMaxMeetingVersion;
    logger.info("gid :{} minMeetingVersion , is set {}", gid, minMeetingVersion);

    return BaseResponse.ok(new GroupMeetingEncInfoRes(memberInfos, minMeetingVersion));
  }

  @Timed
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{gid}")
  public BaseResponse get(@Auth Account account, @PathParam("gid") String gid) {
    Group group = getGroup(account, gid);

    // Members
    List<GetGroupResponse.GroupMember> members = new LinkedList<>();
    try {
      for (GroupMember member : groupManager.getGroupMembersWithPermissionCheck(account, group)) {
        if(account.getNumber().equals(member.getUid())){
          members.add(new GetGroupResponse.GroupMemberWithSelf(member.getUid(), member.getRole(), groupManager.getDisplayName(account,member),member.getNotification(),member.getRemark(),member.isUseGlobal(),member.getRapidRole(),account.getExtId()));
        }else {
          Optional<Account> accountOptional=accountsManager.get(member.getUid());
          if(!accountOptional.isPresent()||accountOptional.get().isinValid()) continue;
          members.add(new GetGroupResponse.GroupMember(member.getUid(), member.getRole(), groupManager.getDisplayName(accountOptional.get(),member),member.getRapidRole(),accountOptional.get().getExtId()));
        }
      }
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    }

    return BaseResponse.ok(new GetGroupResponse(group.getName(), group.getMessageExpiry(), group.getAvatar(),
            group.getInvitationRule(),group.getVersion(), members,group.getRemindCycle(),group.isAnyoneRemove(),
            group.isRejoin(),group.isExt(),group.getPublishRule(), group.linkInviteSwitchOn()));
  }


  @Timed
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{gid}")
  public BaseResponse set(@Auth Account account, @PathParam("gid") String gid, @Valid SetGroupRequest request) {
    if(request==null){
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param ",logger);
    }
    Group group = getGroup(account, gid);
    int changeType=GroupNotify.ChangeType.BASIC.ordinal();
    int display=GroupNotify.Display.NO.ordinal();
    int groupMemeberAction=-1;
    int groupActionType=GroupNotify.ActionType.UPDATE.ordinal();
    int notifyDetailedType;
    Object operatee=null;
    try {
      // Get current owner
      GroupMember owner = null;
      for (GroupMember member: groupManager.getGroupMembersWithPermissionCheck(account, group)) {
        if (member.getRole() == GroupMembersTable.ROLE.OWNER.ordinal()) {
          owner = member;
          break;
        }
      }

      if (request.getOwner().isPresent()&&!owner.getUid().equals( request.getOwner().get())) {
        groupActionType=GroupNotify.ActionType.NONE.ordinal();
        // Transfer group
        Account newOnwer = getAccount(request.getOwner().get());
        if (null == newOnwer) {
          BaseResponse.err(200,BaseResponse.STATUS.NO_SUCH_GROUP_MEMBER, "No such account: " + request.getOwner().get(),logger,new CreateGroupResponse(null,request.getOwner().get()));
        }
        group=groupManager.transferGroupWithPermissionCheck(account, group, newOnwer);
        groupManager.clearGroupMembersCache(gid);
        changeType=GroupNotify.ChangeType.PERSONNEL.ordinal();
        display=GroupNotify.Display.YES.ordinal();
        notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_CHANGE_OWNER.ordinal();
        operatee=newOnwer.getNumber();
        groupMemeberAction=GroupNotify.ActionType.UPDATE.ordinal();
      } else {
        // Change settings
        boolean isChange=groupInfoIsChange(group,request);
        if(!isChange){
          return BaseResponse.ok();
        }
        changeType=GroupNotify.ChangeType.BASIC.ordinal();
        if(request.getName().isPresent()&&!group.getName().equals(request.getName().get())) {
          display=GroupNotify.Display.YES.ordinal();
          notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_NAME_CHANGE.ordinal();
          operatee=request.getName().get();
          group.setName(request.getName().get());
        }else if(request.getAvatar().isPresent()&&!request.getAvatar().get().equals(group.getAvatar())){
          display=GroupNotify.Display.YES.ordinal();
          notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_AVATAR_CHANGE.ordinal();
          group.setAvatar(request.getAvatar().get());
        }else if(request.getMessageExpiry().isPresent()&&group.getMessageExpiry()!=request.getMessageExpiry().get()){
          display=GroupNotify.Display.YES.ordinal();
          notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_MSG_EXPIRY_CHANGE.ordinal();
          if (!validGroupExpireTime(request.getMessageExpiry().get())) {
            //BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param messageExpiry ",logger);
            logger.error("Invalid param messageExpiry, value:{} ", request.getMessageExpiry().get());
            return new BaseResponse(1, BaseResponse.STATUS.INVALID_PARAMETER.getState(), "Invalid param messageExpiry ",null);
          }
          group.setMessageExpiry(request.getMessageExpiry().get());
        }else if(request.getInvitationRule().isPresent()&&group.getInvitationRule()!=request.getInvitationRule().get()&&GroupMembersTable.ROLE.fromOrdinal(request.getInvitationRule().get())!=null){
          display=GroupNotify.Display.NO.ordinal();
          notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_INVITATION_RULE_CHANGE.ordinal();
          group.setInvitationRule(request.getInvitationRule().get());
        }else if(request.getRemindCycle().isPresent()&&!request.getRemindCycle().get().equals(group.getRemindCycle())){
          display=GroupNotify.Display.YES.ordinal();
          notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_REMIND_CHANGE.ordinal();
          group.setRemindCycle(request.getRemindCycle().get());
        }else if(request.getAnyoneRemove().isPresent()&&!request.getAnyoneRemove().get().equals(group.isAnyoneRemove())){
          display=GroupNotify.Display.NO.ordinal();
          notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_ANYONE_REMOVE_CHANGE.ordinal();
          group.setAnyoneRemove(request.getAnyoneRemove().get());
        }else if(request.getLinkInviteSwitch().isPresent()&&!request.getLinkInviteSwitch().get().equals(group.linkInviteSwitchOn())){
          display=GroupNotify.Display.NO.ordinal();
          notifyDetailedType= GroupNotify.GroupNotifyDetailedType.WEBLINK_INVITE_SWITCH_CHANGE.getCode();
          group.setLinkInviteSwitch(request.getLinkInviteSwitch().get());
        }else if(request.getRejoin().isPresent()&&!request.getRejoin().get().equals(group.isRejoin())){
          display=GroupNotify.Display.NO.ordinal();
          notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_REJOIN_CHANGE.ordinal();
          group.setRejoin(request.getRejoin().get());
        }else if(request.getPublishRule().isPresent()&&group.getPublishRule()!=request.getPublishRule().get()&&GroupMembersTable.ROLE.fromOrdinal(request.getPublishRule().get())!=null){
          display=GroupNotify.Display.YES.ordinal();
          notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_PUBLISH_RULE_CHANGE.ordinal();
          group.setPublishRule(request.getPublishRule().get());
        }else{
          display=GroupNotify.Display.NO.ordinal();
          notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_OTHER_CHANGE.ordinal();
        }

        group=groupManager.setGroupWithPermissionCheck(account, group, notifyDetailedType);
        groupManager.clearMemberGroupCacheForGroup(gid);
        logger.info(account.getNumber() + " set group: " + group.getId() + " name: " + request.getName() + ", messageExpiry: " + request.getMessageExpiry() + ", avatar: " + request.getAvatar() );
      }
      groupManager.sendGroupNotify(changeType,account,group,groupActionType, notifyDetailedType, null,groupMemeberAction,null, null,-1,display,operatee);
    } catch (InvalidParameterException e) {
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, e.getMessage(),logger);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    } catch (NoSuchGroupMemberException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP_MEMBER, e.getMessage(),logger);
    }
    return BaseResponse.ok();
  }

  @Timed
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public BaseResponse getMyGroups(@Auth Account account) {
    List<Group> groups = null;
    try {
      groups = groupManager.getMemberGroupsWithPermissionCheck(account);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    }

    List<GetMyGroupsResponse.Group> responseGroups = new LinkedList<>();
    for (Group group : groups) {
      responseGroups.add(new GetMyGroupsResponse.Group(group.getId(), group.getName(), group.getMessageExpiry(), group.getAvatar(),group.getStatus(),group.getInvitationRule(),group.getVersion(),group.getRemindCycle(),group.isAnyoneRemove(),group.isRejoin(),group.isExt(),group.getPublishRule()));
    }

    return BaseResponse.ok(new GetMyGroupsResponse(responseGroups));
  }

  @Timed
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{gid}/members")
  public BaseResponse getMembersInfo(@Auth Account account, @PathParam("gid") String gid) {
    Group group = getGroup(account, gid);
    GroupMember groupMember=null;
    // join members
    try {
      groupMember=groupManager.getMemberWithPermissionCheck(account,group,account);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    }catch (NoSuchGroupMemberException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP_MEMBER, e.getMessage(),logger);
    }
    return BaseResponse.ok(new GetGroupResponse.GroupMemberWithSelf(groupMember.getUid(),groupMember.getRole(), groupManager.getDisplayName(account,groupMember),groupMember.getNotification(),groupMember.getRemark(),groupMember.isUseGlobal(),groupMember.getRapidRole(),account.getExtId()));
  }


  @Timed
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{gid}/members")
  public BaseResponse join(@Auth Account account, @PathParam("gid") String gid, @Valid GroupMemberListRequest groupMemberListRequest) {
    if(groupMemberListRequest==null||groupMemberListRequest.getNumbers()==null||groupMemberListRequest.getNumbers().size()==0){
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param numbers ",logger);
    }
    Group group = getGroup(account, gid);
    List<Account> operatees=new ArrayList<Account>();
    // check if accounts exist
    for (String uid : groupMemberListRequest.getNumbers()) {
      Account operatee = getAccount(uid);
      if (null == operatee) {
        BaseResponse.err(200,BaseResponse.STATUS.NO_SUCH_GROUP_MEMBER, "No such account: " +uid,logger,new CreateGroupResponse(null,uid));
      }
      operatees.add(operatee);
    }
    boolean extChange=false;
    List<GroupMember> groupMembers=new ArrayList<GroupMember>();
    // join members
    try {
      boolean oldExt=group.isExt();
      group=groupManager.addMemberWithPermissionCheck(account, group,operatees, groupMembers);
      if(group.isExt()!=oldExt){
        extChange=true;
      }
      groupManager.clearGroupMembersCache(gid);
      for(Account account1:operatees) {
        groupManager.clearMemberGroupCache(account1.getNumber());
      }
    } catch (InvalidParameterException e) {
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, e.getMessage(),logger);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    }catch (ExceedingGroupMemberSizeException e) {
      BaseResponse.err(BaseResponse.STATUS.GROUP_IS_FULL_OR_EXCEEDS, e.getMessage(),logger);
    }
    if(groupMembers.size()>0) {
      groupManager.sendGroupNotify(GroupNotify.ChangeType.MEMBER.ordinal(), account, group, extChange?GroupNotify.ActionType.UPDATE.ordinal():GroupNotify.ActionType.NONE.ordinal(), GroupNotify.GroupNotifyDetailedType.INVITE_JOIN_GROUP.ordinal(), groupMembers, GroupNotify.ActionType.ADD.ordinal(), null, null,-1, GroupNotify.Display.YES.ordinal(), groupMembers);
    }
    return BaseResponse.ok();
  }

  @Timed
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{gid}/members")
  public BaseResponse leave(@Auth Account account, @PathParam("gid") String gid,  @Valid GroupMemberListRequest groupMemberListRequest) {
    if(groupMemberListRequest==null||groupMemberListRequest.getNumbers()==null||groupMemberListRequest.getNumbers().size()==0){
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param numbers ",logger);
    }
    Group group = getGroup(account, gid);
    int notifyDetailedType;
    int memberActionType;
    boolean isKickout=false;
    boolean extChange=false;
    List<Account> operatees=new ArrayList<Account>();
    // Check if accounts exist
    for (String uid : groupMemberListRequest.getNumbers()) {
      Optional<Account> operatee=accountsManager.get(uid);
      if (!operatee.isPresent()) {
        BaseResponse.err(200,BaseResponse.STATUS.NO_SUCH_GROUP_MEMBER, "No such account: " +uid,logger,new CreateGroupResponse(null,uid));
      }
      if(!account.getNumber().equals(uid)){
        isKickout=true;
      }
      operatees.add(operatee.get());
    }
    List<GroupMember> groupMembers=new ArrayList<GroupMember>();
    // Leave
    try {
      boolean oldExt=group.isExt();
      group=groupManager.removeMemberWithPermissionCheck(account, group, operatees,groupMembers);
      if(group.isExt()!=oldExt){
        extChange=true;
      }
      groupManager.clearGroupMembersCache(gid);
      for(Account account1:operatees) {
        groupManager.clearMemberGroupCache(account1.getNumber());
      }
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    } catch (NoSuchGroupMemberException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP_MEMBER, e.getMessage(),logger);
    }
    if(isKickout) {
      notifyDetailedType= GroupNotify.GroupNotifyDetailedType.KICKOUT_GROUP.ordinal();
      memberActionType= GroupNotify.ActionType.DELETE.ordinal();
    }else{
      notifyDetailedType= GroupNotify.GroupNotifyDetailedType.LEAVE_GROUP.ordinal();
      memberActionType= GroupNotify.ActionType.LEAVE.ordinal();
    }
    groupManager.sendGroupNotify(GroupNotify.ChangeType.MEMBER.ordinal(),account,group,extChange?GroupNotify.ActionType.UPDATE.ordinal():GroupNotify.ActionType.NONE.ordinal(), notifyDetailedType,groupMembers,memberActionType,null, null,-1,GroupNotify.Display.YES.ordinal(),groupMembers);

    // TODO: Change encryption keys which should only be shared within the group if any

    return BaseResponse.ok();
  }

  @Timed
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{gid}/members/{uid}")
  public BaseResponse set(@Auth Account account, @PathParam("gid") String gid, @PathParam("uid") String uid, @Valid SetGroupMemberRequest request) {
    if(request==null){
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param numbers ",logger);
    }
    Group group = getGroup(account, gid);
    Account accountToSet = getAccount(uid);
    if (null == accountToSet) {
      BaseResponse.err(200,BaseResponse.STATUS.NO_SUCH_GROUP_MEMBER, "No such account: " +uid,logger,new CreateGroupResponse(null,uid));
    }
    List<GroupMember> groupMembers=new ArrayList<>();
    int display=GroupNotify.Display.NO.ordinal();
    int notifyDetailedType;
    int groupNotifyType=GroupNotify.ChangeType.PERSONNEL.ordinal();
    boolean isChangePrivate=true;
    Object operatee=null;
    try {
      GroupMember member = groupManager.getMemberWithPermissionCheck(account, group, accountToSet);
      if(request.getRole().isPresent()&&request.getRole().get()!=member.getRole()) {
        member.setRole(request.getRole().get());
      }
      if(request.getRapidRole().isPresent()&&request.getRapidRole().get()!=member.getRapidRole()) {
        member.setRapidRole(request.getRapidRole().get());
        isChangePrivate=false;
      }
      if(request.getDisplayName().isPresent()&&!request.getDisplayName().get().equals(member.getDisplayName())) {
        member.setDisplayName(request.getDisplayName().get());
        isChangePrivate=false;
      }
      if(request.getRemark().isPresent()&&!request.getRemark().get().equals(member.getRemark())) {
        member.setRemark(request.getRemark().get());
      }
      //兼容老版本。不为空且global为true时，将global配置带入到Notification。为空且Notification不为空时默认为老版本，将Global设置为false
      if(request.getUseGlobal().isPresent()&&!request.getUseGlobal().get().equals(member.isUseGlobal())) {
        member.setUseGlobal(request.getUseGlobal().get());
        if(request.getUseGlobal().get()||!request.getNotification().isPresent()) {
          int globalNotification = accountsManager.getGlobalNotification(accountToSet);
          request.setNotification(globalNotification);
        }
      }else{
        if(request.getNotification().isPresent()&&request.getNotification().get()!=member.getNotification()) {
          member.setUseGlobal(false);
        }
      }
      if(request.getNotification().isPresent()&&request.getNotification().get()!=member.getNotification()) {
        member.setNotification(request.getNotification().get());
      }
      int chageType=groupManager.setMemberSettingsWithPermissionCheck(account,group, member);
      groupManager.clearGroupMembersCache(gid);
      if(chageType==GroupManagerWithTransaction.MemberChangeType.BASIC.ordinal()) {
        notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_MEMBERINFO_CHANGE.ordinal();
        if(isChangePrivate){
          groupNotifyType=GroupNotify.ChangeType.PERSONNEL_PRIVATE.ordinal();
          notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_MEMBERINFO_CHANGE_PRIVATE.ordinal();
        }
      }else if(chageType==GroupManagerWithTransaction.MemberChangeType.RAPIDROLE.ordinal()){
        display=GroupNotify.Display.YES.ordinal();
        groupNotifyType=GroupNotify.ChangeType.CHANGE_RAPID_ROLE.getCode();
        notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_CHANGE_RAPID_ROLE.ordinal();
        operatee=member;
      }else if(chageType==GroupManagerWithTransaction.MemberChangeType.SETADMIN.ordinal()){
        display=GroupNotify.Display.YES.ordinal();
        notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_ADD_ADMIN.ordinal();
        operatee=member.getUid();
      }else {
        display=GroupNotify.Display.YES.ordinal();
        notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_DEL_ADMIN.ordinal();
        operatee=member.getUid();
      }
      groupMembers.add(member);
      group =getGroup(account, gid);
      groupManager.sendGroupNotify(groupNotifyType,account,group,GroupNotify.ActionType.NONE.ordinal(), notifyDetailedType,groupMembers,GroupNotify.ActionType.UPDATE.ordinal(),null,null,-1,display,operatee);
    } catch (InvalidParameterException e) {
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, e.getMessage(),logger);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    } catch (NoSuchGroupMemberException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP_MEMBER, e.getMessage(),logger);
    }
    return BaseResponse.ok();
  }

  @Timed
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{gid}/announcement")
  public BaseResponse addAnnouncement(@Auth Account account,  @PathParam("gid") String gid,@Valid SetGroupAnnouncementRequest request) {
    if(request==null){
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param ",logger);
    }
    GroupAnnouncement groupAnnouncement = null;
    List<GroupAnnouncement> groupAnnouncements=null;
    Group group=null;
    try {
      groupAnnouncement=groupManager.addAnnouncementPermissionCheck(account,gid,request);
      groupAnnouncements=new ArrayList<GroupAnnouncement>();
      groupAnnouncements.add(groupAnnouncement);
      logger.info(account.getNumber() + " create groupAnnouncement: " + groupAnnouncement.getId());
    } catch (InvalidParameterException e) {
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, e.getMessage(),logger);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    }
    group =getGroup(account, gid);
    groupManager.sendGroupNotify(GroupNotify.ChangeType.ANNOUNCEMENT.ordinal(),account,group,GroupNotify.ActionType.NONE.ordinal(), GroupNotify.GroupNotifyDetailedType.GROUP_ADD_ANNOUNCEMENT.ordinal(),null,-1,groupAnnouncements, null,GroupNotify.ActionType.ADD.ordinal(),GroupNotify.Display.YES.ordinal(),null);
    return BaseResponse.ok(new CreateGroupAnnouncementResponse(groupAnnouncement.getId()));
  }

  @Timed
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{gid}/announcement/{gaid}")
  public BaseResponse setAnnouncement(@Auth Account account, @PathParam("gid") String gid,  @PathParam("gaid") String gaid, @Valid SetGroupAnnouncementRequest request) {
    if(request==null){
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param ",logger);
    }
    List<GroupAnnouncement> groupAnnouncements=null;
    GroupAnnouncement groupAnnouncement = null;
    Group group=null;
    try {
      groupAnnouncement=groupManager.setAnnouncementPermissionCheck(account,gid,gaid,request);
      groupAnnouncements=new ArrayList<GroupAnnouncement>();
      groupAnnouncements.add(groupAnnouncement);
    } catch (InvalidParameterException e) {
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, e.getMessage(),logger);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    } catch (NoSuchGroupAnnouncementException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP_ANNOUNCEMENT, e.getMessage(),logger);
    }
    group =getGroup(account, gid);
    groupManager.sendGroupNotify(GroupNotify.ChangeType.ANNOUNCEMENT.ordinal(),account,group,GroupNotify.ActionType.NONE.ordinal(), GroupNotify.GroupNotifyDetailedType.GROUP_UPDATE_ANNOUNCEMENT.ordinal(),null,-1,groupAnnouncements, null, GroupNotify.ActionType.UPDATE.ordinal(),GroupNotify.Display.YES.ordinal(),null);
    return BaseResponse.ok();
  }

  @Timed
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{gid}/announcement/{gaid}")
  public BaseResponse delAnnouncement(@Auth Account account, @PathParam("gid") String gid,  @PathParam("gaid") String gaid) {
    List<GroupAnnouncement> groupAnnouncements=null;
    GroupAnnouncement groupAnnouncement = null;
    Group group=null;
    try {
      groupManager.delAnnouncementPermissionCheck(account,gid,gaid);
      groupAnnouncement=new GroupAnnouncement(gaid,gid,null,0,null,0,GroupAnnouncementTable.STATUS.DISMISSED.ordinal(),0,null);
      groupAnnouncements=new ArrayList<GroupAnnouncement>();
      groupAnnouncements.add(groupAnnouncement);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    } catch (NoSuchGroupAnnouncementException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP_ANNOUNCEMENT, e.getMessage(),logger);
    }
    group =getGroup(account, gid);
    groupManager.sendGroupNotify(GroupNotify.ChangeType.ANNOUNCEMENT.ordinal(),account,group,GroupNotify.ActionType.NONE.ordinal(), GroupNotify.GroupNotifyDetailedType.GROUP_DEL_ANNOUNCEMENT.ordinal(),null,-1,groupAnnouncements, null,GroupNotify.ActionType.DELETE.ordinal(),GroupNotify.Display.YES.ordinal(),null);
    return BaseResponse.ok();
  }

  @Timed
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{gid}/announcement/")
  public BaseResponse getGroupAnnouncement(@Auth Account account, @PathParam("gid") String gid) {
    List<GetGroupAnnouncementResponse.GroupAnnouncement> groupAnnouncementList=new ArrayList<GetGroupAnnouncementResponse.GroupAnnouncement>();
    try {
      List<GroupAnnouncement> groupAnnouncements=groupManager.getGroupAnnouncementPermissionCheck(account,gid);
      if(groupAnnouncements!=null&&groupAnnouncements.size()>0){
        for(GroupAnnouncement groupAnnouncement:groupAnnouncements){
          groupAnnouncementList.add(new GetGroupAnnouncementResponse.GroupAnnouncement(groupAnnouncement.getId(), groupAnnouncement.getAnnouncementExpiry(), groupAnnouncement.getContent(),groupAnnouncement.getReviseTime()));
        }
      }
    } catch (InvalidParameterException e) {
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, e.getMessage(),logger);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    } catch (NoSuchGroupAnnouncementException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP_ANNOUNCEMENT, e.getMessage(),logger);
    }
    return BaseResponse.ok(new GetGroupAnnouncementResponse(gid, groupAnnouncementList));
  }

  @Timed
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{gid}/pin")
  public BaseResponse addPin(@Auth Account account,  @PathParam("gid") String gid,@Valid SetGroupPinRequest request) {
    if(request==null || !request.getConversationId().isPresent() ||StringUtil.isEmpty(request.getConversationId().get())){
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param ",logger);
    }
    GroupPin groupPin= null;
    List<GroupPin> groupPins=null;
    Group group=null;
    String conversationId = request.getConversationId().get();
    Lock locker = DistributedLock.getLocker(new String[]{gid, conversationId});
    try{
      locker.lock();
      groupPin=groupManager.checkGroupPinExist(gid, request.getConversationId().get());
      if(groupPin != null){
        return BaseResponse.ok(new CreateGroupAnnouncementResponse(groupPin.getId()));
      }
    }catch (Exception e){
      logger.error(e.getMessage());
      e.printStackTrace();
    }finally {
      locker.unlock();
    }

    try {
      groupPin=groupManager.addPinPermissionCheck(account,gid,request);
      groupPins=new ArrayList<GroupPin>();
      groupPins.add(groupPin);
      logger.info(account.getNumber() + " create groupPin: " + groupPin.getId());
    } catch (InvalidParameterException e) {
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, e.getMessage(),logger);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    } catch (GroupPinContentTooLongException e){
      BaseResponse.err(BaseResponse.STATUS.GROUP_PIN_CONTENT_TOO_LONG, e.getMessage(), logger);
    }
    group =getGroup(account, gid);
    groupManager.sendGroupNotify(GroupNotify.ChangeType.PIN.ordinal(),account,group,GroupNotify.ActionType.NONE.ordinal(), GroupNotify.GroupNotifyDetailedType.GROUP_ADD_PIN.ordinal(),null,-1,null, groupPins,GroupNotify.ActionType.ADD.ordinal(),GroupNotify.Display.YES.ordinal(),null);
    return BaseResponse.ok(new CreateGroupAnnouncementResponse(groupPin.getId()));
  }


  @Timed
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{gid}/pin")
  public BaseResponse delPin(@Auth Account account, @PathParam("gid") String gid, @Valid GroupPinListRequest pinListRequest) {
    if(pinListRequest==null||pinListRequest.getPins()==null||pinListRequest.getPins().size()==0){
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param pins ",logger);
    }
    List<GroupPin> groupPins=null;
    GroupPin groupPin = null;
    Group group=null;
    try {
      groupManager.delPinPermissionCheck(account,gid,pinListRequest);
      groupPins=new ArrayList<>();
      for(String gpid:pinListRequest.getPins()){
        groupPin=new GroupPin(gpid,gid,null,0,GroupPinTable.STATUS.DISMISSED.ordinal(),null, null);
        groupPins.add(groupPin);
      }
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    } catch (NoSuchGroupPinException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP_PIN, e.getMessage(),logger);
    }
    group =getGroup(account, gid);
    groupManager.sendGroupNotify(GroupNotify.ChangeType.PIN.ordinal(),account,group,GroupNotify.ActionType.NONE.ordinal(), GroupNotify.GroupNotifyDetailedType.GROUP_DEL_PIN.ordinal(),null,-1,null, groupPins,GroupNotify.ActionType.DELETE.ordinal(),GroupNotify.Display.YES.ordinal(),null);
    return BaseResponse.ok();
  }

  @Timed
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{gid}/pin")
  public BaseResponse getGroupPin(@Auth Account account, @PathParam("gid") String gid, @QueryParam("page") int page, @QueryParam("size")int size) {
    List<GetGroupPinResponse.GroupPin> groupPinList=new ArrayList<GetGroupPinResponse.GroupPin>();
    try {
      List<GroupPin> groupPins=groupManager.getGroupPinPermissionCheck(account,gid, page, size);
      if(groupPins!=null&&groupPins.size()>0){
        for(GroupPin groupPin:groupPins){
          groupPinList.add(new GetGroupPinResponse.GroupPin(groupPin.getId(), groupPin.getCreator(), groupPin.getCreateTime(), groupPin.getContent(), groupPin.getConversationId()));
        }
      }
    } catch (InvalidParameterException e) {
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, e.getMessage(),logger);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    } catch (NoSuchGroupAnnouncementException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP_ANNOUNCEMENT, e.getMessage(),logger);
    }
    return BaseResponse.ok(new GetGroupPinResponse(gid, groupPinList));
  }

  @Timed
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/invitation/{gid}/")
  public BaseResponse invitation(@Auth Account account, @PathParam("gid") String gid) {
    // 检查是否有权限
    Group group = getGroup(account, gid);
    if(group==null){
      BaseResponse.err(200, BaseResponse.STATUS.NO_SUCH_GROUP, "No such group: " + gid, logger, null);
      return null;
    }
    // 检查是否是启用了邀请码
    if(!group.linkInviteSwitchOn()){
        BaseResponse.err(200, BaseResponse.STATUS.GroupLinkInviteDisabled, "Failed to join the group, this group disabled invite link.", logger, null);
        return null;
    }

    // 检查是否是Only moderators can add members
    final GroupMember member = groupManager.getMember(gid, account.getNumber());
    if(member==null){
      BaseResponse.err(200, BaseResponse.STATUS.NO_PERMISSION, "No permission to get invitation code", logger, null);
      return null;
    }
    if (checkModeratorPermission(member, group)) return null;
    // 是否已经有长期有效的邀请码,没有就生成
    final String inviteCode = member.getInviteCode();
    if (inviteCode == null || inviteCode.isEmpty()) {
      final String newInviteCode = groupManager.generateInviteCode(gid, account.getNumber());
      if (newInviteCode == null) {
        BaseResponse.err(200, BaseResponse.STATUS.OTHER_ERROR, "Failed to generate invite code", logger, null);
        return null;
      }
      return BaseResponse.ok(new GetGroupInvitationCodeResponse(newInviteCode));
    }

    GetGroupInvitationCodeResponse groupInvitationCodeResponse=new GetGroupInvitationCodeResponse(inviteCode);
    return BaseResponse.ok(groupInvitationCodeResponse);
  }

  static class inviteCodeInfo{
    Group group;
    Account accountInviter;

    public inviteCodeInfo(Group group, Account accountInviter) {
      this.group = group;
      this.accountInviter = accountInviter;
    }
  }
  private inviteCodeInfo inviteJoinCheck(String inviteCode){
    final GroupMember memberByInviteCode = groupManager.getMemberByInviteCode(inviteCode);
    if(memberByInviteCode==null){
      logger.info("Invalid group invite link.: {}" , inviteCode);
      BaseResponse.err(200, BaseResponse.STATUS.GroupLinkInvalidInviteCode, "Invalid group invite link.", logger, null);
      return null;
    }
    String inviter = memberByInviteCode.getUid();
    Optional<Account> inviterAccount=accountsManager.get(inviter);
    if(!inviterAccount.isPresent()||inviterAccount.get().isinValid()){
      logger.info("Invalid group invite link.: {}, inviter:{}" , inviteCode, inviter);
      BaseResponse.err(200, BaseResponse.STATUS.GroupLinkInvalidInviteCode, "Invalid group invite link.", logger, null);
      return null;
    }
    String gid = memberByInviteCode.getGid();
    // 获取群，但不检查权限：查看是否存在、是否解散
    Group noCheckGroup = groupManager.getWithoutCheck(gid);
    if(noCheckGroup==null){
      BaseResponse.err(200, BaseResponse.STATUS.GroupLinkInviteInvalid, "Failed to join the group. This group is invalid.", logger, null);
      return null;
    }
    if (noCheckGroup.getStatus() != GroupsTable.STATUS.ACTIVE.ordinal()){
        BaseResponse.err(200, BaseResponse.STATUS.GroupDisbanded, "Failed to join the group. This group has already been disbanded.", logger, null);
        return null;
    }
    // 检查权限方式获取群
    Group group = getGroup(inviterAccount.get(), gid);
    if(group==null){
      BaseResponse.err(200, BaseResponse.STATUS.GroupLinkInvalidInviteCode, "Invalid group invite link.", logger, null);
      return null;
    }
    // 检查是否是启用了邀请码
    if(!group.linkInviteSwitchOn()){
      BaseResponse.err(200, BaseResponse.STATUS.GroupLinkInviteDisabled, "Failed to join the group, this group disabled invite link.", logger, null);
      return null;
    }
    // 检查是否是Only moderators can add members
    if (checkModeratorPermission(memberByInviteCode, group)) return null;
    return new inviteCodeInfo(group, inviterAccount.get());
  }

  private boolean checkModeratorPermission(GroupMember memberByInviteCode, Group group) {
    final GroupMembersTable.ROLE roleInvite = GroupMembersTable.ROLE.fromOrdinal(group.getInvitationRule());
    assert roleInvite != null;
    if (roleInvite.compareTo(GroupMembersTable.ROLE.MEMBER) != 0 ) {
      final GroupMembersTable.ROLE role = GroupMembersTable.ROLE.fromOrdinal(memberByInviteCode.getRole());
      assert role != null;
      if (role.compareTo(GroupMembersTable.ROLE.MEMBER) == 0){
        BaseResponse.err(200, BaseResponse.STATUS.GroupOnlyAdminAddMem, "Failed to join the group, this group only allows moderators to add members to this group.", logger, null);
        return true;
      }
    }
    return false;
  }

  @Timed
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/invitation/groupInfo/{inviteCode}/")
  public BaseResponse groupInfoForInvitation(@Auth Account account, @PathParam("inviteCode") String inviteCode) {
    if(StringUtil.isEmpty(inviteCode)){
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "inviteCode is null! ",logger);
    }
    if (inviteCode.length() > 32){
      return groupInfoForInvitationByToken(account, inviteCode);
    }
    inviteCodeInfo inviteCodeInfo = inviteJoinCheck(inviteCode);
    List<GroupMember> groupMembers=null;
    try {
      assert inviteCodeInfo != null;
      groupMembers =groupManager.getGroupMembersWithPermissionCheck( inviteCodeInfo.accountInviter, inviteCodeInfo.group);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    }
    Group group = inviteCodeInfo.group;
    return BaseResponse.ok(new GetGroupResponse(group.getName(), group.getMessageExpiry(), group.getAvatar(),group.getInvitationRule(),group.getVersion(), groupMembers.size()));

  }
  public BaseResponse groupInfoForInvitationByToken( Account account,  String token) {
    Map<String, Claim> claimMap = tokenUtil.verifyToken(token, groupManager.getGroupConfiguration().getEffectiveDuration());
    if(claimMap==null){
      BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "token is invalid! ",logger);
    }
    String gid=claimMap.get("gid").asString();
    String inviter=claimMap.get("inviter").asString();
    if(StringUtil.isEmpty(gid)||StringUtil.isEmpty(inviter)){
      BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "token is invalid! ",logger);
    }

    Optional<Account> inviterAccount=accountsManager.get(inviter);
    if(!inviterAccount.isPresent()||inviterAccount.get().isinValid()){
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_USER, "no such inviter! ",logger);
    }
    Group group = getGroup(inviterAccount.get(), gid);
    if(group==null){
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, "No such group: " + gid,logger);
    }

    List<GroupMember> groupMembers=null;
    try {
      groupMembers =groupManager.getGroupMembersWithPermissionCheck(inviterAccount.get(), group);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    }
    return BaseResponse.ok(new GetGroupResponse(group.getName(), group.getMessageExpiry(), group.getAvatar(),group.getInvitationRule(),group.getVersion(), groupMembers.size()));
  }

  @Timed
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/invitation/join/{inviteCode}/")
  public BaseResponse joinForInvitation(@Auth Account account, @PathParam("inviteCode") String inviteCode) {
    if (inviteCode.length() >32)
      return joinForInvitationByToken(account, inviteCode);
    // 查短邀请码
    inviteCodeInfo inviteCodeInfo = inviteJoinCheck(inviteCode);
    assert inviteCodeInfo != null;
    Group group = inviteCodeInfo.group;
    Account inviterAccount = inviteCodeInfo.accountInviter;
    List<Account> operatees=new ArrayList<Account>();
    operatees.add(account);
    List<GroupMember> groupMembers=new ArrayList<GroupMember>();
    // join members
    try {
        group = groupManager.addMemberWithPermissionCheck(inviterAccount, group, operatees, groupMembers);
        groupManager.clearGroupMembersCache(group.getId());
        for (Account account1 : operatees) {
          groupManager.clearMemberGroupCache(account1.getNumber());
        }
    } catch (InvalidParameterException e) {
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, e.getMessage(),logger);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    }catch (ExceedingGroupMemberSizeException e) {
      BaseResponse.err(BaseResponse.STATUS.GROUP_IS_FULL_OR_EXCEEDS, e.getMessage(),logger);
    }
    if(groupMembers.size()>0) {
      groupManager.sendGroupNotify(GroupNotify.ChangeType.MEMBER.ordinal(), account, group,
              GroupNotify.ActionType.NONE.ordinal(),
              GroupNotify.GroupNotifyDetailedType.WEBLINK_INVITE_JOIN.getCode(),
              groupMembers, GroupNotify.ActionType.ADD.ordinal(), null, null,-1, GroupNotify.Display.YES.ordinal(), inviterAccount);
    }
    // Members
    List<GetGroupResponse.GroupMember> members = new LinkedList<>();
    try {
      for (GroupMember member : groupManager.getGroupMembersWithPermissionCheck(account, group)) {
        if(account.getNumber().equals(member.getUid())){
          members.add(new GetGroupResponse.GroupMemberWithSelf(member.getUid(), member.getRole(), groupManager.getDisplayName(account,member),member.getNotification(),member.getRemark(),member.isUseGlobal(),member.getRapidRole(),account.getExtId()));
        }else {
          Optional<Account> accountOptional=accountsManager.get(member.getUid());
          if(!accountOptional.isPresent()||accountOptional.get().isinValid()) continue;
          members.add(new GetGroupResponse.GroupMember(member.getUid(), member.getRole(), groupManager.getDisplayName(accountOptional.get(),member),member.getRapidRole(),accountOptional.get().getExtId()));
        }
      }
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    }

    return BaseResponse.ok(new weblinkJoinResponse(group.getId(),group.getName(), group.getMessageExpiry(), group.getAvatar(),group.getInvitationRule(),group.getVersion(), members,group.getRemindCycle(),group.isAnyoneRemove(),group.isRejoin(),group.isExt(),group.getPublishRule(),
            inviterAccount.getNumber()));
  }

  private static class weblinkJoinResponse extends GetGroupResponse {
    public String getInviter() {
      return inviter;
    }

    public void setInviter(String inviter) {
      this.inviter = inviter;
    }

    @JsonProperty
    private String inviter;

    public weblinkJoinResponse(String gid, String name, long messageExpiry, String avatar, int invitationRule,
                               int version, List<GroupMember> members, String remindCycle, boolean anyoneRemove,
                               boolean rejoin, boolean ext, int publishRule, String inviter) {
      super(gid, name, messageExpiry, avatar, invitationRule, version, members, remindCycle, anyoneRemove, rejoin, ext, publishRule);
      this.inviter = inviter;
    }
  }
    public BaseResponse joinForInvitationByToken( Account account, String token) {
    // 再兼容老token
    if(StringUtil.isEmpty(token)){
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "token is null! ",logger);
    }
    Map<String, Claim> claimMap =tokenUtil.verifyToken(token, groupManager.getGroupConfiguration().getEffectiveDuration());
    if(claimMap==null){
      BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "token is invalid! ",logger);
    }
    String gid=claimMap.get("gid").asString();
    String inviter=claimMap.get("inviter").asString();
    if(StringUtil.isEmpty(gid)||StringUtil.isEmpty(inviter)){
      BaseResponse.err(BaseResponse.STATUS.INVALID_TOKEN, "token is invalid! ",logger);
    }

    Optional<Account> inviterAccount=accountsManager.get(inviter);
    if(!inviterAccount.isPresent()||inviterAccount.get().isinValid()){
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_USER, "no such inviter! ",logger);
    }
    Group group = getGroup(inviterAccount.get(), gid);
    if(group==null){
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, "No such group: " + gid,logger);
    }
    List<Account> operatees=new ArrayList<Account>();
    operatees.add(account);
    boolean extChange=false;
    List<GroupMember> groupMembers=new ArrayList<GroupMember>();
    // join members
    try {
      GroupMember groupMember=groupManager.getMember(gid, account.getNumber());
      if(groupMember==null) {
        boolean oldExt=group.isExt();
        group = groupManager.addMemberWithPermissionCheck(inviterAccount.get(), group, operatees, groupMembers);
        if(group.isExt()!=oldExt){
          extChange=true;
        }
        groupManager.clearGroupMembersCache(gid);
        for (Account account1 : operatees) {
          groupManager.clearMemberGroupCache(account1.getNumber());
        }
      }
    } catch (InvalidParameterException e) {
      BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, e.getMessage(),logger);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    }catch (ExceedingGroupMemberSizeException e) {
      BaseResponse.err(BaseResponse.STATUS.GROUP_IS_FULL_OR_EXCEEDS, e.getMessage(),logger);
    }
    if(groupMembers.size()>0) {
      groupManager.sendGroupNotify(GroupNotify.ChangeType.MEMBER.ordinal(), account, group, extChange?GroupNotify.ActionType.UPDATE.ordinal():GroupNotify.ActionType.NONE.ordinal(), GroupNotify.GroupNotifyDetailedType.INVITE_JOIN_GROUP.ordinal(), groupMembers, GroupNotify.ActionType.ADD.ordinal(), null, null,-1, GroupNotify.Display.YES.ordinal(), groupMembers);
    }
    // Members
    List<GetGroupResponse.GroupMember> members = new LinkedList<>();
    try {
      for (GroupMember member : groupManager.getGroupMembersWithPermissionCheck(account, group)) {
        if(account.getNumber().equals(member.getUid())){
          members.add(new GetGroupResponse.GroupMemberWithSelf(member.getUid(), member.getRole(), groupManager.getDisplayName(account,member),member.getNotification(),member.getRemark(),member.isUseGlobal(),member.getRapidRole(),account.getExtId()));
        }else {
          Optional<Account> accountOptional=accountsManager.get(member.getUid());
          if(!accountOptional.isPresent()||accountOptional.get().isinValid()) continue;
          members.add(new GetGroupResponse.GroupMember(member.getUid(), member.getRole(), groupManager.getDisplayName(accountOptional.get(),member),member.getRapidRole(),accountOptional.get().getExtId()));
        }
      }
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    }

    return BaseResponse.ok(new weblinkJoinResponse(group.getId(),group.getName(), group.getMessageExpiry(), group.getAvatar(),group.getInvitationRule(),group.getVersion(), members,group.getRemindCycle(),group.isAnyoneRemove(),group.isRejoin(),group.isExt(),group.getPublishRule(),
            inviterAccount.get().getNumber()));
  }

  
  private Group getGroup(Account operator, String gid) {
    try {
      return groupManager.getGroupWithPermissionCheck(operator, gid);
    } catch (NoPermissionException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, e.getMessage(),logger);
    } catch (NoSuchGroupException e) {
      BaseResponse.err(BaseResponse.STATUS.NO_SUCH_GROUP, e.getMessage(),logger);
    }
    return null;
  }

  private Account getAccount(String uid) {
    Optional<Account> account = accountsManager.get(uid);
    if (!account.isPresent() || account.get().isinValid()) {
      return null;
    }
    return account.get();
  }

  private boolean validGroupExpireTime(long expireTime) {
    for (Long opt : WhisperServerConfigurationApollo.getGroupMessageExpiry()) {
      if (opt == expireTime) {
        return true;
      }
    }
    return false;
  }
  private boolean groupInfoIsChange(Group group,SetGroupRequest request){
    if(request.getName().isPresent()) {
      if (!request.getName().get().equals(group.getName())){
        return true;
      }
    }
    if(request.getInvitationRule().isPresent()) {
      if (group.getInvitationRule()!=request.getInvitationRule().get()){
        return true;
      }
    }
    if(request.getMessageExpiry().isPresent()) {
      if (group.getMessageExpiry()!=request.getMessageExpiry().get()){
        return true;
      }
    }
    if(request.getAvatar().isPresent()) {
      if (!request.getAvatar().get().equals(group.getAvatar())){
        return true;
      }
    }
    if(request.getRemindCycle().isPresent()) {
      if (!request.getRemindCycle().get().equals(group.getRemindCycle())){
        return true;
      }
    }
    if(request.getAnyoneRemove().isPresent()) {
      if (!request.getAnyoneRemove().get().equals(group.isAnyoneRemove())){
        return true;
      }
    }
    if(request.getLinkInviteSwitch().isPresent()) {
      if (!request.getLinkInviteSwitch().get().equals(group.linkInviteSwitchOn())){
        return true;
      }
    }

    if(request.getRejoin().isPresent()) {
      if (!request.getRejoin().get().equals(group.isRejoin())){
        return true;
      }
    }
    if(request.getPublishRule().isPresent()) {
      if (!request.getPublishRule().get().equals(group.getPublishRule())){
        return true;
      }
    }
    return false;
  }

}
