package org.whispersystems.textsecuregcm.storage;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.SharedMetricRegistries;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.lookup.StringLookup;
import org.skife.jdbi.v2.TransactionIsolationLevel;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.configuration.GroupConfiguration;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.exceptions.*;
import org.whispersystems.textsecuregcm.util.*;
import redis.clients.jedis.JedisPubSub;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.codahale.metrics.MetricRegistry.name;

public abstract class GroupManagerWithTransaction {

  private final Logger logger = LoggerFactory.getLogger(GroupManagerWithTransaction.class);
  @CreateSqlObject
  abstract GroupsTable groupsTable();
  @CreateSqlObject
  abstract GroupMembersTable groupMembersTable();
  @CreateSqlObject
  abstract GroupAnnouncementTable groupAnnouncementTable();
  @CreateSqlObject
  abstract GroupPinTable groupPinTable();

  private MemCache memCache;

  private final ObjectMapper mapper=SystemMapper.getMapper();

  private GroupConfiguration groupConfiguration;

  private NotifyManager notifyManager;

  private AccountsManager accountsManager;

  private TokenUtil tokenUtil;

  public int getGroupMaxMeetingVersion() {
    return groupMaxMeetingVersion;
  }

  public void setGroupMaxMeetingVersion(int groupMaxMeetingVersion) {
    this.groupMaxMeetingVersion = groupMaxMeetingVersion;
  }

  private int groupMaxMeetingVersion = 1;

  Cache<String, Object> caffeine= Caffeine.newBuilder().build();

