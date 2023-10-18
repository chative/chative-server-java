//package org.whispersystems.textsecuregcm.storage;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JavaType;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.skife.jdbi.v2.TransactionIsolationLevel;
//import org.skife.jdbi.v2.sqlobject.Transaction;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.whispersystems.textsecuregcm.entities.SetGroupAnnouncementRequest;
//import org.whispersystems.textsecuregcm.exceptions.*;
//import org.whispersystems.textsecuregcm.redis.ReplicatedJedisPool;
//import org.whispersystems.textsecuregcm.util.StringUtil;
//import org.whispersystems.textsecuregcm.util.SystemMapper;
//import redis.clients.jedis.Jedis;
//
//import java.io.IOException;
//import java.util.*;
////Change to GroupManagerWithTransaction and discard
//public class GroupManager {
//
//  private final Logger logger = LoggerFactory.getLogger(GroupManager.class);
//
//  private final GroupsTable groupsTable;
//  private final GroupMembersTable groupMembersTable;
//
//  private final GroupAnnouncementTable groupAnnouncementTable;
//
//  private final ReplicatedJedisPool cacheClient;
//
//  private final ObjectMapper mapper;
//
//  public GroupManager(GroupsTable groupsTable, GroupMembersTable groupMembersTable,GroupAnnouncementTable groupAnnouncementTable, ReplicatedJedisPool cacheClient) {
//    this.groupsTable = groupsTable;
//    this.groupMembersTable = groupMembersTable;
//    this.cacheClient = cacheClient;
//    this.mapper = SystemMapper.getMapper();
//    this.groupAnnouncementTable = groupAnnouncementTable;
//  }
//  public enum MemberChangeType {
//    BASIC,
//    SETADMIN,
//    DELADMIN
//  }
//
//  private Group create(Group group)
//      throws InvalidParameterException {
//
//    validateGroupParameters(group);
//    group = groupsTable.create(group);
//    memcacheSet(getGroupKey(group.getId()), group);
//    return group;
//  }
//
//  private Group set(Group group)
//      throws InvalidParameterException {
//
//    validateGroupParameters(group);
//
//    group=groupsTable.updateForVersion(group);
//
////    // Reload, some columns won't change
////    group = groupsTable.get(group.getId());;
//
//    memcacheSet(getGroupKey(group.getId()), group);
//
//    return group;
//  }
//
//  public Group addGroupVersion(Group group){
//    return groupsTable.updateForVersionOnlyVersion(group.getId());
//  }
//  public Group addGroupVersion(String gid){
//    return groupsTable.updateForVersionOnlyVersion(gid);
//  }
//  private Group get(String gid) {
//    Group group = (Group)memcacheGet(getGroupKey(gid), Group.class);
//    if (null == group) {
//      group = groupsTable.get(gid);
//      if (null != group) {
//        memcacheSet(getGroupKey(gid), group);
//      }
//    }
//    return group;
//  }
//
//  private List<Group> getAll() {
//    return groupsTable.getAll();
//  }
//
//  private boolean addMember(GroupMember member)
//      throws InvalidParameterException {
//
//    validateMemberParameters(member);
//    GroupMember groupMember=getMember(member.getGid(), member.getUid());
//    // Skip already exists ， update will reset his related settings
//    if (null != groupMember) {
//      setMember(member.getGid(), member);
//      memcacheSet(getMemberKey(member.getGid(), member.getUid()), member);
//
//      // Invalidate related caches
//      memcacheRemove(getGroupMembersKey(member.getGid()));
////
////      return member;
//      return true;
//    }
//
//    // Add
//    groupMembersTable.insert(member.getGid(), member.getUid(), member.getRole(), member.getInviter(), member.getDisplayName(), member.getRemark(), member.getNotification());
//    memcacheSet(getMemberKey(member.getGid(), member.getUid()), member);
//
//    // Invalidate related caches
//    memcacheRemove(getGroupMembersKey(member.getGid()));
//    memcacheRemove(getMemberGroupsKey(member.getUid()));
//
//    return true;
//  }
//
//  private void removeMember(String gid, String uid) {
//    groupMembersTable.delete(gid, uid);
//    memcacheRemove(getMemberKey(gid, uid));
//
//    // Invalidate related caches
//    memcacheRemove(getGroupMembersKey(gid));
//    memcacheRemove(getMemberGroupsKey(uid));
//  }
//
//  private void setMember(String gid, GroupMember member)
//      throws InvalidParameterException {
//
//    validateMemberParameters(member);
//    GroupMember groupMember=groupMembersTable.getGroupMember(gid,member.getUid());
//    if(groupMember!=null) {
//      groupMembersTable.update(member.getRole(), member.getDisplayName(), member.getRemark(), member.getNotification(), gid, member.getUid());
//    }else{
//      groupMembersTable.insert(member.getGid(), member.getUid(), member.getRole(), member.getInviter(), member.getDisplayName(), member.getRemark(), member.getNotification());
//    }
//    memcacheSet(getMemberKey(gid, member.getUid()), member);
//
//    // Invalidate related caches
//    memcacheRemove(getGroupMembersKey(gid));
//  }
//
//  private GroupMember getMember(String gid, String uid) {
//    GroupMember member = (GroupMember)memcacheGet(getMemberKey(gid, uid), GroupMember.class);
//    if (null == member) {
//      member = groupMembersTable.getGroupMember(gid, uid);
//      if (null != member) {
//        memcacheSet(getMemberKey(gid, uid), member);
//      }
//    }
//    return member;
//  }
//
//  private List<Group> getMemberGroups(String uid) {
//    JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, Group.class);
//    List<Group> groups = (List<Group>)memcacheGet(getMemberGroupsKey(uid), javaType);
//    if (null == groups) {
//      groups = new LinkedList<>();
//
//      for (GroupMember groupMember : groupMembersTable.getMemberGroups(uid)) {
//        Group group = get(groupMember.getGid());
//        groups.add(group);
//      }
//
//      memcacheSet(getMemberGroupsKey(uid), groups);
//    }
//    return groups;
//  }
//
//  // FIXME: May leave redundant data if the gid doesn't exists
//  public List<GroupMember> getGroupMembers(String gid) {
//    JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, GroupMember.class);
//    List<GroupMember> members = (List<GroupMember>)memcacheGet(getGroupMembersKey(gid), javaType);
//    if (null == members) {
//      members = groupMembersTable.getGroupMembers(gid);
//      memcacheSet(getGroupMembersKey(gid), members);
//    }
//    return members;
//  }
//
//  private Group checkGroupStatus(String gid, GroupsTable.STATUS expectedMostStatus)
//      throws NoPermissionException, NoSuchGroupException {
//    Group group = get(gid);
//    if (null == group) {
//      throw new NoSuchGroupException("No such group: " + gid);
//    }
//
//    if (group.getStatus() > expectedMostStatus.ordinal()) {
//      throw new NoPermissionException("You don't have permission. The group status is: " + group.getStatus());
//    }
//
//    return group;
//  }
//
//  private GroupMembersTable.ROLE checkRole(Group group, String uid, GroupMembersTable.ROLE expectedLeastRole)
//      throws NoPermissionException {
//    List<GroupMember> members = getGroupMembers(group.getId());
//    for (GroupMember member : members) {
//      if (member.getUid().equals(uid)) {
//        if (member.getRole() <= expectedLeastRole.ordinal()) {
//          return GroupMembersTable.ROLE.fromOrdinal(member.getRole());
//        }
//        throw new NoPermissionException("You don't have permission. You role is: " + member.getRole());
//      }
//    }
//
//    throw new NoPermissionException("You don't have permission.");
//  }
//
//  @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
//  public Group createGroupWithPermissionCheck(Account operator, Group group)
//      throws NoPermissionException, InvalidParameterException {
//
//    group = create(group);
//    addMember(new GroupMember(group.getId(), operator.getNumber(), GroupMembersTable.ROLE.OWNER.ordinal(), System.currentTimeMillis(), operator.getNumber(), "", "", GroupMembersTable.NOTIFICATION.ALL.ordinal()));
//    return group;
//  }
//
//  public Group setGroupWithPermissionCheck(Account operator, Group group)
//      throws NoPermissionException, NoSuchGroupException, InvalidParameterException {
//
//    Group currentGroup = checkGroupStatus(group.getId(), GroupsTable.STATUS.ACTIVE);
//
//    // Check if the operator is the owner of the group
//    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.OWNER);
//
//
//    if (group.getStatus() == GroupsTable.STATUS.DISMISSED.ordinal()) {
//      // Only status
//      group = currentGroup;
//      group.setStatus(GroupsTable.STATUS.DISMISSED.ordinal());
//    } else if (group.getStatus() == GroupsTable.STATUS.ACTIVE.ordinal()) {
//      // Change settings
//    } else {
//      throw new NoPermissionException("You don't have permission to change status of the group: " + group.getId());
//    }
//
//    return set(group);
//  }
//
//  public Group getGroupWithPermissionCheck(Account operator, String gid)
//      throws NoPermissionException, NoSuchGroupException {
//
//    Group group = checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);
//
//    // Check if the operator is in the group
//    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);
//
//    return group;
//  }
//  public Group getGroup(String gid) {
//    try {
//      return checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);
//    } catch (NoPermissionException e) {
//      e.printStackTrace();
//    } catch (NoSuchGroupException e) {
//      e.printStackTrace();
//    }
//    return null;
//  }
//
//  public GroupMember addMemberWithPermissionCheck(Account operator, Group group, Account operatee)
//      throws NoPermissionException, NoSuchGroupException, InvalidParameterException {
//
//    group = checkGroupStatus(group.getId(), GroupsTable.STATUS.ACTIVE);
//
//    // Check if the operator is in the group
//    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);
//    GroupMember groupMember=new GroupMember(group.getId(), operatee.getNumber(), GroupMembersTable.ROLE.MEMBER.ordinal(), System.currentTimeMillis(), operator.getNumber(), "", "", GroupMembersTable.NOTIFICATION.ALL.ordinal());
//    boolean isAdd=addMember(groupMember);
//    if(isAdd) {
//      return groupMember;
//    }else{
//      return null;
//    }
//  }
//
//  public GroupMember removeMemberWithPermissionCheck(Account operator, Group group, Account operatee)
//          throws NoPermissionException, NoSuchGroupException, NoSuchGroupMemberException {
//
//    group = checkGroupStatus(group.getId(), GroupsTable.STATUS.ACTIVE);
//
//    // Check if the operator's role is greater than the operatee
//    GroupMember operatorMember = getMember(group.getId(), operator.getNumber());
//    GroupMember targetMember = getMember(group.getId(), operatee.getNumber());
//    if(targetMember==null){
//      throw new NoSuchGroupMemberException("No such member:"+operatee.getNumber());
//    }
//    //leave
//    if(operatorMember.getUid().equals(targetMember.getUid())){
//      if(operatorMember.getRole()==GroupMembersTable.ROLE.OWNER.ordinal()){
//        throw new NoPermissionException("You must transfer the group to someone else to quit or dissolve the group！");
//      }
//    }else{
//      if (operatorMember.getRole() >= targetMember.getRole()) {
//        throw new NoPermissionException("You don't have permission to remove member " + targetMember.getUid() + " from group " + group.getId());
//      }
//    }
//
//
//    removeMember(group.getId(), operatee.getNumber());
//    return targetMember;
//  }
//
//  public int setMemberSettingsWithPermissionCheck(Account operator, Group group, GroupMember member)
//      throws NoPermissionException, NoSuchGroupException, InvalidParameterException {
//
//    int changeType;
//    group = checkGroupStatus(group.getId(), GroupsTable.STATUS.ACTIVE);
//
//    GroupMember memberCurrent = getMember(group.getId(), member.getUid());
//
//    if (memberCurrent.getRole() == member.getRole()) {
//      changeType=MemberChangeType.BASIC.ordinal();
//      // Change settings
//      // Users can only change their own settings
//      if (!operator.getNumber().equals(member.getUid())) {
//        throw new NoPermissionException("You don't have permission to change settings of member " + member.getUid());
//      }
//    } else {
//      // Change role
//      // Only group owner can change other's role
//      checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.OWNER);
//
//      //Owner can not change his role directly
//      if(operator.getNumber().equals(member.getUid())){
//        if(member.getRole()!=GroupMembersTable.ROLE.OWNER.ordinal()){
//          throw new NoPermissionException("You are owner ,You cannot change your role directly，you can transfer the group to someone！");
//        }
//      }
//      // Owner can only add or remove admins
//      if (member.getRole() == GroupMembersTable.ROLE.OWNER.ordinal()) {
//        throw new NoPermissionException("You don't have permission to set another owner");
//      }
//      if(member.getRole()<GroupMembersTable.ROLE.MEMBER.ordinal()) {
//        changeType = MemberChangeType.SETADMIN.ordinal();
//      }else{
//        changeType = MemberChangeType.DELADMIN.ordinal();
//      }
//      // Only change role
//      int role = member.getRole();
//      member = memberCurrent;
//      member.setRole(role);
//    }
//
//    setMember(group.getId(), member);
//    return changeType;
//  }
//
//  public GroupMember getMemberWithPermissionCheck(Account operator, Group group, Account operatee)
//      throws NoPermissionException, NoSuchGroupException, NoSuchGroupMemberException {
//
//    group = checkGroupStatus(group.getId(), GroupsTable.STATUS.ACTIVE);
//
//    if(operator.getNumber().equals(operatee.getNumber())) {
//      //change self Check if the operator is in the group
//      checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);
//    }
//
//    GroupMember member = getMember(group.getId(), operatee.getNumber());
//    if (null == member) {
//      throw new NoSuchGroupMemberException("No such group member: " + group.getId() + "." + operatee.getNumber());
//    }
//
//    return member;
//  }
//
//  public List<Group> getMemberGroupsWithPermissionCheck(Account operator)
//      throws NoPermissionException {
//    JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, Group.class);
//    List<Group> groups = (List<Group>) memcacheGet(getMemberGroupsKey(operator.getNumber()), javaType);
//    if (null == groups) {
//      groups = new LinkedList<>();
//
//      List<GroupMember> memberGroups = groupMembersTable.getMemberGroups(operator.getNumber());
//      for (GroupMember groupMember : memberGroups) {
//        Group group = get(groupMember.getGid());
//        if (null == group) {
//          logger.error("getMemberGroups", "group not found");
//          continue;
//        }
//
//        // Check if is active
//        if (group.getStatus() != GroupsTable.STATUS.ACTIVE.ordinal()) {
//          continue;
//        }
//
//        groups.add(group);
//      }
//
//      memcacheSet(getMemberGroupsKey(operator.getNumber()), groups);
//    }
//    return groups;
//  }
//
//  public List<GroupMember> getGroupMembersWithPermissionCheck(Account operator, Group group)
//      throws NoPermissionException, NoSuchGroupException {
//
//    group = checkGroupStatus(group.getId(), GroupsTable.STATUS.ACTIVE);
//
//    // Check if the operator is in the group
//    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);
//    JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, GroupMember.class);
//    List<GroupMember> members = (List<GroupMember>) memcacheGet(getGroupMembersKey(group.getId()), javaType);
//    if (null == members) {
//      members = getGroupMembers(group.getId());
//
//      memcacheSet(getGroupMembersKey(group.getId()), members);
//    }
//
//    return members;
//  }
//
//  public GroupMember getMemberWithPermissionCheck(Account operator, String gid,String uid)
//          throws NoPermissionException, NoSuchGroupException {
//
//    Group group = checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);
//
//    // Check if the operator is in the group
//    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);
//    GroupMember member = getMember(gid,uid);
//    return member;
//  }
//
//  public void transferGroupWithPermissionCheck(Account operator, Group group, Account operatee)
//          throws NoPermissionException, NoSuchGroupException, InvalidParameterException, NoSuchGroupMemberException {
//
//    group = checkGroupStatus(group.getId(), GroupsTable.STATUS.ACTIVE);
//
//    // Check if the operator is the owner of the group
//    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.OWNER);
//    GroupMember newOwner = getMember(group.getId(), operatee.getNumber());
//    if(newOwner==null){
//      throw new NoSuchGroupMemberException("No such member:"+operatee.getNumber());
//    }
//    GroupMember currentOwner = getMember(group.getId(), operator.getNumber());
//    currentOwner.setRole(GroupMembersTable.ROLE.ADMIN.ordinal());
//    setMember(group.getId(), currentOwner);
//
//    newOwner.setRole(GroupMembersTable.ROLE.OWNER.ordinal());
//    setMember(group.getId(), newOwner);
//  }
//
//  // TODO: notify client
//  private void notify(Group group, String message) {
//
//  }
//
//  public GroupAnnouncement  addAnnouncementPermissionCheck(Account operator, String gid, SetGroupAnnouncementRequest request) throws NoPermissionException, NoSuchGroupException, InvalidParameterException {
//    validateGroupAnnouncementParameters(request);
//    Group group = checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);
//    // Check if the operator is the admin/owner of the group
//    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.ADMIN);
//    GroupAnnouncement groupAnnouncement=new GroupAnnouncement();
//    groupAnnouncement.setGid(gid);
//    groupAnnouncement.setCreator(operator.getNumber());
//    groupAnnouncement.setReviser(operator.getNumber());
//    groupAnnouncement.setAnnouncementExpiry(request.getAnnouncementExpiry());
//    groupAnnouncement.setContent(request.getContent());
//    groupAnnouncement=groupAnnouncementTable.create(groupAnnouncement);
//    memcacheHset(getGroupAnnouncementKey(gid),getGroupAnnouncementHashKey(gid,groupAnnouncement.getId()), groupAnnouncement);
//    return groupAnnouncement;
//  }
//
//  public GroupAnnouncement  setAnnouncementPermissionCheck(Account operator, String gid, String gaid,SetGroupAnnouncementRequest request) throws NoPermissionException, NoSuchGroupException, InvalidParameterException, NoSuchGroupAnnouncementException {
//    validateGroupAnnouncementParameters(request);
//    Group group = checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);
//    // Check if the operator is the admin/owner of the group
//    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.ADMIN);
//    checkGroupAnnouncementStatus(gid,gaid,GroupAnnouncementTable.STATUS.ACTIVE);
//    groupAnnouncementTable.update(request.getAnnouncementExpiry(),request.getContent(),operator.getNumber(),System.currentTimeMillis(),gaid);
//    memcacheHdel(getGroupAnnouncementKey(gid),getGroupAnnouncementHashKey(gid,gaid));
//    return getGroupAnnouncement(gid,gaid);
//  }
//
//  public void  delAnnouncementPermissionCheck(Account operator, String gid, String gaid) throws NoPermissionException, NoSuchGroupException, NoSuchGroupAnnouncementException {
//    Group group = checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);
//    // Check if the operator is the admin/owner of the group
//    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.ADMIN);
//    checkGroupAnnouncementStatus(gid,gaid,GroupAnnouncementTable.STATUS.ACTIVE);
//    groupAnnouncementTable.updateStatus(GroupAnnouncementTable.STATUS.DISMISSED.ordinal(), gaid);
//    memcacheHdel(getGroupAnnouncementKey(gid),getGroupAnnouncementHashKey(gid,gaid));
//  }
//
//  public List<GroupAnnouncement>  getGroupAnnouncementPermissionCheck(Account operator, String gid) throws NoPermissionException, NoSuchGroupException, InvalidParameterException, NoSuchGroupAnnouncementException {
//    Group group = checkGroupStatus(gid, GroupsTable.STATUS.ACTIVE);
//    // Check if the operator is the admin/owner of the group
//    checkRole(group, operator.getNumber(), GroupMembersTable.ROLE.MEMBER);
//    return getGroupAnnouncement(gid);
//  }
//
//  private void  checkGroupAnnouncementStatus(String gid,String gaid, GroupAnnouncementTable.STATUS expectedMostStatus)
//          throws NoPermissionException,NoSuchGroupAnnouncementException{
//    GroupAnnouncement groupAnnouncement = getGroupAnnouncement(gid,gaid);
//    if (null == groupAnnouncement) {
//      throw new NoSuchGroupAnnouncementException("No such groupAnnouncement: " + gaid);
//    }
//
//    if (groupAnnouncement.getStatus() > expectedMostStatus.ordinal()) {
//      throw new NoPermissionException("You don't have permission. The groupAnnouncement status is: " + groupAnnouncement.getStatus());
//    }
//  }
//
//  private GroupAnnouncement getGroupAnnouncement(String gid,String gaid) {
//    GroupAnnouncement groupAnnouncement = (GroupAnnouncement)memcacheHget(getGroupAnnouncementKey(gid),getGroupAnnouncementHashKey(gid,gaid), GroupAnnouncement.class);
//    if(groupAnnouncement!=null) {
//      boolean isExpiry = checkGroupAnnouncementIsExpiry(groupAnnouncement);
//      if (isExpiry) {
//        groupAnnouncement.setStatus(GroupAnnouncementTable.STATUS.EXPIRED.ordinal());
//        memcacheHdel(getGroupAnnouncementKey(gid), getGroupAnnouncementHashKey(gid, gaid));
//        groupAnnouncementTable.updateStatus(GroupAnnouncementTable.STATUS.EXPIRED.ordinal(), gaid);
//        return groupAnnouncement;
//      }
//    }else{
//      groupAnnouncement = groupAnnouncementTable.get(gaid);
//      if(null != groupAnnouncement){
//        boolean isExpiry = checkGroupAnnouncementIsExpiry(groupAnnouncement);
//        if(!isExpiry&&groupAnnouncement.getStatus()==GroupAnnouncementTable.STATUS.ACTIVE.ordinal()){
//          memcacheHset(getGroupAnnouncementKey(gid),getGroupAnnouncementHashKey(gid,gaid), groupAnnouncement);
//        }else if(isExpiry&&groupAnnouncement.getStatus()==GroupAnnouncementTable.STATUS.ACTIVE.ordinal()){
//          groupAnnouncementTable.updateStatus(GroupAnnouncementTable.STATUS.EXPIRED.ordinal(), gaid);
//        }
//      }
//    }
//    return groupAnnouncement;
//  }
//
//  private List<GroupAnnouncement> getGroupAnnouncement(String gid) {
//    Map<String,Object> objectMap= memcacheHgetAll(getGroupAnnouncementKey(gid), GroupAnnouncement.class);
//    List<GroupAnnouncement> acitveGroupAnnouncements=new ArrayList<GroupAnnouncement>();
//    if(objectMap!=null) {
//      for(String hashKey:objectMap.keySet()) {
//        GroupAnnouncement groupAnnouncement=(GroupAnnouncement)objectMap.get(hashKey);
//        boolean isExpiry = checkGroupAnnouncementIsExpiry(groupAnnouncement);
//        if (isExpiry) {
//          groupAnnouncement.setStatus(GroupAnnouncementTable.STATUS.EXPIRED.ordinal());
//          memcacheHdel(getGroupAnnouncementKey(gid), getGroupAnnouncementHashKey(gid, groupAnnouncement.getId()));
//          groupAnnouncementTable.updateStatus(GroupAnnouncementTable.STATUS.EXPIRED.ordinal(), groupAnnouncement.getId());
//        }else{
//          acitveGroupAnnouncements.add(groupAnnouncement);
//        }
//      }
//    }else{
//      List<GroupAnnouncement> groupAnnouncements = groupAnnouncementTable.getByGid(gid);
//      if(null != groupAnnouncements&&groupAnnouncements.size()>0){
//        for(GroupAnnouncement groupAnnouncement:groupAnnouncements) {
//          boolean isExpiry = checkGroupAnnouncementIsExpiry(groupAnnouncement);
//          if (!isExpiry && groupAnnouncement.getStatus() == GroupAnnouncementTable.STATUS.ACTIVE.ordinal()) {
//            memcacheHset(getGroupAnnouncementKey(gid), getGroupAnnouncementHashKey(gid, groupAnnouncement.getId()), groupAnnouncement);
//            acitveGroupAnnouncements.add(groupAnnouncement);
//          } else if (isExpiry && groupAnnouncement.getStatus() == GroupAnnouncementTable.STATUS.ACTIVE.ordinal()) {
//            groupAnnouncementTable.updateStatus(GroupAnnouncementTable.STATUS.EXPIRED.ordinal(), groupAnnouncement.getId());
//          }
//        }
//      }
//    }
//    return acitveGroupAnnouncements;
//  }
//
//  private boolean checkGroupAnnouncementIsExpiry(GroupAnnouncement groupAnnouncement){
//    if(groupAnnouncement.getAnnouncementExpiry()==0){
//      return false;
//    }
//    long expiryTime=groupAnnouncement.getReviseTime()+groupAnnouncement.getAnnouncementExpiry()*1000;
//    if(System.currentTimeMillis()>expiryTime){
//      return true;
//    }
//    return false;
//  }
//
//  private void validateGroupAnnouncementParameters(SetGroupAnnouncementRequest request)
//          throws InvalidParameterException {
//    validateGroupAnnouncementContent(request.getContent());
//    validateGroupAnnouncementExpiry(request.getAnnouncementExpiry());
//
//  }
//
//  private void validateGroupAnnouncementContent(String content)
//          throws InvalidParameterException {
//    if (StringUtil.isEmpty(content) ||content.length() > 4096) {
//      throw new InvalidParameterException("Invalid groupAnnouncement content: " + content);
//    }
//  }
//
//  private void validateGroupAnnouncementExpiry(long groupAnnouncementExpiry)
//          throws InvalidParameterException {
//    if (groupAnnouncementExpiry==0||groupAnnouncementExpiry > 60 * 60 * 24 * 30) {
//      throw new InvalidParameterException("Invalid groupAnnouncement expiry : " + groupAnnouncementExpiry);
//    }
//  }
//
//  private void validateGroupParameters(Group group)
//    throws InvalidParameterException {
//    validateGroupName(group.getName());
//    validateGroupStatus(group.getStatus());
//    validateGroupMessageExpiry(group.getMessageExpiry());
//    validateGroupAvatar(group.getAvatar());
//  }
//
//  private void validateGroupName(String name)
//      throws InvalidParameterException {
//    if (StringUtil.isEmpty(name)||name.length() > 64) {
//      throw new InvalidParameterException("Invalid group name: " + name);
//    }
//  }
//
//  private void validateGroupStatus(int status)
//      throws InvalidParameterException {
//    if (status >= GroupsTable.STATUS.values().length) {
//      throw new InvalidParameterException("Invalid group status: " + status);
//    }
//  }
//
//  private void validateGroupMessageExpiry(long messageExpiry)
//      throws InvalidParameterException {
//    if (messageExpiry > 60 * 60 * 24 * 7) {
//      throw new InvalidParameterException("Group message expiry too long: " + messageExpiry);
//    }
//  }
//
//  private void validateGroupAvatar(String avatar)
//      throws InvalidParameterException {
//    if (avatar!=null&&avatar.length() > 4096) {
//      throw new InvalidParameterException("Group avatar too long: " + avatar);
//    }
//  }
//
//
//  private void validateMemberParameters(GroupMember member)
//      throws InvalidParameterException {
//    validateMemberRole(member.getRole());
//    validateMemberDisplayName(member.getDisplayName());
//    validateMemberRemark(member.getRemark());
//    validateMemberNotification(member.getNotification());
//  }
//
//  private void validateMemberRole(int role)
//      throws InvalidParameterException {
//    if (role >= GroupMembersTable.ROLE.values().length) {
//      throw new InvalidParameterException("Invalid member role: " + role);
//    }
//  }
//
//  private void validateMemberDisplayName(String displayName)
//      throws InvalidParameterException {
//    if (displayName.length() > 64) {
//      throw new InvalidParameterException("Member display name too long: " + displayName);
//    }
//  }
//
//  private void validateMemberRemark(String remark)
//      throws InvalidParameterException {
//    if (remark.length() > 64) {
//      throw new InvalidParameterException("Member remark too long: " + remark);
//    }
//  }
//
//  private void validateMemberNotification(int notification)
//      throws InvalidParameterException {
//    if (notification >= GroupMembersTable.NOTIFICATION.values().length) {
//      throw new InvalidParameterException("Invalid notification: " + notification);
//    }
//  }
//
//  private String getGroupKey(String gid) {
//    return String.join("_", Group.class.getSimpleName(), "Group", String.valueOf(Group.MEMCACHE_VERION), gid);
//  }
//
//  private String getGroupAnnouncementKey(String gid) {
//    return String.join("_", Group.class.getSimpleName(), "GroupAnnouncement", String.valueOf(GroupAnnouncement.MEMCACHE_VERION), gid);
//  }
//
//  private String getGroupAnnouncementHashKey(String gid,String gaid) {
//    return String.join("_", GroupAnnouncement.class.getSimpleName(), "GroupAnnouncement", String.valueOf(GroupAnnouncement.MEMCACHE_VERION), gid,gaid);
//  }
//
//  private String getMemberKey(String gid, String uid) {
//    return String.join("_", Group.class.getSimpleName(), "Member", String.valueOf(Group.MEMCACHE_VERION), gid, uid);
//  }
//
//  private String getGroupMembersKey(String gid) {
//    return String.join("_", Group.class.getSimpleName(), "GroupMembers", String.valueOf(Group.MEMCACHE_VERION), gid);
//  }
//
//  private String getMemberGroupsKey(String uid) {
//    return String.join("_",Group.class.getSimpleName(), "MemberGroups", String.valueOf(Group.MEMCACHE_VERION), uid);
//  }
//
//  private void memcacheSet(String key, Object clazz) {
//    try (Jedis jedis = cacheClient.getWriteResource()) {
//      jedis.set(key, mapper.writeValueAsString(clazz));
//    } catch (JsonProcessingException e) {
//      throw new IllegalArgumentException(e);
//    }
//  }
//
//  private Object memcacheGet(String key, Class clazz) {
//    try (Jedis jedis = cacheClient.getWriteResource()) {
//      String json = jedis.get(key);
//      if (json != null) {
//        return mapper.readValue(json, clazz);
//      }
//    } catch (IOException e) {
//      logger.error("GroupManager", "Deserialization error", e);
//    }
//    return null;
//  }
//  private Object memcacheGet(String key, JavaType javaType) {
//    try (Jedis jedis = cacheClient.getWriteResource()) {
//      String json = jedis.get(key);
//      if (json != null) {
//        return mapper.readValue(json, javaType);
//      }
//    } catch (IOException e) {
//      logger.error("GroupManager", "Deserialization error", e);
//    }
//    return null;
//  }
//
//  private Long memcacheRemove(String key) {
//    try (Jedis jedis = cacheClient.getReadResource()) {
//      return jedis.del(key);
//    }
//  }
//
//  private void memcacheHset(String key,String hashKey, Object clazz) {
//    try (Jedis jedis = cacheClient.getWriteResource()) {
//      jedis.hset(key,hashKey, mapper.writeValueAsString(clazz));
//    } catch (JsonProcessingException e) {
//      throw new IllegalArgumentException(e);
//    }
//  }
//
//  private Object memcacheHget(String key,String hashKey, Class clazz) {
//    try (Jedis jedis = cacheClient.getWriteResource()) {
//      String json = jedis.hget(key,hashKey);
//      if (json != null) {
//        return mapper.readValue(json, clazz);
//      }
//    } catch (IOException e) {
//      logger.error("GroupManager", "Deserialization error", e);
//    }
//    return null;
//  }
//
//  private Long memcacheHdel(String key,String hashKey) {
//    try (Jedis jedis = cacheClient.getReadResource()) {
//      return jedis.hdel(key,hashKey);
//    }
//  }
//
//  private  Map<String,Object> memcacheHgetAll(String key, Class clazz) {
//    try (Jedis jedis = cacheClient.getWriteResource()) {
//      Map<String, String> map = jedis.hgetAll(key);
//      if (map != null&&map.size()>0) {
//        Map<String,Object> mapObj=new HashMap<String,Object>();
//        for(String hashKey:map.keySet()){
//          String json=map.get(hashKey);
//          mapObj.put(hashKey,mapper.readValue(json, clazz));
//        }
//        return mapObj;
//      }
//    } catch (IOException e) {
//      logger.error("GroupManager", "Deserialization error", e);
//    }
//    return null;
//  }
//
//  public static void main(String[] args) {
//    ObjectMapper mapper=SystemMapper.getMapper();
//    String json="[{\"gid\":\"22b472ed91fb47ec9f3824efc78a06e0\",\"uid\":\"+72212204429\",\"role\":0,\"create_time\":1629182934413,\"inviter\":\"+72212204429\",\"displayName\":\"aaa\",\"remark\":\"\",\"notification\":1},{\"gid\":\"22b472ed91fb47ec9f3824efc78a06e0\",\"uid\":\"+71497434439\",\"role\":1,\"create_time\":1629208008751,\"inviter\":\"+72212204429\",\"displayName\":\"\",\"remark\":\"\",\"notification\":0},{\"gid\":\"22b472ed91fb47ec9f3824efc78a06e0\",\"uid\":\"+77245523887\",\"role\":2,\"create_time\":1629208088772,\"inviter\":\"+72212204429\",\"displayName\":\"\",\"remark\":\"\",\"notification\":0}]\n";
//    JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, GroupMember.class);
//    try {
//      List<GroupMember> groupMembers=(List<GroupMember>)mapper.readValue(json, javaType);
//      System.out.println(groupMembers.get(0).getGid());
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }
//}
