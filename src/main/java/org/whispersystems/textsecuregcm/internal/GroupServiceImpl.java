package org.whispersystems.textsecuregcm.internal;


import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.GroupNotify;
import org.whispersystems.textsecuregcm.exceptions.*;
import org.whispersystems.textsecuregcm.internal.accounts.UidsRequest;
import org.whispersystems.textsecuregcm.internal.common.BaseAnyResponse;
import org.whispersystems.textsecuregcm.internal.common.BaseResponse;
import org.whispersystems.textsecuregcm.internal.common.STATUS;
import org.whispersystems.textsecuregcm.internal.common.Step;
import org.whispersystems.textsecuregcm.internal.groups.*;
import org.whispersystems.textsecuregcm.s3.UrlSignerAli;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.AvatarEnc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GroupServiceImpl extends GroupServiceGrpc.GroupServiceImplBase {

    final private Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);

    final private AccountsManager accountsManager;
    final private GroupManagerWithTransaction groupManagerWithTransaction;
    final private UrlSignerAli urlSignerAli;

    public GroupServiceImpl(UrlSignerAli urlSignerAli, AccountsManager accountsManager, GroupManagerWithTransaction groupManagerWithTransaction) {
        this.urlSignerAli = urlSignerAli;
        this.accountsManager = accountsManager;
        this.groupManagerWithTransaction=groupManagerWithTransaction;
    }

    @Override
    public void kickoutAllGroupForUser(UidsRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder resBuilder = BaseResponse.newBuilder();
        resBuilder.setVer(1);
        List<String> uids = request.getUidsList();
        if(uids!=null&&uids.size()>0){
            KickoutResponse.Builder kickoutResponseBuilder=KickoutResponse.newBuilder();
            for (String uid:uids){
                Optional<Account> accountOptional=accountsManager.get(uid);
                KickoutResponse.ResultInfo.Builder resultInfo=KickoutResponse.ResultInfo.newBuilder();
                resultInfo.setNumber(uid);
                if(accountOptional.isPresent()&&!accountOptional.get().isinValid()){
                    try {
                        groupManagerWithTransaction.kickoutAllGroupForUser(accountOptional.get());
                        resultInfo.setResult("success!");
                    }catch (Exception e){
                        resultInfo.setResult("error! msg:"+e.getMessage());
                    }
                }else{
                    resultInfo.setResult("account not exists or not disable!");
                }
                kickoutResponseBuilder.addResultInfos(resultInfo.build());
            }
            resBuilder.setStatus(STATUS.OK_VALUE);
            resBuilder.setReason(kickoutResponseBuilder.build().toString());
            resBuilder.setData(Any.pack(kickoutResponseBuilder.build()));
        }else{
            resBuilder.setStatus(STATUS.OTHER_ERROR_VALUE);
            resBuilder.setReason("param error!");
        }
        responseObserver.onNext(resBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getAll(GroupsRequest request, StreamObserver<GroupsResponse> responseObserver) {
        GroupsResponse.Builder response = GroupsResponse.newBuilder();

        Step step = request.getStep();

        List<Group> groupsList = groupManagerWithTransaction.getGroupsList(step.getName(), step.getOffset(), step.getLength());
        long total = groupManagerWithTransaction.getGroupsTotal(step.getName());
        response.setTotal(total);

        if(!groupsList.isEmpty()) {
            for(Group group : groupsList){
                GroupsInfo.Builder groupsInfo = GroupsInfo.newBuilder();
                groupsInfo.setId(group.getId());
                groupsInfo.setName(group.getName());
                if(StringUtils.isNotBlank(group.getAvatar())){
                    groupsInfo.setAvatar(group.getAvatar());
                }
                groupsInfo.setStatus(group.getStatus());
                groupsInfo.setCreator(group.getCreator());
                groupsInfo.setCreateTime(group.getCreateTime());
                groupsInfo.setInvitationRule(group.getInvitationRule());
                groupsInfo.setLastActiveTime(group.getLastActiveTime());
                groupsInfo.setMessageExpiry(group.getMessageExpiry());
                response.addGroupsInfo(groupsInfo.build());
            }
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void create(GroupsCreateRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder builder = BaseResponse.newBuilder();
        builder.setVer(1);
        GroupsInfo groupsInfo = request.getGroupsInfo();
        Account account = new Account();
        account.setName(groupsInfo.getCreator());
        boolean internal=false;
        Group group = new Group(groupsInfo.getName(), groupsInfo.getCreator(), GroupsTable.STATUS.ACTIVE.ordinal(), groupsInfo.getMessageExpiry(), groupsInfo.getAvatar(),groupsInfo.getInvitationRule(), GroupsTable.RemindCycle.NONE.getValue(),internal,internal,false,GroupMembersTable.ROLE.MEMBER.ordinal());
        List<GroupMember> groupMembers=new ArrayList<GroupMember>();
        List<Account> accounts = new ArrayList<>();
        try {
            Group groupWithPermissionCheck = groupManagerWithTransaction.createGroupWithPermissionCheck(account, group, accounts, groupMembers,null);
            builder.setStatus(STATUS.OK_VALUE);

            BaseAnyResponse.Builder base = BaseAnyResponse.newBuilder();
            base.setValue(groupWithPermissionCheck.getId());
            builder.setData(Any.pack(base.build()));
        } catch (Exception e){
            builder.setStatus(STATUS.SERVER_INTERNAL_ERROR_VALUE);
            builder.setReason(e.getMessage());
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void edit(GroupsRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder builder = BaseResponse.newBuilder();
        builder.setVer(1);
        GroupsInfo groupsInfo = request.getGroupInfo();

        Account account = new Account();
        List<GroupMember> groupMembersList = groupManagerWithTransaction.getGroupMembers(groupsInfo.getId());
        for(GroupMember groupMember : groupMembersList){
            if(groupMember.getRole() == GroupMembersTable.ROLE.OWNER.ordinal()){
                account = accountsManager.get(groupMember.getUid()).get();
                break;
            }
        }
        if(StringUtils.isEmpty(account.getNumber())){
            builder.setStatus(STATUS.NO_SUCH_USER_VALUE);
            builder.setReason("no group owner");
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
            return;
        }
        if(groupsInfo != null) {
            Device device = new Device();
            device.setId(0);
            account.setAuthenticatedDevice(device);
            Group group =  groupManagerWithTransaction.getGroupWithPermissionCheck(account, groupsInfo.getId());
            int changeType = GroupNotify.ChangeType.BASIC.ordinal();
            int display = GroupNotify.Display.NO.ordinal();
            int groupMemeberAction = -1;
            int groupActionType = GroupNotify.ActionType.UPDATE.ordinal();
            int notifyDetailedType = 0;
            Object operatee = null;
            try {
                // Get current owner
                GroupMember owner = null;
                for (GroupMember member : groupManagerWithTransaction.getGroupMembersWithPermissionCheck(account, group)) {
                    if (member.getRole() == GroupMembersTable.ROLE.OWNER.ordinal()) {
                        owner = member;
                        break;
                    }
                }

                if (StringUtils.isNotBlank(groupsInfo.getOwner()) && !owner.getUid().equals(groupsInfo.getOwner())) {
                    groupActionType = GroupNotify.ActionType.NONE.ordinal();
                    // Transfer group
                    Optional<Account> newOnwer = accountsManager.get(groupsInfo.getOwner());
                    if (newOnwer.isPresent()) {
                        group = groupManagerWithTransaction.transferGroupWithPermissionCheck(account, group, newOnwer.get());
                        groupManagerWithTransaction.clearGroupMembersCache(group.getId());
                        changeType = GroupNotify.ChangeType.PERSONNEL.ordinal();
                        display = GroupNotify.Display.YES.ordinal();
                        notifyDetailedType = GroupNotify.GroupNotifyDetailedType.GROUP_CHANGE_OWNER.ordinal();
                        operatee = newOnwer.get().getNumber();
                        groupMemeberAction = GroupNotify.ActionType.UPDATE.ordinal();
                    }
                } else {
                    // Change settings
                    boolean isChange = groupInfoIsChange(group, groupsInfo);
                    if (isChange) {
                        changeType = GroupNotify.ChangeType.BASIC.ordinal();
                        if (StringUtils.isNotBlank(groupsInfo.getName()) && !group.getName().equals(groupsInfo.getName())) {
                            display = GroupNotify.Display.YES.ordinal();
                            notifyDetailedType = GroupNotify.GroupNotifyDetailedType.GROUP_NAME_CHANGE.ordinal();
                            operatee = groupsInfo.getName();
                            group.setName(groupsInfo.getName());
                        } else if (StringUtils.isNotBlank(groupsInfo.getAvatar()) && !groupsInfo.getAvatar().equals(group.getAvatar())) {
                            display = GroupNotify.Display.YES.ordinal();
                            notifyDetailedType = GroupNotify.GroupNotifyDetailedType.GROUP_AVATAR_CHANGE.ordinal();
                            group.setAvatar(groupsInfo.getAvatar());
                        } else if (groupsInfo.getMessageExpiry() != 0 && group.getMessageExpiry() != groupsInfo.getMessageExpiry()) {
                            display = GroupNotify.Display.NO.ordinal();
                            notifyDetailedType = GroupNotify.GroupNotifyDetailedType.GROUP_MSG_EXPIRY_CHANGE.ordinal();
                            group.setMessageExpiry(groupsInfo.getMessageExpiry());
                        } else if(groupsInfo.hasInvitationRule()&&group.getInvitationRule()!=groupsInfo.getInvitationRule()&&GroupMembersTable.ROLE.fromOrdinal(groupsInfo.getInvitationRule())!=null){
                            display=GroupNotify.Display.NO.ordinal();
                            notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_INVITATION_RULE_CHANGE.ordinal();
                            group.setInvitationRule(groupsInfo.getInvitationRule());
                        }else if(groupsInfo.hasRemindCycle()&&StringUtils.isNotBlank(groupsInfo.getRemindCycle())&&!groupsInfo.getRemindCycle().equals(group.getRemindCycle())){
                            display=GroupNotify.Display.YES.ordinal();
                            notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_REMIND_CHANGE.ordinal();
                            group.setRemindCycle(groupsInfo.getRemindCycle());
                        }else {
                            display = GroupNotify.Display.NO.ordinal();
                            notifyDetailedType = GroupNotify.GroupNotifyDetailedType.GROUP_OTHER_CHANGE.ordinal();
                        }
                        if (groupsInfo.getInvitationRule() != 0 && GroupMembersTable.ROLE.fromOrdinal(groupsInfo.getInvitationRule()) != null) {
                            group.setInvitationRule(groupsInfo.getInvitationRule());
                        }
                        group = groupManagerWithTransaction.setGroupWithPermissionCheck(account, group,notifyDetailedType);
                        groupManagerWithTransaction.clearMemberGroupCacheForGroup(group.getId());
                        logger.info(account.getNumber() + " set group: " + group.getId() + " name: " + groupsInfo.getName() + ", messageExpiry: " + groupsInfo.getMessageExpiry() + ", avatar: " + groupsInfo.getAvatar());
                    }
                }
                groupManagerWithTransaction.sendGroupNotify(changeType, account, group, groupActionType, notifyDetailedType, null, groupMemeberAction, null, null,-1, display, operatee);
                builder.setStatus(STATUS.OK_VALUE);
            } catch (InvalidParameterException e) {
                builder.setStatus(STATUS.INVALID_PARAMETER_VALUE);
                builder.setReason(e.getMessage());
                logger.error(e.getMessage());
            } catch (NoPermissionException e) {
                builder.setStatus(STATUS.NO_PERMISSION_VALUE);
                builder.setReason(e.getMessage());
                logger.error(e.getMessage());
            } catch (NoSuchGroupException e) {
                builder.setStatus(STATUS.NO_SUCH_GROUP_VALUE);
                builder.setReason(e.getMessage());
                logger.error(e.getMessage());
            } catch (NoSuchGroupMemberException e) {
                builder.setStatus(STATUS.NO_SUCH_GROUP_MEMBER_VALUE);
                builder.setReason(e.getMessage());
                logger.error(e.getMessage());
            }
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    private boolean groupInfoIsChange(Group group, GroupsInfo request){
        if(StringUtils.isNotBlank(request.getName())) {
            if (!group.getName().equals(request.getName())){
                return true;
            }
        }
        if(request.getInvitationRule() == 0) {
            if (group.getInvitationRule()!=request.getInvitationRule()){
                return true;
            }
        }
        if(request.getMessageExpiry() != 0) {
            if (group.getMessageExpiry()!=request.getMessageExpiry()){
                return true;
            }
        }
        if(StringUtils.isNotBlank(request.getAvatar())) {
            if (!group.getAvatar().equals(request.getAvatar())){
                return true;
            }
        }
        return false;
    }

    @Override
    public void join(GroupsCreateRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder builder = BaseResponse.newBuilder();
        builder.setVer(1);
        builder.setStatus(STATUS.OK_VALUE);

        GroupsInfo groupsInfo = request.getGroupsInfo();
        Account account = new Account();
        List<GroupMember> groupMembersList = groupManagerWithTransaction.getGroupMembers(groupsInfo.getId());
        for(GroupMember groupMember : groupMembersList){
            if(groupMember.getRole() == GroupMembersTable.ROLE.OWNER.ordinal()){
                account = accountsManager.get(groupMember.getUid()).get();
                break;
            }
        }
        if(StringUtils.isEmpty(account.getNumber())){
            builder.setStatus(STATUS.NO_SUCH_USER_VALUE);
            builder.setReason("no group owner");
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
            return;
        }
        Device device = new Device();
        device.setId(0);
        account.setAuthenticatedDevice(device);
        Group group = groupManagerWithTransaction.getGroup(groupsInfo.getId());
        boolean extChange=false;
        List<Account> operatees=new ArrayList<Account>();
        List<GroupMember> groupMembers = new ArrayList<>();
        try{
            ProtocolStringList membersList = request.getMembersList();
            for (String number : membersList){
                Optional<Account> optAccount = accountsManager.get(number);
                if (optAccount.isPresent()){
                    operatees.add(optAccount.get());
                }
            }
            boolean oldExt=group.isExt();
           group= groupManagerWithTransaction.addMemberWithPermissionCheck(account, group, operatees, groupMembers);
            if(group.isExt()!=oldExt){
                extChange=true;
            }
            groupManagerWithTransaction.clearGroupMembersCache(group.getId());
            for(Account account1:operatees) {
                groupManagerWithTransaction.clearMemberGroupCache(account1.getNumber());
            }
            groupManagerWithTransaction.sendGroupNotify(GroupNotify.ChangeType.MEMBER.ordinal(),account,group,extChange?GroupNotify.ActionType.UPDATE.ordinal():GroupNotify.ActionType.NONE.ordinal(), GroupNotify.GroupNotifyDetailedType.JOIN_GROUP.ordinal(),groupMembers,GroupNotify.ActionType.ADD.ordinal(),null,null,-1,GroupNotify.Display.YES.ordinal(),groupMembers);
        } catch (InvalidParameterException e) {
            builder.setStatus(STATUS.INVALID_PARAMETER_VALUE);
            builder.setReason(e.getMessage());
            logger.error(e.getMessage());
        } catch (NoPermissionException e) {
            builder.setStatus(STATUS.NO_PERMISSION_VALUE);
            builder.setReason(e.getMessage());
            logger.error(e.getMessage());
        } catch (NoSuchGroupException e) {
            builder.setStatus(STATUS.NO_SUCH_GROUP_VALUE);
            builder.setReason(e.getMessage());
            logger.error(e.getMessage());
        } catch (ExceedingGroupMemberSizeException e) {
            builder.setStatus(STATUS.GROUP_IS_FULL_OR_EXCEEDS_VALUE);
            builder.setReason(e.getMessage());
            logger.error(e.getMessage());
        } finally {
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void leave(GroupsCreateRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder builder = BaseResponse.newBuilder();
        builder.setVer(1);
        builder.setStatus(STATUS.OK_VALUE);

        GroupsInfo groupsInfo = request.getGroupsInfo();
        Account account = new Account();
        List<GroupMember> groupMembersList = groupManagerWithTransaction.getGroupMembers(groupsInfo.getId());
        for(GroupMember groupMember : groupMembersList){
            if(groupMember.getRole() == GroupMembersTable.ROLE.OWNER.ordinal()){
                account = accountsManager.get(groupMember.getUid()).get();
                break;
            }
        }
        if(StringUtils.isEmpty(account.getNumber())){
            builder.setStatus(STATUS.NO_SUCH_USER_VALUE);
            builder.setReason("no group owner");
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
            return;
        }
        Device device=new Device();
        device.setId(0);
        account.setAuthenticatedDevice(device);
        Group group = groupManagerWithTransaction.getGroupWithPermissionCheck(account, groupsInfo.getId());
        int notifyDetailedType;
        int memberActionType;
        boolean isKickout=false;
        boolean extChange=false;
        List<Account> operatees=new ArrayList<Account>();
        // Check if accounts exist
        for (String uid : request.getMembersList()) {
            Optional<Account> operatee=accountsManager.get(uid);
            if (operatee.isPresent()) {
                if(!account.getNumber().equals(uid)){
                    isKickout=true;
                }
                operatees.add(operatee.get());
            }
        }
        List<GroupMember> groupMembers=new ArrayList<GroupMember>();
        // Leave
        try {
            boolean oldExt=group.isExt();
            group=groupManagerWithTransaction.removeMemberWithPermissionCheck(account, group, operatees,groupMembers);
            if(group.isExt()!=oldExt){
                extChange=true;
            }
            groupManagerWithTransaction.clearGroupMembersCache(group.getId());
            for(Account account1:operatees) {
                groupManagerWithTransaction.clearMemberGroupCache(account1.getNumber());
            }
            if(isKickout) {
                notifyDetailedType= GroupNotify.GroupNotifyDetailedType.KICKOUT_GROUP.ordinal();
                memberActionType= GroupNotify.ActionType.DELETE.ordinal();
            }else{
                notifyDetailedType= GroupNotify.GroupNotifyDetailedType.LEAVE_GROUP.ordinal();
                memberActionType= GroupNotify.ActionType.LEAVE.ordinal();
            }
            groupManagerWithTransaction.sendGroupNotify(GroupNotify.ChangeType.MEMBER.ordinal(),account,group,extChange?GroupNotify.ActionType.UPDATE.ordinal():GroupNotify.ActionType.NONE.ordinal(), notifyDetailedType,groupMembers,memberActionType,null,null,-1,GroupNotify.Display.YES.ordinal(),groupMembers);
        } catch (NoPermissionException e) {
            builder.setStatus(STATUS.NO_PERMISSION_VALUE);
            builder.setReason(e.getMessage());
            logger.error(org.whispersystems.textsecuregcm.entities.BaseResponse.STATUS.NO_PERMISSION+e.getMessage());
        } catch (NoSuchGroupException e) {
            builder.setStatus(STATUS.NO_SUCH_GROUP_VALUE);
            builder.setReason(e.getMessage());
            logger.error(org.whispersystems.textsecuregcm.entities.BaseResponse.STATUS.NO_SUCH_GROUP+e.getMessage());
        } catch (NoSuchGroupMemberException e) {
            builder.setStatus(STATUS.NO_SUCH_GROUP_MEMBER_VALUE);
            builder.setReason(e.getMessage());
            logger.error(org.whispersystems.textsecuregcm.entities.BaseResponse.STATUS.NO_SUCH_GROUP_MEMBER+e.getMessage());
        } finally {
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        }

    }

    @Override
    public void getGroupMembers(GroupsRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder builder = BaseResponse.newBuilder();
        builder.setVer(1);
        GroupsInfo groupsInfo = request.getGroupInfo();

        String groupId = groupsInfo.getId();
        if(StringUtils.isBlank(groupId)){
            builder.setStatus(STATUS.INVALID_PARAMETER_VALUE);
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
            return;
        }
        List<GroupMember> groupMembers = groupManagerWithTransaction.getGroupMembers(groupId);

        GroupMembersResponse.Builder memberBuilder = GroupMembersResponse.newBuilder();
        for(GroupMember member : groupMembers){
            GroupMemberInfo.Builder memberInfoBuilder = GroupMemberInfo.newBuilder();
            Optional<Account> account1 = accountsManager.get(member.getUid());
            if(account1.isPresent()) {
                Account account2 = account1.get();
                memberInfoBuilder.setGid(groupId);
                memberInfoBuilder.setUid(member.getUid());
                memberInfoBuilder.setDisplayName(member.getDisplayName());
                memberInfoBuilder.setRole(member.getRole()+"");
                memberInfoBuilder.setNotification(member.getNotification()+"");
                if(StringUtils.isNotBlank(account2.getPlainName())){memberInfoBuilder.setPlainName(account2.getPlainName());}
                if(StringUtils.isNotBlank(account2.getOktaId())){memberInfoBuilder.setOktaId(account2.getOktaId());}
                memberBuilder.addGroupMemberInfo(memberInfoBuilder.build());
            }
        }

        builder.setStatus(STATUS.OK_VALUE);
        builder.setData(Any.pack(memberBuilder.build()));

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMyGroups(GroupsRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder builder = BaseResponse.newBuilder();
        builder.setVer(1);
        Step step = request.getStep();
        String number = step.getNumber();

        Optional<Account> accountOpt = accountsManager.get(number);
        if(!accountOpt.isPresent()){
            builder.setStatus(STATUS.INVALID_PARAMETER_VALUE);
            builder.setReason("invalid parameter");
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
            return;
        }
        Account account = accountOpt.get();

        List<Group> groups = groupManagerWithTransaction.getMemberGroupsWithPermissionCheck(account);
        GroupsResponse.Builder response = GroupsResponse.newBuilder();
        Set<Device> devices = account.getDevices();
        if(!devices.isEmpty()){
            long lastSeen = devices.stream().mapToLong(s -> s.getLastSeen()).max().getAsLong();
            response.setTotal(lastSeen);
        }
        for(Group group : groups){
            GroupsInfo.Builder groupsInfo = GroupsInfo.newBuilder();
            groupsInfo.setId(group.getId());
            groupsInfo.setName(group.getName());
            if(StringUtils.isNotBlank(group.getAvatar())){
                groupsInfo.setAvatar(group.getAvatar());
            }
            groupsInfo.setStatus(group.getStatus());
            groupsInfo.setCreator(group.getCreator());
            groupsInfo.setCreateTime(group.getCreateTime());
            groupsInfo.setInvitationRule(group.getInvitationRule());
            groupsInfo.setLastActiveTime(group.getLastActiveTime());
            groupsInfo.setMessageExpiry(group.getMessageExpiry());
            response.addGroupsInfo(groupsInfo.build());
        }
        builder.setData(Any.pack(response.build()));
        builder.setStatus(STATUS.OK_VALUE);
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void changeRole(GroupMembersResponse request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder base = BaseResponse.newBuilder();
        base.setVer(1);
        base.setStatus(STATUS.OK_VALUE);
        try{
            List<GroupMemberInfo> groupMemberInfoList = request.getGroupMemberInfoList();
            if(groupMemberInfoList.isEmpty()){
                base.setStatus(STATUS.INVALID_PARAMETER_VALUE);
                responseObserver.onNext(base.build());
                responseObserver.onCompleted();
                return;
            }
            GroupMemberInfo groupMemberInfo = request.getGroupMemberInfo(0);
            String gid = groupMemberInfo.getGid();

            Group group = groupManagerWithTransaction.getGroup(gid);
            Account account = null;
            List<GroupMember> groupMembersList = groupManagerWithTransaction.getGroupMembers(gid);
            for(GroupMember groupMember : groupMembersList){
                if(groupMember.getRole() == GroupMembersTable.ROLE.OWNER.ordinal()){
                    account = accountsManager.get(groupMember.getUid()).get();
                    Device device = new Device();
                    device.setId(0l);
                    account.setAuthenticatedDevice(device);
                    break;
                }
            }
            for(GroupMemberInfo groupMember : groupMemberInfoList){
                Optional<Account> accountOpt = accountsManager.get(groupMember.getUid());
                if (!accountOpt.isPresent()) {
                    logger.error("uid {} not exist", groupMemberInfo.getUid());
                    continue;
                }
                List<GroupMember> groupMembers=new ArrayList<>();
                int display=GroupNotify.Display.NO.ordinal();
                int notifyDetailedType;
                int groupNotifyType=GroupNotify.ChangeType.PERSONNEL.ordinal();
                boolean isChangePrivate=true;
                Object operatee=null;
                try {
                    Account accountToSet = accountOpt.get();
                    GroupMember member = groupManagerWithTransaction.getMemberWithPermissionCheck(account, group, accountToSet);
                    int role = Integer.parseInt(groupMember.getRole());
                    if(role!=member.getRole()) {
                        member.setRole(role);
                    }
                    String displayName = groupMember.getDisplayName();
                    if(StringUtils.isNotEmpty(displayName)&&!displayName.equals(member.getDisplayName())) {
                        member.setDisplayName(displayName);
                        isChangePrivate=false;
                    }
                    String remark = groupMember.getRemark();
                    if(StringUtils.isNotEmpty(remark)&&!remark.equals(member.getRemark())) {
                        member.setRemark(remark);
                    }
//                    //兼容老版本。不为空且global为true时，将global配置带入到Notification。为空且Notification不为空时默认为老版本，将Global设置为false
//                    if(request.getUseGlobal().isPresent()&&!request.getUseGlobal().get().equals(member.isUseGlobal())) {
//                        member.setUseGlobal(request.getUseGlobal().get());
//                        if(request.getUseGlobal().get()||!request.getNotification().isPresent()) {
//                            int globalNotification = accountsManager.getGlobalNotification(accountToSet);
//                            request.setNotification(globalNotification);
//                        }
//                    }else{
//                        if(request.getNotification().isPresent()&&request.getNotification().get()!=member.getNotification()) {
//                            member.setUseGlobal(false);
//                        }
//                    }
//                    if(request.getNotification().isPresent()&&request.getNotification().get()!=member.getNotification()) {
//                        member.setNotification(request.getNotification().get());
//                    }
                    int chageType=groupManagerWithTransaction.setMemberSettingsWithPermissionCheck(account,group, member);
                    groupManagerWithTransaction.clearGroupMembersCache(gid);
                    if(chageType==GroupManagerWithTransaction.MemberChangeType.BASIC.ordinal()) {
                        notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_MEMBERINFO_CHANGE.ordinal();
                        if(isChangePrivate){
                            groupNotifyType=GroupNotify.ChangeType.PERSONNEL_PRIVATE.ordinal();
                            notifyDetailedType= GroupNotify.GroupNotifyDetailedType.GROUP_MEMBERINFO_CHANGE_PRIVATE.ordinal();
                        }
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
                    group =groupManagerWithTransaction.getGroup(gid);
                    groupManagerWithTransaction.sendGroupNotify(groupNotifyType,account,group,GroupNotify.ActionType.NONE.ordinal(), notifyDetailedType,groupMembers,GroupNotify.ActionType.UPDATE.ordinal(),null,null,-1,display,operatee);
                } catch (InvalidParameterException e) {
                    base.setStatus(STATUS.INVALID_PARAMETER_VALUE);
                    logger.error(e.getMessage());
                } catch (NoPermissionException e) {
                    base.setStatus(STATUS.NO_PERMISSION_VALUE);
                    logger.error(e.getMessage());
                } catch (NoSuchGroupException e) {
                    base.setStatus(STATUS.NO_SUCH_GROUP_VALUE);
                    logger.error(e.getMessage());
                } catch (NoSuchGroupMemberException e) {
                    base.setStatus(STATUS.NO_SUCH_GROUP_MEMBER_VALUE);
                    logger.error(e.getMessage());
                }
            }
        }catch (Exception e){
            base.setStatus(STATUS.OTHER_ERROR_VALUE);
            base.setReason(e.getMessage());
        }finally {
            responseObserver.onNext(base.build());
            responseObserver.onCompleted();
        }

    }

    @Override
    public void uploadAvatar(GroupAvatarRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder base = BaseResponse.newBuilder();
        base.setVer(1);
        final ByteString avatarContent = request.getAvatarContent();
        final int size = avatarContent.size();
        if (size < 10 || size > 10*1024*1024){ // 检查 image size
            // 检查image type
            base.setStatus(STATUS.INVALID_PARAMETER_VALUE);
            base.setReason("error image size");
            responseObserver.onNext(base.build());
            responseObserver.onCompleted();
            return;
        }

        if( !avatarContent.substring(1, 4).toStringUtf8().equals("PNG") &&
                !avatarContent.substring(6, 10).toStringUtf8().equals("JFIF") ){ // 检查image type
            base.setStatus(STATUS.OTHER_ERROR_VALUE);
            base.setReason("error image type");
            responseObserver.onNext(base.build());
            responseObserver.onCompleted();
            return;
        }

        int changeType = GroupNotify.ChangeType.BASIC.ordinal();
        int notifyDetailedType = GroupNotify.GroupNotifyDetailedType.GROUP_AVATAR_CHANGE.ordinal();
        int groupActionType = GroupNotify.ActionType.UPDATE.ordinal();

        final String operator = request.getOperator();

        try {
            Optional<Account> accountOpt = accountsManager.get(operator);
            if (!accountOpt.isPresent()) {
                base.setStatus(STATUS.NO_SUCH_USER_VALUE);
                base.setReason("no such operator");
                return;
            }
            final Account account = accountOpt.get();
            Device device = new Device();
            device.setId(0);
            account.setAuthenticatedDevice(device);
            Group group = groupManagerWithTransaction.getGroup(request.getGid());
            final String avatar = AvatarEnc.uploadGroupAvatar(avatarContent.toByteArray(), urlSignerAli);
            group.setAvatar(avatar);
            logger.warn("uploadGroupAvatar:{}",avatar);
            group = groupManagerWithTransaction.setGroupWithPermissionCheck(account, group, notifyDetailedType);
            groupManagerWithTransaction.sendGroupNotify(changeType, account, group, groupActionType, notifyDetailedType,
                    null, GroupNotify.ActionType.UPDATE.ordinal(), null, null,
                    -1, GroupNotify.Display.YES.ordinal(), group.getName());
            base.setStatus(STATUS.OK_VALUE);
            base.setReason("OK");
        } catch (IOException e){
            base.setStatus(STATUS.SERVER_INTERNAL_ERROR_VALUE);
            base.setReason("IOException");
            logger.warn("IOException in uploadGroupAvatar ",e);
        } catch (Exception e){
            base.setStatus(STATUS.OTHER_ERROR_VALUE);
            base.setReason(e.getMessage());
            logger.error("Exception in uploadGroupAvatar",e);
        } finally {
            responseObserver.onNext(base.build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void bulkJoin(GroupJoinRequest joinRequest, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder builder = BaseResponse.newBuilder();
        builder.setVer(1);
        builder.setStatus(STATUS.OK_VALUE);

        List<GroupsCreateRequest> groupCreateRequestList = joinRequest.getGroupCreateRequestList();
        for(GroupsCreateRequest request : groupCreateRequestList){
            GroupsInfo groupsInfo = request.getGroupsInfo();
            Account account = new Account();
            List<GroupMember> groupMembersList = groupManagerWithTransaction.getGroupMembers(groupsInfo.getId());
            for(GroupMember groupMember : groupMembersList){
                if(groupMember.getRole() == GroupMembersTable.ROLE.OWNER.ordinal()){
                    Optional<Account> accountOptional= accountsManager.get(groupMember.getUid());
                    if(accountOptional.isPresent()){
                        account = accountOptional.get();
                        break;
                    }
                }
            }
            if(StringUtils.isEmpty(account.getNumber())){
                logger.error("group: {} doesn't have owner or owner not exist", groupsInfo.getId());
                continue;
            }
            Device device = new Device();
            device.setId(0);
            account.setAuthenticatedDevice(device);
            Group group = groupManagerWithTransaction.getGroup(groupsInfo.getId());
            boolean extChange=false;
            List<Account> operatees=new ArrayList<Account>();
            List<GroupMember> groupMembers = new ArrayList<>();
            try{
                ProtocolStringList membersList = request.getMembersList();
                for (String number : membersList){
                    Optional<Account> optAccount = accountsManager.get(number);
                    if (optAccount.isPresent()){
                        operatees.add(optAccount.get());
                    }
                }
                boolean oldExt=group.isExt();
                group= groupManagerWithTransaction.addMemberWithPermissionCheck(account, group, operatees, groupMembers);
                if(group.isExt()!=oldExt){
                    extChange=true;
                }
                groupManagerWithTransaction.clearGroupMembersCache(group.getId());
                for(Account account1:operatees) {
                    groupManagerWithTransaction.clearMemberGroupCache(account1.getNumber());
                }

                groupManagerWithTransaction.sendGroupNotify(GroupNotify.ChangeType.MEMBER.ordinal(),account,group,extChange?GroupNotify.ActionType.UPDATE.ordinal():GroupNotify.ActionType.NONE.ordinal(), GroupNotify.GroupNotifyDetailedType.JOIN_GROUP.ordinal(),groupMembers,GroupNotify.ActionType.ADD.ordinal(),null,null,-1,GroupNotify.Display.NO.ordinal(),groupMembers);
            } catch (InvalidParameterException e) {
                logger.error(e.getMessage());
            } catch (NoPermissionException e) {
                logger.error(e.getMessage());
            } catch (NoSuchGroupException e) {
                logger.error(e.getMessage());
            }catch (ExceedingGroupMemberSizeException e) {
                logger.error(e.getMessage());
            }
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

}
