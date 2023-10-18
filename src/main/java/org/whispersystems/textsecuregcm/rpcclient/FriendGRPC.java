package org.whispersystems.textsecuregcm.rpcclient;

import com.github.difftim.accountgrpc.*;
import com.github.difftim.common.BaseResponse;
import com.github.difftim.friend.*;
import com.github.difftim.register.*;
import com.google.protobuf.ProtocolStringList;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.configuration.FriendServerConfiguration;
import org.whispersystems.textsecuregcm.entities.UserProfile;

import java.util.List;

public class FriendGRPC {
    private static final Logger logger = LoggerFactory.getLogger(FriendGRPC.class);
    FriendGrpc.FriendBlockingStub friendBlockingStub;
    RegiterGrpc.RegiterBlockingStub registerBlockingStub;
    AccountGrpc.AccountBlockingStub accountBlockingStub;
    public FriendGRPC(FriendServerConfiguration friendServerConfiguration){
        try {
            ManagedChannel originChannel = NettyChannelBuilder.forAddress(friendServerConfiguration.getGrpcHost(), friendServerConfiguration.getGrpcPort())
                    .usePlaintext()
                    .build();
            friendBlockingStub = FriendGrpc.newBlockingStub(originChannel);
            registerBlockingStub = RegiterGrpc.newBlockingStub(originChannel);
            accountBlockingStub = AccountGrpc.newBlockingStub(originChannel);
        } catch (Exception e){
            logger.error("newBlockingStub Exception",e);
        }
    }

    public boolean isFriend(String uid1,String uid2){
        final ExistRequest existRequest = ExistRequest.newBuilder().setUser(uid1).setFriend(uid2).build();
        final ExistResponse response = friendBlockingStub.exist(existRequest);
        final BaseResponse base = response.getBase();
        logger.info("isFriend response.status:{},uid1:{},uid2:{}",base.getStatus(), uid1, uid2);
        if (base.getStatus() != 0){
            logger.error("isFriend return status:{},reason:{}", base.getStatus(), base.getReason());
        }
        return response.getExist();
    }

    public void addFriend(String inviter,String invitee){
        final AddRequest addRequest = AddRequest.newBuilder().setInviter(inviter).setInvitee(invitee).build();
        final AddResponse addResponse = friendBlockingStub.add(addRequest);
        final BaseResponse base = addResponse.getBase();
        if (base.getStatus()!=0){
            logger.error("addFriend error,status:{},reason:{}",base.getStatus(),base.getReason());
        }
    }

    public List<String> ListFriend(String userID) {
        final ListRequest listRequest = ListRequest.newBuilder().setUser(userID).build();
        final ListResponse listResponse = friendBlockingStub.list(listRequest);
        //friendsList.
        return listResponse.getFriendsList();
    }

    public BaseResponse sendSMSCode(String phone){
        SendSMSRequest sendSMSRequest = SendSMSRequest.newBuilder().setPhone(phone).build();
        final SendSMSResponse sendSMSResponse = registerBlockingStub.sendSMS(sendSMSRequest);
        return sendSMSResponse.getBase();
    }

    public BaseResponse verifySMSCode(String phone,String code){
        VerifySMSRequest verifySMSRequest = VerifySMSRequest.newBuilder().setPhone(phone).setCode(code).build();
        final VerifySMSResponse verifySMSResponse = registerBlockingStub.verifySMS(verifySMSRequest);
        final BaseResponse base = verifySMSResponse.getBase();
        if (base.getStatus()!=0){
            logger.error("verifySMSCode error,status:{},reason:{}",base.getStatus(),base.getReason());
        }
        return base;
    }

    public void initUserProfile(String uid,String emailHash,String phoneHash){
        final InitUserProfileRequest.Builder builder = InitUserProfileRequest.newBuilder();
        builder.setUid(uid);
        if (emailHash!=null&& !emailHash.isEmpty()){
            builder.setEmailHash(emailHash);
        }
        if (phoneHash!=null&& !phoneHash.isEmpty()){
            builder.setPhoneHash(phoneHash);
        }
        final InitUserProfileResponse initUserProfileResponse = accountBlockingStub.initUserProfile(builder.build());
        final BaseResponse base = initUserProfileResponse.getBase();
        if (base.getStatus()!=0){
            logger.error("initUserProfile error,status:{},reason:{}",base.getStatus(),base.getReason());
        }
    }
    public BaseResponse syncProfile(String uid, String email, String phone, String emailHash, String phoneHash) {
        SyncProfileRequest.Builder syncProfileRequest = SyncProfileRequest.newBuilder().setUid(uid);
        if (email!=null&&!email.isEmpty()){
            syncProfileRequest.setEmail(email);
        }
        if (phone!=null&&!phone.isEmpty()){
            syncProfileRequest.setPhone(phone);
        }
        if (emailHash!=null&&!emailHash.isEmpty()){
            syncProfileRequest.setEmailHash(emailHash);
        }
        if (phoneHash!=null&&!phoneHash.isEmpty()){
            syncProfileRequest.setPhoneHash(phoneHash);
        }
        final SyncProfileResponse syncProfileResponse = accountBlockingStub.syncProfile(syncProfileRequest.build());
        final BaseResponse base = syncProfileResponse.getBase();
        if (base.getStatus() != 0) {
            logger.error("syncProfile error,status:{},reason:{},uid :{}", base.getStatus(), base.getReason(), uid);
        }
        return base;
    }