  ThreadPoolExecutor executor=new ThreadPoolExecutor(20,20,0, TimeUnit.SECONDS,new LinkedBlockingDeque<>(), new ThreadPoolExecutor.CallerRunsPolicy());
  public void registerMetrics(){
    SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME)
            .register(name(GroupManagerWithTransaction.class, "GroupManagerWithTransaction_executor_depth"),
                    (Gauge<Long>) ((ThreadPoolExecutor)executor)::getTaskCount);
  }


  public enum MemberChangeType {
    BASIC,
    SETADMIN,
    DELADMIN,
    RAPIDROLE
  }

  private Group create(Group group)
      throws InvalidParameterException {
    validateGroupParameters(group);
    group = groupsTable().create(group);
    memCache.set(getGroupKey(group.getId()), group);
    return group;
  }

  private Group upgradeCreate(Group group)
          throws InvalidParameterException {
    if (StringUtil.isEmpty(group.getId())||group.getId().length()> 64) {
      throw new InvalidParameterException("Invalid group id: " + group.getId());
    }
    validateGroupParameters(group);
    group = groupsTable().upgradeCreate(group);
    memCache.set(getGroupKey(group.getId()), group);
    return group;
  }

  private Group set(Group group)
      throws InvalidParameterException {

    validateGroupParameters(group);

    group=groupsTable().updateForVersion(group);

//    // Reload, some columns won't change
//    group = groupsTable.get(group.getId());;

    memCache.set(getGroupKey(group.getId()), group);

    // Invalidate related caches
    List<GroupMember> groupMembers=getGroupMembers(group.getId());
    if(groupMembers!=null&&groupMembers.size()>0) {
      for(GroupMember groupMember:groupMembers) {
        memCache.remove(getMemberGroupsKey(groupMember.getUid()));
      }
    }
    return group;
  }

  private void destroyGroup(String gid) {
    groupsTable().deleteGroup(gid);
    groupMembersTable().deleteByGroup(gid);
    groupAnnouncementTable().deleteByGroup(gid);
    groupPinTable().deleteGroup(gid);
    memCache.remove(getGroupKey(gid));
    List<GroupMember> groupMembers=getGroupMembers(gid);
    if(groupMembers!=null&&groupMembers.size()>0) {
      for(GroupMember groupMember:groupMembers) {
        memCache.remove(getMemberGroupsKey(groupMember.getUid()));
      }
    }
  }

  private Group setGroupWithoutLastActiveTime(Group group)
          throws InvalidParameterException {

    validateGroupParameters(group);

    group=groupsTable().updateForVersionWithoutLastActiveTime(group);

//    // Reload, some columns won't change
//    group = groupsTable.get(group.getId());;

    memCache.set(getGroupKey(group.getId()), group);

    // Invalidate related caches
    List<GroupMember> groupMembers=getGroupMembers(group.getId());
    if(groupMembers!=null&&groupMembers.size()>0) {
      for(GroupMember groupMember:groupMembers) {
        memCache.remove(getMemberGroupsKey(groupMember.getUid()));
      }
    }
    return group;
  }

  public void updateGroupActiveTime(String gid) {
    executor.submit(new Runnable() {
      @Override
      public void run() {
        Group group = getWithoutCheck(gid);
        if (group != null && TimeUnit.MILLISECONDS.toDays(group.getLastActiveTime()) < TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())) {
          group = groupsTable().updateLastActiveTime(gid);
          if (null != group) {
            memCache.set(getGroupKey(group.getId()), group);
          }
        }
      }
    });
  }
    public void updateGroupActiveTime(final Group group) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          if(group!=null&&TimeUnit.MILLISECONDS.toDays(group.getLastActiveTime()) < TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())) {
            Group groupTemp = groupsTable().updateLastActiveTime(group.getId());
            if (null != groupTemp) {
              memCache.set(getGroupKey(group.getId()), groupTemp);
            }
          }
        }
      });
  }

  public Group addGroupVersion(Group group){
    group=groupsTable().updateForVersionWithLastActiveTime(group.getId(),group.isExt());
    if (null != group) {
      memCache.set(getGroupKey(group.getId()), group);
    }
    reloadMemberGroups(group.getId());
    return group;
  }
  public Group addGroupVersion(String gid){
    Group group=groupsTable().updateForVersionWithLastActiveTime(gid,null);
    if (null != group) {
      memCache.set(getGroupKey(gid), group);
    }
    reloadMemberGroups(gid);
    return group;
  }
  public Group addGroupVersionOnlyVersion(String gid){
    Group group=groupsTable().updateOnlyVersion(gid);
    if (null != group) {
      memCache.set(getGroupKey(gid), group);
    }
    reloadMemberGroups(gid);
    return group;
  }

  private void reloadMemberGroups(String gid){
    // Invalidate related caches
    List<GroupMember> groupMembers=getGroupMembers(gid);
      if(groupMembers!=null&&groupMembers.size()>0) {
        for(GroupMember groupMember:groupMembers) {
          memCache.remove(getMemberGroupsKey(groupMember.getUid()));
        }
    }
  }
  // 使用时注意⚠️，该接口不检查权限；一般情况是使用 getGroupWithPermissionCheck
  public Group getWithoutCheck(String gid) {
    Group group = (Group)memCache.get(getGroupKey(gid), Group.class);
    if (null == group) {
      group = groupsTable().get(gid);
      if (null != group) {
        group.setExt(false);
        memCache.set(getGroupKey(gid), group);
      }
    }else {
      group.setExt(false);
    }
    return group;
  }

  private List<Group> getAll() {
    return groupsTable().getAll();
  }

  private boolean addMember(GroupMember member)
      throws InvalidParameterException {

    validateMemberParameters(member);
    GroupMember groupMember=getMember(member.getGid(), member.getUid());
    // Skip already exists ， update will reset his related settings
    if (null != groupMember) {
//      setMember(member.getGid(), member);
//      memCache.set(getMemberKey(member.getGid(), member.getUid()), member);
      // Invalidate related caches
//      memCache.remove(getGroupMembersKey(member.getGid()));
      return false;
    }
    // Add
    groupMembersTable().insert(member.getGid(), member.getUid(), member.getRole(), member.getInviter(), member.getDisplayName(), member.getRemark(), member.getNotification(),member.isUseGlobal(),member.getRapidRole());
    memCache.set(getMemberKey(member.getGid(), member.getUid()), member);
    // Invalidate related caches
    memCache.remove(getGroupMembersKey(member.getGid()));
    memCache.remove(getMemberGroupsKey(member.getUid()));
    return true;
  }

  private void removeMember(String gid, String uid) {
    groupMembersTable().delete(gid, uid);
    memCache.remove(getMemberKey(gid, uid));

    // Invalidate related caches
    memCache.remove(getGroupMembersKey(gid));
    memCache.remove(getMemberGroupsKey(uid));
  }

  public void clearGroupMembersCache(String gid){
    memCache.remove(getGroupMembersKey(gid));
    reloadGroupMembers(gid);
  }

  public void clearMemberGroupCache(String uid){
      memCache.remove(getMemberGroupsKey(uid));
  }
  public void clearMemberGroupCacheForGroup(String gid){
    List<GroupMember> groupMembers=getGroupMembers(gid);
    if(groupMembers!=null&&groupMembers.size()>0) {
      for(GroupMember groupMember:groupMembers) {
        memCache.remove(getMemberGroupsKey(groupMember.getUid()));
      }
    }
  }



  private void setMember(String gid, GroupMember member)
      throws InvalidParameterException {

    validateMemberParameters(member);
    GroupMember groupMember=groupMembersTable().getGroupMember(gid,member.getUid());
    if(groupMember!=null) {
      groupMembersTable().update(member.getRole(), member.getDisplayName(), member.getRemark(), member.getNotification(), member.isUseGlobal(),gid, member.getUid(),member.getRapidRole());
    }else{
      groupMembersTable().insert(member.getGid(), member.getUid(), member.getRole(), member.getInviter(), member.getDisplayName(), member.getRemark(), member.getNotification(), member.isUseGlobal(),member.getRapidRole());
    }
    memCache.set(getMemberKey(gid, member.getUid()), member);

    // Invalidate related caches
    memCache.remove(getGroupMembersKey(gid));
    memCache.remove(getMemberGroupsKey(member.getUid()));
  }

  public GroupMember getMember(String gid, String uid) {
    GroupMember member = (GroupMember)memCache.get(getMemberKey(gid, uid), GroupMember.class);
    if (null == member) {
      member = groupMembersTable().getGroupMember(gid, uid);
      if (null != member) {
        memCache.set(getMemberKey(gid, uid), member);
      }
    }
    return member;
  }

  public GroupMember getMemberByInviteCode(String inviteCode) {
    return groupMembersTable().getGroupMemberByInviteCode(inviteCode);
  }

  private List<Group> getMemberGroups(String uid) {
    JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, Group.class);
    List<Group> groups = (List<Group>)memCache.get(getMemberGroupsKey(uid), javaType);
    if (null == groups) {
      groups = new LinkedList<>();

      for (GroupMember groupMember : groupMembersTable().getMemberGroups(uid)) {
        Group group = getWithoutCheck(groupMember.getGid());
        groups.add(group);
      }

      memCache.set(getMemberGroupsKey(uid), groups);
    }
    return groups;
  }
  private void reloadGroupMembers(String gid){
    List<GroupMember> members = groupMembersTable().getGroupMembers(gid);
    String groupMembersKey=getGroupMembersKey(gid);
    memCache.set(groupMembersKey, members);
    if((members!=null&&members.size()>=100)||caffeine.getIfPresent(groupMembersKey)!=null) {
      caffeinePutGroupMembers(gid, members,true);
    }
  }
  // FIXME: May leave redundant data if the gid doesn't exists
  public List<GroupMember> getGroupMembers(String gid) {
    String groupMembersKey=getGroupMembersKey(gid);
    List<GroupMember> members = (List<GroupMember>)caffeine.getIfPresent(groupMembersKey);
    if(members==null||members.size()==0) {
      JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, GroupMember.class);
      members = (List<GroupMember>) memCache.get(groupMembersKey, javaType);
      if (null == members) {
        members = groupMembersTable().getGroupMembers(gid);
        memCache.set(groupMembersKey, members);
      }
      for (GroupMember member : members) {
        accountsManager.get(member.getUid()).ifPresent(account ->
        {
          member.setMeetingVersion(account.getRealMeetingVersion());
          member.setMsgEncVersion(account.getRealMsgEncVersion());
          member.setRegistrationId(account.getMainDeviceRegistrationId());
          member.setIdentityKey(account.getIdentityKey());
        });
      }
      if((members!=null&&members.size()>=100)||caffeine.getIfPresent(groupMembersKey)!=null) {
        caffeinePutGroupMembers(gid, members,true);
      }
    }else{
      logger.info("GroupManagerWithTransaction getGroupMembersFromCaffeine gid:{} ",gid);
    }
    return members;
  }

  private void caffeinePutGroupMembers(String gid,Object object, boolean isPublish){
    String groupMembersKey=getGroupMembersKey(gid);
    caffeine.put(groupMembersKey, object);
    if(isPublish) {
      memCache.getJedisCluster().publish("caffeinePutGroupMembers", gid);
    }
    logger.info("GroupManagerWithTransaction caffeinePutGroupMembers gid:{} ",gid);
  }

  public void caffeineSub(){
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
              memCache.getJedisCluster().subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                  if(channel.equals("caffeinePutGroupMembers")){
                    caffeinePutForSub(message);
                  }
                }
              },"caffeinePutGroupMembers");
            }
          },0L,1000L, TimeUnit.MILLISECONDS).get();
        } catch (Exception e) {
          logger.error("GroupManagerWithTransaction.caffeineSub error!",e);
        }
      }
    }).start();
  }

  public void caffeinePutForSub(String gid) {
    String groupMembersKey=getGroupMembersKey(gid);
    JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, GroupMember.class);
    List<GroupMember> members = (List<GroupMember>) memCache.get(groupMembersKey, javaType);
    if (null == members) {
      members = groupMembersTable().getGroupMembers(gid);
      memCache.set(groupMembersKey, members);
    }
    if((members!=null&&members.size()>=100)||caffeine.getIfPresent(groupMembersKey)!=null) {
      caffeinePutGroupMembers(gid, members,false);
    }

  }

  private Group checkGroupStatus(String gid, GroupsTable.STATUS expectedMostStatus)
      throws NoPermissionException, NoSuchGroupException {
    Group group = getWithoutCheck(gid);
    if (null == group) {
      throw new NoSuchGroupException("No such group: " + gid);
    }

    if (group.getStatus() > expectedMostStatus.ordinal()) {
      throw new NoSuchGroupException("No such group: " + gid);
//      throw new NoPermissionException("You don't have permission. The group status is: " + group.getStatus());
    }

    return group;
  }

  private GroupMembersTable.ROLE checkRole(Group group, String uid, GroupMembersTable.ROLE expectedLeastRole)
      throws NoPermissionException {
    List<GroupMember> members = getGroupMembers(group.getId());
    for (GroupMember member : members) {
      if (member.getUid().equals(uid)) {
        if (member.getRole() <= expectedLeastRole.ordinal()) {
          return GroupMembersTable.ROLE.fromOrdinal(member.getRole());
        }
        throw new NoPermissionException("You don't have permission. You role is: " + member.getRole());
      }
    }

    throw new NoPermissionException("You don't have permission.");
  }

  private GroupMembersTable.ROLE checkPublishRole(Group group, String uid)
          throws NoPermissionException {
    GroupMember groupMember=getMember(group.getId(),uid);
    if(groupMember==null){
      throw new NoPermissionException("You don't have permission.");
    }
    if (groupMember.getRole() <= group.getPublishRule()) {
      return GroupMembersTable.ROLE.fromOrdinal(groupMember.getRole());
    }else {
      throw new NoPermissionException("You don't have permission. You role is: " + groupMember.getRole());
    }
  }

  @Transaction(TransactionIsolationLevel.READ_COMMITTED)
  public Group createGroupWithPermissionCheck(Account operator, Group group,List<Account> accounts,List<GroupMember> groupMembers,Integer notification)
      throws NoPermissionException, InvalidParameterException {
    try {
      int isExistsOperator=1;
      for (Account operatee : accounts) {
        if(operatee.getNumber().equals(operator.getNumber())){
          isExistsOperator=0;
          break;
        }
      }
      validateGroupMemberSize(group.getId(),accounts.size()+isExistsOperator);
      group = create(group);
      boolean useGlobal=true;
      int notificationTemp=accountsManager.getGlobalNotification(operator);
      if(notification!=null){
        useGlobal=false;
        notificationTemp=notification;
      }
      GroupMember groupOwner=new GroupMember(group.getId(), operator.getNumber(), GroupMembersTable.ROLE.OWNER.ordinal(), System.currentTimeMillis(), operator.getNumber(), "", "",notificationTemp,useGlobal,GroupMembersTable.RAPID_ROLE.NONE.ordinal());
      addMember(groupOwner);
      groupMembers.add(groupOwner);
      for (Account operatee : accounts) {
        if(operatee.getNumber().equals(operator.getNumber())){
          continue;
        }
        notificationTemp=accountsManager.getGlobalNotification(operatee);
        if(notification!=null){
          useGlobal=false;
          notificationTemp=notification;
        }
        GroupMember groupMember = new GroupMember(group.getId(), operatee.getNumber(), GroupMembersTable.ROLE.MEMBER.ordinal(), System.currentTimeMillis(), operator.getNumber(), "", "", notificationTemp,useGlobal,GroupMembersTable.RAPID_ROLE.NONE.ordinal());
        addMember(groupMember);
        groupMembers.add(groupMember);
      }
    }catch (Exception e){
      //回滚清除memcache
      for (Account operatee : accounts) {
        memCache.remove(getMemberKey(group.getId(), operatee.getNumber()));
      }
      memCache.remove(getMemberKey(group.getId(), operator.getNumber()));
      memCache.remove(getGroupKey(group.getId()));
      throw e;
    }
    return group;
  }

  @Transaction(TransactionIsolationLevel.READ_COMMITTED)
  public Group upgradeCreateGroupWithPermissionCheck(Account operator, Group group,List<Account> accounts,List<GroupMember> groupMembers)
          throws NoPermissionException, InvalidParameterException {
     try {
       group = upgradeCreate(group);
       GroupMember groupOwner=new GroupMember(group.getId(), operator.getNumber(), GroupMembersTable.ROLE.OWNER.ordinal(), System.currentTimeMillis(), operator.getNumber(), "", "", accountsManager.getGlobalNotification(operator),true,GroupMembersTable.RAPID_ROLE.NONE.ordinal());
       addMember(groupOwner);
       groupMembers.add(groupOwner);
       for (Account operatee : accounts) {
        if(operatee.getNumber().equals(operator.getNumber())){
          continue;
        }
        GroupMember groupMember = new GroupMember(group.getId(), operatee.getNumber(), GroupMembersTable.ROLE.MEMBER.ordinal(), System.currentTimeMillis(), operator.getNumber(), "", "", accountsManager.getGlobalNotification(operatee),true,GroupMembersTable.RAPID_ROLE.NONE.ordinal());
        addMember(groupMember);
        groupMembers.add(groupMember);
      }
    }catch (Exception e){
      //回滚清除memcache
      for (Account operatee : accounts) {
        memCache.remove(getMemberKey(group.getId(), operatee.getNumber()));
      }
      memCache.remove(getMemberKey(group.getId(), operator.getNumber()));
      memCache.remove(getGroupKey(group.getId()));
      throw e;
    }
    return group;
  }

  @Transaction(TransactionIsolationLevel.READ_COMMITTED)
  public Group setGroupWithPermissionCheck(Account operator, Group group,int changeType)
      throws NoPermissionException, NoSuchGroupException, InvalidParameterException {

    Group currentGroup = checkGroupStatus(group.getId(), GroupsTable.STATUS.ACTIVE);

    if (group.getStatus() == GroupsTable.STATUS.DISMISSED.ordinal()) {
      // Check if the operator is the owner of the group
      checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.OWNER);
      // Only status
      group = currentGroup;
      group.setStatus(GroupsTable.STATUS.DISMISSED.ordinal());
    } else if (group.getStatus() == GroupsTable.STATUS.ACTIVE.ordinal()) {
      // Change settings
      // Check if the operator is the owner of the group
      if(changeType== GroupNotify.GroupNotifyDetailedType.GROUP_INVITATION_RULE_CHANGE.ordinal()||
              changeType== GroupNotify.GroupNotifyDetailedType.GROUP_ANYONE_REMOVE_CHANGE.ordinal()||
              changeType== GroupNotify.GroupNotifyDetailedType.GROUP_REJOIN_CHANGE.ordinal()||
              changeType== GroupNotify.GroupNotifyDetailedType.WEBLINK_INVITE_SWITCH_CHANGE.getCode() ||
              changeType== GroupNotify.GroupNotifyDetailedType.GROUP_PUBLISH_RULE_CHANGE.ordinal()) {
        checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.OWNER);
      }else if(changeType== GroupNotify.GroupNotifyDetailedType.GROUP_REMIND_CHANGE.ordinal()) {
        checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);
      }else{
        checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.ADMIN);
      }
    } else {
      throw new NoPermissionException("You don't have permission to change status of the group: " + group.getId());
    }

    return set(group);
  }

  public Group getGroupWithPermissionCheck(Account operator, String gid)
      throws NoPermissionException, NoSuchGroupException {

    Group group = checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);

    // Check if the operator is in the group
    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);

    return group;
  }

  public Group getGroupWithPermissionCheckForSendMsg(Account operator, String gid,int detailMessageType)
          throws NoPermissionException, NoSuchGroupException {

    Group group = checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);

    // Check if the operator is in the group
    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);

    if (detailMessageType != IncomingMessage.DetailMessageType.REACTION.getCode()
            && detailMessageType != IncomingMessage.DetailMessageType.TOOK_SCREENSHOT.getCode()) {
      // Check if the operator can publish
      checkPublishRole(group, operator.getNumber());
    }
    return group;
  }

  public Group getGroup(String gid) {
    try {
      return checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);
    } catch (NoPermissionException e) {
      e.printStackTrace();
    } catch (NoSuchGroupException e) {
      e.printStackTrace();
    }
    return null;
  }
  private RandomString mRandomString = new RandomString(8);

  public String generateInviteCode(String gid, String uid) {
    for (int i = 0; i < 3; i++) {
      // 生成邀请码
      final String s = mRandomString.nextString();
      try {
        groupMembersTable().saveInviteCode(gid, uid, s);
      } catch (Exception e) {
        logger.warn("generateInviteCode error", e);
        continue;
      }
      // 删除缓存
      memCache.remove(getMemberKey(gid, uid));
      return s;
    }
    return null;
  }

  @Transaction(TransactionIsolationLevel.READ_COMMITTED)
  public Group addMemberWithPermissionCheck(Account operator, Group group, List<Account> operatees,List<GroupMember> groupMembers)
      throws NoPermissionException, NoSuchGroupException, InvalidParameterException {
//    List<Account> noPermissionAccounts=checkTeam(operator,operatees);
//    if(noPermissionAccounts!=null&&noPermissionAccounts.size()>0){
//      StringBuffer sb=new StringBuffer();
//      sb.append("You don't have permission. ");
//      for(Account account:noPermissionAccounts){
//        sb.append(account.getNumber())
//                .append("(")
//                .append(account.getPublicConfig(AccountExtend.FieldName.PUBLIC_NAME))
//                .append("),");
//      }
//      sb=sb.delete(sb.length()-1,sb.length());
//      sb.append(" not in your team!");
//      throw new NoPermissionException(sb.toString());
//    }
    group = checkGroupStatus(group.getId(), GroupsTable.STATUS.ACTIVE);

    // Check the operator has permission
    switch(GroupMembersTable.ROLE.fromOrdinal(group.getInvitationRule())) {
      case OWNER:
        checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.OWNER);
        break;
      case ADMIN:
        checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.ADMIN);
        break;
      case MEMBER:
        checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);
        break;
      default:
        throw new NoPermissionException("You don't have permission. ");
    }
    validateGroupMemberSize(group.getId(),operatees.size());
    logger.info("validateGroupMemberSize . over, addSize:{},getMembersMaxSize:{}",
            operatees.size(),groupConfiguration.getMembersMaxSize());
    try {
      for (Account operatee : operatees) {
        GroupMember groupMember = new GroupMember(group.getId(), operatee.getNumber(), GroupMembersTable.ROLE.MEMBER.ordinal(), System.currentTimeMillis(), operator.getNumber(), "", "", accountsManager.getGlobalNotification(operatee),true,GroupMembersTable.RAPID_ROLE.NONE.ordinal());
        if(addMember(groupMember)) {
          groupMembers.add(groupMember);
        }
      }
      if(groupMembers.size()>0) {
        if(!group.isExt()){
            group.setExt(isExt(group));
        }
        group = addGroupVersion(group);
      }
    }catch (Exception e){
      //回滚清除memcache
      for (Account operatee : operatees) {
        memCache.remove(getMemberKey(group.getId(), operatee.getNumber()));
      }
      throw e;
    }
    return group;
  }

  private boolean isExt(Group group){
    List<GroupMember> groupMembers=this.getGroupMembers(group.getId());
    Set<Long> extIds=new HashSet<>();
    for(GroupMember groupMember:groupMembers){
      if(accountsManager.isBootAccount(groupMember.getUid())){
        continue;
      }
      Optional<Account> accountOptional=accountsManager.get(groupMember.getUid());
      if(accountOptional.isPresent()&&!accountOptional.get().isinValid()) {
        extIds.add(accountOptional.get().getExtId());
        if(extIds.size()>1) return true;
      }
    }
    return false;
  }
