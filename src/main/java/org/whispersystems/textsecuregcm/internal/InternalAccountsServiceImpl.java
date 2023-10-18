package org.whispersystems.textsecuregcm.internal;

import com.aliyun.oss.HttpMethod;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.InternalAccount.InternalAccountManager;
import org.whispersystems.textsecuregcm.controllers.NoSuchUserException;
import org.whispersystems.textsecuregcm.controllers.RateLimitExceededException;
import org.whispersystems.textsecuregcm.entities.AuthenticationOktaResponse;
import org.whispersystems.textsecuregcm.entities.ConversationNotify;
import org.whispersystems.textsecuregcm.exceptions.NumberIsBindingOtherPuidException;
import org.whispersystems.textsecuregcm.exceptions.PuidIsRegisteringException;
import org.whispersystems.textsecuregcm.exceptions.UserDisabledException;
import org.whispersystems.textsecuregcm.internal.accounts.*;
import org.whispersystems.textsecuregcm.internal.common.BaseResponse;
import org.whispersystems.textsecuregcm.internal.common.STATUS;
import org.whispersystems.textsecuregcm.internal.common.Step;
import org.whispersystems.textsecuregcm.s3.UrlSignerAli;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.AvatarEnc;

import java.io.IOException;
import java.util.*;

public class InternalAccountsServiceImpl extends InternalAccountsServiceGrpc.InternalAccountsServiceImplBase {

    final private Logger logger = LoggerFactory.getLogger(InternalAccountsServiceImpl.class);

    private final ConversationManager conversationManager;
    final private AccountsManager accountsManager;

    final private InternalAccountManager internalAccountManager;
    final private InternalServicePermissionService internalServicePermissionService;
    final private String MGMT_PID = "mgmt";

    final private UrlSignerAli urlSignerAli;

    public InternalAccountsServiceImpl(ConversationManager conversationManager, UrlSignerAli urlSignerAli, AccountsManager accountsManager,
                                       InternalAccountManager internalAccountManager,
                                       InternalServicePermissionService internalServicePermissionService) {
        this.conversationManager = conversationManager;
        this.urlSignerAli = urlSignerAli;
        this.accountsManager = accountsManager;
        this.internalAccountManager = internalAccountManager;
        this.internalServicePermissionService = internalServicePermissionService;
    }

