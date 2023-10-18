package org.whispersystems.textsecuregcm.push;

import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.configuration.UserStatusServerConfiguration;
import org.whispersystems.textsecuregcm.internal.common.BaseResponse;
import org.whispersystems.textsecuregcm.internal.common.STATUS;
import org.whispersystems.textsecuregcm.internal.status.UserStatusRequest;
import org.whispersystems.textsecuregcm.internal.status.UserStatusResponse;
import org.whispersystems.textsecuregcm.internal.status.UserStatusServiceGrpc;
import org.whispersystems.textsecuregcm.util.StringUtil;

import java.util.concurrent.TimeUnit;


public class UserStatusServerGrpc {
    private static final Logger logger = LoggerFactory.getLogger(UserStatusServerGrpc.class);
    UserStatusServiceGrpc.UserStatusServiceBlockingStub blockingStub;
    public UserStatusServerGrpc(UserStatusServerConfiguration userStatusServerConfiguration){
        if(userStatusServerConfiguration==null||StringUtil.isEmpty(userStatusServerConfiguration.getGrpcHost())){
            logger.error("UserStatusServerGrpc userStatusServerConfiguration is error!");
        }
        try {
            ManagedChannel originChannel = NettyChannelBuilder.forAddress(userStatusServerConfiguration.getGrpcHost(), userStatusServerConfiguration.getGrpcPort())
                        .usePlaintext()
                        .build();

            blockingStub = UserStatusServiceGrpc.newBlockingStub(originChannel);
            blockingStub.withDeadline(Deadline.after(userStatusServerConfiguration.getTimeout()>0?userStatusServerConfiguration.getTimeout():5, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("UserStatusServerGrpc create blockingStub is error! msg:{}",e.getMessage());
        }
    }

    public boolean isDND(String number){
        Long begin=System.currentTimeMillis();
        UserStatusRequest.Builder  requestBuilder=UserStatusRequest.newBuilder();
        requestBuilder.setNumber(number);
        if(blockingStub==null){
            logger.error("UserStatusServerGrpc isDND blockingStub is null!");
            return false;
        }
        try {
            BaseResponse response = blockingStub.isDND(requestBuilder.build());
            if (response != null) {
                if(response.getStatus()== STATUS.OK_VALUE&&response.hasData()&&response.getData()!=null){
                    UserStatusResponse userStatusResponse= response.getData().unpack(UserStatusResponse.class);
                    if(userStatusResponse!=null&&userStatusResponse.hasIsDND()&&userStatusResponse.getIsDND()){
                        return true;
                    }
                }
            }
        }catch (Exception e){
            logger.error("UserStatusServerGrpc isDND  error! cost:{},msg:{}",System.currentTimeMillis()-begin,e.getMessage());
        }
        return false;
    }

}