//  public List<Account> checkTeam(Account operator, List<Account> operatees){
//    List<Account> noPermissionAccounts=new ArrayList<>();
//    for(Account account:operatees) {
//      if(!teamsManager.isHasSameTeam(operator.getNumber(),account.getNumber())){
//        noPermissionAccounts.add(account);
//      }
//    }
//    return noPermissionAccounts;
//  }
  @Transaction(TransactionIsolationLevel.READ_COMMITTED)
  public Group removeMemberWithPermissionCheck(Account operator, Group group, List<Account> operatees,List<GroupMember> groupMembers)
          throws NoPermissionException, NoSuchGroupException, NoSuchGroupMemberException {

    group = checkGroupStatus(group.getId(), GroupsTable.STATUS.ACTIVE);

    // Check if the operator's role is greater than the operatee
    GroupMember operatorMember = getMember(group.getId(), operator.getNumber());
    for(Account operatee:operatees) {
      GroupMember targetMember = getMember(group.getId(), operatee.getNumber());
      if (targetMember == null) {
        throw new NoSuchGroupMemberException("No such member:" + operatee.getNumber());
      }
      //leave
      if (operatorMember.getUid().equals(targetMember.getUid())) {
        if (operatorMember.getRole() == GroupMembersTable.ROLE.OWNER.ordinal()) {
          throw new NoPermissionException("You must transfer the group to someone else to quit or dissolve the group！");
        }
      } else {
        if(!group.isAnyoneRemove()){
          if (operatorMember.getRole() >= targetMember.getRole()) {
            throw new NoPermissionException("You don't have permission to remove member " + targetMember.getUid() + " from group " + group.getId());
          }
        }else{
          if(operatorMember.getRole()==GroupMembersTable.ROLE.MEMBER.ordinal()&&targetMember.getRole()==GroupMembersTable.ROLE.MEMBER.ordinal()) {
          }else{
            if (operatorMember.getRole() >= targetMember.getRole()) {
              throw new NoPermissionException("You don't have permission to remove member " + targetMember.getUid() + " from group " + group.getId());
            }
          }
        }
      }
      removeMember(group.getId(), operatee.getNumber());
      groupMembers.add(targetMember);
    }
    if(groupMembers.size()>0) {
      if(group.isExt()){
        group.setExt(isExt(group));
      }
      group = addGroupVersion(group);
    }
    return group;
  }

  @Transaction(TransactionIsolationLevel.READ_COMMITTED)
  public int setMemberSettingsWithPermissionCheck(Account operator, Group group, GroupMember member)
      throws NoPermissionException, NoSuchGroupException, InvalidParameterException {
    int changeType;
    group = checkGroupStatus(group.getId(), GroupsTable.STATUS.ACTIVE);

    GroupMember memberCurrent = getMember(group.getId(), member.getUid());
    //变更个人配置时设置为true，不增加groupVersion
    boolean isChangePrivate=false;
    if (memberCurrent.getRole() == member.getRole()) {
      changeType= MemberChangeType.BASIC.ordinal();
      // Change settings
      // Users can only change their own settings
      if(memberCurrent.getRapidRole()==member.getRapidRole()) {
        if (!operator.getNumber().equals(member.getUid())) {
          throw new NoPermissionException("You don't have permission to change settings of member " + member.getUid());
        }

        if (!ObjectUtils.objEquals(memberCurrent.getRemark(), member.getRemark())) {
          isChangePrivate = true;
        }
        if (memberCurrent.getNotification() != member.getNotification()) {
          isChangePrivate = true;
        }
        if (memberCurrent.isUseGlobal() != member.isUseGlobal()) {
          isChangePrivate = true;
        }
      }else{
        if(!operator.getNumber().equals(member.getUid())){
          checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);
        }
        changeType= MemberChangeType.RAPIDROLE.ordinal();
        isChangePrivate = false;
        // Only change RapidRole
        int role = member.getRapidRole();
        member = memberCurrent;
        member.setRapidRole(role);
      }
    } else {
      // Change role
      // Only group owner can change other's role
      checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.OWNER);

      //Owner can not change his role directly
      if(operator.getNumber().equals(member.getUid())){
        if(member.getRole()!=GroupMembersTable.ROLE.OWNER.ordinal()){
          throw new NoPermissionException("You are owner ,You cannot change your role directly，you can transfer the group to someone！");
        }
      }
      // Owner can only add or remove admins
      if (member.getRole() == GroupMembersTable.ROLE.OWNER.ordinal()) {
        throw new NoPermissionException("You don't have permission to set another owner");
      }
      if(member.getRole()<GroupMembersTable.ROLE.MEMBER.ordinal()) {
        changeType = MemberChangeType.SETADMIN.ordinal();
      }else{
        changeType = MemberChangeType.DELADMIN.ordinal();
      }
      // Only change role
      int role = member.getRole();
      member = memberCurrent;
      member.setRole(role);
    }
    if(!isChangePrivate) {
      addGroupVersion(group);
    }
    setMember(group.getId(), member);
    return changeType;
  }

  public GroupMember getMemberWithPermissionCheck(Account operator, Group group, Account operatee)
      throws NoPermissionException, NoSuchGroupException, NoSuchGroupMemberException {

    group = checkGroupStatus(group.getId(), GroupsTable.STATUS.ACTIVE);

    if(operator.getNumber().equals(operatee.getNumber())) {
      //change self Check if the operator is in the group
      checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);
    }

    GroupMember member = getMember(group.getId(), operatee.getNumber());
    if (null == member) {
      throw new NoSuchGroupMemberException("No such group member: " + group.getId() + "." + operatee.getNumber());
    }

    return member;
  }

  public List<Group> getMemberGroupsWithPermissionCheck(Account operator)
      throws NoPermissionException {
    JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, Group.class);
    List<Group> groups = (List<Group>) memCache.get(getMemberGroupsKey(operator.getNumber()), javaType);
    if (null == groups) {
      groups = new LinkedList<>();
      List<GroupMember> memberGroups = groupMembersTable().getMemberGroups(operator.getNumber());
      for (GroupMember groupMember : memberGroups) {
        Group group = getWithoutCheck(groupMember.getGid());
        if (null == group) {
          logger.error("getMemberGroups", "group not found");
          continue;
        }

        // Check if is active
        if (group.getStatus() != GroupsTable.STATUS.ACTIVE.ordinal()) {
          continue;
        }

        groups.add(group);
      }

      memCache.set(getMemberGroupsKey(operator.getNumber()), groups);
    } else {
      for (Group group : groups ) {
        group.setExt(false);
      }
    }
    return groups;
  }

  public boolean inSameGroup(Account uid1, Account uid2) {
    final List<Group> groupList1 = getMemberGroupsWithPermissionCheck(uid1);
    final List<Group> groupList2 = getMemberGroupsWithPermissionCheck(uid2);
    HashMap<String, Boolean> groupMap = new HashMap<>();
    for (Group group1 : groupList1) {
      groupMap.put(group1.getId(), true);
    }
    for (Group group2 : groupList2) {
      if (groupMap.containsKey(group2.getId())) {
        return true;
      }
    }

    return false;
  }

  public List<GroupMember> getGroupMembersWithPermissionCheck(Account operator, Group group)
      throws NoPermissionException, NoSuchGroupException {

    group = checkGroupStatus(group.getId(), GroupsTable.STATUS.ACTIVE);

    // Check if the operator is in the group
    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);