    @Override
    public void getInfo(UidsRequest request, StreamObserver<AccountInfoResponse> responseObserver) {
        AccountInfoResponse response = getInfo(request.getUidsList());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    //@Override
    //public void getInfoByEmail(EmailsRequest request, StreamObserver<AccountInfoResponse> responseObserver) {
    //    List<String> uids = new ArrayList<>();
    //    for (String email : request.getEmailsList()) {
    //        List<Optional<Account>> accounts = accountsManager.getByEmail(email);
    //        if (accounts.isEmpty()) {
    //            logger.warn("getInfoByEmail(): no such user: " + email);
    //            continue;
    //        }
    //        for(Optional<Account> oaccount : accounts){
    //            Account account = oaccount.get();
    //            uids.add(account.getNumber());
    //        }
    //    }
    //    AccountInfoResponse response = getInfo(uids);
    //
    //    responseObserver.onNext(response);
    //    responseObserver.onCompleted();
    //}

    @Override
    public void disable(UidsRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder base = BaseResponse.newBuilder();
        base.setVer(1);
        List<Account> accounts = new ArrayList<>();
        for (String uid : request.getUidsList()) {
            Optional<Account> oaccount = accountsManager.get(uid);
            if (!oaccount.isPresent() || oaccount.get().isDisabled()) {
                logger.warn("disable(): no such user or is disabled: " + uid);
                //
                base.setStatus(STATUS.NO_SUCH_USER_VALUE);
                base.setReason("disable: no such user or user is disabled");
                responseObserver.onNext(base.build());
                responseObserver.onCompleted();
                return;
            }
            if (StringUtils.isNotBlank(request.getAppid())) {
                boolean hasObjectPermissionForApp = internalServicePermissionService.isHasObjectPermissionForApp(request.getAppid(), oaccount.get());
                if (!hasObjectPermissionForApp) {
                    base.setStatus(STATUS.NO_PERMISSION_VALUE);
                    base.setReason("disable: no permission");
                    responseObserver.onNext(base.build());
                    responseObserver.onCompleted();
                    return;
                }
            }
            accounts.add(oaccount.get());
        }
        if (accounts.size() > 0) {
            accountsManager.disable(accounts);
        }
        base.setStatus(STATUS.OK_VALUE);
        responseObserver.onNext(base.build());
        responseObserver.onCompleted();
    }

    @Override
    public void enable(UidsRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder base = BaseResponse.newBuilder();
        base.setVer(1);
        List<Account> accounts = new ArrayList<>();
        for (String uid : request.getUidsList()) {
            Optional<Account> oaccount = accountsManager.get(uid);
            if (!oaccount.isPresent() || !oaccount.get().isDisabled()) {
                logger.warn("enable(): no such user or is enabled: " + uid);
                //
                base.setStatus(STATUS.NO_SUCH_USER_VALUE);
                base.setReason("enable: no such user or user is enabled");
                responseObserver.onNext(base.build());
                responseObserver.onCompleted();
                return;
            }
            if (StringUtils.isNotBlank(request.getAppid())) {
                boolean hasObjectPermissionForApp = internalServicePermissionService.isHasObjectPermissionForApp(request.getAppid(), oaccount.get());
                if (!hasObjectPermissionForApp) {
                    base.setStatus(STATUS.NO_PERMISSION_VALUE);
                    base.setReason("enable: no permission");
                    responseObserver.onNext(base.build());
                    responseObserver.onCompleted();
                    return;
                }
            }
            accounts.add(oaccount.get());
        }
        if (accounts.size() > 0) {
            accountsManager.enable(accounts);
        }
        base.setStatus(STATUS.OK_VALUE);
        responseObserver.onNext(base.build());
        responseObserver.onCompleted();
    }

    private AccountInfoResponse getInfo(List<String> uids) {
        AccountInfoResponse.Builder responseBuilder = AccountInfoResponse.newBuilder();
        for (String uid : uids) {
            Optional<Account> oaccount = accountsManager.get(uid);
            if (!oaccount.isPresent()) {
                logger.warn("getInfo(): no such user: " + uid);
                continue;
            }
            Account account = oaccount.get();

            AccountInfoResponse.AccountInfo.Builder accountInfoBuilder = AccountInfoResponse.AccountInfo.newBuilder();
            accountInfoBuilder.setNumber(account.getNumber());
            for (Device device : account.getDevices()) {
                accountInfoBuilder.addDevices(device.getId());
            }
            accountInfoBuilder.setIdentityKey(Strings.nullToEmpty(account.getIdentityKey()));
            accountInfoBuilder.setName(Strings.nullToEmpty(account.getName()));
            accountInfoBuilder.setAvatar(Strings.nullToEmpty(account.getAvatar()));
            accountInfoBuilder.setAvatarDigest(Strings.nullToEmpty(account.getAvatarDigest()));
            if (account.getPin().isPresent()) {
                accountInfoBuilder.setPin(account.getPin().get());
            } else {
                accountInfoBuilder.setPin("");
            }
            accountInfoBuilder.setPlainName(Strings.nullToEmpty(account.getPlainName()));
            accountInfoBuilder.setPushType(Strings.nullToEmpty(account.getPushType()));
            accountInfoBuilder.setPushToken(Strings.nullToEmpty(account.getPushToken()));
            accountInfoBuilder.setVcode(account.getVcode());
            accountInfoBuilder.setRegistered(account.isRegistered());
            accountInfoBuilder.setInvitationPerDay(account.getInvitationPerDay());
            accountInfoBuilder.setDisabled(account.isDisabled());
            accountInfoBuilder.setEmail("");
            accountInfoBuilder.setPhone("");
            accountInfoBuilder.setOktaId(Strings.nullToEmpty(account.getOktaId()));
            accountInfoBuilder.setTimeZone(Strings.nullToEmpty(account.getTimeZone()));
            if (account.getPublicConfig(AccountExtend.FieldName.PUBLIC_NAME) != null) {
                String nickName=(String) account.getPublicConfig(AccountExtend.FieldName.PUBLIC_NAME);
                if(nickName.equals(account.getNumber())){
                    nickName=account.getPublicName(account.getPlainName());
                }
                accountInfoBuilder.setNickname(nickName);
            }
            if(StringUtils.isNotBlank(account.getAvatar2())){
                accountInfoBuilder.setAvatar(account.getAvatar2());
            }
            if(StringUtils.isNotBlank(account.getSignature())){
                accountInfoBuilder.setSignature(account.getSignature());
            }
            responseBuilder.addAccounts(accountInfoBuilder.build());
        }

        return responseBuilder.build();
    }

    @Override
    public void getAll(Step request, StreamObserver<AccountInfoResponse> responseObserver) {
        AccountInfoResponse.Builder responseBuilder = AccountInfoResponse.newBuilder();
        int offset = request.getOffset();
        int length = request.getLength();
        List<InternalAccountsTeamRow> accounts = new ArrayList<>();
        long accountsTotal = 0l;
        if(StringUtils.isEmpty(request.getEmail())){
            accounts = internalAccountManager.getAccountsList(offset, length,
                    null, request.getName(), request.getNumber(), request.getTeamsId(), request.getDisabledList());
            accountsTotal = internalAccountManager.getAccountsTotal(null, request.getName(),
                    request.getNumber(), request.getTeamsId(), request.getDisabledList());
        } else {
            String[] split = request.getEmail().split(",");
            List<String> list = new ArrayList<String>();
            for(String str : split){
                list.add(str);
            }
            accounts = internalAccountManager.getAccountsListByTeam(offset, length,
                    null, request.getName(), request.getNumber(), list, request.getDisabledList());
            accountsTotal = internalAccountManager.getAccountsTotalByTeamByTeam(null, request.getName(),
                    request.getNumber(), list, request.getDisabledList());
        }

        for (InternalAccountsTeamRow account : accounts) {
//            if ( account.isDisabled() ) { //  registered 状态可能不准
//                continue;
//            }
            AccountInfoResponse.AccountInfo.Builder accountInfoBuilder = AccountInfoResponse.AccountInfo.newBuilder();
            accountInfoBuilder.setNumber(account.getNumber());
            accountInfoBuilder.setName(account.getName());
            accountInfoBuilder.setDisabled(account.isDisabled());
            accountInfoBuilder.setRegistered(account.isRegistered());
            accountInfoBuilder.setInactive(account.isInactive());
            accountInfoBuilder.setVcode(account.getVcode());
            if (StringUtils.isNotBlank(account.getOktaId())) {
                accountInfoBuilder.setOktaId(account.getOktaId());
            }
            if (StringUtils.isNotBlank(account.getOkta_org())) {
                accountInfoBuilder.setOktaOrg(account.getOkta_org());
            }
            accountInfoBuilder.setPlainName(account.getName());
            accountInfoBuilder.setTeamName(StringUtils.isNotBlank(account.getTeamName()) ? account.getTeamName() : "");
            accountInfoBuilder.setTeamId(StringUtils.isNotBlank(account.getTeamId()) ? account.getTeamId() : "");

            Optional<Account> accountOptional = accountsManager.get(account.getNumber());
            if(accountOptional.isPresent()){
                Account account1 = accountOptional.get();
                if(StringUtils.isNotBlank(account1.getAvatar2())){
                    accountInfoBuilder.setAvatar(account1.getAvatar2());
                }
                if(StringUtils.isNotBlank(account1.getSignature())){
                    accountInfoBuilder.setSignature(account1.getSignature());
                }
            }
            responseBuilder.addAccounts(accountInfoBuilder.build());

        }
        responseBuilder.setTotal(accountsTotal);
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void edit(AccountInfoRequest request, StreamObserver<BaseResponse> responseObserver) {

        String appid = request.getAppid();
        String pid = request.getPid();
        boolean hasObjectPermissionForApp = internalServicePermissionService.isExists(pid, appid);

        BaseResponse.Builder builder = BaseResponse.newBuilder();
        builder.setVer(1);

        if (!hasObjectPermissionForApp) {
            builder.setStatus(STATUS.NO_PERMISSION_VALUE);
        } else {
            Optional<Account> account = Optional.empty();
            if(StringUtils.isNotBlank(request.getNumber())) {
                account = accountsManager.get(request.getNumber());
            }
            //} else {
            //    account = accountsManager.getByEmail(request.getOktaOrg(), request.getEmail());
            //}
            if ( account.isPresent()) {
                Account accountNew = account.get();
                //if (StringUtils.isNotBlank(request.getEmail()) && StringUtils.isNotBlank(request.getNumber())) {
                //    List<Optional<Account>> emails = accountsManager.getByEmail(request.getEmail());
                //    if (!emails.isEmpty()) {
                //        for(Optional<Account> emailOpt : emails){
                //            Account emailAccount = emailOpt.get();
                //            if (!emailAccount.getNumber().equals(accountNew.getNumber())) {
                //                builder.setStatus(STATUS.USER_EMAIL_EXIST_VALUE);
                //                builder.setReason("email exist");
                //                responseObserver.onNext(builder.build());
                //                responseObserver.onCompleted();
                //                return;
                //            }
                //        }
                //    }
                //}

                if (StringUtils.isNotBlank(request.getOktaId())) {
                    String oktaId = request.getOktaId();
                    Optional<Account> byOkta = accountsManager.getByOktaID(request.getOktaOrg(),oktaId);
                    if(byOkta.isPresent()){
                        Account oktaAccount = byOkta.get();
                        if (!oktaAccount.getNumber().equals(accountNew.getNumber())) {
                            builder.setStatus(STATUS.USER_OKTAID_EXIST_VALUE);
                            builder.setReason("oktaid exist");
                            responseObserver.onNext(builder.build());
                            responseObserver.onCompleted();
                            return;
                        }
                    }
                }
                if(StringUtils.isNotBlank(request.getNumber())){
                    accountNew.setPlainName(request.getName());
                    if (pid.equals(MGMT_PID)) {
                        boolean delEmail = false,delPhone = false;
                        if (request.hasEmail() && request.getEmail().equals("del@del.com")) delEmail = true;
                        if (request.hasPhone() && request.getPhone().equals("del")) delPhone = true;
                        if(delEmail || delPhone) internalAccountManager.delUserHash(request.getNumber(),delEmail,delPhone);
                        accountNew.setOktaId(request.getOktaId());
                        accountNew.setSignature(request.getSignature());
                        accountNew.setAvatar2(request.getAvatar());
                    }
                }

                builder.setStatus(STATUS.INVALID_PARAMETER_VALUE);
                accountsManager.update(accountNew, null, false);
                builder.setStatus(STATUS.OK_VALUE);
            } else {
                builder.setStatus(STATUS.OTHER_ERROR_VALUE);
            }
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }


    @Override
    public void renew(AccountInfoRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder builder = BaseResponse.newBuilder();
        builder.setVer(1);

        int code = 0;
        try {
            code = accountsManager.renew(request.getNumber());
            builder.setStatus(STATUS.OK_VALUE);
        } catch (NoSuchUserException e) {
            builder.setStatus(STATUS.SERVER_INTERNAL_ERROR_VALUE);
        }

        RenewResponse.Builder renewRes = RenewResponse.newBuilder();
        renewRes.setVcode(code);
        builder.setData(Any.pack(renewRes.build()));

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void createAccount(AccountCreateRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder builder = BaseResponse.newBuilder();
        builder.setVer(1);
        try {
            String pid = request.getPid();
            String appid = request.getDeviceInfo().getAppid();
            if (!internalServicePermissionService.isExists(pid, appid)) {
                builder.setStatus(STATUS.NO_PERMISSION_VALUE);
            } else {
                Account account = internalAccountManager.registerForOpenApi(request);
                builder.setStatus(STATUS.OK_VALUE);
                AccountCreateResponse.Builder responseBuilder = AccountCreateResponse.newBuilder();
                responseBuilder.setNumber(account.getNumber());
                builder.setData(Any.pack(responseBuilder.build()));
            }
        } catch (RateLimitExceededException e) {
            builder.setStatus(STATUS.RATE_LIMIT_EXCEEDED_VALUE);
        } catch (NoSuchUserException e) {
            builder.setStatus(STATUS.INVALID_INVITER_VALUE);
        } catch (UserDisabledException e) {
            builder.setStatus(STATUS.USER_IS_DISABLED_VALUE);
        } catch (PuidIsRegisteringException e) {
            builder.setStatus(STATUS.PUID_IS_REGISTERING_VALUE);
        } catch (NumberIsBindingOtherPuidException e) {
            builder.setStatus(STATUS.NUMBER_IS_BINDING_OTHER_PUID_VALUE);
        } catch (Exception e) {
            builder.setStatus(STATUS.OTHER_ERROR_VALUE);
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    void setAvatarInfo(AccountQueryInfo.Builder accountInfoBuilder,Account account){
        final String avatar = account.getAvatar2();
        if (avatar == null || avatar.equals("")) return;
        final JsonObject jsonObject = new Gson().fromJson(avatar, JsonObject.class);
        try {
            JsonElement jsonElement = jsonObject.get("attachmentId");
            final long attachmentId = jsonElement.getAsLong();
            if (attachmentId == 0)return;
            final AvatarInfo.Builder builderAvatar = AvatarInfo.newBuilder();
            builderAvatar.setId(jsonElement.getAsString());
            builderAvatar.setUrl(urlSignerAli.getPreSignedUrl(attachmentId, HttpMethod.GET).toExternalForm());
            builderAvatar.setEncAlgo(jsonObject.get("encAlgo").getAsString());
            builderAvatar.setEncKey(jsonObject.get("encKey").getAsString());
            accountInfoBuilder.setAvatarInfo(builderAvatar);
        } catch (Exception e) {
            logger.error("build avatar info,Exception:", e);
        }

    }


    @Override
    public void kickOffDevice(AccountInfoRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder builder = BaseResponse.newBuilder();
        builder.setVer(1);
        String number = request.getNumber();
        Optional<Account> accountOptional = accountsManager.get(number);
        if(!accountOptional.isPresent()){
            builder.setStatus(STATUS.NO_SUCH_USER_VALUE);
        } else {
            Account existingAccount = accountOptional.get();
            // kick off and delete devices
            for (Device device : existingAccount.getDevices()) {
                accountsManager.update(existingAccount, device,true);
                accountsManager.kickOffDevice(existingAccount.getNumber(), device.getId());
            }
            builder.setStatus(STATUS.OK_VALUE);
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getUserTeams(TeamRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder builder = BaseResponse.newBuilder();
        builder.setVer(1);
        String number = request.getWuid();
        final long start = System.currentTimeMillis();
        final Set<String> userTeams = accountsManager.getUserTeams(number);
        logger.warn("getUserTeams cost:{},number:{}",System.currentTimeMillis()-start,number);

        final TeamResponse.Builder builderTeams = TeamResponse.newBuilder();
        for (String team : userTeams) {
            builderTeams.addTeams(team);
        }
        builder.setData(Any.pack(builderTeams.build()));
        builder.setStatus(STATUS.OK_VALUE);

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void upload(UploadRequest request, StreamObserver<BaseResponse> responseObserver) {
        final ByteString avatarContent = request.getContent();

        BaseResponse.Builder builder = BaseResponse.newBuilder();
        builder.setVer(1);
        final int size = avatarContent.size();
        if (size < 10 || size > 10*1024*1024){ // 检查 image size
            // 检查image type
            builder.setStatus(STATUS.INVALID_PARAMETER_VALUE);
            builder.setReason("error image size");
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
            return;
        }
        if( !avatarContent.substring(1, 4).toStringUtf8().equals("PNG") &&
                !avatarContent.substring(6, 10).toStringUtf8().equals("JFIF") ){ // 检查image type
            builder.setStatus(STATUS.OTHER_ERROR_VALUE);
            builder.setReason("error image type");
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
            return;
        }

        try {
            HashMap<String, Object> avatar = AvatarEnc.uploadAvatar(avatarContent.toByteArray(), urlSignerAli);
//            account.setAvatar2(new Gson().toJson(avatar));
            UploadResponse.Builder resBuild = UploadResponse.newBuilder();
            Set<String> setString = avatar.keySet();
            for(String key : setString){
                resBuild.putMap(key, avatar.get(key).toString());
            }
            builder.setData(Any.pack(resBuild.build()));
            builder.setStatus(0);
            builder.setReason("OK");
        } catch (IOException e) {
            builder.setStatus(STATUS.OTHER_ERROR_VALUE);
            builder.setReason("IOException:" + e.getMessage());
            logger.warn("uploadAvatar IOException:", e);
        } catch (Exception e) {
            builder.setStatus(STATUS.OTHER_ERROR_VALUE);
            builder.setReason("Exception:" + e.getMessage());
            logger.warn("uploadAvatar Exception:", e);
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void syncAccountBuInfo(SyncAccountBuRequest request, StreamObserver<BaseResponse> responseObserver) {
    }

    @Override
    public void inactive(UidsRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder base = BaseResponse.newBuilder();
        base.setVer(1);
        List<Account> accounts = new ArrayList<>();
        Set<String> reloadTeams = new HashSet<>();
        for (String uid : request.getUidsList()) {
            Optional<Account> oaccount = accountsManager.get(uid);
            if (!oaccount.isPresent() || oaccount.get().isDisabled()||oaccount.get().isInactive()) {
                logger.warn("inactive(): no such user or is disabled or is inactive :" + uid);
                //
                base.setStatus(STATUS.NO_SUCH_USER_VALUE);
                base.setReason("inactive: no such user or user is disabled or is inactive :"+uid);
                responseObserver.onNext(base.build());
                responseObserver.onCompleted();
                return;
            }
            if (StringUtils.isNotBlank(request.getAppid())) {
                boolean hasObjectPermissionForApp = internalServicePermissionService.isHasObjectPermissionForApp(request.getAppid(), oaccount.get());
                if (!hasObjectPermissionForApp) {
                    base.setStatus(STATUS.NO_PERMISSION_VALUE);
                    base.setReason("inactive: no permission");
                    responseObserver.onNext(base.build());
                    responseObserver.onCompleted();
                    return;
                }
            }
        }
        if (accounts.size() > 0) {
            accountsManager.inactive(accounts,reloadTeams);
        }
        base.setStatus(STATUS.OK_VALUE);
        responseObserver.onNext(base.build());
        responseObserver.onCompleted();
    }

    @Override
    public void downloadAvatar(AccountInfoRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder base = BaseResponse.newBuilder();
        base.setVer(1);

        String avatar = request.getAvatar();
        if(StringUtils.isBlank(avatar)){
            logger.error("avatar is null");
            base.setStatus(STATUS.OK_VALUE);
            responseObserver.onNext(base.build());
            responseObserver.onCompleted();
        }
        Map<String, String> map = new Gson().fromJson(avatar, Map.class);
        String encKey = map.get("encKey");
        String attachmentId = map.get("attachmentId");
        try {
            byte[] bytes = AvatarEnc.downloadAvatar(attachmentId, encKey, urlSignerAli);
            DownloadAvatarResponse.Builder builder = DownloadAvatarResponse.newBuilder();
            builder.setAvatarContent(ByteString.copyFrom(bytes));
            base.setData(Any.pack(builder.build()));
            base.setStatus(STATUS.OK_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("downloadAvatar Exception: {}", e.getMessage());
            base.setStatus(STATUS.OTHER_ERROR_VALUE);
            base.setReason("Exception:" + e.getMessage());
        }
        responseObserver.onNext(base.build());
        responseObserver.onCompleted();
    }

    @Override
    public void genLoginInfo(LoginInfoReq request, StreamObserver<BaseResponse> responseObserver) {
        final AuthenticationOktaResponse authenticationOktaResponse = internalAccountManager.GenLoginInfo(
                request.getUid(), request.getUa(), request.getSupportTransfer());
        BaseResponse.Builder base = BaseResponse.newBuilder();
        base.setVer(1);
        if (authenticationOktaResponse == null) {
            base.setStatus(STATUS.NO_SUCH_USER_VALUE);
            base.setReason("no such user");
        } else {
            base.setStatus(STATUS.OK_VALUE);
            LoginInfoRes.Builder builder = LoginInfoRes.newBuilder();
            if (authenticationOktaResponse.getAccount() != null)
                builder.setAccount(authenticationOktaResponse.getAccount());
            builder.setTransferable(authenticationOktaResponse.getTransferable());
            if (authenticationOktaResponse.getRequirePin() != null)
                builder.setRequirePin(authenticationOktaResponse.getRequirePin());
            else
                builder.setRequirePin(false);
            if(authenticationOktaResponse.getVerificationCode()!=null){
                builder.setVerificationCode(authenticationOktaResponse.getVerificationCode());
            }
            final AuthenticationOktaResponse.TransferTokens tokens = authenticationOktaResponse.getTokens();
            if (tokens != null)
                builder.setTokens(LoginInfoRes.Token.newBuilder().setLogintoken(tokens.getLogintoken()).
                        setTdtoken(tokens.getTdtoken()));
            base.setData(Any.pack(builder.build()));
        }
        responseObserver.onNext(base.build());
        responseObserver.onCompleted();
    }

    @Override
    public void blockConversation(BlockConversationRequest request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder base = BaseResponse.newBuilder();
        base.setVer(1);
        boolean isChange = false;
        final Optional<Account> accountOptional = accountsManager.get(request.getOperator());
        if (!accountOptional.isPresent()) {
            logger.error("blockConversation(): no such user :" + request.getOperator());
            base.setStatus(STATUS.NO_SUCH_USER_VALUE);
            base.setReason("no such user :" + request.getOperator());
            responseObserver.onNext(base.build());
            responseObserver.onCompleted();
            return;
        }
        ConversationNotify.ChangeType changeType=ConversationNotify.ChangeType.BLOCK;
        Conversation conversation = conversationManager.get(request.getOperator(), request.getConversationId());
        if(conversation==null) {
            conversation = new Conversation(request.getOperator(), request.getConversationId(), "",
                    System.currentTimeMillis(), null,
                    null, null, request.getBlock(), null, 1);
            conversation = conversationManager.store(conversation);
            isChange = true;
        } else {
            if(conversation.getBlockStatus()==null || conversation.getBlockStatus()!= request.getBlock()){
                conversation.setBlockStatus(request.getBlock());

                conversation = conversationManager.update(conversation,changeType);
                isChange = true;
            }
        }
        if(isChange) {
            conversationManager.sendConversationNotify(changeType.ordinal(), accountOptional.get(), conversation);
        }

        responseObserver.onNext(base.setStatus(0).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getConversationBlockStatus(GetConversationBlockReq request, StreamObserver<BaseResponse> responseObserver) {
        BaseResponse.Builder base = BaseResponse.newBuilder();
        base.setVer(1);
        final Conversation conversation = conversationManager.get(request.getOperator(), request.getConversationId());
        GetConversationBlockResp.Builder builder = GetConversationBlockResp.newBuilder();
        if (conversation == null) {
            builder.setBlockStatus(0);
        } else {
            builder.setBlockStatus(conversation.getBlockStatus());
        }
        responseObserver.onNext(base.setStatus(0).setData(Any.pack(builder.build())).build());
        responseObserver.onCompleted();
    }

    public static void main(String[] args) {
        AccountQueryInfo.Builder accountInfoBuilder = AccountQueryInfo.newBuilder();
        accountInfoBuilder.setEmail("aaa");
//        accountInfoBuilder.setNickname(null);
        accountInfoBuilder.setWuid("aa");
        accountInfoBuilder.build();
    }
}
