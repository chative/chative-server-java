package org.whispersystems.textsecuregcm.internal;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.Notification;
import org.whispersystems.textsecuregcm.internal.common.BaseResponse;
import org.whispersystems.textsecuregcm.internal.common.Empty;
import org.whispersystems.textsecuregcm.internal.common.STATUS;
import org.whispersystems.textsecuregcm.internal.message.InternalMessageServiceGrpc;
import org.whispersystems.textsecuregcm.internal.message.MsgCountResponse;
import org.whispersystems.textsecuregcm.internal.notify.InternalNotifyServiceGrpc;
import org.whispersystems.textsecuregcm.internal.notify.NotifySendRequest;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.StringUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class InternalMessageServiceImpl extends InternalMessageServiceGrpc.InternalMessageServiceImplBase{
    final private Logger logger = LoggerFactory.getLogger(InternalMessageServiceImpl.class);

    private final MessagesManager messagesManager;

    public InternalMessageServiceImpl( MessagesManager messagesManager) {
        this.messagesManager = messagesManager;
    }


    @Override
    public void getMsgCount(Empty request, StreamObserver<MsgCountResponse> responseObserver) {
        MsgCountResponse.Builder responseBuilder = MsgCountResponse.newBuilder();
        responseBuilder.setCount(messagesManager.count());
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMsgCountByEstimate(Empty request, StreamObserver<MsgCountResponse> responseObserver) {
        MsgCountResponse.Builder responseBuilder = MsgCountResponse.newBuilder();
        responseBuilder.setCount(messagesManager.countByEstimate());
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

}