//    JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, GroupMember.class);
//    List<GroupMember> members = (List<GroupMember>) memCache.get(getGroupMembersKey(group.getId()), javaType);
//    if (null == members) {
//      members = getGroupMembers(group.getId());
//
//      memCache.set(getGroupMembersKey(group.getId()), members);
//    }

    return getGroupMembers(group.getId());
  }

  public GroupMember getMemberWithPermissionCheck(Account operator, String gid,String uid)
          throws NoPermissionException, NoSuchGroupException {

    Group group = checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);

    // Check if the operator is in the group
    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);
    GroupMember member = getMember(gid,uid);
    return member;
  }

  @Transaction(TransactionIsolationLevel.READ_COMMITTED)
  public Group transferGroupWithPermissionCheck(Account operator, Group group, Account operatee)
          throws NoPermissionException, NoSuchGroupException, InvalidParameterException, NoSuchGroupMemberException {

    group = checkGroupStatus(group.getId(), GroupsTable.STATUS.ACTIVE);

    // Check if the operator is the owner of the group
    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.OWNER);
    GroupMember newOwner = getMember(group.getId(), operatee.getNumber());
    if(newOwner==null){
      throw new NoSuchGroupMemberException("No such member:"+operatee.getNumber());
    }
    GroupMember currentOwner = getMember(group.getId(), operator.getNumber());
    currentOwner.setRole(GroupMembersTable.ROLE.ADMIN.ordinal());
    newOwner.setRole(GroupMembersTable.ROLE.OWNER.ordinal());
    group =addGroupVersion(group);
    setMember(group.getId(), currentOwner);
    setMember(group.getId(), newOwner);
    return group;
  }

  // TODO: notify client
  private void notify(Group group, String message) {

  }

  @Transaction(TransactionIsolationLevel.READ_COMMITTED)
  public GroupAnnouncement  addAnnouncementPermissionCheck(Account operator, String gid, SetGroupAnnouncementRequest request) throws NoPermissionException, NoSuchGroupException, InvalidParameterException {
    validateGroupAnnouncementParameters(request);
    Group group = checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);
    // Check if the operator is the admin/owner of the group
    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.ADMIN);
    GroupAnnouncement groupAnnouncement=new GroupAnnouncement();
    groupAnnouncement.setGid(gid);
    groupAnnouncement.setCreator(operator.getNumber());
    groupAnnouncement.setReviser(operator.getNumber());
    groupAnnouncement.setAnnouncementExpiry(request.getAnnouncementExpiry().get());
    groupAnnouncement.setContent(request.getContent().get());
    groupAnnouncement=groupAnnouncementTable().create(groupAnnouncement);
    addGroupVersion(gid);
    memCache.hset(getGroupAnnouncementKey(gid),getGroupAnnouncementHashKey(gid,groupAnnouncement.getId()), groupAnnouncement);
    return groupAnnouncement;
  }

  @Transaction(TransactionIsolationLevel.READ_COMMITTED)
  public GroupAnnouncement  setAnnouncementPermissionCheck(Account operator, String gid, String gaid,SetGroupAnnouncementRequest request) throws NoPermissionException, NoSuchGroupException, InvalidParameterException, NoSuchGroupAnnouncementException {
    validateGroupAnnouncementParametersForUpdate(request);
    Group group = checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);
    // Check if the operator is the admin/owner of the group
    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.ADMIN);
    GroupAnnouncement groupAnnouncement=checkGroupAnnouncementStatus(gid,gaid,GroupAnnouncementTable.STATUS.ACTIVE);
    if(request.getContent().isPresent()){
      groupAnnouncement.setContent(request.getContent().get());
    }
    if(request.getAnnouncementExpiry().isPresent()){
      groupAnnouncement.setAnnouncementExpiry(request.getAnnouncementExpiry().get());
    }
    groupAnnouncementTable().update(groupAnnouncement.getAnnouncementExpiry(),groupAnnouncement.getContent(),operator.getNumber(),System.currentTimeMillis(),gaid);
    addGroupVersion(gid);
    memCache.hdel(getGroupAnnouncementKey(gid),getGroupAnnouncementHashKey(gid,gaid));
    return getGroupAnnouncement(gid,gaid);
  }

  @Transaction(TransactionIsolationLevel.READ_COMMITTED)
  public void  delAnnouncementPermissionCheck(Account operator, String gid, String gaid) throws NoPermissionException, NoSuchGroupException, NoSuchGroupAnnouncementException {
    Group group = checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);
    // Check if the operator is the admin/owner of the group
    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.ADMIN);
    checkGroupAnnouncementStatus(gid,gaid,GroupAnnouncementTable.STATUS.ACTIVE);
    groupAnnouncementTable().updateStatus(GroupAnnouncementTable.STATUS.DISMISSED.ordinal(), gaid);
    addGroupVersion(gid);
    memCache.hdel(getGroupAnnouncementKey(gid),getGroupAnnouncementHashKey(gid,gaid));
  }

  public List<GroupAnnouncement>  getGroupAnnouncementPermissionCheck(Account operator, String gid) throws NoPermissionException, NoSuchGroupException, InvalidParameterException, NoSuchGroupAnnouncementException {
    Group group = checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);
    // Check if the operator is the admin/owner of the group
    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);
    return getGroupAnnouncement(gid);
  }

  @Transaction(TransactionIsolationLevel.READ_COMMITTED)
  public GroupPin addPinPermissionCheck(Account operator, String gid, SetGroupPinRequest request) throws NoPermissionException, NoSuchGroupException, InvalidParameterException {
    validateGroupPinContent(request.getContent());
    Group group = checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);
    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);
    checkMaxSize(gid);
    GroupPin groupPin=new GroupPin();
    groupPin.setGid(gid);
    groupPin.setCreator(operator.getNumber());
    groupPin.setContent(request.getContent().get());
    groupPin.setConversationId(request.getConversationId().get());
    groupPin=groupPinTable().create(groupPin);
    addGroupVersion(gid);
    memCache.hset(getGroupPinKey(gid),getGroupPinHashKey(gid,groupPin.getId()), groupPin);
    return groupPin;
  }

  public GroupPin checkMaxSize(String gid){
    List<GroupPin> groupPins = getGroupPin(gid);
    if(groupPins.size() >= groupConfiguration.getPinMaxSize()) {
      groupPins.sort(Comparator.comparing(GroupPin::getCreateTime));
      GroupPin groupPin = groupPins.get(0);
      groupPinTable().updateStatus(GroupPinTable.STATUS.DISMISSED.ordinal(), groupPin.getId());
      memCache.hdel(getGroupPinKey(gid), getGroupPinHashKey(gid, groupPin.getId()));

      List<GroupPin> groupPinList=new ArrayList<>();
      groupPinList.add(groupPin);
      Account accountServer = new Account();
      accountServer.setNumber("server");
      Device device = new Device();
      device.setId(0l);
      accountServer.setAuthenticatedDevice(device);
      Group group1 = addGroupVersionOnlyVersion(gid);
      sendGroupNotify(GroupNotify.ChangeType.PIN.ordinal(),accountServer,group1,GroupNotify.ActionType.NONE.ordinal(), GroupNotify.GroupNotifyDetailedType.GROUP_DEL_PIN.ordinal(),null,-1,null, groupPinList,GroupNotify.ActionType.DELETE.ordinal(),GroupNotify.Display.YES.ordinal(),null);
      return groupPin;
    }
    return null;
  }

  public GroupPin checkGroupPinExist(String gid, String conversationId){
    List<GroupPin> groupPins = getGroupPin(gid);
    Map<String, String> collect = groupPins.stream().filter(e -> StringUtils.isNotBlank(e.getConversationId())).collect(Collectors.toMap(GroupPin::getConversationId, GroupPin::getId));
    if(null != collect && !collect.isEmpty()){
      String gpid = collect.get(conversationId);
      if (StringUtils.isNotBlank(gpid)){
        return getGroupPin(gid, gpid);
      }
    }
    return null;
  }

  @Transaction(TransactionIsolationLevel.READ_COMMITTED)
  public void  delPinPermissionCheck(Account operator, String gid, GroupPinListRequest gpids) throws NoPermissionException, NoSuchGroupException, NoSuchGroupAnnouncementException {
    Group group = checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);
    // Check if the operator is the admin/owner of the group
    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);
    for(String gpid : gpids.getPins()){
      groupPinTable().updateStatus(GroupPinTable.STATUS.DISMISSED.ordinal(), gpid);
      memCache.hdel(getGroupPinKey(gid),getGroupPinHashKey(gid,gpid));
    }
    addGroupVersion(gid);
  }

  public List<GroupPin>  getGroupPinPermissionCheck(Account operator, String gid, int page, int size) throws NoPermissionException, NoSuchGroupException, InvalidParameterException, NoSuchGroupAnnouncementException {
    Group group = checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);
    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);
    List<GroupPin> groupPins = getGroupPin(gid);
    if(groupPins.size() > 0){
      groupPins.sort(Comparator.comparing(GroupPin::getCreateTime).reversed());
      size = size==0?groupConfiguration.getPinMaxSize():size;
      page = page==0?1:page;
      int offset =  (page-1)*size;
      int toIndex = offset + size;
      if(toIndex > groupPins.size()){
        toIndex = groupPins.size();
      }
      if(offset >= groupPins.size()){
        offset=0;
        toIndex=0;
      }
      return groupPins.subList(offset, toIndex);
    } else {
      return groupPins;
    }
  }

  private GroupPin  checkGroupPinStatus(String gid,String gpid, GroupPinTable.STATUS expectedMostStatus)
          throws NoPermissionException,NoSuchGroupAnnouncementException{
    GroupPin groupPin = getGroupPin(gid,gpid);
    if (null == groupPin) {
      throw new NoSuchGroupPinException("You don't have permission: " + gpid);
    }

    if (groupPin.getStatus() > expectedMostStatus.ordinal()) {
      throw new NoSuchGroupPinException("You don't have permission. The groupPin status is: " + groupPin.getStatus());
    }
    return groupPin;
  }

  public GroupPin getGroupPin(String gid,String gpid) {
    GroupPin groupPin = (GroupPin)memCache.hget(getGroupPinKey(gid),getGroupPinHashKey(gid,gpid), GroupPin.class);
    if(groupPin==null) {
      groupPin = groupPinTable().get(gpid);
      if(null != groupPin && groupPin.getStatus() == GroupPinTable.STATUS.ACTIVE.ordinal()){
        memCache.hset(getGroupPinKey(gid),getGroupPinHashKey(gid,gpid), groupPin);
      }
    }
    return groupPin;
  }

  private List<GroupPin> getGroupPin(String gid) {
    Map<String,Object> objectMap= memCache.hgetAll(getGroupPinKey(gid), GroupPin.class);
    List<GroupPin> activeGroupPins=new ArrayList<>();
    if(objectMap!=null) {
      for(String hashKey:objectMap.keySet()) {
        GroupPin groupPin=(GroupPin)objectMap.get(hashKey);
        if(groupPin.getStatus() == GroupPinTable.STATUS.ACTIVE.ordinal()){
          activeGroupPins.add(groupPin);
        }
      }
    }else {
      List<GroupPin> groupPins = groupPinTable().getByGid(gid, GroupPinTable.STATUS.ACTIVE.ordinal(), 0, groupConfiguration.getPinMaxSize());
      if (null != groupPins && groupPins.size() > 0) {
        for (GroupPin groupPin : groupPins) {
          memCache.hset(getGroupPinKey(gid), getGroupPinHashKey(gid, groupPin.getId()), groupPin);
          activeGroupPins.add(groupPin);
        }
      }
    }
    return activeGroupPins;
  }

  private void validateGroupPinContent(Optional<String> content)
          throws InvalidParameterException {
    if (content.isPresent()&&StringUtil.isEmpty(content.get())) {
      throw new InvalidParameterException("Invalid groupPin content: " + content);
    }
    if (content.isPresent()&&content.get().length() > 1024 * 1024){
      throw new GroupPinContentTooLongException("Group pin content is too long: "+ content);
    }
  }

  private GroupAnnouncement  checkGroupAnnouncementStatus(String gid,String gaid, GroupAnnouncementTable.STATUS expectedMostStatus)
          throws NoPermissionException,NoSuchGroupAnnouncementException{
    GroupAnnouncement groupAnnouncement = getGroupAnnouncement(gid,gaid);
    if (null == groupAnnouncement) {
      throw new NoSuchGroupAnnouncementException("No such groupAnnouncement: " + gaid);
    }

    if (groupAnnouncement.getStatus() > expectedMostStatus.ordinal()) {
      throw new NoPermissionException("You don't have permission. The groupAnnouncement status is: " + groupAnnouncement.getStatus());
    }
    return groupAnnouncement;
  }

  private GroupAnnouncement getGroupAnnouncement(String gid,String gaid) {
    GroupAnnouncement groupAnnouncement = (GroupAnnouncement)memCache.hget(getGroupAnnouncementKey(gid),getGroupAnnouncementHashKey(gid,gaid), GroupAnnouncement.class);
    if(groupAnnouncement!=null) {
      boolean isExpiry = checkGroupAnnouncementIsExpiry(groupAnnouncement);
      if (isExpiry) {
        groupAnnouncement.setStatus(GroupAnnouncementTable.STATUS.EXPIRED.ordinal());
        memCache.hdel(getGroupAnnouncementKey(gid), getGroupAnnouncementHashKey(gid, gaid));
        groupAnnouncementTable().updateStatus(GroupAnnouncementTable.STATUS.EXPIRED.ordinal(), gaid);
        return groupAnnouncement;
      }
    }else{
      groupAnnouncement = groupAnnouncementTable().get(gaid);
      if(null != groupAnnouncement){
        boolean isExpiry = checkGroupAnnouncementIsExpiry(groupAnnouncement);
        if(!isExpiry&&groupAnnouncement.getStatus()==GroupAnnouncementTable.STATUS.ACTIVE.ordinal()){
          memCache.hset(getGroupAnnouncementKey(gid),getGroupAnnouncementHashKey(gid,gaid), groupAnnouncement);
        }else if(isExpiry&&groupAnnouncement.getStatus()==GroupAnnouncementTable.STATUS.ACTIVE.ordinal()){
          groupAnnouncementTable().updateStatus(GroupAnnouncementTable.STATUS.EXPIRED.ordinal(), gaid);
        }
      }
    }
    return groupAnnouncement;
  }

  private List<GroupAnnouncement> getGroupAnnouncement(String gid) {
    Map<String,Object> objectMap= memCache.hgetAll(getGroupAnnouncementKey(gid), GroupAnnouncement.class);
    List<GroupAnnouncement> acitveGroupAnnouncements=new ArrayList<GroupAnnouncement>();
    if(objectMap!=null) {
      for(String hashKey:objectMap.keySet()) {
        GroupAnnouncement groupAnnouncement=(GroupAnnouncement)objectMap.get(hashKey);
        boolean isExpiry = checkGroupAnnouncementIsExpiry(groupAnnouncement);
        if (isExpiry) {
          groupAnnouncement.setStatus(GroupAnnouncementTable.STATUS.EXPIRED.ordinal());
          memCache.hdel(getGroupAnnouncementKey(gid), getGroupAnnouncementHashKey(gid, groupAnnouncement.getId()));
          groupAnnouncementTable().updateStatus(GroupAnnouncementTable.STATUS.EXPIRED.ordinal(), groupAnnouncement.getId());
        }else{
          acitveGroupAnnouncements.add(groupAnnouncement);
        }
      }
    }else{
      List<GroupAnnouncement> groupAnnouncements = groupAnnouncementTable().getByGid(gid);
      if(null != groupAnnouncements&&groupAnnouncements.size()>0){
        for(GroupAnnouncement groupAnnouncement:groupAnnouncements) {
          boolean isExpiry = checkGroupAnnouncementIsExpiry(groupAnnouncement);
          if (!isExpiry && groupAnnouncement.getStatus() == GroupAnnouncementTable.STATUS.ACTIVE.ordinal()) {
            memCache.hset(getGroupAnnouncementKey(gid), getGroupAnnouncementHashKey(gid, groupAnnouncement.getId()), groupAnnouncement);
            acitveGroupAnnouncements.add(groupAnnouncement);
          } else if (isExpiry && groupAnnouncement.getStatus() == GroupAnnouncementTable.STATUS.ACTIVE.ordinal()) {
            groupAnnouncementTable().updateStatus(GroupAnnouncementTable.STATUS.EXPIRED.ordinal(), groupAnnouncement.getId());
          }
        }
      }
    }
    return acitveGroupAnnouncements;
  }

  private boolean checkGroupAnnouncementIsExpiry(GroupAnnouncement groupAnnouncement){
    if(groupAnnouncement.getAnnouncementExpiry()==0){
      return false;
    }
    long expiryTime=groupAnnouncement.getReviseTime()+groupAnnouncement.getAnnouncementExpiry()*1000;
    if(System.currentTimeMillis()>expiryTime){
      return true;
    }
    return false;
  }
  private void validateGroupAnnouncementParametersForUpdate(SetGroupAnnouncementRequest request)
          throws InvalidParameterException {
    validateGroupAnnouncementContentForUpdate(request.getContent());
    validateGroupAnnouncementExpiryForUpdate(request.getAnnouncementExpiry());

  }
  private void validateGroupAnnouncementContentForUpdate(Optional<String> content)
          throws InvalidParameterException {
    if (content.isPresent()&&(StringUtil.isEmpty(content.get()) ||content.get().length() > 4096)) {
      throw new InvalidParameterException("Invalid groupAnnouncement content: " + content);
    }
  }

  private void validateGroupAnnouncementExpiryForUpdate(Optional<Long> groupAnnouncementExpiry)
          throws InvalidParameterException {
    if (groupAnnouncementExpiry.isPresent()&&(groupAnnouncementExpiry.get()==0||groupAnnouncementExpiry.get() > 60 * 60 * 24 * 30)) {
      throw new InvalidParameterException("Invalid groupAnnouncement expiry : " + groupAnnouncementExpiry);
    }
  }

  private void validateGroupAnnouncementParameters(SetGroupAnnouncementRequest request)
          throws InvalidParameterException {
    validateGroupAnnouncementContent(request.getContent());
    validateGroupAnnouncementExpiry(request.getAnnouncementExpiry());

  }

  private void validateGroupAnnouncementContent(Optional<String> content)
          throws InvalidParameterException {
    if (!content.isPresent()||StringUtil.isEmpty(content.get()) ||content.get().length() > 4096) {
      throw new InvalidParameterException("Invalid groupAnnouncement content: " + content);
    }
  }

  private void validateGroupAnnouncementExpiry(Optional<Long> groupAnnouncementExpiry)
          throws InvalidParameterException {
    if (!groupAnnouncementExpiry.isPresent()||groupAnnouncementExpiry.get()==0||groupAnnouncementExpiry.get() > 60 * 60 * 24 * 30) {
      throw new InvalidParameterException("Invalid groupAnnouncement expiry : " + groupAnnouncementExpiry);
    }
  }

  private void validateGroupParameters(Group group)
    throws InvalidParameterException {
    validateGroupName(group.getName());
    validateGroupStatus(group.getStatus());
    validateGroupMessageExpiry(group.getMessageExpiry());
    validateGroupAvatar(group.getAvatar());
    validateGroupAvatar(group.getAvatar());
    validateGroupInvitationRule(group);
    validateGroupRemindCycle(group);
  }

  private void validateGroupRemindCycle(Group group)
          throws InvalidParameterException {
    if(StringUtil.isEmpty(group.getRemindCycle())){
      group.setRemindCycle(GroupsTable.RemindCycle.NONE.getValue());
    }
    if(GroupsTable.RemindCycle.fromValue(group.getRemindCycle())==null){
      throw new InvalidParameterException("Invalid group remindCycle: " + group.getRemindCycle());
    }
  }

  private void validateGroupInvitationRule(Group group)
          throws InvalidParameterException {
    if(GroupMembersTable.ROLE.fromOrdinal(group.getInvitationRule())==null){
      group.setInvitationRule(groupConfiguration.getDefaultInvitationRule().ordinal());
    }
  }

  private void validateGroupName(String name)
      throws InvalidParameterException {
    if (StringUtil.isEmpty(name)||name.length() > 64) {
      throw new InvalidParameterException("Invalid group name: " + name);
    }
  }

  private void validateGroupStatus(int status)
      throws InvalidParameterException {
    if (status >= GroupsTable.STATUS.values().length) {
      throw new InvalidParameterException("Invalid group status: " + status);
    }
  }

  private void validateGroupMessageExpiry(long messageExpiry)
      throws InvalidParameterException {
  }

  private void validateGroupAvatar(String avatar)
      throws InvalidParameterException {
    if (avatar!=null&&avatar.length() > 4096) {
      throw new InvalidParameterException("Group avatar too long: " + avatar);
    }
  }

  private void validateGroupMemberSize(String gid,int addSize){
    if(StringUtil.isEmpty(gid)){
      if(addSize>groupConfiguration.getMembersMaxSize()){
        logger.warn("validateGroupMemberSize,gid isEmpty,new group addSize:{},getMembersMaxSize:{}",addSize,groupConfiguration.getMembersMaxSize());
        throw new ExceedingGroupMemberSizeException("The group is full or the number of invited persons exceeds the upper limit of the group");
      }
    }else {
      List<GroupMember> members = this.getGroupMembers(gid);
      int memberSize=0;
      if (members!=null){
        memberSize=members.size();
      }
      logger.info("validateGroupMemberSize,before sum,memberSize:{},addSize:{},getMembersMaxSize:{}",
              memberSize,addSize,groupConfiguration.getMembersMaxSize());
      memberSize=memberSize+addSize;
      if(memberSize>groupConfiguration.getMembersMaxSize()){
        logger.warn("validateGroupMemberSize,gid {},memberSize:{},addSize:{},getMembersMaxSize:{}",gid,memberSize,addSize,groupConfiguration.getMembersMaxSize());
        throw new ExceedingGroupMemberSizeException("The group is full or the number of invited persons exceeds the upper limit of the group");
      }
      logger.info("validateGroupMemberSize,success memberSize:{},addSize:{},getMembersMaxSize:{}",
              memberSize,addSize,groupConfiguration.getMembersMaxSize());
    }
  }
  private void validateMemberParameters(GroupMember member)
      throws InvalidParameterException {
    validateMemberRole(member.getRole());
    validateMemberDisplayName(member.getDisplayName());
    validateMemberRemark(member.getRemark());
    validateMemberNotification(member.getNotification());
    validateMemberRapidRole(member.getRapidRole());
  }

  private void validateMemberRapidRole(int rapidRole)
      throws InvalidParameterException {
    if (GroupMembersTable.RAPID_ROLE.fromOrdinal(rapidRole)==null) {
      throw new InvalidParameterException("Invalid member rapidRole: " + rapidRole);
    }
  }

  private void validateMemberRole(int role)
          throws InvalidParameterException {
    if (role >= GroupMembersTable.ROLE.values().length) {
      throw new InvalidParameterException("Invalid member role: " + role);
    }
  }

  private void validateMemberDisplayName(String displayName)
      throws InvalidParameterException {
    if (!StringUtil.isEmpty(displayName)&&displayName.length() > 64) {
      throw new InvalidParameterException("Member display name too long: " + displayName);
    }
  }

  private void validateMemberRemark(String remark)
      throws InvalidParameterException {
    if (!StringUtil.isEmpty(remark)&&remark.length() > 64) {
      throw new InvalidParameterException("Member remark too long: " + remark);
    }
  }

  private void validateMemberNotification(int notification)
      throws InvalidParameterException {
    if (GroupMembersTable.NOTIFICATION.fromOrdinal(notification)==null) {
      throw new InvalidParameterException("Invalid notification: " + notification);
    }
  }

  private String getGroupKey(String gid) {
    return String.join("_", Group.class.getSimpleName(), "Group", String.valueOf(Group.MEMCACHE_VERION), gid);
  }

  private String getGroupAnnouncementKey(String gid) {
    return String.join("_", Group.class.getSimpleName(), "GroupAnnouncement", String.valueOf(GroupAnnouncement.MEMCACHE_VERION), gid);
  }

  private String getGroupPinKey(String gid) {
    return String.join("_", Group.class.getSimpleName(), "GroupPin", String.valueOf(GroupPin.MEMCACHE_VERION), gid);
  }

  private String getGroupAnnouncementHashKey(String gid,String gaid) {
    return String.join("_", GroupAnnouncement.class.getSimpleName(), "GroupAnnouncement", String.valueOf(GroupAnnouncement.MEMCACHE_VERION), gid,gaid);
  }

  private String getGroupPinHashKey(String gid,String gpid) {
    return String.join("_", GroupPin.class.getSimpleName(), "GroupPin", String.valueOf(GroupPin.MEMCACHE_VERION), gid, gpid);
  }

  private String getMemberKey(String gid, String uid) {
    return String.join("_", Group.class.getSimpleName(), "Member", String.valueOf(Group.MEMCACHE_VERION), gid, uid);
  }

  private String getGroupMembersKey(String gid) {
    return String.join("_", Group.class.getSimpleName(), "GroupMembers", String.valueOf(Group.MEMCACHE_VERION), gid);
  }

  private String getMemberGroupsKey(String uid) {
    return String.join("_",Group.class.getSimpleName(), "MemberGroups", String.valueOf(Group.MEMCACHE_VERION), uid);
  }
  

  public MemCache getMemCache() {
    return memCache;
  }

  public void setMemCache(MemCache memCache) {
    this.memCache = memCache;
  }

  public GroupConfiguration getGroupConfiguration() {
    return groupConfiguration;
  }

  public void setGroupConfiguration(GroupConfiguration groupConfiguration) {
    this.groupConfiguration = groupConfiguration;
  }

  public void deleteNotActiveGroup(int groupExpireThreshold){
    long  timestamp = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(groupExpireThreshold);
    List<Group> expireGroups=groupsTable().ListGroupByActiveTime(timestamp);
    if(expireGroups!=null&&expireGroups.size()>0){
      Account operator=new Account("server",null);
      Device device=new Device();
      device.setId(0);
      operator.setAuthenticatedDevice(device);
      for(Group group:expireGroups){
        int previousStatus = group.getStatus();
        group.setStatus(GroupsTable.STATUS.EXPIRED.ordinal());
        group=setGroupWithoutLastActiveTime(group);
        logger.info("groupId:{}, groupName:{} is expired! is deleted!,previousStatus:{}", group.getId(), group.getName(),previousStatus);
        if (previousStatus == GroupsTable.STATUS.ACTIVE.ordinal()) {
          sendGroupNotify(GroupNotify.ChangeType.BASIC.ordinal(), operator, group, GroupNotify.ActionType.DELETE.ordinal(),
                  GroupNotify.GroupNotifyDetailedType.GROUP_DESTROY.getCode(), null,
                  -1, null, null, -1, GroupNotify.Display.NO.ordinal(), null);
        }
        destroyGroup(group.getId());
      }
    }
  }

  public void changeGroupExtForUser(Account account){
    executor.submit(new Runnable() {
      @Override
      public void run() {
        List<Group> groups=getMemberGroupsWithPermissionCheck(account);
        if(groups!=null&&groups.size()>0){
          for(Group group:groups) {
            if (group.getStatus() != GroupsTable.STATUS.ACTIVE.ordinal()) {
              continue;
            }
            GroupMember groupMember = getMember(group.getId(), account.getNumber());
            if (groupMember == null) {
              continue;
            }
            List<GroupMember> groupMembers = getGroupMembers(group.getId());
            if (groupMembers.size() <= 1) {
              continue;
            }
            boolean oldIsExt=group.isExt();
            boolean isExt=isExt(group);
            Account operator = new Account("server", null);
            Device device = new Device();
            device.setId(0);
            operator.setAuthenticatedDevice(device);
            List<GroupMember> groupMemberList = new ArrayList<>();
            groupMemberList.add(groupMember);
            group.setExt(isExt);
            group = setGroupWithoutLastActiveTime(group);
            if (oldIsExt != isExt) {
              sendGroupNotify(GroupNotify.ChangeType.MEMBER.ordinal(), operator, group, GroupNotify.ActionType.UPDATE.ordinal(), GroupNotify.GroupNotifyDetailedType.GROUP_EXT_CHANGE_ACCOUNT.getCode(), groupMemberList, GroupNotify.ActionType.UPDATE.ordinal(), null, null, -1, GroupNotify.Display.NO.ordinal(), null);
            }else{
              sendGroupNotify(GroupNotify.ChangeType.MEMBER.ordinal(), operator, group, GroupNotify.ActionType.NONE.ordinal(), GroupNotify.GroupNotifyDetailedType.GROUP_EXT_CHANGE_ACCOUNT.getCode(), groupMemberList, GroupNotify.ActionType.UPDATE.ordinal(), null, null, -1, GroupNotify.Display.NO.ordinal(), null);
            }
          }
        }
      }
    });
  }

  public void kickoutAllGroupForUser(Account account){
    List<Group> groups=getMemberGroupsWithPermissionCheck(account);
    if(groups!=null&&groups.size()>0){
      for(Group group:groups){
        if(group.getStatus()!=GroupsTable.STATUS.ACTIVE.ordinal()){
          continue;
        }
        GroupMember groupMember=getMember(group.getId(),account.getNumber());
        if(groupMember==null) {
          continue;
        }
        List<GroupMember> groupMembers=getGroupMembers(group.getId());
        if(groupMembers.size()<=1) {
          try {
            group.setStatus(GroupsTable.STATUS.DISMISSED.ordinal());
            group=setGroupWithPermissionCheck(account, group,-1);
            clearMemberGroupCacheForGroup(group.getId());
            sendGroupNotify(GroupNotify.ChangeType.BASIC.ordinal(),account,group,GroupNotify.ActionType.DELETE.ordinal(), GroupNotify.GroupNotifyDetailedType.DISMISS_GROUP.ordinal(), null,-1,null,null,-1,GroupNotify.Display.YES.ordinal(),null);
          } catch (Exception e){
            logger.warn("DISMISSED failed group:{}",group.getId(),e);
          }
          continue;
        }
        boolean extChange=false;
        boolean oldExt=group.isExt();
        GroupMember transferGM=null;
        Account operator=new Account("server",null);
        Device device=new Device();
        device.setId(0);
        operator.setAuthenticatedDevice(device);
        if(groupMember.getRole()==GroupMembersTable.ROLE.OWNER.ordinal()){
          logger.info("GroupManagerWithTransaction.kickoutAllGroupForUser,user:{},groupId:{}, is owner! transfer",account.getNumber(),group.getId());
          for (GroupMember groupMemberTemp:groupMembers){
            Optional<Account> accountOptional=accountsManager.get(groupMemberTemp.getUid());
            if(!accountOptional.isPresent()||accountOptional.get().isinValid()){
              continue;
            }
            if(account.getNumber().equals(accountOptional.get().getNumber())){
              continue;
            }
            if(groupMemberTemp.getRole()==GroupMembersTable.ROLE.ADMIN.ordinal()){
              transferGM=groupMemberTemp;
              break;
            }
            if(transferGM==null){
              transferGM=groupMemberTemp;
              continue;
            }
            if(groupMemberTemp.getCreate_time()<transferGM.getCreate_time()){
              transferGM=groupMemberTemp;
            }
          }
          if(transferGM!=null) {
            logger.info("GroupManagerWithTransaction.kickoutAllGroupForUser,user:{},groupId:{}, is owner! transfer to {}", account.getNumber(), group.getId(), transferGM.getUid());
            transferGM.setRole(GroupMembersTable.ROLE.OWNER.ordinal());
            setMember(group.getId(), transferGM);
            logger.info("GroupManagerWithTransaction.kickoutAllGroupForUser,user:{},groupId:{}, is owner! transfer to {} ,set success!", account.getNumber(), group.getId(), transferGM.getUid());
            List<GroupMember> groupMemberList = new ArrayList<>();
            groupMemberList.add(transferGM);
            group = addGroupVersionOnlyVersion(group.getId());
            sendGroupNotify(GroupNotify.ChangeType.PERSONNEL.ordinal(), operator, group, GroupNotify.ActionType.NONE.ordinal(), GroupNotify.GroupNotifyDetailedType.GROUP_CHANGE_OWNER.getCode(), groupMemberList, GroupNotify.ActionType.UPDATE.ordinal(), null, null,-1, GroupNotify.Display.NO.ordinal(), null);
            logger.info("GroupManagerWithTransaction.kickoutAllGroupForUser,user:{},groupId:{}, is owner! transfer to {} ,set success! sendNotify success!", account.getNumber(), group.getId(), transferGM.getUid());
          }else{
            logger.info("GroupManagerWithTransaction.kickoutAllGroupForUser,user:{},groupId:{}, not have valid member！",account.getNumber(),group.getId());
          }
        }
        List<GroupMember> groupMemberList=new ArrayList<>();
        groupMemberList.add(groupMember);
        removeMember(group.getId(),groupMember.getUid());
        if(group.isExt()){
          group.setExt(this.isExt(group));
        }
        group=setGroupWithoutLastActiveTime(group);
        if(group.isExt()!=oldExt){
          extChange=true;
        }
        sendGroupNotify(GroupNotify.ChangeType.MEMBER.ordinal(),operator,group,extChange?GroupNotify.ActionType.UPDATE.ordinal():GroupNotify.ActionType.NONE.ordinal(),GroupNotify.GroupNotifyDetailedType.KICKOUT_GROUP_ACCOUNT_INVALID.getCode(),groupMemberList,GroupNotify.ActionType.DELETE.ordinal(), null,null,-1,GroupNotify.Display.NO.ordinal(), null);
      }
    }
  }

  public void setGroupNotificationForChangeGlobalNotification(Account account){
    List<Group> groups=getMemberGroupsWithPermissionCheck(account);
    int groupNotifyType=GroupNotify.ChangeType.PERSONNEL_PRIVATE.ordinal();
    int notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_MEMBERINFO_CHANGE_PRIVATE.ordinal();
    int display=GroupNotify.Display.NO.ordinal();
    for(Group group:groups){
      GroupMember groupMember=getMember(group.getId(),account.getNumber());
      if(groupMember!=null){
        if(groupMember.isUseGlobal()){
          if(groupMember.getNotification()!=accountsManager.getGlobalNotification(account)) {
            groupMember.setNotification(accountsManager.getGlobalNotification(account));
            this.setMemberSettingsWithPermissionCheck(account, group, groupMember);
            this.clearGroupMembersCache(group.getId());
            List<GroupMember> groupMembers = new ArrayList<>();
            groupMembers.add(groupMember);
            sendGroupNotify(groupNotifyType, account, group, GroupNotify.ActionType.NONE.ordinal(), notifyDetailedType, groupMembers, GroupNotify.ActionType.UPDATE.ordinal(), null, null,-1, display, null);
          }
        }
      }
    }
  }

  private String getRejoinToken(Account operator,String gid){
    Map<String,Object> objectMap=new HashMap<String,Object>();
    objectMap.put("gid",gid);
    objectMap.put("inviter",operator.getNumber());
    long effectiveDuration=172800000L;
    String rejoinTokenEffectiveDuration=memCache.get("rejoinTokenEffectiveDuration");
    if(!StringUtil.isEmpty(rejoinTokenEffectiveDuration)) {
      try {
        effectiveDuration = Long.parseLong(rejoinTokenEffectiveDuration);
      }catch (Exception e){
        logger.error("get rejoinTokenEffectiveDuration from memCache is error");
      }
    }
    return tokenUtil.createToken(objectMap,effectiveDuration);
  }

  public void sendGroupNotify(int changeType,Account operator, Group group,int groupAction,int notifyDetailedType, List<GroupMember> members,int groupMemeberAction , List<GroupAnnouncement> groupAnnouncements , List<GroupPin> groupPins,int groupAnnouncementAction,int display,final Object operatee){
    executor.submit(new Runnable() {
      @Override
      public void run() {
        String content=null;
        try {
          List<GroupNotify.GroupMember> notifyMembers = null;
          Map<String,GroupNotify.GroupMember> notifySelfMembers = null;
          List<GroupMember> needNotifymembers =members;

          if(notifyDetailedType== GroupNotify.GroupNotifyDetailedType.GROUP_CHANGE_OWNER.ordinal()){
            needNotifymembers=new ArrayList<GroupMember>();
            GroupMember operatorMem=getMemberWithPermissionCheck(operator,group.getId(),operator.getNumber());
            GroupMember operateeMem=getMemberWithPermissionCheck(operator,group.getId(),(String) operatee);
            needNotifymembers.add(operatorMem);
            needNotifymembers.add(operateeMem);
          }
          if(notifyDetailedType== GroupNotify.GroupNotifyDetailedType.GROUP_ADD_ADMIN.ordinal()||notifyDetailedType== GroupNotify.GroupNotifyDetailedType.GROUP_DEL_ADMIN.ordinal()){
            List<GroupMember> groupMembers=new ArrayList<GroupMember>();
            for(GroupMember groupMember:needNotifymembers){
              String uid=groupMember.getUid();
              GroupMember groupMemberN=getMemberWithPermissionCheck(operator,group.getId(),uid);
              if(groupMemberN!=null) {
                groupMembers.add(groupMemberN);
              }
            }
            needNotifymembers=groupMembers;
          }

          if (needNotifymembers != null && needNotifymembers.size() > 0) {
            notifyMembers = new ArrayList<GroupNotify.GroupMember>();
            notifySelfMembers= new HashMap<String,GroupNotify.GroupMember>();
            for (GroupMember groupMember : needNotifymembers) {
              GroupNotify.GroupMember notifyMember =null;
              GroupNotify.GroupMember notifySelfMember =null;
              Optional<Account> accountOptional=accountsManager.get(groupMember.getUid());
              if(!accountOptional.isPresent()||accountOptional.get().isinValid()) continue;
              String displayName=getDisplayName(accountOptional.get(),groupMember);
              String inviteCode=null;
              if(notifyDetailedType==GroupNotify.GroupNotifyDetailedType.KICKOUT_GROUP.ordinal()&&group.isRejoin()) {
                GroupMember operatorMem = getMemberWithPermissionCheck(operator, group.getId(), operator.getNumber());
                if (operatorMem.getRole()<=group.getInvitationRule()) {
                  inviteCode = getRejoinToken(operator, group.getId());
                }
              }
              notifySelfMember = new GroupNotify.GroupMemberWithSelf(groupMember.getUid(), groupMember.getRole(), displayName, groupMemeberAction, groupMember.getNotification(), groupMember.getRemark(), groupMember.isUseGlobal(), groupMember.getRapidRole(),inviteCode,accountOptional.get().getExtId());
              notifyMember=new GroupNotify.GroupMember(groupMember.getUid(), groupMember.getRole(), displayName, groupMemeberAction,groupMember.getRapidRole(),null,accountOptional.get().getExtId());
              notifyMembers.add(notifyMember);
              notifySelfMembers.put(groupMember.getUid(),notifySelfMember);
            }
          }
          List<GroupNotify.GroupAnnouncement> notifyAnnouncements = null;
          if (groupAnnouncements != null && groupAnnouncements.size() > 0) {
            notifyAnnouncements = new ArrayList<GroupNotify.GroupAnnouncement>();
            for (GroupAnnouncement groupAnnouncement : groupAnnouncements) {
              GroupNotify.GroupAnnouncement notifyAnnouncement = new GroupNotify.GroupAnnouncement(groupAnnouncement.getId(), groupAnnouncement.getAnnouncementExpiry(), groupAnnouncement.getContent(), groupAnnouncement.getReviseTime(), groupAnnouncementAction);
              notifyAnnouncements.add(notifyAnnouncement);
            }
          }
          List<GroupNotify.GroupPin> notifyPins = null;
          if (groupPins != null && groupPins.size() > 0){
            notifyPins = new ArrayList<>();
            for(GroupPin groupPin : groupPins){
              GroupNotify.GroupPin notifyPin = new GroupNotify.GroupPin(groupPin.getId(), groupPin.getConversationId(), groupPin.getContent(), groupPin.getCreator(), groupPin.getCreateTime(), groupAnnouncementAction);
              notifyPins.add(notifyPin);
            }
          }
          GroupNotify.Group nogifyGroup= null;
          if(groupAction!=GroupNotify.ActionType.NONE.ordinal()) {
            String avatar=null;
            if(notifyDetailedType == GroupNotify.GroupNotifyDetailedType.GROUP_AVATAR_CHANGE.ordinal()||notifyDetailedType == GroupNotify.GroupNotifyDetailedType.CREATE_GROUP.ordinal()){
              avatar=group.getAvatar();
            }
            nogifyGroup =new GroupNotify.Group(group.getName(), group.getMessageExpiry(),avatar, groupAction, group.getInvitationRule(),group.getRemindCycle(),group.isAnyoneRemove(),
                    group.linkInviteSwitchOn(),group.isRejoin(),group.isExt(),group.getPublishRule());
          }
          GroupNotify.NodifyData nodifyData = new GroupNotify.NodifyData(operator.getNumber(),operator.getAuthenticatedDevice().get().getId(),group.getId(),changeType,notifyDetailedType,group.getVersion(),nogifyGroup, notifyMembers, notifyAnnouncements, notifyPins,null);
          content=createGroupNotifyContent(operator,notifyDetailedType,operatee);
          if (notifyDetailedType == GroupNotify.GroupNotifyDetailedType.WEBLINK_INVITE_JOIN.getCode()) {
            Account inviter = (Account) operatee;
            logger.info("sendGroupNotify WEBLINK_INVITE_JOIN,inivter:{}",inviter.getNumber());
            nodifyData.setInviter(inviter.getNumber());
            nodifyData.setGroupNotifyDetailedType(GroupNotify.GroupNotifyDetailedType.INVITE_JOIN_GROUP.ordinal());
          }
          GroupNotify groupNotify = new GroupNotify( content, System.currentTimeMillis(), nodifyData, display);
          sendNotify(group, groupNotify,notifyDetailedType,members,notifySelfMembers,operator);
        }catch (Exception e){
          logger.error(String.format("sendGroupNotify error! changeType:%d,operator:s%,group:%s,content:%s",changeType,operator.getNumber(),group.getId(),content));
        }
      }
    });
  }
  private void sendNotify(Group group,GroupNotify notify,int notifyDetailedType,List<GroupMember> members,Map<String,GroupNotify.GroupMember> notifySelfMembers,Account operator){
    Notify sendNotify=null;
    if(notifyDetailedType==GroupNotify.GroupNotifyDetailedType.GROUP_MEMBERINFO_CHANGE_PRIVATE.ordinal()){//只通知自己
      for(GroupMember groupMember:members){
        Account memAccount = getAccount(groupMember.getUid());
        if(null != memAccount){
          sendNotify=handleNotify(memAccount,notify,notifySelfMembers);
          notifyManager.sendNotify(memAccount,sendNotify,null);
        }
      }
      return;
    }

    //通知群成员
    notifyManager.sendNotifyForGroup(group,notify,null);
    List<GroupMember> groupMembers=getGroupMembers(group.getId());
//    for(GroupMember groupMember:groupMembers){//通知群成员
//      Account memAccount = getAccount(groupMember.getUid());
//      if(null == memAccount) continue;
//      sendNotify=handleNotify(memAccount,notify,notifySelfMembers);
//      if(notifyDetailedType== GroupNotify.GroupNotifyDetailedType.GROUP_ADD_ANNOUNCEMENT.ordinal()||notifyDetailedType== GroupNotify.GroupNotifyDetailedType.GROUP_UPDATE_ANNOUNCEMENT.ordinal()) {
//        Notification notification=new Notification();
//        if(notifyDetailedType== GroupNotify.GroupNotifyDetailedType.GROUP_ADD_ANNOUNCEMENT.ordinal()) {
//          notification.setType(Notification.Type.GROUP_ADD_ANNOUNCEMENT.getCode());
//        }else if(notifyDetailedType== GroupNotify.GroupNotifyDetailedType.GROUP_UPDATE_ANNOUNCEMENT.ordinal()){
//          notification.setType(Notification.Type.GROUP_UPDATE_ANNOUNCEMENT.getCode());
//        }
//        Notification.Args args=new Notification.Args();
//        args.setSource(operator);
//        args.setGid(group.getId());
//        notification.setArgs(args);
//        notifyManager.sendNotify(memAccount, sendNotify, notification);
//      }else{
//        notifyManager.sendNotify(memAccount, sendNotify, null);
//      }
//    }
    if(notifyDetailedType== GroupNotify.GroupNotifyDetailedType.KICKOUT_GROUP.ordinal()||notifyDetailedType== GroupNotify.GroupNotifyDetailedType.LEAVE_GROUP.ordinal()){//自己被踢出,或自己退出，通知自己
      for(GroupMember groupMember:members){
        Account memAccount = getAccount(groupMember.getUid());
        if(null != memAccount){
          sendNotify=handleNotify(memAccount,notify,notifySelfMembers);
          notifyManager.sendNotify(memAccount,sendNotify,null);
        }
      }
    }
  }

  private Notify handleNotify(Account notifyAccount ,GroupNotify notify,Map<String,GroupNotify.GroupMember> notifySelfMembers){
    Notify returnNotify=new GroupNotify(notify.getContent(),notify.getNotifyTime(),(GroupNotify.NodifyData)notify.getData(),notify.getDisplay());
    if(returnNotify!=null&&returnNotify.getData()!=null&&((GroupNotify.NodifyData)returnNotify.getData()).getMembers()!=null&&((GroupNotify.NodifyData)returnNotify.getData()).getMembers().size()!=0){
      GroupNotify.NodifyData nodifyData=(GroupNotify.NodifyData)notify.getData();
      GroupNotify.NodifyData returnNotifyDate=new GroupNotify.NodifyData(nodifyData.getOperator(),nodifyData.getOperatorDeviceId(),nodifyData.getGid(),nodifyData.getGroupNotifyType(),nodifyData.getGroupNotifyDetailedType(),nodifyData.getGroupVersion(),nodifyData.getGroup(),nodifyData.getMembers(),nodifyData.getGroupAnnouncements(), nodifyData.getGroupPins(),null);
      List<GroupNotify.GroupMember> members=new ArrayList<GroupNotify.GroupMember>();
      members.addAll(returnNotifyDate.getMembers());
      String number=notifyAccount.getNumber();
      GroupNotify.GroupMember groupMemberSelf=notifySelfMembers.get(number);
      if(groupMemberSelf!=null){
        Iterator<GroupNotify.GroupMember> iterator=members.iterator();
        while (iterator.hasNext()){
          GroupNotify.GroupMember groupMember=iterator.next();
          if(groupMember.getUid().equals(number)){
            iterator.remove();
          }
        }
        members.add(groupMemberSelf);
        returnNotifyDate.setMembers(members);
        returnNotify.setData(returnNotifyDate);
      }
    }
    return returnNotify;
  }
  private String createGroupNotifyContent(Account operator,int notifyType,Object operatee){
    String content=null;
    Account opertorIAR = getAccount(operator.getNumber());
    String operatorName=operator.getNumber();
    if(opertorIAR!=null&& !StringUtil.isEmpty(opertorIAR.getPlainName())){
      operatorName=opertorIAR.getPlainName();
    }
    GroupNotify.GroupNotifyDetailedType groupNotifyDetailedType= GroupNotify.GroupNotifyDetailedType.fromCode(notifyType);
    if(groupNotifyDetailedType==null){
      return null;
    }
    switch (groupNotifyDetailedType){
      case CREATE_GROUP:
        content=String.format(GroupNotify.ContentTermTemplate.CREATE_GROUP,operatorName);
        break;
      case JOIN_GROUP:
        break;
      case LEAVE_GROUP:
        StringBuffer leaveNames=new StringBuffer();
        for(GroupMember groupMember:(List<GroupMember>)operatee) {
          String uid=groupMember.getUid();
          Account leaveMember=getAccount(uid);
          String leaveMemberName=uid;
          if(leaveMember!=null&& !StringUtil.isEmpty(leaveMember.getPlainName())){
            leaveMemberName=leaveMember.getPlainName();
          }
          leaveNames.append(leaveMemberName).append(",");
        }
        if(leaveNames.length()>0){
          leaveNames=leaveNames.delete(leaveNames.length()-1,leaveNames.length());
        }
        content = String.format(GroupNotify.ContentTermTemplate.LEAVE_GROUP, leaveNames.toString());
        break;
      case INVITE_JOIN_GROUP:
        StringBuffer joinNames=new StringBuffer();
        for(GroupMember groupMember:(List<GroupMember>)operatee) {
          String uid=groupMember.getUid();
          Account joinMember=getAccount(uid);
          String joinMemberName=uid;
          if(joinMember!=null&& !StringUtil.isEmpty(joinMember.getPlainName())){
            joinMemberName=joinMember.getPlainName();
          }
          joinNames.append(joinMemberName).append(",");
        }
        content=String.format(GroupNotify.ContentTermTemplate.INVITE_JOIN_GROUP,operatorName,joinNames.toString());
        break;
      case KICKOUT_GROUP:
        StringBuffer kickoutNames=new StringBuffer();
        for(GroupMember groupMember:(List<GroupMember>)operatee) {
          String uid=groupMember.getUid();
          Account kickoutMember=getAccount(uid);
          String kickoutMemberName=uid;
          if(kickoutMember!=null&& !StringUtil.isEmpty(kickoutMember.getPlainName())){
            kickoutMemberName=kickoutMember.getPlainName();
          }
          kickoutNames.append(kickoutMemberName).append(",");
        }
        content = String.format(GroupNotify.ContentTermTemplate.KICKOUT_GROUP,operatorName, kickoutNames.toString());
        break;
      case DISMISS_GROUP:
      case GROUP_DESTROY:
        content = String.format(GroupNotify.ContentTermTemplate.DISMISS_GROUP, operatorName);
        break;
      case GROUP_NAME_CHANGE:
        content = String.format(GroupNotify.ContentTermTemplate.GROUP_NAME_CHANGE, operatorName,operatee);
        break;
      case GROUP_AVATAR_CHANGE:
        content = String.format(GroupNotify.ContentTermTemplate.GROUP_AVATAR_CHANGE, operatorName);
        break;
      case GROUP_INVITATION_RULE_CHANGE:
        content = String.format(GroupNotify.ContentTermTemplate.GROUP_INVITATION_RULE_CHANGE, operatorName);
        break;
      case GROUP_MSG_EXPIRY_CHANGE:
        content = String.format(GroupNotify.ContentTermTemplate.GROUP_MSG_EXPIRY_CHANGE, operatorName);
        break;
      case GROUP_OTHER_CHANGE:
        content = String.format(GroupNotify.ContentTermTemplate.GROUP_OTHER_CHANGE, operatorName);
        break;
      case GROUP_ADD_ADMIN:
        Account addAdminIAR=getAccount((String)operatee);
        String addAdminName=(String)operatee;
        if(addAdminIAR!=null&& !StringUtil.isEmpty(addAdminIAR.getPlainName())){
          addAdminName=addAdminIAR.getPlainName();
        }
        content = String.format(GroupNotify.ContentTermTemplate.GROUP_ADD_ADMIN, operatorName,addAdminName);
        break;
      case GROUP_DEL_ADMIN:
        Account delAdminIAR=getAccount((String)operatee);
        String delAdminName=(String)operatee;
        if(delAdminIAR!=null&& !StringUtil.isEmpty(delAdminIAR.getPlainName())){
          delAdminName=delAdminIAR.getPlainName();
        }
        content = String.format(GroupNotify.ContentTermTemplate.GROUP_DEL_ADMIN, operatorName,delAdminName);
        break;
      case GROUP_MEMBERINFO_CHANGE:
      case GROUP_MEMBERINFO_CHANGE_PRIVATE:
        content = String.format(GroupNotify.ContentTermTemplate.GROUP_MEMBERINFO_CHANGE, operatorName);
        break;
      case GROUP_CHANGE_OWNER:
        Account changeOwerIAR=getAccount((String)operatee);
        String changeOwerName=(String)operatee;
        if(changeOwerIAR!=null&& !StringUtil.isEmpty(changeOwerIAR.getPlainName())){
          changeOwerName=changeOwerIAR.getPlainName();
        }
        content=String.format(GroupNotify.ContentTermTemplate.GROUP_CHANGE_OWNER,operatorName,changeOwerName);
        break;
      case GROUP_ADD_ANNOUNCEMENT:
        content=String.format(GroupNotify.ContentTermTemplate.GROUP_ADD_ANNOUNCEMENT,operatorName);
        break;
      case GROUP_UPDATE_ANNOUNCEMENT:
        content=String.format(GroupNotify.ContentTermTemplate.GROUP_UPDATE_ANNOUNCEMENT,operatorName);
        break;
      case GROUP_DEL_ANNOUNCEMENT:
        content=String.format(GroupNotify.ContentTermTemplate.GROUP_DEL_ANNOUNCEMENT,operatorName);
        break;
      case GROUP_ADD_PIN:
        content=String.format(GroupNotify.ContentTermTemplate.GROUP_ADD_PIN,operatorName);
        break;
      case GROUP_DEL_PIN:
        content=String.format(GroupNotify.ContentTermTemplate.GROUP_DEL_PIN,operatorName);
        break;
      case GROUP_REMIND_CHANGE:
        content=String.format(GroupNotify.ContentTermTemplate.GROUP_REMIND_CHANGE,operatorName);
        break;
      case GROUP_CHANGE_RAPID_ROLE:
        if(operatee!=null) {
          GroupMember gm=(GroupMember)operatee;
          if (operator.getNumber().equals(gm.getUid())) {
            content = String.format(GroupNotify.ContentTermTemplate.GROUP_CHANGE_RAPID_ROLE_SELF, operatorName,GroupMembersTable.RAPID_ROLE.fromOrdinal(gm.getRapidRole()).name().toLowerCase());
          } else {
            Account accountTemp = getAccount(gm.getUid());
            String nameTemp = gm.getUid();
            if (accountTemp != null && !StringUtil.isEmpty(accountTemp.getPlainName())) {
              nameTemp = accountTemp.getPlainName();
            }
            content = String.format(GroupNotify.ContentTermTemplate.GROUP_CHANGE_RAPID_ROLE, operatorName,nameTemp,GroupMembersTable.RAPID_ROLE.fromOrdinal(gm.getRapidRole()).name().toLowerCase());
          }
        }
        break;
      case GROUP_ANYONE_REMOVE_CHANGE:
        content=String.format(GroupNotify.ContentTermTemplate.GROUP_ANYONE_REMOVE_CHANGE,operatorName);
        break;
      case GROUP_REJOIN_CHANGE:
        content=String.format(GroupNotify.ContentTermTemplate.GROUP_REJOIN_CHANGE,operatorName);
        break;
      case GROUP_PUBLISH_RULE_CHANGE:
        content=String.format(GroupNotify.ContentTermTemplate.GROUP_PUBLISH_RULE_CHANGE,operatorName);
        break;
      case WEBLINK_INVITE_JOIN:
        content=String.format(GroupNotify.ContentTermTemplate.GROUP_WEBLINK_INVITE_JOIN,operatorName);
        break;
      default:
    }
    return content;
  }

  private Account getAccount(String uid) {
    Optional<Account> account = accountsManager.get(uid);
    if (!account.isPresent() || account.get().isinValid()) {
      return null;
    }
    return account.get();
  }

  public NotifyManager getNotifyManager() {
    return notifyManager;
  }

  public void setNotifyManager(NotifyManager notifyManager) {
    this.notifyManager = notifyManager;
  }

  public AccountsManager getAccountsManager() {
    return accountsManager;
  }

  public void setAccountsManager(AccountsManager accountsManager) {
    this.accountsManager = accountsManager;
  }

  public List<Group> getGroupsList(String name, int offset, int limit) {
    List<Group> groupsList = groupsTable().getGroupsList(name, offset, limit);
    return groupsList;
  }

  public long getGroupsTotal(String name) {
    return groupsTable().getGroupsTotal(name);
  }

  public String getDisplayName(Account account,GroupMember groupMember){
    if(StringUtil.isEmpty(groupMember.getDisplayName())){
      if(account!=null) {
        return (String)account.getPublicConfig(AccountExtend.FieldName.PUBLIC_NAME);
      }else{
        Optional<Account> optionalAccount=accountsManager.get(groupMember.getUid());
        if(optionalAccount.isPresent()) return (String)optionalAccount.get().getPublicConfig(AccountExtend.FieldName.PUBLIC_NAME);
      }
    }
    return groupMember.getDisplayName();
  }

  public GidStringLookup  getGidStringLookup(){
    return new GidStringLookup();
  }
  public class GidStringLookup implements StringLookup {
    @Override
    public String lookup(String gid) {
      Group group= getWithoutCheck(gid);
      if(group!=null){
        return group.getName();
      }
      return "unknown group";
    }
  }


  public void groupCycleRemind(String remindCycle) {
    try {
      List<Group> groups =groupsTable().getGroupByRemindCycle(remindCycle);
      if(groups==null||groups.size()==0){
        logger.info("groupCycleRemind groups.size is 0 , remindCycle:{}",remindCycle);
        return;
      }
      GroupNotify.GroupRemind groupRemind=new GroupNotify.GroupRemind(remindCycle);
      for (Group group:groups){
        GroupNotify.NodifyData nodifyData = new GroupNotify.NodifyData(group.getId(),GroupNotify.ChangeType.REMIND.ordinal(),GroupNotify.GroupNotifyDetailedType.GROUP_REMIND.ordinal(),groupRemind);
        GroupNotify groupNotify = new GroupNotify( null, System.currentTimeMillis(), nodifyData, Notify.Display.YES.ordinal());
        sendNotify(group, groupNotify,GroupNotify.GroupNotifyDetailedType.GROUP_REMIND.ordinal(), null,null,null);
      }
    } catch (NoPermissionException e) {
      e.printStackTrace();
    } catch (NoSuchGroupException e) {
      e.printStackTrace();
    }
  }

  public void setTokenUtil(TokenUtil tokenUtil) {
    this.tokenUtil = tokenUtil;
  }

  public static class GroupSendAccounts{
    public Map<String, Account> getValidAccounts() {
      return validAccounts;
    }

    public List<UnavailableAccount> getUnavailableAccounts() {
      return unavailableAccounts;
    }

    private Map<String,Account> validAccounts;

    public Map<String, Boolean> getAllAccounts() {
      return allAccounts;
    }

    private Map<String,Boolean> allAccounts;
    List<UnavailableAccount> unavailableAccounts;
  }

  public GroupSendAccounts getGroupSendAccounts(String gid) {
    GroupSendAccounts groupSendAccounts=new GroupSendAccounts();
    groupSendAccounts.validAccounts = new HashMap<>();
    groupSendAccounts.allAccounts = new HashMap<>();
    groupSendAccounts.unavailableAccounts = new ArrayList<>();
    final List<GroupMember> groupMembers = getGroupMembers(gid);
    for (GroupMember groupMember : groupMembers) {
      final Optional<Account> accountOptional = accountsManager.get(groupMember.getUid());
      if (accountOptional.isPresent()) {
        final Account account = accountOptional.get();
        groupSendAccounts.allAccounts.put(account.getNumber(), true);
        if (!account.isRegistered()) {
          groupSendAccounts.unavailableAccounts.add(new UnavailableAccount(account.getNumber(), "Logged out"));
          continue;
        }
        if (account.isInactive()) {
          groupSendAccounts.unavailableAccounts.add(new UnavailableAccount(account.getNumber(), "Inactive"));
          continue;
        }
        groupSendAccounts.validAccounts.put(account.getNumber(), account);
      }
    }
    logger.info("getGroupSendAccounts gid:{},validAccounts count:{},unavailableAccounts count:{}", gid, groupSendAccounts.validAccounts.size(),groupSendAccounts.unavailableAccounts.size());
    return groupSendAccounts;
  }
  public List<UnavailableAccount> getUnavailableAccounts(String gid) {
    List<UnavailableAccount> unavailableAccounts = new ArrayList<>();
    final List<GroupMember> groupMembers = getGroupMembers(gid);
    for (GroupMember groupMember : groupMembers) {
      final Optional<Account> accountOptional = accountsManager.get(groupMember.getUid());
      if (accountOptional.isPresent()) {
        final Account account = accountOptional.get();
        if (!account.isRegistered()) {
          unavailableAccounts.add(new UnavailableAccount(account.getNumber(), "Logged out"));
          continue;
        }
        if (account.isInactive()) {
          unavailableAccounts.add(new UnavailableAccount(account.getNumber(), "Inactive"));
        }
      }
    }
    logger.info("getUnavailableAccounts gid:{},unavailableAccounts count:{}", gid, unavailableAccounts.size());
    return unavailableAccounts;
  }
  //  public static void main(String[] args) {