    public UserProfile getUserProfile(String uid, String email, String phone, String emailHash, String phoneHash) {
        final GetUserProfileRequest.Builder builder = GetUserProfileRequest.newBuilder();
        if (uid != null){
            builder.setUid(uid);
        }
        if (email != null){
            builder.setEmail(email);
        }
        if (phone != null){
            builder.setPhone(phone);
        }
        if (emailHash != null){
            builder.setEmailHash(emailHash);
        }
        if (phoneHash != null){
            builder.setPhoneHash(phoneHash);
        }
        GetUserProfileRequest getUserProfileRequest = builder.build();
        final GetUserProfileResponse getUserProfileResponse = accountBlockingStub.getUserProfile(getUserProfileRequest);
        final BaseResponse base = getUserProfileResponse.getBase();
        if (base.getStatus() != 0) {
            logger.error("getUserProfile error,status:{},reason:{},uid :{}", base.getStatus(), base.getReason(), uid);
            return  null;
        }
        return new UserProfile(getUserProfileResponse.getUid(),getUserProfileResponse.getEmailHash(),getUserProfileResponse.getPhoneHash());
    }

    public String genEmailVcode(String email) {
        final GenEmailVcodeResponse genEmailVcodeResponse = accountBlockingStub.genEmailVerificationCode(GenEmailVcodeRequest.newBuilder().setEmail(email).build());
        final BaseResponse base = genEmailVcodeResponse.getBase();
        if (base.getStatus() != 0) {
            logger.error("genEmailVcode error,status:{},reason:{},", base.getStatus(), base.getReason() );
            return  null;
        }
        return genEmailVcodeResponse.getVcode();
    }

    public UserProfile hashUserMetadata(String email, String phone) {
        final HashUserMetaRequest.Builder builder = HashUserMetaRequest.newBuilder();
        if (email != null) builder.setEmail(email);
        if (phone != null) builder.setPhone(phone);
        HashUserMetaResponse hashUserMetadataResponse = accountBlockingStub.hashUserMeta(builder.build());
        final BaseResponse base = hashUserMetadataResponse.getBase();
        if (base.getStatus() != 0) {
            logger.error("hashUserMetadata error,status:{},reason:{},", base.getStatus(), base.getReason() );
            return  null;
        }
        return new UserProfile(null,hashUserMetadataResponse.getEmailHash(),hashUserMetadataResponse.getPhoneHash());
    }

    public BaseResponse checkEmailVerify(String email, String emailHash, String code){
        final CheckEmailVcodeRequest.Builder builder = CheckEmailVcodeRequest.newBuilder();
        if (email != null && !email.isEmpty()) builder.setEmail(email);
        if (emailHash != null && !emailHash.isEmpty()) builder.setEmailHash(emailHash);
        builder.setVcode(code);
        final CheckEmailVcodeResponse checkEmailVcodeResponse = accountBlockingStub.checkEmailVerificationCode(builder.build());
        final BaseResponse base = checkEmailVcodeResponse.getBase();
        if (base.getStatus() != 0) {
            logger.error("checkEmailVerify error,emailHash:{}, status:{},reason:{},", emailHash,base.getStatus(), base.getReason() );
            return  base;
        }
        return base;
    }

    public void delUserHash(String uid, boolean delEmail, boolean delPhone){
        final DelUserHashRequest.Builder builder = DelUserHashRequest.newBuilder();
        if (uid != null && !uid.isEmpty()) builder.setUid(uid);
        builder.setDelEmail(delEmail);
        builder.setDelPhone(delPhone);
        final DelUserHashResponse delUserHashResponse = accountBlockingStub.delUserHash(builder.build());
        final BaseResponse base = delUserHashResponse.getBase();
        if (base.getStatus() != 0) {
            logger.error("delUserHash error,status:{},reason:{},", base.getStatus(), base.getReason() );
        }
    }

    public void disableSearch(String uid) {
        final DisableSearchRequest.Builder builder = DisableSearchRequest.newBuilder();
        if (uid != null && !uid.isEmpty()) builder.setUid(uid);
        final DisableSearchResponse disableSearchResponse = accountBlockingStub.disableSearch(builder.build());
        final BaseResponse base = disableSearchResponse.getBase();
        logger.info("disableSearch status:{},reason:{},", base.getStatus(), base.getReason());
        if (base.getStatus() != 0) {
            logger.error("disableSearch error,uid:{},status:{},reason:{},", uid, base.getStatus(), base.getReason());
        }
    }

    public void DeleteAccount(String number) {
        final DeleteAccResp deleteAccResp = accountBlockingStub.deleteAccount(DeleteAccReq.newBuilder().setUid(number).build());
        final BaseResponse base = deleteAccResp.getBase();
        logger.info("DeleteAccount status:{},reason:{},", base.getStatus(), base.getReason());
    }


    //
    public SourceDescribeResult getSourceDescribe(List<String> src,String dst,String lang, String sourceQueryType){
        final HowToMetRequest build = HowToMetRequest.newBuilder().setDst(dst).setLang(lang).setSourceQueryType(sourceQueryType).addAllSrc(src).build();
        final HowToMetResponse howToMetResponse = friendBlockingStub.howToMet(build);
        final BaseResponse base = howToMetResponse.getBase();
        if (base.getStatus() != 0) {
            logger.error("getSourceDescribe error,status:{},reason:{},", base.getStatus(), base.getReason() );
            return  null;
        }
        return new SourceDescribeResult( howToMetResponse.getDescribeList(), howToMetResponse.getFindyouList());
    }


    public static class  SourceDescribeResult{
        List<String> describeList;
        List<String> findyouList;

        public SourceDescribeResult(List<String> describeList, List<String> findyouList) {
            this.describeList = describeList;
            this.findyouList = findyouList;
        }

        public List<String> getDescribeList() {
            return describeList;
        }

        public List<String> getFindyouList() {
            return findyouList;
        }
    }
}
