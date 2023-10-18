package org.whispersystems.textsecuregcm.internal;

import com.google.gson.Gson;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.directorynotify.DirectoryNotifyManager;
import org.whispersystems.textsecuregcm.distributedlock.DistributedLock;
import org.whispersystems.textsecuregcm.entities.DirectoryNotify;
import org.whispersystems.textsecuregcm.internal.common.BaseResponse;
import org.whispersystems.textsecuregcm.internal.common.Empty;
import org.whispersystems.textsecuregcm.internal.common.STATUS;
import org.whispersystems.textsecuregcm.internal.directory.DirectorySendNotifyRequest;
import org.whispersystems.textsecuregcm.internal.directory.InternalDirectoryServiceGrpc;
import org.whispersystems.textsecuregcm.storage.*;
import java.util.*;
import java.util.concurrent.locks.Lock;

public class InternalDirectoryServiceImpl extends InternalDirectoryServiceGrpc.InternalDirectoryServiceImplBase{
    final private Logger logger = LoggerFactory.getLogger(InternalDirectoryServiceImpl.class);

    private final AccountsManager accountsManager;
    private final TeamsManagerCasbin teamsManager;
    private final NotifyManager notifyManager;
    private final DirectoryNotifyManager directoryNotifyManager;

    public InternalDirectoryServiceImpl(AccountsManager accountsManager, NotifyManager notifyManager, TeamsManagerCasbin teamsManager,DirectoryNotifyManager directoryNotifyManager) {
        this.accountsManager = accountsManager;
        this.notifyManager=notifyManager;
        this.teamsManager=teamsManager;
        this.directoryNotifyManager=directoryNotifyManager;
    }

     @Override
    public void sendGetContactsMsg(DirectorySendNotifyRequest directorySendNotifyRequest, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder responseBuilder = BaseResponse.newBuilder();
        if(directorySendNotifyRequest==null||(directorySendNotifyRequest.getUidsList().isEmpty()&&directorySendNotifyRequest.getTeamsList().isEmpty()&&!directorySendNotifyRequest.getAll())){
            responseBuilder.setStatus(STATUS.INVALID_PARAMETER_VALUE);
        }
        logger.info("InternalDirectoryServiceImpl.sendGetContactsMsg directorySendNotifyRequest:{}",new Gson().toJson(directorySendNotifyRequest));
        new Thread(new Runnable() {
            @Override
            public void run() {
                Set<String> sendNumbers = new HashSet<>();
                if(directorySendNotifyRequest.getAll()){
                    List<Boolean> booleanList=new ArrayList<>();
                    booleanList.add(false);
                    List<Team> teams = teamsManager.getTree("",booleanList);
                    if (teams != null && teams.size() > 0) {
                        logger.info("InternalDirectoryServiceImpl.sendGetContactsMsg team.size:{}",teams.size());
                        for (Team team : teams) {
                            Map<String, Account> accountMap = teamsManager.getTeamMembers(team.getName());
                            logger.info("InternalDirectoryServiceImpl.sendGetContactsMsg team:{},members:{}",team.getName(),accountMap.size());
                            if (accountMap != null && accountMap.size() > 0) {
                                for (String number : accountMap.keySet()) {
                                    Account account = accountMap.get(number);
                                    if (!account.isinValid()&&!sendNumbers.contains(number)) {
                                        sendNotify(number);
                                        sendNumbers.add(number);
                                    }
                                }
                            }
                        }
                    }

                }else {
                    if (!directorySendNotifyRequest.getUidsList().isEmpty()) {
                        for (String uid : directorySendNotifyRequest.getUidsList()) {
                            if (!sendNumbers.contains(uid)) {
                                sendNotify(uid);
                                sendNumbers.add(uid);
                            }
                        }
                    }
                    if (!directorySendNotifyRequest.getTeamsList().isEmpty()) {
                        for (String team : directorySendNotifyRequest.getTeamsList()) {
                            Map<String, Account> accountMap = teamsManager.getTeamMembers(team);
                            logger.info("InternalDirectoryServiceImpl.sendGetContactsMsg team:{}, members:{}",team,accountMap.size());
                            if (accountMap != null && accountMap.size() > 0) {
                                for (String number : accountMap.keySet()) {
                                    Account account = accountMap.get(number);
                                    if (!account.isinValid()&&!sendNumbers.contains(number)) {
                                        sendNotify(number);
                                        sendNumbers.add(number);
                                    }
                                }
                            }
                        }
                    }
                }
                logger.info("InternalDirectoryServiceImpl.sendGetContactsMsg sendNumbers.size:{}",sendNumbers.size());
            }
        }).start();
        responseBuilder.setVer(1);
        responseBuilder.setStatus(STATUS.OK_VALUE);
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void fixMeetingVersion(Empty request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder responseBuilder = BaseResponse.newBuilder();

        new Thread(() -> {
            // 列举所有的账号
            final List<InternalAccountsRow> allActive = accountsManager.getInternalAccountsTable().getAllActive();
            // 修正所有的meeting version
            for (InternalAccountsRow internalAcc : allActive) {
                accountsManager.fixAccountMeetingVersion(internalAcc.getNumber(),
                        accountsManager.getAccMaxMeetingVersion());
            }
            for (InternalAccountsRow internalAcc : allActive) {
                sendNotify(internalAcc.getNumber());
            }
        }).start();

        responseBuilder.setVer(1);
        responseBuilder.setStatus(STATUS.OK_VALUE);
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();

    }

    private void sendNotify(String uid){
        Optional<Account> accountOptional=accountsManager.get(uid);
        if(accountOptional.isPresent()&&!accountOptional.get().isinValid()){
            DirectoryNotify directoryNotify = directoryNotifyManager.createNotify(new HashSet<DirectoryNotify.DirectoryMember>());
            Lock notifyLocker = DistributedLock.getLocker(new String[]{DirectoryNotify.DIRECTORY_CHANGE_NOTIFY_LOCK_KEY + accountOptional.get().getNumber()});
            try {
                notifyLocker.lock();
                notifyManager.sendNotifySingle(accountOptional.get(), directoryNotifyManager.addVersion(directoryNotify, accountOptional.get().getNumber(), true), null);
                Thread.sleep(1000);
            } catch (Exception e) {
                logger.error("InternalDirectoryServiceImpl sendGetContactsMsg sendNotifySingle error! msg:{}", e.getMessage());
                e.printStackTrace();
            } finally {
                notifyLocker.unlock();
            }
        }
    }
}
