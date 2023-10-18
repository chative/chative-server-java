package org.whispersystems.textsecuregcm.internal;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.Notification;
import org.whispersystems.textsecuregcm.internal.common.BaseResponse;
import org.whispersystems.textsecuregcm.internal.common.STATUS;
import org.whispersystems.textsecuregcm.internal.notify.InternalNotifyServiceGrpc;
import org.whispersystems.textsecuregcm.internal.notify.NotifySendRequest;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.StringUtil;

import java.util.*;

public class InternalNotifyServiceImpl extends InternalNotifyServiceGrpc.InternalNotifyServiceImplBase{
    final private Logger logger = LoggerFactory.getLogger(InternalNotifyServiceImpl.class);

    private final AccountsManager accountsManager;
    private final GroupManagerWithTransaction groupManager;
    private final NotifyManager notifyManager;
    private final RateLimiters rateLimiters;

    public InternalNotifyServiceImpl(AccountsManager accountsManager, RateLimiters rateLimiters,NotifyManager notifyManager,GroupManagerWithTransaction groupManager) {
        this.accountsManager = accountsManager;
        this.rateLimiters = rateLimiters;
        this.notifyManager=notifyManager;
        this.groupManager=groupManager;
    }

    @Override
    public void sendNotify(NotifySendRequest notifySendRequest, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder responseBuilder = BaseResponse.newBuilder();
        if(notifySendRequest==null||!notifySendRequest.hasContent()||(notifySendRequest.getUidsList().isEmpty()&&notifySendRequest.getGidsList().isEmpty())){
            responseBuilder.setStatus(STATUS.INVALID_PARAMETER_VALUE);
        }
        Set<String> sendNumbers=new HashSet<>();
        if(!notifySendRequest.getUidsList().isEmpty()) {
            for (String uid:notifySendRequest.getUidsList()){
                if(!sendNumbers.contains(uid)) {
                    sendNotify(uid, notifySendRequest);
                    sendNumbers.add(uid);
                }
            }
        }
        if(!notifySendRequest.getGidsList().isEmpty()){
            for (String gid:notifySendRequest.getGidsList()){
                Group group=groupManager.getGroup(gid);
                if(group!=null&&group.getStatus()==GroupsTable.STATUS.ACTIVE.ordinal()){
                    sendNotifyForGroup(group,notifySendRequest);
//                    List<GroupMember> groupMemberList=groupManager.getGroupMembers(gid);
//                    if(groupMemberList!=null&&groupMemberList.size()>0){
//                        for(GroupMember groupMember:groupMemberList){
//                            if(!sendNumbers.contains(groupMember.getUid())) {
//                                sendNotify(groupMember.getUid(), notifySendRequest);
//                                sendNumbers.add(groupMember.getUid());
//                            }
//                        }
//                    }
                }
            }
        }

        responseBuilder.setVer(1);
        responseBuilder.setStatus(STATUS.OK_VALUE);
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    private void sendNotifyForGroup(Group group,NotifySendRequest notifySendRequest){
        Notification notification=null;
        if(notifySendRequest.hasDefaultNotification()&&!StringUtil.isEmpty(notifySendRequest.getDefaultNotification())){
            notification=new Notification();
            notification.setPayload(notifySendRequest.getDefaultNotification());
        }
        notifyManager.sendNotifyForGroup(group,notifySendRequest.getContent(),notification);
    }

    private void sendNotify(String uid,NotifySendRequest notifySendRequest){
        Optional<Account> accountOptional=accountsManager.get(uid);
        if(accountOptional.isPresent()&&!accountOptional.get().isinValid()){
            Notification notification=null;
            String notificationPayload=notifySendRequest.getNotificationsMap().get(uid);
            if(!StringUtil.isEmpty(notificationPayload)){
                notification=new Notification();
                notification.setPayload(notificationPayload);
            }else if(notifySendRequest.hasDefaultNotification()){
                notification=new Notification();
                notification.setPayload(notifySendRequest.getDefaultNotification());
            }
            notifyManager.sendNotifySingle(accountOptional.get(),notifySendRequest.getContent(),notification);
        }
    }
}
