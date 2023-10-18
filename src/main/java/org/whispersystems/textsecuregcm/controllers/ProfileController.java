package org.whispersystems.textsecuregcm.controllers;

import com.aliyun.oss.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import io.dropwizard.auth.Auth;
import org.apache.commons.codec.binary.Base64;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.configuration.ProfilesConfiguration;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.limits.RateLimiter;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.s3.PolicySigner;
import org.whispersystems.textsecuregcm.s3.PostPolicyGenerator;
import org.whispersystems.textsecuregcm.s3.UrlSignerAli;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.Conversions;
import org.whispersystems.textsecuregcm.util.Pair;
import org.whispersystems.textsecuregcm.util.StringUtil;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.security.SecureRandom;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

@Path("/v1/profile")
public class ProfileController {

    private final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private final MemCache memCache;
    private final RateLimiters rateLimiters;
    private final AccountsManager accountsManager;
    private final TeamsManagerCasbin teamsManager;
    private final PolicySigner policySigner;
    private final PostPolicyGenerator policyGenerator;

    private final AmazonS3 s3client;
    private final String bucket;

    private UrlSignerAli urlSigner = null;
    private UrlSignerAli urlSignerLogger = null;

    InternalAccountsInvitationTable mInternalAccountsInvitationTable;

    public ProfileController(MemCache memCache,
                             RateLimiters rateLimiters,
                             AccountsManager accountsManager,
                             ProfilesConfiguration profilesConfiguration,
                             UrlSignerAli urlSigner,
                             InternalAccountsInvitationTable internalAccountsInvitationTable,
                             TeamsManagerCasbin teamsManager
    ) {
        this(memCache, rateLimiters, accountsManager, profilesConfiguration,teamsManager);
        this.urlSigner = urlSigner;

        this.urlSignerLogger = new UrlSignerAli(urlSigner);
        this.urlSignerLogger.setBucket("sx-cltfilelogs");
        this.urlSignerLogger.setEndpoint("https://oss-cn-beijing.aliyuncs.com");

        this.mInternalAccountsInvitationTable = internalAccountsInvitationTable;
    }

    public ProfileController(MemCache memCache,
                             RateLimiters rateLimiters,
                             AccountsManager accountsManager,
                             ProfilesConfiguration profilesConfiguration,TeamsManagerCasbin teamsManager) {
        AWSCredentials credentials = new BasicAWSCredentials(profilesConfiguration.getAccessKey(), profilesConfiguration.getAccessSecret());
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);

        this.memCache = memCache;
        this.rateLimiters = rateLimiters;
        this.accountsManager = accountsManager;
        this.bucket = profilesConfiguration.getBucket();
        this.s3client = AmazonS3Client.builder()
                .withRegion("ap-northeast-1")
                .withCredentials(credentialsProvider)
                .withRegion(profilesConfiguration.getRegion())
                .build();

        this.policyGenerator = new PostPolicyGenerator(profilesConfiguration.getRegion(),
                profilesConfiguration.getBucket(),
                profilesConfiguration.getAccessKey());

