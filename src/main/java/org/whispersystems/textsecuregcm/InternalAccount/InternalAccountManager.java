package org.whispersystems.textsecuregcm.InternalAccount;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.codahale.metrics.Meter;
import com.github.difftim.common.BaseResponse;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.WhisperServerConfigurationApollo;
import org.whispersystems.textsecuregcm.auth.AuthenticationCredentials;
import org.whispersystems.textsecuregcm.auth.StoredVerificationCode;
import org.whispersystems.textsecuregcm.controllers.NoSuchUserException;
import org.whispersystems.textsecuregcm.controllers.RateLimitExceededException;
import org.whispersystems.textsecuregcm.distributedlock.DistributedLock;
import org.whispersystems.textsecuregcm.distributedlock.FairLock;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.exceptions.NumberIsBindingOtherPuidException;
import org.whispersystems.textsecuregcm.exceptions.PuidIsRegisteringException;
import org.whispersystems.textsecuregcm.exceptions.RegisterException;
import org.whispersystems.textsecuregcm.exceptions.UserDisabledException;
import org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.rpcclient.FriendGRPC;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.JwtHelper;
import org.whispersystems.textsecuregcm.util.RandomString;
import org.whispersystems.textsecuregcm.util.StringUtil;
import org.whispersystems.textsecuregcm.util.VerificationCode;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.locks.Lock;


public class InternalAccountManager {
    private final Logger logger = LoggerFactory.getLogger(InternalAccountManager.class);

    public static final int MEMCACHE_VERION = 1;

    private RandomString mRandomString = new RandomString(32);
    private InternalAccountsTable mInternalAccountsTable;
    private InternalTeamsDB mInternalTeamsDB;
    private InternalTeamsAccountsTable mInternalTeamsAccountsTable;
    private InternalAccountsInvitationTable mInternalAccountsInvitationTable;
    private PendingAccountsManager mPendingAccounts;
    private AccountsManager mAccountsManager;
    private TeamsManagerCasbin teamsManager;

    private Map<String, Integer> mTestDevices;
    private RateLimiters mRateLimiters;
    private MemCache memCache;
    private Keys keys;
    private MessagesManager messagesManager;
    private WhisperServerConfigurationApollo whisperServerConfigurationApollo;

    public void setFriendGRPC(FriendGRPC friendGRPC) {
        this.friendGRPC = friendGRPC;
        this.mAccountsManager.setFriendGRPC(friendGRPC);
    }

    FriendGRPC friendGRPC;

    public void setJwtHelper(JwtHelper jwtHelper) {
        this.jwtHelper = jwtHelper;
    }

    JwtHelper jwtHelper;

    public InternalAccountManager() { // cglib.proxy 需要 null constructors
    }

    public InternalAccountManager(InternalAccountsTable internalAccountsTable, InternalTeamsDB internalTeamsDB,
                                  InternalTeamsAccountsTable internalTeamsAccountsTable,
                                  InternalAccountsInvitationTable internalAccountsInvitationTable,
                                  PendingAccountsManager pendingAccountsManager, AccountsManager accountsManager, TeamsManagerCasbin teamsManager,
                                  Map<String, Integer> testDevices, RateLimiters rateLimiters, MemCache memCache, Keys keys, MessagesManager messagesManager,
                                  WhisperServerConfigurationApollo whisperServerConfigurationApollo) {
        this.mInternalAccountsTable = internalAccountsTable;
        this.mInternalTeamsDB = internalTeamsDB;
        this.mInternalTeamsAccountsTable = internalTeamsAccountsTable;
        this.mInternalAccountsInvitationTable = internalAccountsInvitationTable;
        this.mPendingAccounts = pendingAccountsManager;
        this.mAccountsManager = accountsManager;
        this.teamsManager = teamsManager;
        this.mTestDevices = testDevices;
        this.mRateLimiters = rateLimiters;

        this.memCache = memCache;
        this.keys=keys;
        this.messagesManager=messagesManager;
        this.whisperServerConfigurationApollo=whisperServerConfigurationApollo;
    }


    private InternalAccountsTable getInternalAccountsTable() {
        return mInternalAccountsTable;
    }

    private String generateInvitationCode(Optional<String> number,
                                          Optional<String> name,
                                          Optional<String> email,
                                          Optional<String> phone,
                                          Optional<List<String>> teams,
                                          Optional<String> invitor,
                                          Optional<String> okta_id,Optional<String> okta_org) {
        final String emailLower = email.orElse("").toLowerCase().trim();

        //if(okta_id.isPresent()&&okta_org.isPresent()){
        //    InternalAccountsInvitationRow internalAccountsInvitationRow=mInternalAccountsInvitationTable.getByOktaId(okta_id.get(),okta_org.get());
        //    if(internalAccountsInvitationRow!=null){
        //        mInternalAccountsInvitationTable.update(internalAccountsInvitationRow.getCode(), internalAccountsInvitationRow.getCode(), invitor.orElse(""), System.currentTimeMillis(), 0, number.orElse(""), name.orElse(""),  teams.map(strings -> String.join(",", strings)).orElse(""), emailLower, internalAccountsInvitationRow.getOkta_id(),internalAccountsInvitationRow.getOkta_org());
        //        return internalAccountsInvitationRow.getCode();
        //    }
        //}
        String emailHash = "",phoneHash="";
        if (emailLower.length() > 0 || (phone.isPresent()&& !phone.get().isEmpty())){
            final UserProfile userProfile = friendGRPC.hashUserMetadata(emailLower, phone.orElse(""));
            if (userProfile != null) {
                emailHash = userProfile.getEmailHash();
                phoneHash = userProfile.getPhoneHash();
            }
            logger.info("generateInvitationCode emailHash:{},phoneHash:{}",emailHash,phoneHash);
        }

        while (true) {
            String code = mRandomString.nextString();
            List<InternalAccountsInvitationRow> il = mInternalAccountsInvitationTable.get(code);
            if (0 != il.size()) {
                continue;
            }

            mInternalAccountsInvitationTable.insert(
                    code,
                    invitor.orElse(""),
                    System.currentTimeMillis(),
                    0,
                    number.orElse(""),
                    name.orElse(""),
                    teams.map(strings -> String.join(",", strings)).orElse(""),
                    emailHash,
                    phoneHash,
                    okta_id.orElse(""),
                    okta_org.orElse("")
            );

            return code;
        }
    }

