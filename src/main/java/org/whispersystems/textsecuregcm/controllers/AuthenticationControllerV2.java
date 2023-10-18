package org.whispersystems.textsecuregcm.controllers;

import com.codahale.metrics.annotation.Timed;
import com.okta.jwt.*;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.InternalAccount.InternalAccountManager;
import org.whispersystems.textsecuregcm.configuration.OktaConfiguration;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.util.StringUtil;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/v2/auth")
public class AuthenticationControllerV2 {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationControllerV2.class);

    private final AccountsManager accountsManager;
    private final InternalAccountManager internalAccountManager;

    private final Map<String, OktaConfiguration> oktaConfigurationMap = new HashMap<>();
    private final Map<String, AccessTokenVerifier> accessTokenVerifierMap = new HashMap<>();
    private final Map<String, IdTokenVerifier> idTokenVerifierMap = new HashMap<>();

    public AuthenticationControllerV2(AccountsManager accountsManager, InternalAccountManager internalAccountManager, List<OktaConfiguration> okta) {
        this.accountsManager = accountsManager;
        this.internalAccountManager = internalAccountManager;
        for(OktaConfiguration oktaConfiguration : okta){
            oktaConfigurationMap.put(oktaConfiguration.getDomain(), oktaConfiguration);
        }
        Set<String> domainSet = oktaConfigurationMap.keySet();
        Iterator<String> iterator = domainSet.iterator();
        while (iterator.hasNext()){
            String domain = iterator.next();
            OktaConfiguration oktaConfiguration = oktaConfigurationMap.get(domain);
            AccessTokenVerifier domainAccessToken = JwtVerifiers.accessTokenVerifierBuilder()
                    .setIssuer(oktaConfiguration.getIssuer())
                    .setAudience(oktaConfiguration.getAudience())
                    .setConnectionTimeout(Duration.ofSeconds(5))
                    .setRetryMaxAttempts(3)
                    .setRetryMaxElapsed(Duration.ofSeconds(10)) // defaults to 10s
                    .build();
            accessTokenVerifierMap.put(domain, domainAccessToken);
            IdTokenVerifier domainIdToken = JwtVerifiers.idTokenVerifierBuilder()
                    .setIssuer(oktaConfiguration.getIssuer())
                    .setClientId(oktaConfiguration.getClientId())
                    .setConnectionTimeout(Duration.ofSeconds(5))    // defaults to 1s
                    .setRetryMaxAttempts(3)                     // defaults to 2
                    .setRetryMaxElapsed(Duration.ofSeconds(10)) // defaults to 10s
                    .build();
            idTokenVerifierMap.put(domain, domainIdToken);
        }

    }

    @Timed
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login/address")
    public BaseResponse domain(@Valid AuthenticationDomainCheckRequest domainCheckRequest){
        if (!domainCheckRequest.validate()) {
            BaseResponse.err(400, BaseResponse.STATUS.INVALID_PARAMETER,"invalid parameters", logger, null);
        }
        String domain = domainCheckRequest.getDomain();
        OktaConfiguration oktaConfiguration = oktaConfigurationMap.get(domain.toLowerCase());
        if(null == oktaConfiguration ){
            BaseResponse.err(403, BaseResponse.STATUS.NO_SUCH_FILE, "invalid domain", logger, null);
        }
        return BaseResponse.ok(oktaConfiguration);
    }

    //@Timed
    //@POST
    //@Consumes(MediaType.APPLICATION_JSON)
    //@Produces(MediaType.APPLICATION_JSON)
    //@Path("/okta")
    //public BaseResponse okta(
    //        @Valid AuthenticationOktaRequestV2 auth
    //) {
    //    // verify tokens
    //    Jwt accessTokenJwt = null;
    //    try {
    //        AccessTokenVerifier accessTokenVerifier = accessTokenVerifierMap.get(auth.getDomain().toLowerCase());
    //        if(null == accessTokenVerifier){
    //            BaseResponse.err(403, BaseResponse.STATUS.NO_SUCH_FILE, "invalid domain", logger, null);
    //        }
    //        accessTokenJwt = accessTokenVerifier.decode(auth.getAccessToken());
    //    } catch (JwtVerificationException e) {
    //        logger.error("invalid access token", e);
    //        BaseResponse.err(401, BaseResponse.STATUS.INVALID_TOKEN, "nvalid token", logger, new AuthenticationOktaResponse(5, 0, "", "", "", false));
    //    }
    //    Jwt idTokenJwt = null;
    //    try {
    //        IdTokenVerifier idTokenVerifier = idTokenVerifierMap.get(auth.getDomain().toLowerCase());
    //        idTokenJwt = idTokenVerifier.decode(auth.getIdToken(), auth.getNonce());
    //    } catch (JwtVerificationException e) {
    //        logger.error("invalid id token", e);
    //        BaseResponse.err(401, BaseResponse.STATUS.INVALID_TOKEN, "nvalid token", logger, new AuthenticationOktaResponse(5, 0, "", "", "", false));
    //    }
    //
    //    // get information
    //    String sub = (String) idTokenJwt.getClaims().get("sub");
    //    String idp = (String) idTokenJwt.getClaims().get("idp");
    //    String preferred_username = (String) idTokenJwt.getClaims().get("preferred_username");
    //    AuthenticationOktaResponse authenticationOktaResponse = internalAccountManager.oktaInvitation(idp,sub, preferred_username);
    //    logger.info("oktaInvitation returned, idp:{} ，sub:{},preferred_username:{},requirePin:{}",idp, sub, preferred_username.toLowerCase(), authenticationOktaResponse.getRequirePin());
    //    if(authenticationOktaResponse.getStatus() == 2){
    //        BaseResponse.err(403, BaseResponse.STATUS.USER_IS_DISABLED, "the account is disabled", logger, new AuthenticationOktaResponse(14, 0, "", "", "", false));
    //    }
    //    return BaseResponse.ok(authenticationOktaResponse);
    //}
    //
    @Timed
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/bind/send")
    public BaseResponse sendCodeForBind(@Auth Account account, @Valid SendCodeForBindRequest sendCodeForBindRequest) throws MessagingException, RateLimitExceededException {
        if(sendCodeForBindRequest==null||StringUtil.isEmpty(sendCodeForBindRequest.getEmail())){
            BaseResponse.err(403,BaseResponse.STATUS.INVALID_PARAMETER,"invalid parameter", logger, null);
            return null;
        }
        Pattern p = Pattern.compile(AuthenticationController.emailReg,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sendCodeForBindRequest.getEmail());
        if(!m.matches()) {
            BaseResponse.err(403,BaseResponse.STATUS.INVALID_PARAMETER,"invalid parameter", logger, null);
        }
        final UserProfile userProfile = internalAccountManager.getUserProfile(account.getNumber(),null,null,null,null);
        if (userProfile == null) {
            logger.error("user profile not found,uid:{}", account.getNumber());
            BaseResponse.err(404,BaseResponse.STATUS.OTHER_ERROR,"Internal Error", logger, null);
            return null;
        }
        final String emailHash = internalAccountManager.hashUserEmailMeta(sendCodeForBindRequest.getEmail());
        if (!StringUtil.isEmpty(userProfile.getEmailHash()) && userProfile.getEmailHash().equals(emailHash)) {
            BaseResponse.err(403,BaseResponse.STATUS.ALREADY_BOUND,"The account has been bound to the email", logger, null);
        }
        List<Optional<Account>> list=internalAccountManager.getByEmail(sendCodeForBindRequest.getEmail());
        if(list.size()>0){
            BaseResponse.err(403,BaseResponse.STATUS.EMAIL_OCCUPIED,"Already exists, please try another email or login with this email.", logger, null);
        }

        boolean isSendEmailSuccess;

        try {
            String verificationCode = internalAccountManager.genEmailVcode(sendCodeForBindRequest.getEmail());
            isSendEmailSuccess = accountsManager.sendEmailAndVerification(account, sendCodeForBindRequest.getEmail(),emailHash,verificationCode);
            logger.info("send bind emailHash:{}, verification code {} ,isSendEmailSuccess:{}", emailHash, verificationCode, isSendEmailSuccess);
            if(!isSendEmailSuccess){
                throw new RateLimitExceededException(account.getNumber());
            }
        } catch (MessagingException e){
            BaseResponse.err(500,BaseResponse.STATUS.EMAIL_SEND_CODE_FAILED,"email verification code send failed", logger, null);
        }

        return BaseResponse.ok();
    }

    @Timed
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/bind/verification")
    public BaseResponse verificationCodeForBind(@Auth Account account, @Valid BindRequest bindRequest) throws RateLimitExceededException {
        if(bindRequest==null||StringUtil.isEmpty(bindRequest.getVerificationCode())){
            BaseResponse.err(403,BaseResponse.STATUS.INVALID_PARAMETER,"invalid parameter", logger, null);
            return null;
        }

        // 获取临时的email hash
        final String tempEmailHash = accountsManager.getTempEmailHash(account.getNumber());

        UserProfile userProfile = internalAccountManager.getUserProfile(null,null,null, tempEmailHash,null);
        if (userProfile != null && !Objects.equals(userProfile.getUid(), account.getNumber())) {
            logger.error("user profile  found ,other uid:{},this uid:{}",userProfile.getUid(), account.getNumber());
            BaseResponse.err(403,BaseResponse.STATUS.EMAIL_OCCUPIED,"Already exists, please try another email or login with this email.", logger, null);
            return null;
        }

        // 验证码校验
        final com.github.difftim.common.BaseResponse baseResponse = internalAccountManager.verifyEmailCode(null, tempEmailHash, bindRequest.getVerificationCode());
        logger.info("bind verifyEmailCode result status:{}, reason:{},emailHash:{}", baseResponse.getStatus(), baseResponse.getReason(), tempEmailHash);
        if (baseResponse.getStatus() != 0) {
            logger.error("bind verifyEmailCode failed, status:{}, reason:{},emailHash:{}", baseResponse.getStatus(), baseResponse.getReason(), tempEmailHash);
            accountsManager.increaseLoginVerificationCodeFailureCount(tempEmailHash);
            throw new WebApplicationException(Response.status(403).entity(new BaseResponse(1,baseResponse.getStatus(), baseResponse.getReason(), null)).build());
        } else {
            accountsManager.removeLoginVerificationCode(tempEmailHash);
        }

        // 删除老的
        userProfile = internalAccountManager.getUserProfile(account.getNumber(),null,null, null,null);
        if (userProfile != null ) {
            final String emailHashOld = userProfile.getEmailHash();
            if (emailHashOld != null && !emailHashOld.isEmpty()) {
                internalAccountManager.delUserHash(account.getNumber(), true, false);
            }
        }

        // 同步到用户的profile
        internalAccountManager.syncProfile(account.getNumber(),null,null,tempEmailHash,null);


        //BaseResponse.STATUS status = accountsManager.bindEmail(account, bindRequest.getVerificationCode());

        //if(status == BaseResponse.STATUS.EMAIL_OCCUPIED){
        //    BaseResponse.err(403,BaseResponse.STATUS.EMAIL_OCCUPIED,"Already exists, please try another email or login with this email.", logger, null);
        //}
        //if(status == BaseResponse.STATUS.EMAIL_VERIFICATION_CODE_ERROR_MANY) {
        //    BaseResponse.err(403, BaseResponse.STATUS.EMAIL_VERIFICATION_CODE_ERROR_MANY, "email verification code is error too many times!", logger, null);
        //}

        //if(status == BaseResponse.STATUS.EMAIL_VERIFICATION_CODE_ERROR){
        //    BaseResponse.err(403,BaseResponse.STATUS.EMAIL_VERIFICATION_CODE_ERROR,"OTP is not correct, please try again!",logger, null);
        //}

        return BaseResponse.ok();
    }


    @Timed
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login/send")
    public BaseResponse sendCodeForLogin(@Valid SendCodeForBindRequest sendCodeForBindRequest) throws RateLimitExceededException {
        if(sendCodeForBindRequest==null||StringUtil.isEmpty(sendCodeForBindRequest.getEmail())){
            BaseResponse.err(403,BaseResponse.STATUS.INVALID_PARAMETER,"invalid parameter",logger,null);
            return null;
        }
        sendCodeForBindRequest.setEmail(sendCodeForBindRequest.getEmail().toLowerCase().trim());
        Pattern p = Pattern.compile(AuthenticationController.emailReg,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sendCodeForBindRequest.getEmail());
        if(!m.matches()) {
            BaseResponse.err(403,BaseResponse.STATUS.INVALID_PARAMETER,"invalid parameter",logger,null);
        }

        List<Optional<Account>> list=internalAccountManager.getByEmail(sendCodeForBindRequest.getEmail());
        if (list.size() > 0) {
            Account account = list.get(0).get();
            if (account.isDisabled()) {
                logger.warn("user is disabled,uid:{}", account.getNumber());
                BaseResponse.err(403, BaseResponse.STATUS.USER_IS_DISABLED, "Account is disabled", logger, null);
            }
        }


        final String emailHash = internalAccountManager.hashUserEmailMeta(sendCodeForBindRequest.getEmail());
        if (emailHash == null ){
            logger.error("emailHash is null, hashUserEmailMeta failed");
            BaseResponse.err(500,BaseResponse.STATUS.OTHER_ERROR,"System error, please try another email.",logger,null);
        }
        // 限制校验次数
        boolean ifCanSend = accountsManager.verificationCodeFailureCounts(emailHash);
        if(!ifCanSend){
            BaseResponse.err(403, BaseResponse.STATUS.EMAIL_VERIFICATION_CODE_ERROR_MANY,
                    "email verification code is error too many times!", logger, null);
        }

        try {
            String verificationCode = internalAccountManager.genEmailVcode(sendCodeForBindRequest.getEmail());
            boolean b = accountsManager.sendLoginVerificationCode(sendCodeForBindRequest.getEmail(),emailHash,verificationCode);
            if(!b) {
                throw new RateLimitExceededException(sendCodeForBindRequest.getEmail());
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            BaseResponse.err(500,BaseResponse.STATUS.EMAIL_SEND_CODE_FAILED,"email verification code send failed",logger,null);
        }

        return BaseResponse.ok();
    }

    @Timed
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/bind/sms/verification")
    public BaseResponse verificationCodeForBindPhone(@Auth Account account, @Valid BindRequest bindRequest) {
        if(bindRequest==null||StringUtil.isEmpty(bindRequest.getVerificationCode())
                ||StringUtil.isEmpty(bindRequest.getPhone())){
            BaseResponse.err(200,BaseResponse.STATUS.INVALID_PARAMETER,"invalid parameter", logger, null);
            return null;
        }
        // 获取临时的phone hash
        final String phoneHash = internalAccountManager.hashUserPhoneMeta(bindRequest.getPhone());
        //
        UserProfile userProfile = internalAccountManager.getUserProfile(null,null,null, null,phoneHash);
        if (userProfile != null && !Objects.equals(userProfile.getUid(), account.getNumber())) {
            logger.error("user profile  found ,other uid:{},this uid:{}",userProfile.getUid(), account.getNumber());
            return  new BaseResponse(1, 10109,
                    "Already exists, please try another phone or login with this phone.", null);
        }

        // 验证码校验
        try {
            final com.github.difftim.common.BaseResponse baseResponse = internalAccountManager.verifySMSCode(
                    bindRequest.getPhone(), bindRequest.getVerificationCode());
            if (baseResponse.getStatus()!= 0){
                return  new BaseResponse(1, baseResponse.getStatus(), baseResponse.getReason(), null);
            }
        } catch (Exception e) {
            logger.error("verifySMSCode error", e);
            return  new BaseResponse(1, 10009, "Please try again for network abnormality.", null);
        }

        // 删除老的
        userProfile = internalAccountManager.getUserProfile(account.getNumber(),null,null, null,null);
        if (userProfile != null) {
            final String phoneHashOld = userProfile.getPhoneHash();
            if (phoneHashOld != null && !phoneHashOld.isEmpty()) {
                internalAccountManager.delUserHash(account.getNumber(), false, true);
            }
        }

        // 同步到用户的profile
        internalAccountManager.syncProfile(account.getNumber(), null, null, null, phoneHash);

        return BaseResponse.ok();
    }

    @Timed
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login/verification")
    public BaseResponse verificationCodeForLogin(@Valid EmailLoginRequest emailLoginRequest,@HeaderParam("User-Agent")  String userAgent) {
        if(emailLoginRequest==null||StringUtil.isEmpty(emailLoginRequest.getEmail())||StringUtil.isEmpty(emailLoginRequest.getVerificationCode())){
            BaseResponse.err(403,BaseResponse.STATUS.INVALID_PARAMETER,"invalid parameter", logger, null);
            return null;
        }

        emailLoginRequest.setEmail(emailLoginRequest.getEmail().toLowerCase().trim());

        // 限制校验次数
        final String emailHash = internalAccountManager.hashUserEmailMeta(emailLoginRequest.getEmail());

        List<Optional<Account>> list=internalAccountManager.getByEmail(emailLoginRequest.getEmail());
        //if(list.size()==0||!list.get(0).isPresent()){
        //    BaseResponse.err(403,BaseResponse.STATUS.NO_SUCH_USER,"Email does not exist, please try another email.",logger,null);
        //}
        Account account = null;
        if (list.size() > 0) {
            account = list.get(0).get();
            if (account.isDisabled()) {
                BaseResponse.err(403, BaseResponse.STATUS.USER_IS_DISABLED, "Account is disabled", logger, null);
            }
        }

        // 验证码校验
        final com.github.difftim.common.BaseResponse baseResponse = internalAccountManager.verifyEmailCode(emailLoginRequest.getEmail(), null, emailLoginRequest.getVerificationCode());
        if (baseResponse.getStatus() != 0) {
            logger.error("verificationCodeForLogin verifyEmailCode result status:{}, reason:{},", baseResponse.getStatus(), baseResponse.getReason());
            accountsManager.increaseLoginVerificationCodeFailureCount(emailHash);
            BaseResponse.err(403,BaseResponse.STATUS.EMAIL_VERIFICATION_CODE_ERROR, baseResponse.getReason(), logger, null);
            //return  new BaseResponse(1,baseResponse.getStatus(), baseResponse.getReason(), null);
            return null;
        } else {
            accountsManager.removeLoginVerificationCode(emailHash);
        }

        AuthenticationOktaResponse authenticationOktaResponse = internalAccountManager.emailCodeInvitation(account, emailLoginRequest.getEmail(),
                userAgent,emailLoginRequest.getSupportTransfer());
        if(authenticationOktaResponse==null){
            logger.error("emailCodeInvitation failed");
            BaseResponse.err(403,BaseResponse.STATUS.NO_SUCH_USER,"Email does not exist, please try another email.",logger,null);
        }
        return BaseResponse.ok(authenticationOktaResponse);
    }

    @Timed
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/bind/sms/send")
    public BaseResponse sendSmsCodeForBind(@Auth Account account, @Valid SendCodeForBindRequest sendCodeForBindRequest) throws MessagingException, RateLimitExceededException {
        if (sendCodeForBindRequest == null || StringUtil.isEmpty(sendCodeForBindRequest.getPhone())) {
            BaseResponse.err(200, BaseResponse.STATUS.INVALID_PARAMETER,
                    "invalid parameter", logger, null);
            return null;
        }
        sendCodeForBindRequest.setPhone(fixPhone(sendCodeForBindRequest.getPhone()));

        final UserProfile userProfile = internalAccountManager.getUserProfile(account.getNumber(), null, null, null, null);
        if (userProfile == null) {
            logger.error("in sendSmsCodeForBind profile not found,by uid:{}", account.getNumber());
            BaseResponse.err(200, BaseResponse.STATUS.OTHER_ERROR, "Internal Error", logger, null);
            return null;
        }
        // 是否是自己绑定的
        final String phoneHash = internalAccountManager.hashUserPhoneMeta(sendCodeForBindRequest.getPhone());
        if (userProfile.getPhoneHash() != null && userProfile.getPhoneHash().equals(phoneHash)) {
            logger.warn("in sendSmsCodeForBind phone already bind,by uid:{}", account.getNumber());
            return  new BaseResponse(1, 10108, "The account has been bound to the phone", null);
        }
        // 是否其他账号已经绑定过手机号
        final List<Optional<Account>> accounts = internalAccountManager.getByPhone(sendCodeForBindRequest.getPhone());
        if (accounts.size()>0){
            logger.warn("in sendSmsCodeForBind phone already bind by uid:{}", accounts.get(0).get().getNumber());
            return  new BaseResponse(1, 10109, "Already exists, please try another phone or login with this phone.", null);
        }
        try {
            final com.github.difftim.common.BaseResponse baseResponse = internalAccountManager.sendSMSCode(sendCodeForBindRequest.getPhone());
            return new BaseResponse(1, baseResponse.getStatus(), baseResponse.getReason(), null);
        } catch (Exception e) {
            logger.error("sendSmsCodeForBind code error", e);
            return  new BaseResponse(1, 10009, "Please try again for network abnormality.", null);
        }
    }

    @Timed
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login/sms/send")
    public BaseResponse sendCodeForLogin(@Valid SendSMSCodeRequest sendCodeRequest) throws RateLimitExceededException {
        if(sendCodeRequest==null||StringUtil.isEmpty(sendCodeRequest.getPhone())){
            BaseResponse.err(200,BaseResponse.STATUS.INVALID_PARAMETER,"invalid parameter",logger,null);
            return null;
        }
        sendCodeRequest.setPhone(fixPhone(sendCodeRequest.getPhone()));
        List<Optional<Account>> list=internalAccountManager.getByPhone(sendCodeRequest.getPhone());
        if (list.size() > 0) {
            Account account = list.get(0).get();
            if (account.isDisabled()) {
                BaseResponse.err(403, BaseResponse.STATUS.USER_IS_DISABLED, "Account is disabled", logger, null);
            }
        }
        try {
            final com.github.difftim.common.BaseResponse baseResponse = internalAccountManager.sendSMSCode(sendCodeRequest.getPhone());
            return new BaseResponse(1, baseResponse.getStatus(), baseResponse.getReason(), null);
        } catch (Exception e) {
            logger.error("send sms code error", e);
            return  new BaseResponse(1, 10009, "Please try again for network abnormality.", null);
        }
    }

    @Timed
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login/sms/verification")
    public BaseResponse smsCodeLogin(@Valid PhoneLoginRequest phoneLoginRequest,@HeaderParam("User-Agent")  String userAgent) {
        if(phoneLoginRequest==null||StringUtil.isEmpty(phoneLoginRequest.getPhone())||StringUtil.isEmpty(phoneLoginRequest.getVerificationCode())){
            BaseResponse.err(200,BaseResponse.STATUS.INVALID_PARAMETER,"invalid parameter", logger, null);
            return null;
        }

        //VerificationCode verificationCode = accountsManager.getVerificationCode(phoneLoginRequest.getPhone());
        //if(null == verificationCode || StringUtils.isEmpty(verificationCode.getVerificationCode())){
        //    BaseResponse.err(403,BaseResponse.STATUS.INVALID_PARAMETER,"invalid parameter",logger, null);
        //}
        //
        //BaseResponse.STATUS status = accountsManager.checkLoginVerificationCode(verificationCode, phoneLoginRequest);
        //if(status == BaseResponse.STATUS.EMAIL_VERIFICATION_CODE_ERROR_MANY) {
        //    BaseResponse.err(403, BaseResponse.STATUS.EMAIL_VERIFICATION_CODE_ERROR_MANY, "email verification code is error too many times!", logger, null);
        //}
        //
        //if(status == BaseResponse.STATUS.EMAIL_VERIFICATION_CODE_ERROR){
        //    BaseResponse.err(403,BaseResponse.STATUS.EMAIL_VERIFICATION_CODE_ERROR,"OTP is not correct, please try again!",logger, null);
        //}

        phoneLoginRequest.setPhone(fixPhone(phoneLoginRequest.getPhone()));
        List<Optional<Account>> list=internalAccountManager.getByPhone(phoneLoginRequest.getPhone());

        Account account = null;
        if (list.size() > 0) {
            account = list.get(0).get();
            if (account.isDisabled()) {
                BaseResponse.err(200, BaseResponse.STATUS.USER_IS_DISABLED, "Account is disabled", logger, null);
            }
        }

        try {
            final com.github.difftim.common.BaseResponse baseResponse = internalAccountManager.verifySMSCode(phoneLoginRequest.getPhone(), phoneLoginRequest.getVerificationCode());
            if (baseResponse.getStatus()!= 0){
                return  new BaseResponse(1, baseResponse.getStatus(), baseResponse.getReason(), null);
            }
        } catch (Exception e) {
            logger.error("verifySMSCode error", e);
            return  new BaseResponse(1, 10009, "Please try again for network abnormality.", null);
        }

        AuthenticationOktaResponse authenticationOktaResponse = internalAccountManager.smsCodeInvitation(account, phoneLoginRequest.getPhone(),
                userAgent,phoneLoginRequest.getSupportTransfer());
        if(authenticationOktaResponse==null){
            BaseResponse.err(200,BaseResponse.STATUS.NO_SUCH_USER,"phone does not exist, please try another phone.",logger,null);
        }
        return BaseResponse.ok(authenticationOktaResponse);
    }

    private String fixPhone(String phone) {
        if (phone.length()>=15){
            int l = 3;
            String prefix1 = phone.substring(1, 1 + l);
            String prefix2 = phone.substring(1 + l, 1 + l + l);
            if (prefix1.equals(prefix2)) {
                phone = "+" + phone.substring(1 + l );
                return phone;
            }
            l = 2;
            prefix1 = phone.substring(1, 1 + l);
            prefix2 = phone.substring(1 + l, 1 + l + l);
            if (prefix1.equals(prefix2)) {
                phone = "+" + phone.substring(1 + l );
                return phone;
            }
        }
        return phone;
    }

}