//    ObjectMapper mapper=SystemMapper.getMapper();
//    String json="[{\"gid\":\"22b472ed91fb47ec9f3824efc78a06e0\",\"uid\":\"+72212204429\",\"role\":0,\"create_time\":1629182934413,\"inviter\":\"+72212204429\",\"displayName\":\"aaa\",\"remark\":\"\",\"notification\":1},{\"gid\":\"22b472ed91fb47ec9f3824efc78a06e0\",\"uid\":\"+71497434439\",\"role\":1,\"create_time\":1629208008751,\"inviter\":\"+72212204429\",\"displayName\":\"\",\"remark\":\"\",\"notification\":0},{\"gid\":\"22b472ed91fb47ec9f3824efc78a06e0\",\"uid\":\"+77245523887\",\"role\":2,\"create_time\":1629208088772,\"inviter\":\"+72212204429\",\"displayName\":\"\",\"remark\":\"\",\"notification\":0}]\n";
//    JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, GroupMember.class);
//    try {
//      List<GroupMember> groupMembers=(List<GroupMember>)mapper.readValue(json, javaType);
//      System.out.println(groupMembers.get(0).getGid());
//      throw new RuntimeException("aaa");
//    } catch (Exception e) {
//      e.printStackTrace();
//      System.out.println(e.getMessage());
//    }
//  }
  static public class UnavailableAccount{
    String uid;
    String reason;

    public UnavailableAccount(String uid, String reason) {
      this.uid = uid;
      this.reason = reason;
    }
  }

  public static void main(String[] args) {
    List<String> strings = new ArrayList<>();
    strings.add("1");
    strings.add("2");
    strings.add("3");
    strings.add("4");
    strings.add("5");
    strings.add("6");
    strings.add("7");
    strings.add("8");
    strings.add("9");
    System.out.println(strings.subList(5, 9));
  }
}
