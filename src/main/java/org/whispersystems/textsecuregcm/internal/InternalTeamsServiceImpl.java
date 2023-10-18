package org.whispersystems.textsecuregcm.internal;

import com.google.protobuf.Any;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.internal.common.BaseAnyResponse;
import org.whispersystems.textsecuregcm.internal.common.BaseResponse;
import org.whispersystems.textsecuregcm.internal.common.STATUS;
import org.whispersystems.textsecuregcm.internal.common.Step;
import org.whispersystems.textsecuregcm.internal.teams.*;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.Team;
import org.whispersystems.textsecuregcm.storage.TeamsManagerCasbin;

import java.util.*;
import java.util.stream.Collectors;

public class InternalTeamsServiceImpl extends InternalTeamsServiceGrpc.InternalTeamsServiceImplBase {

    final private Logger logger = LoggerFactory.getLogger(InternalTeamsServiceImpl.class);

    final private String MGMT_PID = "mgmt";

    final private AccountsManager accountsManager;
    final private TeamsManagerCasbin teamsManager;
    final private InternalServicePermissionService internalServicePermissionService;

    public InternalTeamsServiceImpl(AccountsManager accountsManager, TeamsManagerCasbin teamsManager, InternalServicePermissionService internalServicePermissionService) {
        this.accountsManager = accountsManager;
        this.teamsManager = teamsManager;
        this.internalServicePermissionService = internalServicePermissionService;
    }