        this.policySigner = new PolicySigner(profilesConfiguration.getAccessSecret(),
                profilesConfiguration.getRegion());
        this.teamsManager=teamsManager;
    }

    @Timed
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{number}")
    public Profile getProfile(@Auth Account account,
                              @PathParam("number") String number,
                              @QueryParam("ca") boolean useCaCertificate)
            throws RateLimitExceededException {
        rateLimiters.getProfileLimiter().validate(account.getNumber());

        Optional<Account> accountProfile = accountsManager.get(number);

        if (!accountProfile.isPresent()) {
            throw new WebApplicationException(Response.status(404).build());
        }

        return new Profile(accountProfile.get().getName(),
                accountProfile.get().getAvatar(),
                accountProfile.get().getIdentityKey()
        );
    }

    @Timed
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse setProfile(@Auth Account account,
                                   @Valid Profile2 request) throws RateLimitExceededException {

        RateLimiter.validate(memCache, "limiter_setProfile", account.getNumber(), 30, 30);

        if (null == request) {
            BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "invalid parameter", logger);
        }
        try {
            logger.error("ProfileController.setProfile ,Number:{}, deviceId:{} request:{}", account.getNumber(), account.getAuthenticatedDevice().get().getId(), new Gson().toJson(request));
        }catch (Exception e){
            logger.error("ProfileController.setProfile",e);
        }

        request.validate(account,false,logger);

        Device device = null;

        if (null != request.getName()) account.setPlainName(StringUtil.nullIfEmpty(request.getName()));
        if (null != request.getSignature()) account.setSignature(StringUtil.nullIfEmpty(request.getSignature()));
        if (null != request.getTimeZone()) setTimeZone(account,request);
        if (null != request.getAvatar()) account.setAvatar2(StringUtil.nullIfEmpty(request.getAvatar()));
        if (null != request.getGender()) account.setGender(StringUtil.nullIfZero(request.getGender()));
        if (null != request.getAddress()) account.setAddress(StringUtil.nullIfEmpty(request.getAddress()));
        if (null != request.getPrivateConfigs()&&request.getPrivateConfigs().size()>0) account.setPrivateConfigs(request.getPrivateConfigs());
        if (null != request.getProtectedConfigs()&&request.getProtectedConfigs().size()>0) account.setProtectedConfigs(request.getProtectedConfigs());
        if (null != request.getPublicConfigs()&&request.getPublicConfigs().size()>0) account.setPublicConfigs(request.getPublicConfigs());
        if (null != request.getSupportTransfer() ) account.setSupportTransfer(request.getSupportTransfer());

        if (null != request.getMeetingVersion()){ // 更新会议版本
            device = account.setMeetingVersion(request.getMeetingVersion(), account.getAuthenticatedDevice().orElse(null), accountsManager.getAccMaxMeetingVersion());
        }
        if (null != request.getMsgEncVersion()){
            device = account.setMsgEncVersion(request.getMsgEncVersion(), account.getAuthenticatedDevice().orElse(null));
        }


        accountsManager.update(account, device, false);

        return BaseResponse.ok();
    }

    @Timed
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/name/{name}")
    public void setName(@Auth Account account, @PathParam("name") @UnwrapValidatedValue(true) @Length(min = 72, max = 72) Optional<String> name) {
        account.setName(name.orElse(null));
        accountsManager.update(account, null, false);
    }

    @Timed
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/form/avatar")
    public ProfileAvatarUploadAttributes getAvatarUploadForm(@Auth Account account) {
        String previousAvatar = account.getAvatar();
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        String objectName = generateAvatarObjectName();
        Pair<String, String> policy = policyGenerator.createFor(now, objectName);
        String signature = policySigner.getSignature(now, policy.second());

        if (previousAvatar != null && previousAvatar.startsWith("profiles/")) {
            s3client.deleteObject(bucket, previousAvatar);
        }

        account.setAvatar(objectName);
        accountsManager.update(account, null, false);

        return new ProfileAvatarUploadAttributes(objectName, policy.first(), "private", "AWS4-HMAC-SHA256",
                now.format(PostPolicyGenerator.AWS_DATE_TIME), policy.second(), signature);
    }

    @Timed
    @GET
    @Path("/avatar/attachment")
    @Produces(MediaType.APPLICATION_JSON)
    public AttachmentDescriptor setAvatarAttachment(@Auth Account account) {
        // allocate an attachment space
        long attachmentId = generateAttachmentId();
        URL url = urlSigner.getPreSignedUrl(attachmentId, HttpMethod.PUT);

        // set avatar
//        account.setAvatar("" + attachmentId);
//        accountsManager.update(account, null, false);

        return new AttachmentDescriptor(attachmentId, url.toExternalForm());
    }

    @Timed
    @GET
    @Path("/logger/attachment")
    @Produces(MediaType.APPLICATION_JSON)
    public AttachmentDescriptor genLoggerAttachment(@Auth Account account) {
        // allocate an attachment space
        long attachmentId = generateAttachmentId();
        URL url = urlSignerLogger.getPreSignedUrl(attachmentId, HttpMethod.PUT);

        return new AttachmentDescriptor(attachmentId, url.toExternalForm());
    }


    @Timed
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/avatar/attachment/{attachmentId}")
    public AttachmentUri getAvatarAttachment(@Auth Account account, @PathParam("attachmentId") long attachmentId) throws RateLimitExceededException {
        RateLimiter.validate(memCache, "limiter_getAvatarAttachment", account.getNumber(), 300, 300);
        return new AttachmentUri(urlSigner.getPreSignedUrl(attachmentId, HttpMethod.GET));
    }

    private String generateAvatarObjectName() {
        byte[] object = new byte[16];
        new SecureRandom().nextBytes(object);

        return "profiles/" + Base64.encodeBase64URLSafeString(object);
    }

    private long generateAttachmentId() {
        byte[] attachmentBytes = new byte[8];
        new SecureRandom().nextBytes(attachmentBytes);

        attachmentBytes[0] = (byte) (attachmentBytes[0] & 0x7F);
        return Conversions.byteArrayToLong(attachmentBytes);
    }

    private void setTimeZone(Account account,Profile2 request){
        if (StringUtil.isEmpty(account.getTimeZone())||(account.getAuthenticatedDevice().isPresent() &&account.getAuthenticatedDevice().get().isMaster())) {
            account.setTimeZone(StringUtil.nullIfEmpty(request.getTimeZone()));
            if(account.getAuthenticatedDevice().isPresent() &&account.getAuthenticatedDevice().get().isMaster()){
                memCache.setex(account.getNumber()+"_setTimeZone",86400,"");
            }
        }else{
            if(!memCache.exists(account.getNumber()+"_setTimeZone")) account.setTimeZone(StringUtil.nullIfEmpty(request.getTimeZone()));
        }
    }
}