    @FairLock(names = {"internal_accounts", "internal_accounts_invitation"})
    public AuthenticationOktaResponse smsCodeInvitation(Account account,String phone, String ua, boolean supportTransfer) {
        // 账号不存在,返回邀请码进行注册
        logger.info("smsCodeInvitation,account is {}null", account != null ? "not " : "");
        if (account == null){
            final String invitationCode = generateInvitationCode(Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.of(phone), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
            return new AuthenticationOktaResponse(0, 0, invitationCode, "", "", false);
        }

        // 账号存在
        return existsAccountLogin(account, ua, supportTransfer);
    }


    @FairLock(names = {"internal_accounts", "internal_accounts_invitation"})
    public AuthenticationOktaResponse emailCodeInvitation(Account account,String email, String ua, boolean supportTransfer) {
        // 账号不存在,返回邀请码进行注册
        logger.info("emailCodeInvitation,account is {}null",  account != null ? "not " : "");
        if (account == null){
            final String invitationCode = generateInvitationCode(Optional.empty(), Optional.empty(), Optional.of(email),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
            return new AuthenticationOktaResponse(0, 0, invitationCode, "", "", false);
        }

        // 账号存在
        return existsAccountLogin(account, ua, supportTransfer);
    }

    private AuthenticationOktaResponse existsAccountLogin(Account account, String ua, boolean supportTransfer){
        Boolean requirePin = account.getPin().isPresent();; // 是否需要pin码
        if (supportTransfer && account.isSupportTransfer()) {
            final Device device = account.getDevice(Device.MASTER_ID).orElse(null);
            if (device != null && device.isSameOS(ua)){ // 一样的OS,生成两个token给client
                return new AuthenticationOktaResponse(0, 1,
                        genTransferTokens(account), requirePin);
            } else {
                logger.warn("existsAccountLogin not sameOS,uid:{},ua:{},device.getUserAgent:{}",
                        account.getNumber(), ua, device != null ?device.getUserAgent():"no device");
            }
        } else {
          logger.warn("existsAccountLogin supportTransfer:{}, account.isSupportTransfer:{}",
                  supportTransfer,account.isSupportTransfer());
        }
        int newVcode;
        try {
            newVcode = mAccountsManager.renew(account.getNumber());
        } catch (NoSuchUserException e) {
            logger.error("account not found in accounts table: " + account.getNumber());
            return null;
        }
        return new AuthenticationOktaResponse(0, 1, "", account.getNumber(), String.valueOf(newVcode), requirePin);

    }

    public AuthenticationOktaResponse GenLoginInfo(String uid, String ua, boolean supportTransfer){
        final Optional<Account> accountOptional = mAccountsManager.get(uid);
        return accountOptional.map(account -> existsAccountLogin(account, ua, supportTransfer)).orElse(null);
    }

    private AuthenticationOktaResponse.TransferTokens genTransferTokens(Account account){
        List<String> transfer = new ArrayList<>();
        transfer.add("transfer");
        Map<String, Object> payload = new HashMap(){{
            put("ver", 1);
            put("uid", account.getNumber());
            put("scope", transfer);
        }};
        final String tdtoken = jwtHelper.Sign(payload, 1000 * 3600);
        List<String> login = new ArrayList<>();
        login.add("login");// 重置密码、renew
        payload.put("scope", login);
        final String loginToken = jwtHelper.Sign(payload, 1000 * 3600);
        return new AuthenticationOktaResponse.TransferTokens(tdtoken,loginToken);
    }

    public boolean verifyTransferToken(String token,Account account) throws InterrupterProcessException {
        try {
            final DecodedJWT decodedJWT = jwtHelper.verify(token);
            String uid = decodedJWT.getClaim("uid").asString();
            // expired?
            Date expiresAt = decodedJWT.getExpiresAt();
            if (expiresAt.before(new Date())) {
                logger.warn("verifyTransferToken has expired,token:{},expiresAt:{}", token, expiresAt);
                throw new InterrupterProcessException(5, "Token has expired");
            }
            if (!uid.equals(account.getNumber())) {
                throw new InterrupterProcessException(11, "NO SUCH USER");
            }
            return true;
        } catch (JWTVerificationException e) {
            logger.warn("verifyTransferToken invalid token");
            throw new InterrupterProcessException(5, "Invalid Token");
        }
    }

    public Account verifyResetPasswordToken(String token) throws InterrupterProcessException {
        try {
            final DecodedJWT decodedJWT = jwtHelper.verify(token);
            // expired?
            Date expiresAt = decodedJWT.getExpiresAt();
            if (expiresAt.before(new Date())) {
                logger.warn("verifyResetPasswordToken, Token has expired,token:{},expiresAt:{}", token, expiresAt);
                throw new InterrupterProcessException(5, "Token has expired");
            }
            String[] scope = decodedJWT.getClaim("scope").asArray(String.class);
            if (scope.length != 1 || !scope[0].equals("login")) {
                logger.warn("no permission,scope :{}", (Object) scope);
                throw new InterrupterProcessException(2, "NO PERMISSION");
            }
            String uid = decodedJWT.getClaim("uid").asString();
            final Optional<Account> optionalAccount = mAccountsManager.get(uid);
            if (!optionalAccount.isPresent())
                throw new InterrupterProcessException(11, "NO SUCH USER");
            return optionalAccount.get();
        } catch (JWTVerificationException e) {
            logger.warn("invalid token");
            throw new InterrupterProcessException(5, "Invalid Token");
        }
    }

    public AuthenticationOktaResponse renew(String token) throws InterrupterProcessException {
        try {
            final DecodedJWT decodedJWT = jwtHelper.verify(token);
            // expired?
            Date expiresAt = decodedJWT.getExpiresAt();
            if (expiresAt.before(new Date())) {
                logger.warn("Token has expired,token:{},expiresAt:{}", token, expiresAt);
                throw new InterrupterProcessException(5, "Token has expired");
            }
            String[] scope = decodedJWT.getClaim("scope").asArray(String.class);
            if (scope.length != 1 || !scope[0].equals("login")) {
                logger.warn("no permission,scope :{}", (Object) scope);
                throw new InterrupterProcessException(2, "NO PERMISSION");
            }
            String uid = decodedJWT.getClaim("uid").asString();
            final Optional<Account> optionalAccount = mAccountsManager.get(uid);
            if (optionalAccount.isPresent()){
                final Account account = optionalAccount.get();
                int newVcode = mAccountsManager.renew(uid);
            return new AuthenticationOktaResponse(0, 1, "", account.getNumber(),
                    String.valueOf(newVcode), account.getPin().isPresent());
            }

        } catch (JWTVerificationException e) {
            logger.warn("invalid token");
            throw new InterrupterProcessException(5, "Invalid Token");
        } catch (NoSuchUserException e) {
            logger.warn("no such user");
            throw new InterrupterProcessException(11, "NO SUCH USER");
        }
        throw new InterrupterProcessException(11, "NO SUCH USER");
    }

    // 关联的表 internal_accounts，internal_groups ，internal_groups_accounts，internal_accounts_invitation
    @FairLock(names = {"internal_accounts", "internal_accounts_invitation", "internal_groups"})
    public InvitationAccount verifyInvitation(String inviteCode) {
        // 查InternalAccountsTable里面的
        final InternalAccountsRow byInviteCode = mInternalAccountsTable.getByInviteCode(inviteCode);
        if (byInviteCode == null) {
            return null;
        }

        SecureRandom random = new SecureRandom();
        int vcode = 100000 + random.nextInt(999999 - 100000);
        String presetNumber;
        do {
            presetNumber = String.format("+7%05d%05d", random.nextInt(99999), random.nextInt(99999));
        } while (null != mInternalAccountsTable.get(presetNumber));
        long extId=mAccountsManager.getExtId(presetNumber,"");
        String name = presetNumber;
        logger.warn("in verifyInvitation, createAccount number:{},extId:{}",presetNumber,extId);
        mInternalAccountsTable.insert(presetNumber, name, "", "", vcode, false,
                0, false,  "", "", extId);
        Account account = new Account();
        account.setNumber(presetNumber);
        account.setPlainName(name);
        account.setVcode(vcode);
        account.setRegistered(false);
        friendGRPC.initUserProfile(account.getNumber(),"","");
        account.setExtId(extId);
        if (!mAccountsManager.create(account)) {
            logger.warn("* failed to create account,uid:{}, invitationCode:{} " , presetNumber, inviteCode);
            throw new WebApplicationException(Response.status(500).build());
        }

        final InvitationAccount invitationAccount = new InvitationAccount(presetNumber, "" + vcode);
        invitationAccount.setInviter(byInviteCode.getNumber());
        return invitationAccount;
    }

    // 关联的表 internal_accounts，internal_groups ，internal_groups_accounts，internal_accounts_invitation
    @FairLock(names = {"internal_accounts", "internal_accounts_invitation", "internal_groups"})
    public InvitationAccount verifyInvitation(InternalAccountsInvitationRow inv) {
        // preset information
        SecureRandom random = new SecureRandom();
        int vcode = 100000 + random.nextInt(999999 - 100000);
        String presetNumber = inv.getAccount().trim();

        // FIXME: what if the number is occupied by another user
        boolean bCreateAccount = true;
        if(StringUtil.isEmpty(presetNumber)){
            if(!StringUtil.isEmpty(inv.getOkta_id())&&!StringUtil.isEmpty(inv.getOkta_org())) {
                InternalAccountsRow a = mInternalAccountsTable.getByOktaId(inv.getOkta_org(),inv.getOkta_id());
                if(a!=null){
                    presetNumber=a.getNumber();
                }
            }
        }

        if (!presetNumber.isEmpty()) {
            InternalAccountsRow a = mInternalAccountsTable.get(presetNumber);
            if (null != a) {
                String name = a.getName();
                if (!inv.getName().isEmpty()) {
                    name = inv.getName();
                }
                mInternalAccountsTable.update(presetNumber, name, a.getPush_type(), a.getPush_token(), vcode, false, a.getInvitation_per_day(), a.isDisabled(),  a.getOktaId(), a.getOkta_org(),a.isInactive(),
                        mAccountsManager.getExtId(presetNumber,inv.getEmailHash()), false);
                memCache.remove(getInternalAccountKey(presetNumber));
                bCreateAccount = false;
            }
        }

        // create account if the presetNumber is empty or it doesn't exists
        if (bCreateAccount) {
            if (presetNumber.isEmpty()) {
                while (true) {
                    presetNumber = String.format("+7%05d%05d", random.nextInt(99999), random.nextInt(99999));
                    if (null == mInternalAccountsTable.get(presetNumber)) {
                        break;
                    }
                }
            }

            String name = inv.getName();
            if (name.isEmpty()) {
                name = presetNumber; // preserved code, it doesn't actually works for now
            }
            long extId=mAccountsManager.getExtId(presetNumber,inv.getEmailHash());
            // 暂时屏蔽，后续放开2022-10-5
//            int invitationPerDay = 0;
//            if(inv.getCode().startsWith("CHATIVE")){
//                invitationPerDay = Integer.valueOf(whisperServerConfigurationApollo.getChativeInvitationPerDay());
//            }
            logger.warn("createAccount number:{},name:{},EmailHash:{},PhoneHash:{},extId:{}",presetNumber,name,inv.getEmailHash(),inv.getPhoneHash(),extId);
            mInternalAccountsTable.insert(presetNumber, name, "", "", vcode, false, 0, false,  inv.getOkta_id(), inv.getOkta_org(), extId);
            Account account = new Account();
            account.setNumber(presetNumber);
            account.setPlainName(name);
            account.setVcode(vcode);
            account.setRegistered(false);
            friendGRPC.initUserProfile(account.getNumber(),inv.getEmailHash(),inv.getPhoneHash());
            account.setOktaId(inv.getOkta_id());
            account.setOktaOrg(inv.getOkta_org());
//            account.setInvitationCode(inv.getCode());
            account.setExtId(extId);
            if (!mAccountsManager.create(account)) {
                logger.warn("* failed to create account: " + inv.getCode());
                throw new WebApplicationException(Response.status(500).build());
            }

        }

        // update invitation info
        mInternalAccountsInvitationTable.update(inv.getCode(), inv.getCode(), inv.getInviter(), inv.getTimestamp(), System.currentTimeMillis(), presetNumber, inv.getName(), inv.getOrgs(), inv.getOkta_id(),inv.getOkta_org());
        return new InvitationAccount(presetNumber, "" + vcode);
    }

    @FairLock(names = {"pending_accounts"})
    public boolean sendVerificationCode(String number) {
        Optional<Account> account = mAccountsManager.get(number);
        logger.info("in sendVerificationCode,number:{},isPresent:{}", number, account.isPresent());
        if (!account.isPresent()) {
            logger.warn("* the account number does not exists: " + number);
            throw new WebApplicationException(Response.status(403).build());
        }

        // check if the account is disabled
        if (account.get().isDisabled()) {
            logger.warn("* trying to register an account that is disabled: " + number);
            throw new WebApplicationException(Response.status(403).build());
        }

        // check if the account is already registered
        if (account.get().isRegistered()) {
            logger.warn("* trying to register an account that is already registered: " + number);
            throw new WebApplicationException(Response.status(403).build());
        }

        VerificationCode verificationCode = generateVerificationCode(account.get(), number);
        StoredVerificationCode storedVerificationCode = new StoredVerificationCode(verificationCode.getVerificationCode(), System.currentTimeMillis());

        mPendingAccounts.store(number, storedVerificationCode);

        return account.get().getPin().isPresent();
//        if (mTestDevices.containsKey(number)) {
        // noop
//        } else if (transport.equals("sms")) {
////      smsSender.deliverSmsVerification(number, client, verificationCode.getVerificationCodeDisplay());
//        } else if (transport.equals("voice")) {
////      smsSender.deliverVoxVerification(number, verificationCode.getVerificationCodeSpeech());
//        }

    }

    @FairLock(names = {"pending_accounts", "accountsManager", "internal_accounts"})
    public void register(String number, String verificationCode, AccountAttributes accountAttributes,
                         String password, String userAgent, MessagesManager messagesManager, Meter newUserMeter)
            throws RateLimitExceededException {

        Optional<Account> oExistingAccount = mAccountsManager.get(number);
        if (!oExistingAccount.isPresent()) {
            logger.warn("* trying to login an account that doesn't exist: " + number);
            throw new WebApplicationException(Response.status(403).build());
        }
        Account existingAccount = oExistingAccount.get();

        // check if the account already registered
        if (existingAccount.isRegistered()) {
            logger.warn("* trying to login an account that is already registered: " + number);
            throw new WebApplicationException(Response.status(403).build());
        }

        // check vcode usage count
        Integer count = (Integer) memCache.get(getVCodeUsageCountKey(number), Integer.class);
        if (null != count && count >= 10) {
            // block
            existingAccount.setRegistered(true);
            mAccountsManager.update(existingAccount, null, false);

            // remove counter
            memCache.remove(getVCodeUsageCountKey(number));

            logger.warn("* exceeded maximum vcode usage limit: " + number);
            throw new WebApplicationException(Response.status(403).build());
        }

        logger.info("trying to login account: " + number);

        // verify vocde
        final int vcode = existingAccount.getVcode();
        if (verificationCode.equals("") || vcode != Integer.valueOf(verificationCode)) {
            logger.error("invalid vcode: " + verificationCode + "!=" + vcode);

            // count
            if (null == count) {
                count = 0;
            }
            count += 1;
            memCache.set(getVCodeUsageCountKey(number), count);

            throw new WebApplicationException(Response.status(403).build());
        }

        // remove vcode usage counter
        memCache.remove(getVCodeUsageCountKey(number));

        if (existingAccount.getPin().isPresent()) { // &&
//                System.currentTimeMillis() - existingAccount.get().getLastSeen() < TimeUnit.DAYS.toMillis(7)) {
            mRateLimiters.getVerifyLimiter().clear(number);

//            long timeRemaining = TimeUnit.DAYS.toMillis(7) - (System.currentTimeMillis() - existingAccount.get().getLastSeen());

            if (accountAttributes.getPin() == null) {
                logger.warn("need pin code ,but no pin in request ,when {} registering ", number);
                throw new RegisterException("Login failed, bad pin code.");
            }

            mRateLimiters.getPinLimiter().validate(number);

            if (!MessageDigest.isEqual(existingAccount.getPin().get().getBytes(), accountAttributes.getPin().getBytes())) {
                logger.warn("bad pin code ,when {} registering ", number);
                throw new RegisterException("Login failed, bad pin code.");
//                throw new WebApplicationException(Response.status(423)
//                        .entity(new RegistrationLockFailure(timeRemaining))
//                        .build());
            }

            mRateLimiters.getPinLimiter().clear(number);
        }
        //Account existingAccountForSR= (Account) existingAccount.clone();
        // kick off devices
        final Set<Device> devices = existingAccount.getDevices();
        if (devices != null && devices.size() >0)
            for (Device device : devices) {
                mAccountsManager.update(existingAccount, device, true);
                mAccountsManager.kickOffDevice(existingAccount.getNumber(), device.getId());
            }

        // recreate the account
        createAccount(number, password, userAgent, accountAttributes, messagesManager, newUserMeter);

        // set flag
        //Optional<Account> optionalAccount = mAccountsManager.get(number);
        //final Account account = optionalAccount.get();
        //account.setVcode(0); // 注册成功后将vCode置为0
        //account.setRegistered(true);
        //account.setInactive(false);
        //mAccountsManager.update(account, null, false);

        //mAccountsManager.sendReactivate(existingAccountForSR,optionalAccount.get());
    }

    private void disconnectDevices(Account account) {
        for (Device device : account.getDevices()) { // 遍历所有设备
            mAccountsManager.kickOffDevice(account.getNumber(), device.getId());
        }
    }

    @VisibleForTesting
    protected VerificationCode generateVerificationCode(Account account, String number) {
        if (mTestDevices.containsKey(number)) {
            return new VerificationCode(mTestDevices.get(number));
        }

        return new VerificationCode(account.getVcode());
    }


    protected void createAccount(String number, String password, String userAgent, AccountAttributes
            accountAttributes, MessagesManager messagesManager, Meter newUserMeter) {

        Device device = new Device();
        device.setId(Device.MASTER_ID);
        device.setAuthenticationCredentials(new AuthenticationCredentials(password));
        device.setSignalingKey(accountAttributes.getSignalingKey());
        device.setFetchesMessages(accountAttributes.getFetchesMessages());
        device.setRegistrationId(accountAttributes.getRegistrationId());
        device.setName(accountAttributes.getName());
        device.setVoiceSupported(accountAttributes.getVoice());
        device.setVideoSupported(accountAttributes.getVideo());
        device.setCreated(System.currentTimeMillis());
        device.setLastSeen(System.currentTimeMillis());
        device.setUserAgent(userAgent);

        Account account = new Account();
        account.setNumber(number);
        account.setRegistered(true);
        account.setInactive(false);

        account.setPublicConfig(AccountExtend.FieldName.MeetingVersion, accountAttributes.getMeetingVersion());
        account.setPublicConfig(AccountExtend.FieldName.MsgEncVersion, accountAttributes.getMsgEncVersion());
        account.setPin(accountAttributes.getPin());
        account.setVcode(0); // 注册成功后将vCode置为0

        account.addDevice(device);
        if (mAccountsManager.create(account)) {
            newUserMeter.mark();
        }

        messagesManager.clear(number);
        mPendingAccounts.remove(number);

        memCache.remove(getInternalAccountKey(number));
        final Optional<Account> optionalAccount = mAccountsManager.reload(number);
        logger.info("createAccount !  number:{},meetingVersion:{}, isRegistered:{},after reload:{}",
                number, accountAttributes.getMeetingVersion(), account.isRegistered(), optionalAccount.get().isRegistered());

        final InternalAccountsInvitationRow accountsInvitationRow = mInternalAccountsInvitationTable.getByAccount(number);
        //teamsManager.reloadTeamMembersForUser(number);
        //logger.info("createAccount ! reloadTeamMembersForUser number:{},accountsInvitationRow:{},",number,accountsInvitationRow );
        if (accountsInvitationRow != null && !Objects.equals(accountsInvitationRow.getInviter(), "") &&
                System.currentTimeMillis()-  accountsInvitationRow.getRegister_time() < 60000){

            String inviter = accountsInvitationRow.getInviter();
            friendGRPC.addFriend(inviter,number);
            logger.info("createAccount inviter:{},invitee:{}", inviter, number);
        }
        String emailHash = null,phoneHash = null;
        if (accountsInvitationRow != null) {
            emailHash = accountsInvitationRow.getEmailHash();
            phoneHash = accountsInvitationRow.getPhoneHash();
        }
        friendGRPC.syncProfile(number,null,null, emailHash, phoneHash);

        // 通知好友
        List<Account> accounts = new ArrayList<>();
        accounts.add(account);
        mAccountsManager.getDirectoryNotifyManager().directoryNotifyHandle(DirectoryNotify.ChangeType.REGISTER, new Object[]{accounts});
    }

    public void syncProfile(String uid,String email,
                            String phone,
                            String emailHash,
                            String phoneHash){
        friendGRPC.syncProfile(uid,email,phone,emailHash,phoneHash);
    }

    public InternalAccountsRow get(String number) {
        InternalAccountsRow internalAccountsRow = (InternalAccountsRow) memCache.get(getInternalAccountKey(number), InternalAccountsRow.class);
        if (null == internalAccountsRow) {
            internalAccountsRow = mInternalAccountsTable.get(number);
            if (null != internalAccountsRow) {
                memCache.set(getInternalAccountKey(number), internalAccountsRow);
            }
        }
        return internalAccountsRow;
    }

    static public String getInternalAccountKey(String number) {
        return String.join("_", InternalAccountsRow.class.getSimpleName(),
                "InternalAccountsRow", String.valueOf(MEMCACHE_VERION), number);
    }

    static public String getVCodeUsageCountKey(String number) {
        return String.join("_", InternalAccountsRow.class.getSimpleName(),
                "VCodeUsageCount", String.valueOf(MEMCACHE_VERION), number);
    }

    public List<InternalAccountsTeamRow> getAccountsList(int offset, int length, String email, String name, String
            number, long teamsId, List<Boolean> list) {
        return mInternalAccountsTable.getAccountsList(offset, length, email, name, number,teamsId, list);
    }

    public long getAccountsTotal(String email, String name, String number, long teamsId, List<Boolean> list) {
        return mInternalAccountsTable.getAllCount(email, name, number, teamsId, list);
    }

    public List<InternalAccountsTeamRow> getAccountsListByTeam(int offset, int length, String email, String name, String
            number, List<String> teamIds, List<Boolean> list) {
        return mInternalAccountsTable.getAccountsListByTeam(offset, length, email, name, number,teamIds, list);
    }

    public long getAccountsTotalByTeamByTeam(String email, String name, String number, List<String> teamIds, List<Boolean> list) {
        return mInternalAccountsTable.getAllCountByTeam(email, name, number, teamIds, list);
    }

    @FairLock(names = {"accountsManager", "internal_accounts"})
    public Account registerForOpenApi(AccountCreateRequest accountCreateRequest)
            throws RateLimitExceededException, NoSuchUserException, UserDisabledException, UserDisabledException, PuidIsRegisteringException, NumberIsBindingOtherPuidException {
        Lock lock= DistributedLock.getLocker(new String[]{"registerForOpenApiLock_"+accountCreateRequest.getPid()+"_"+accountCreateRequest.getPuid()});
        if(lock.tryLock()) {
//            mRateLimiters.getCreateAccountLimiter().validate(accountCreateRequest.getPid());
            try {
                if (!StringUtil.isEmpty(accountCreateRequest.getInviter())) {
                    String inviter = accountCreateRequest.getInviter();
                    Optional<Account> optionalAccount = mAccountsManager.get(inviter);
                    if (!optionalAccount.isPresent() || optionalAccount.get().isinValid()) {
                        logger.warn("registerForOpenApi * trying to register but inviter not exists or inValid!");
                        throw new NoSuchUserException(inviter);
                    }
                }
                String number;
                Account accountByNumber;
                Optional<Account> oExistingAccount = Optional.empty();
                if (accountCreateRequest.getWuid().isEmpty()) {
                    InternalAccountsRow internalAccountsRow = getInternalAccountsTable().getByPuid(accountCreateRequest.getPid(), accountCreateRequest.getPuid());
                    if (internalAccountsRow != null && !internalAccountsRow.isDisabled()) {
                        number = internalAccountsRow.getNumber();
                        Optional<Account> accountOptional = mAccountsManager.get(number);
                        if (accountOptional.isPresent() && !internalAccountsRow.isDisabled()) {
                            return accountOptional.get();
                        }
                    }
                    Account account = mAccountsManager.getByPuid(accountCreateRequest.getPid(), accountCreateRequest.getPuid());
                    if (account != null) {
                        number = account.getNumber();
                    } else {
                        number = createNumber(accountCreateRequest.getSegment());
                    }
                } else {
                    number = accountCreateRequest.getWuid();
                    Account accountByPuid = mAccountsManager.getByPuid(accountCreateRequest.getPid(), accountCreateRequest.getPuid());
                    if (accountByPuid != null && !accountByPuid.getNumber().equals(number)) {
                        logger.warn("registerForOpenApi * trying to register but number is binding other puid! number:" + number);
                        throw new NumberIsBindingOtherPuidException("* trying to register but number is binding other puid! number:" + number);
                    }

                    oExistingAccount = mAccountsManager.get(number); // 通过wuid获取account
                    if (oExistingAccount.isPresent()) {
                        accountByNumber = oExistingAccount.get();
                        if (accountByNumber.isDisabled()) {
                            logger.warn("registerForOpenApi * trying to register  disabled uid :{}!", number);
                            throw new UserDisabledException(number);
                        }
                        final String puidBefore = accountByNumber.getPuid();
                        if (puidBefore == null || puidBefore.isEmpty()) { // 没有puid
                            if (number.length() > 6) { // 如果wuid长度大于6，则说明不是机器人
                                logger.warn("registerForOpenApi * trying to register but number for normal user,not bot:" + number);
                                throw new NumberIsBindingOtherPuidException("number for normal user,not bot");
                            }
                            logger.warn("registerForOpenApi * trying to register  botID :{}", number);
                        } else if (!accountCreateRequest.getPuid().equals(puidBefore)) {// puid 不匹配
                            logger.warn("registerForOpenApi * trying to register but number is binding other puid! number:{},puidBefore:{},new Puid:{}", number, puidBefore, accountCreateRequest.getPuid());
                            throw new NumberIsBindingOtherPuidException("* trying to register but number is binding other puid! number:" + number+" old puid:"+puidBefore);
                        }
                    }

                }
                if (StringUtil.isEmpty(number)) {
                    logger.warn("registerForOpenApi * trying to register but number is null!");
                    throw new RuntimeException("* trying to register but number is null!");
                }
                Account existingAccountForSR = null;
                oExistingAccount = !oExistingAccount.isPresent() ? mAccountsManager.get(number) : oExistingAccount;
                if (oExistingAccount.isPresent()) {
                    Account existingAccount = oExistingAccount.get();
                    existingAccountForSR = (Account) existingAccount.clone();
                    // kick off devices
                    for (Device device : existingAccount.getDevices()) {
                        //if (device.getId() > 99) {
                        mAccountsManager.update(existingAccount, device, true);
                        mAccountsManager.kickOffDevice(existingAccount.getNumber(), device.getId());
                        //}
                    }
                }

                // recreate the account
                Account account = createAccount(number, accountCreateRequest);

                //if (existingAccountForSR != null) {
                //    mAccountsManager.sendReactivate(existingAccountForSR, mAccountsManager.get(number).get());
                //}
                return account;
            }catch (Exception e){
                logger.error("registerForOpenApi error! msg:{}",e.getMessage());
                throw e;
            }finally {
                lock.unlock();
            }
        }else{
            logger.warn("registerForOpenApi * trying to register but puid is registering! pid:{} , puid{}",
                    accountCreateRequest.getPid(),accountCreateRequest.getPuid());
            throw new PuidIsRegisteringException("* trying to register but puid is registering!");
        }
    }

    protected Account createAccount(String number, AccountCreateRequest accountCreateRequest) {
        Device device = new Device();
        device.setId(accountCreateRequest.getDeviceInfo().getDeviceId());
        device.setAuthenticationCredentials(new AuthenticationCredentials(accountCreateRequest.getDeviceInfo().getPwd()));
        device.setSignalingKey(accountCreateRequest.getDeviceInfo().getSignalingKey());
        device.setFetchesMessages(true);
        device.setRegistrationId(accountCreateRequest.getDeviceInfo().getRegistrationId());
        device.setVoiceSupported(true);
        device.setVideoSupported(true);
        device.setCreated(System.currentTimeMillis());
        device.setLastSeen(System.currentTimeMillis());
        device.setUserAgent("Chative/9.9.9 (macOS;20.3.0;node-fetch/1.0)");
        SignedPreKey signedPreKey=new SignedPreKey(accountCreateRequest.getDeviceInfo().getSignedPreKey().getPreKey().getKeyId(),accountCreateRequest.getDeviceInfo().getSignedPreKey().getPreKey().getPublicKey(),accountCreateRequest.getDeviceInfo().getSignedPreKey().getSignature());
        device.setSignedPreKey(signedPreKey);
        device.setAppId(accountCreateRequest.getDeviceInfo().getAppid());
        device.setReceiveType(accountCreateRequest.getDeviceInfo().getReceiveType());
        device.setReceiveChannel(accountCreateRequest.getDeviceInfo().getReceiveChannel());
        Account account = new Account();
        account.setNumber(number);
        account.setPlainName(accountCreateRequest.getNickname());
        account.setRegistered(true);
        account.addDevice(device);
        account.setRegistered(true);
        account.setPid(accountCreateRequest.getPid());
        account.setPuid(accountCreateRequest.getPuid());
        account.setIdentityKey(accountCreateRequest.getIdentityKey());
        account.setAccountMsgHandleType(accountCreateRequest.getAccountMsgHandleType());

        mAccountsManager.create(account);
        storeKeys(account, device, accountCreateRequest);
        String inviter = accountCreateRequest.getInviter();
        List<String> teams=new ArrayList<>();
        generateInvitationCode(Optional.of(number),Optional.of(accountCreateRequest.getNickname()),Optional.empty(),Optional.empty(),Optional.of(teams),Optional.of(accountCreateRequest.getInviter()),Optional.empty(),Optional.empty());
        createInternalAccount(number, accountCreateRequest);
        messagesManager.clear(number);
        memCache.remove(getInternalAccountKey(number));
        logger.warn("createAccount for openapi! reloadTeamMembersForUser number:{}",number );
        //teamsManager.reloadTeamMembersForUser(number);
        List<Account> accounts = new ArrayList<>();
        accounts.add(account);
        mAccountsManager.getDirectoryNotifyManager().directoryNotifyHandle(DirectoryNotify.ChangeType.REGISTER, new Object[]{accounts});
        return account;

    }

    public List<Optional<Account>> getByEmail(String email){
        List<Optional<Account>> accounts = new ArrayList<>();
        final UserProfile userProfile = friendGRPC.getUserProfile(null, email, null,null,null);
        if (userProfile != null) {
            final Optional<Account> account = mAccountsManager.get(userProfile.getUid());
            if (account.isPresent()) {
                accounts.add(account);
            }
        }
        return accounts;
    }

    public List<Optional<Account>> getByPhone(String phone){
        List<Optional<Account>> accounts = new ArrayList<>();
        final UserProfile userProfile = friendGRPC.getUserProfile(null, null, phone,null,null);
        if (userProfile != null) {
            final Optional<Account> account = mAccountsManager.get(userProfile.getUid());
            if (account.isPresent()) {
                accounts.add(account);
            }
        }
        return accounts;
    }

    public UserProfile getUserProfile(String uid,String email,
                                      String phone,
                                      String emailHash,
                                      String phoneHash){
        //return friendGRPC.getUserProfile(uid, null, null,null,null);
        return friendGRPC.getUserProfile(uid, email, phone,emailHash,phoneHash);
    }

    public String genEmailVcode(String email){
        return friendGRPC.genEmailVcode(email);
    }

    private void storeKeys(Account account,Device device,AccountCreateRequest accountCreateRequest){
        List<PreKey> preKeys=new ArrayList<>();
        for(AccountCreateRequest.PreKey preKey:accountCreateRequest.getDeviceInfo().getPreKeysList()){
            preKeys.add(new PreKey(preKey.getKeyId(),preKey.getPublicKey()));
        }
        keys.store(account.getNumber(), device.getId(), preKeys);
    }
    private void createInternalAccount(String number,AccountCreateRequest accountCreateRequest){
        InternalAccountsRow internalAccountsRow=mInternalAccountsTable.get(number);
        if(internalAccountsRow!=null){
            mInternalAccountsTable.update(number,accountCreateRequest.getNickname(),internalAccountsRow.getPush_type(),internalAccountsRow.getPush_token(),internalAccountsRow.getVcode(),
                    true,internalAccountsRow.getInvitation_per_day(),false,internalAccountsRow.getOktaId(), internalAccountsRow.getOkta_org(),false,
                    internalAccountsRow.getExtId(), false);
        }else {
            mInternalAccountsTable.insertForOpenApi(number, accountCreateRequest.getNickname(), "", "", 0, true, 0, false,  "", accountCreateRequest.getPid(), accountCreateRequest.getPuid(),"", mAccountsManager.getExtId(number,""));
        }
    }

    private String createNumber(String segment){
        String presetNumber=null;
        if (StringUtil.isEmpty(segment)) {
            segment="7";
        }
        SecureRandom random = new SecureRandom();
        while (true) {
            presetNumber = String.format(segment+"%05d%05d", random.nextInt(99999), random.nextInt(99999));
            if (null == mInternalAccountsTable.get(presetNumber)) {
                break;
            }
        }
        return presetNumber;
    }

    public boolean canInviteReg(String uid) {
        return true;
        //final InternalAccountsRow internalAccountsRow = mInternalAccountsTable.get(uid);
        //return internalAccountsRow != null && (internalAccountsRow.getInviteRule() & 0x1) == 0x1;
    }
    public BaseResponse sendSMSCode(String phone){
        return friendGRPC.sendSMSCode(phone);
    }
    public BaseResponse verifySMSCode(String phone,String code){
        return friendGRPC.verifySMSCode(phone,code);
    }

    public String hashUserEmailMeta(String email) {
        final UserProfile userProfile = friendGRPC.hashUserMetadata(email, null);
        if (userProfile != null) {
            return userProfile.getEmailHash();
        }
        return null;
    }

    public String hashUserPhoneMeta(String phone) {
        final UserProfile userProfile = friendGRPC.hashUserMetadata(null, phone);
        if (userProfile != null) {
            return userProfile.getPhoneHash();
        }
        return null;
    }

    public BaseResponse verifyEmailCode(String email,String emailHash,String code){
        return friendGRPC.checkEmailVerify(email, emailHash, code);
    }


    public void delUserHash(String number, boolean delEmail, boolean delPhone) {
        friendGRPC.delUserHash(number, delEmail, delPhone);
    }
    public void disableSearch(String uid) {
        friendGRPC.disableSearch(uid);
    }
}