    @Override
    public void get(GetRequest request, StreamObserver<GetResponse> responseObserver) {
        GetResponse.Builder base = GetResponse.newBuilder();

        for (String team : request.getTeamsList()) {
            GetResponse.TeamInfo.Builder teamInfo = GetResponse.TeamInfo.newBuilder();

            Map<String, Account> members = new HashMap<>();
            Team teamT = new Team();
            if(!request.getPid().equals(MGMT_PID)){
                Team temp = teamsManager.getById(Integer.valueOf(team));
                if(temp == null){
                    continue;
                }else{
                    teamT = temp;
                }
            }else {
                teamT  = teamsManager.get(team);
            }
            members = teamsManager.getTeamMembers(teamT.getName());
            teamInfo.setName(teamT.getName());
            teamInfo.setId(teamT.getId());
            for (Map.Entry<String, Account> member : members.entrySet()) {
                String uid = member.getKey();
                Account account = member.getValue();
                teamInfo.addMembers(
                        MemberInfo.newBuilder()
                                .setUid(uid)
                                .setName(account.getPlainName())
                                .build()
                );
            }
            base.addTeams(teamInfo.build());
        }

        responseObserver.onNext(base.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getAll(Step request, StreamObserver<GetResponse> responseObserver) {
        GetResponse.Builder result = GetResponse.newBuilder();
        boolean exists = internalServicePermissionService.isExists(request.getPid(), request.getAppid());
        if(exists){
            List<Team> all = teamsManager.getAll(request.getOffset(), request.getLength(), request.getPid());
            if(request.getTeamsId() != 0){
                all = all.stream().filter(t -> t.getId() == request.getTeamsId()).collect(Collectors.toList());
            }
            for (Team team : all) {
                GetResponse.TeamInfo.Builder teamInfo = GetResponse.TeamInfo.newBuilder();

                if(StringUtils.isNotBlank(request.getAppid())){
                    teamInfo.setId(Long.valueOf(team.getId()+""));
                    teamInfo.setName(team.getName());
                }else{
                    Map<String, Account> members = teamsManager.getTeamMembers(team.getName());
                    teamInfo.setName(team.getName());
                    for (Map.Entry<String, Account> member : members.entrySet()) {
                        String uid = member.getKey();
                        Account account = member.getValue();
                        teamInfo.addMembers(
                                MemberInfo.newBuilder()
                                        .setUid(uid)
                                        .setName(account.getPlainName())
                                        .build()
                        );
                    }
                }
                result.addTeams(teamInfo.build());
            }
        }

        responseObserver.onNext(result.build());
        responseObserver.onCompleted();
    }

    @Override
    public void join(JoinLeaveRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder base = BaseResponse.newBuilder();
        base.setStatus(STATUS.OK_VALUE);
        base.setVer(1);
        Map<String, List<Account>> classifiedJoins = new HashMap<>();
        for (JoinLeaveRequest.JoinLeaveInfo join : request.getJoinleavesList()) {
            String team = join.getTeam();
            String uid = join.getUid();
            logger.info("team : {}, uid: {}", team, uid);
            Optional<Account> oaccount = accountsManager.get(uid);
            // 区分haodesk和管理后台
            if(!request.getPid().equals(MGMT_PID)){
                Team teamById = teamsManager.getById(Integer.valueOf(join.getTeam()));
                if (!oaccount.isPresent()||oaccount.get().isinValid()) {
                    logger.warn("join(): no such user or is disabled: " + uid);
                    base.setStatus(STATUS.NO_SUCH_USER_VALUE);
                    base.setReason("no such user or is disabled");
                    responseObserver.onNext(base.build());
                    responseObserver.onCompleted();
                    return;
                }
                boolean accountTeamPid = StringUtils.isBlank(oaccount.get().getPid())?false:oaccount.get().getPid().equals(teamById.getPid());
                boolean hasObjectPermissionForApp = internalServicePermissionService.isHasObjectPermissionForApp(request.getAppid(), teamById);
                if(!((accountTeamPid && hasObjectPermissionForApp) )){
                    base.setStatus(STATUS.NO_PERMISSION_VALUE);
                    base.setReason("can not join accounts:"+uid+" from other teams:"+team);
                    responseObserver.onNext(base.build());
                    responseObserver.onCompleted();
                    return;
                }
                team = teamById.getName();
            }


            if (teamsManager.getUserTeams(uid).contains(team)) {
                logger.warn("join(): " + uid + " is already in team: " + team);
                continue;
            }

            List<Account> members = classifiedJoins.get(team);
            if (null == members) {
                members = new ArrayList<>();
                members.add(oaccount.get());
                classifiedJoins.put(team, members);
            } else {
                members.add(oaccount.get());
                classifiedJoins.replace(team, members);
            }
        }

        //for (Map.Entry<String, List<Account>> join : classifiedJoins.entrySet()) {
        //    String team = join.getKey();
        //    List<Account> members = join.getValue();
        //    teamsManager.joinTeam(members, team);
        //}

        logger.info("join end : {}", System.currentTimeMillis());
        responseObserver.onNext(base.build());
        responseObserver.onCompleted();
    }

    @Override
    public void leave(JoinLeaveRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder base = BaseResponse.newBuilder();
        base.setVer(1);
        base.setStatus(STATUS.OK_VALUE);
        Map<String, List<Account>> classifiedLeaves = new HashMap<>();
        for (JoinLeaveRequest.JoinLeaveInfo leave : request.getJoinleavesList()) {
            String team = leave.getTeam();
            if(StringUtils.isNotBlank(request.getAppid())){
                Team teamById = teamsManager.getById(Integer.valueOf(leave.getTeam()));
                team = teamById.getName();
                boolean hasObjectPermissionForApp = internalServicePermissionService.isHasObjectPermissionForApp(request.getAppid(), teamById);
                if(!hasObjectPermissionForApp){
                    base.setStatus(STATUS.NO_PERMISSION_VALUE);
                    responseObserver.onNext(base.build());
                    responseObserver.onCompleted();
                    return;
                }
            }
            String uid = leave.getUid();

            Optional<Account> oaccount = accountsManager.get(uid);
            if (!oaccount.isPresent()) {
                logger.warn("leave(): no such user or is disabled: " + uid);
                base.setStatus(STATUS.NO_SUCH_USER_VALUE);
                base.setReason("no such user or is disabled: "+uid);
                responseObserver.onNext(base.build());
                responseObserver.onCompleted();
                return;
            }

            if (!teamsManager.getUserTeams(uid).contains(team)) {
                logger.warn("leave(): " + uid + " is not in team: " + team);
                base.setStatus(STATUS.NO_SUCH_USER_VALUE);
                base.setReason(uid + " is not in team: " + team);
                responseObserver.onNext(base.build());
                responseObserver.onCompleted();
                return;
            }

            List<Account> members = classifiedLeaves.get(team);
            if (null == members) {
                members = new ArrayList<>();
                members.add(oaccount.get());
                classifiedLeaves.put(team, members);
            } else {
                members.add(oaccount.get());
                classifiedLeaves.replace(team, members);
            }
        }


        responseObserver.onNext(base.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getTree(GetTreeRequest request, StreamObserver<GetResponse> responseObserver) {
        GetResponse.Builder result = GetResponse.newBuilder();

        List<Team> tree = teamsManager.getTree(request.getName(), request.getStatusList());

        for (Team team : tree){
            GetResponse.TeamInfo.Builder teamInfo = GetResponse.TeamInfo.newBuilder();

            teamInfo.setId(team.getId());
            teamInfo.setName(team.getName());
            teamInfo.setParentId(team.getParentId());
            teamInfo.setStatus(team.getStatus());
            teamInfo.setOrderNum(team.getOrderNum());
            teamInfo.setCreateTime(team.getCreateTime());
            if(StringUtils.isNotEmpty(team.getPid())){
                teamInfo.setPid(team.getPid());
            }
            if(StringUtils.isNotEmpty(team.getAppid())){
                teamInfo.setAppid(team.getAppid());
            }
            if(StringUtils.isNotEmpty(team.getRemark())){
                teamInfo.setRemark(team.getRemark());
            }
//            Map<String, Account> members = teamsManager.getTeamMembers(team.getName());
//            if (!members.isEmpty()){
//                for (Map.Entry<String, Account> member : members.entrySet()) {
//                    String uid = member.getKey();
//                    Account account = member.getValue();
//                    teamInfo.addMembers(
//                            MemberInfo.newBuilder()
//                                    .setUid(uid)
//                                    .setName(account.getPlainName())
//                                    .build()
//                    );
//                }
//            }

            result.addTeams(teamInfo.build());
        }
        responseObserver.onNext(result.build());
        responseObserver.onCompleted();
    }

    @Override
    public void createOrUpdate(CreateOrUpdateRequest request, StreamObserver<BaseResponse> responseObserver) {

        BaseResponse.Builder builder = BaseResponse.newBuilder();
        builder.setVer(1);
        builder.setStatus(0);

        if(StringUtils.isBlank(request.getAppid()) || StringUtils.isBlank(request.getPid())){
            builder.setStatus(STATUS.INVALID_PARAMETER_VALUE);
        } else {
            long id = request.getId();
            if (0 == id) {
                // add
                Team team = teamsManager.get(request.getName());
                if(team != null) {
                    builder.setStatus(STATUS.GROUP_EXISTS_VALUE);
                    builder.setReason("team exist");
                    logger.warn("createTeam:{};already exists!",request.getName());
                    responseObserver.onNext(builder.build());
                    responseObserver.onCompleted();
                    return;
                }
            } else {
                // update
                // 先判断权限
                Team team = teamsManager.getById(Integer.parseInt(request.getId()+""));
                if(!request.getPid().equals(MGMT_PID)){
                    boolean hasObjectPermissionForApp = internalServicePermissionService.isHasObjectPermissionForApp(request.getAppid(), team);
                    if(!hasObjectPermissionForApp){
                        builder.setStatus(STATUS.NO_PERMISSION_VALUE);
                        responseObserver.onNext(builder.build());
                        responseObserver.onCompleted();
                        return;
                    }
                }

                // 再判断要修改的team name是否存在
                Team teamByName = teamsManager.get(request.getName());
                if( null != teamByName && teamByName.getId() != request.getId()){
                    builder.setStatus(STATUS.GROUP_EXISTS_VALUE);
                    builder.setReason("team exist");
                    responseObserver.onNext(builder.build());
                    responseObserver.onCompleted();
                    return;
                }

                teamsManager.updateNew(request.getId(), request.getName(), request.getParentId(), request.getAncestors(), request.getStatus(), request.getOrderNum(), request.getRemark());
            }

            Team team = teamsManager.get(request.getName());
            BaseAnyResponse.Builder value = BaseAnyResponse.newBuilder();
            value.setValue(team.getId()+"");
            builder.setVer(1);
            builder.setStatus(0);
            builder.setData(Any.pack(value.build()));
        }


        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void delete(GetRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder base = BaseResponse.newBuilder();
        base.setVer(1);
        base.setStatus(STATUS.OK_VALUE);

        String appid = request.getAppid();
        for(String team : request.getTeamsList()){
            if(StringUtils.isNotBlank(appid)){
                Team teamT = teamsManager.getById(Integer.valueOf(team));
                boolean hasObjectPermissionForApp = internalServicePermissionService.isHasObjectPermissionForApp(appid, teamT);
                if(!hasObjectPermissionForApp){
                    base.setStatus(STATUS.NO_PERMISSION_VALUE);
                    base.setReason("no permission");
                    responseObserver.onNext(base.build());
                    responseObserver.onCompleted();
                    return;
                }

                Map<String, Account> teamMembers = teamsManager.getTeamMembers(teamT.getName());
                if(!teamMembers.isEmpty()){
                    base.setStatus(STATUS.TEAM_HAS_MEMBERS_VALUE);
                    base.setReason("team "+team+" has members");
                    responseObserver.onNext(base.build());
                    responseObserver.onCompleted();
                    return;
                }
                teamsManager.updateNew(teamT.getId(), teamT.getName(), teamT.getParentId(), teamT.getAncestors(), true, teamT.getOrderNum(), teamT.getRemark());
            }
        }

        responseObserver.onNext(base.build());
        responseObserver.onCompleted();
    }
}
