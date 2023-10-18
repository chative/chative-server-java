/**
 * Copyright (C) 2013 Open WhisperSystems
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.whispersystems.textsecuregcm.storage;


import com.codahale.metrics.Meter;
import com.github.difftim.eslogger.ESLogger;
import com.github.difftim.security.encryption.AES;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.lookup.StringLookup;
import org.skife.jdbi.v2.TransactionIsolationLevel;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.InternalAccount.InternalAccountManager;
import org.whispersystems.textsecuregcm.WhisperServerConfigurationApollo;
import org.whispersystems.textsecuregcm.auth.AuthorizationHeader;
import org.whispersystems.textsecuregcm.configuration.AccountManagerConfiguration;
import org.whispersystems.textsecuregcm.configuration.EmailConfiguration;
import org.whispersystems.textsecuregcm.controllers.NoSuchUserException;
import org.whispersystems.textsecuregcm.directorynotify.DirectoryNotifyManager;
import org.whispersystems.textsecuregcm.email.SMTPClient;
import org.whispersystems.textsecuregcm.entities.BaseResponse;
import org.whispersystems.textsecuregcm.entities.ClientContact;
import org.whispersystems.textsecuregcm.entities.DirectoryNotify;
import org.whispersystems.textsecuregcm.entities.EmailLoginRequest;
import org.whispersystems.textsecuregcm.rpcclient.FriendGRPC;
import org.whispersystems.textsecuregcm.util.RandomString;
import org.whispersystems.textsecuregcm.util.StringUtil;
import org.whispersystems.textsecuregcm.util.Util;
import org.whispersystems.textsecuregcm.websocket.WebsocketAddress;

import javax.mail.MessagingException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;


public abstract class AccountsManager {

    private final Logger logger = LoggerFactory.getLogger(AccountsManager.class);

    private RandomString verificationString = new RandomString(6, new SecureRandom(), RandomString.digits);

    @CreateSqlObject
    public abstract Accounts getAccounts();

    @CreateSqlObject
    public abstract InternalAccountsTable getInternalAccountsTable();
    private DirectoryNotifyManager directoryNotifyManager;
    private GroupManagerWithTransaction groupManager;
    private int deivceExpireThreshold=60;

    public int getAccMaxMeetingVersion() {
        return accMaxMeetingVersion;
    }

    static public Integer getMaxMsgEncVersion() {
        return whisperServerConfigurationApollo.getMaxMsgEncVersion();
    }

    public void setAccMaxMeetingVersion(int accMaxMeetingVersion) {
        this.accMaxMeetingVersion = accMaxMeetingVersion;
    }

    private int accMaxMeetingVersion = 255;

    private AccountManagerConfiguration accountManagerConfiguration;
    static private WhisperServerConfigurationApollo whisperServerConfigurationApollo;
    private EmailConfiguration emailConfiguration;
    public void setFields(DirectoryManager directory, MemCache memCache, int deivceExpireThreshold, AccountManagerConfiguration accountManagerConfiguration,
                          WhisperServerConfigurationApollo whisperServerConfigurationApollo, EmailConfiguration emailConfiguration) {
        this.directory = directory;
        this.memCache = memCache;
        if(deivceExpireThreshold>=30) {
            this.deivceExpireThreshold = deivceExpireThreshold;
        }
        this.accountManagerConfiguration=accountManagerConfiguration;
        AccountsManager.whisperServerConfigurationApollo =whisperServerConfigurationApollo;
        this.emailConfiguration=emailConfiguration;
    }

    private MemCache memCache;
    private DirectoryManager directory;
    private PubSubManager pubSubManager;
    private TeamsManagerCasbin teamsManager;
    private MessagesManager messagesManager;

    public void setFriendGRPC(FriendGRPC friendGRPC) {
        this.friendGRPC = friendGRPC;
    }

    FriendGRPC friendGRPC;

    private String msgNotRemindKey="msgNR_";

    public void setPubSubManager(PubSubManager pubSubManager) {
        this.pubSubManager = pubSubManager;
    }

    public void setTeamsManager(TeamsManagerCasbin teamsManager) {
        this.teamsManager = teamsManager;
    }

    public long getCount() {
        return getAccounts().getCount();
    }

    public DirectoryNotifyManager getDirectoryNotifyManager() {
        return directoryNotifyManager;
    }

    public void setDirectoryNotifyManager(DirectoryNotifyManager directoryNotifyManager) {
        this.directoryNotifyManager = directoryNotifyManager;
    }

    public Map<String, Account> getAll(int offset, int length) {
        Map<String, Account> result = new HashMap<>();
        for (Account a : getAccounts().getAll(offset, length)) {
            a = mergeInternalAccount(a);
            result.put(a.getNumber(), a);
        }

        return result;
    }

    public boolean create(Account account) {
        final boolean registered = account.isRegistered();
        final Object meetingVersion = account.getPublicConfig(AccountExtend.FieldName.MeetingVersion);
        final Object msgEncVersion = account.getPublicConfig(AccountExtend.FieldName.MsgEncVersion);

        account = mergeInternalAccount(account);
        account= mergeOldAccountInfo(account);
        account.setRegistered(registered); //mergeInternalAccount will set registered to false,recover it
        account.setPublicConfig(AccountExtend.FieldName.MeetingVersion, meetingVersion); // recover meetingVersion
        account.setPublicConfig(AccountExtend.FieldName.MsgEncVersion, msgEncVersion); // recover meetingVersion
        //memCache.set(getKey(account.getNumber()), account);

        return getAccounts().create(account);
    }

    //    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    public Optional<Account> authenticate(AuthorizationHeader authorizationHeader,
                                          Meter authenticationFailedMeter,
                                          Meter authenticationSucceededMeter) {

        Optional<Account> accountOptional = get(authorizationHeader.getNumber(), false);
        if (!accountOptional.isPresent()) {
            logger.warn("no such account:{}", authorizationHeader.getNumber());
            return Optional.empty();
        }

        Account account = accountOptional.get();
        if (account.isinValid() || !account.isRegistered()) {
            logger.warn("invalid account:{}, isInactive:{},isDisabled:{},isRegistered:{}",
                    account.getNumber(), account.isInactive(), account.isDisabled(), account.isRegistered());
            return Optional.empty();
        }

        Optional<Device> device = accountOptional.get().getDevice(authorizationHeader.getDeviceId());
        logger.info("getNumber:{},DeviceId:{},isPresent:{}", authorizationHeader.getNumber(),
                authorizationHeader.getDeviceId(), device.isPresent());

        if (!device.isPresent()) {
            logger.warn("getNumber:{},DeviceId:{},not Present, authenticate failed!",
                    authorizationHeader.getNumber(), authorizationHeader.getDeviceId());
            return Optional.empty();
        }
//第一次注册时，/v2/keys、/v1/accounts/apn、/v1/accounts/attributes/等接口，设备相关字段还没有赋值，会判定设备非活跃
//        if(!isActiveDevice(device.get(),account)){
//            logger.warn("getNumber:{},DeviceId:{},not active, authenticate failed!",
//                    authorizationHeader.getNumber(), authorizationHeader.getDeviceId());
//            return Optional.empty();
//        }
        if (!device.get().getAuthenticationCredentials().verify(authorizationHeader.getPassword())) {
            logger.warn("getNumber:{},DeviceId:{},verify authenticationToken failed, authenticate failed!",
                    authorizationHeader.getNumber(), authorizationHeader.getDeviceId());

            authenticationFailedMeter.mark();
            return Optional.empty();
        }

        authenticationSucceededMeter.mark();
        account.setAuthenticatedDevice(device.get());
        updateLastSeen(account, device.get());
        return Optional.of(account);
    }

    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    public void disable(List<Account> accounts) {
        for(Account account:accounts){
            Account currentAccount=(Account)account.clone();
            account.setDisabled(true);
            update(account,currentAccount);
            kickOff(account.getNumber());
            messagesManager.clear(account.getNumber());
            groupManager.kickoutAllGroupForUser(account);
        }
        directoryNotifyManager.directoryNotifyHandle(DirectoryNotify.ChangeType.DISABLE,new Object[]{accounts});
    }
    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    public void enable(List<Account> accounts) {
        for(Account account:accounts){
            Account currentAccount=(Account)account.clone();
            account.setDisabled(false);
            update(account,currentAccount);
        }
        directoryNotifyManager.directoryNotifyHandle(DirectoryNotify.ChangeType.ENABLE,new Object[]{accounts});
    }
    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    public void inactive(List<Account> accounts,Set<String> reloadTeams) {
        for(Account account:accounts){
            Account currentAccount=(Account)account.clone();
            account.setInactive(true);
            updateNoReloadTeam(account,currentAccount);
            kickOff(account.getNumber());
            messagesManager.clear(account.getNumber());
            groupManager.kickoutAllGroupForUser(account);
        }
        for(String team:reloadTeams){
            teamsManager.reloadTeamMembers(team);
        }
        directoryNotifyManager.directoryNotifyHandle(DirectoryNotify.ChangeType.NOT_ACTIVE,new Object[]{accounts});
    }

    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    public void logout(Account account) {
        Set<Device> devices = account.getDevices();
        Account currentAccount = (Account) account.clone();
        account.setVcode(0);
        account.setDevices(null);
        account.setRegistered(false);
        update(account, currentAccount);
        for (Device device : devices) {
            kickOffDevice(account.getNumber(), device.getId());
        }
        //reload(account.getNumber());
    }

    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    public void delete(Account account) {
        Set<Device> devices=account.getDevices();
        groupManager.kickoutAllGroupForUser(account);
        // 拉取好友列表
        final List<String> contactList = friendGRPC.ListFriend(account.getNumber());
        // 删除账号信息
        friendGRPC.DeleteAccount(account.getNumber());
        for(Device device:devices) {
            kickOffDevice(account.getNumber(),device.getId());
            messagesManager.clear(account.getNumber(), device.getId());
        }

        // 更新缓存
        memCache.set(getKey(account.getNumber()),
                new Account(account.getNumber(),true));

        directoryNotifyManager.directoryNotifyHandle(DirectoryNotify.ChangeType.DELETE,
                new Object[]{account, contactList});
    }

    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    public void update(Account accountNew, Device device, boolean delDevice) {
        // 拉取数据库中的数据
        Optional<Account> accountOptional = get(accountNew.getNumber(), true);

        if (!accountOptional.isPresent()) {
            memCache.remove(accountNew.getNumber());
            return;
        }

        Account accountOld = accountOptional.get();
        if (device != null) {
            accountOld.removeDevice(device.getId());
            if (!delDevice) accountOld.addDevice(device);
        }
        accountNew.setDevices(accountOld.getDevices());
        logger.info("in update account, uid:{}, accountNew.isRegistered:{}, accountNew.isinValid:{}," +
                        "device size:{}, accountOld.isRegistered;{}, accountOld.isinValid:{}",
                accountNew.getNumber(), accountNew.isRegistered(), accountNew.isinValid(),
                accountNew.getDevices().size(), accountOld.isRegistered(), accountOld.isinValid());
        // 使用新的device
        update(accountNew,accountOld);
        directoryNotifyManager.directoryNotifyHandle(DirectoryNotify.ChangeType.UPDATE,new Object[]{accountOld,accountNew});
    }

    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    public void fixAccountMeetingVersion(String uid, int maxVersion){
        final Optional<Account> accountOptional = get(uid, true);
        if (!accountOptional.isPresent()){
            logger.error("in fixAccountMeetingVersion, uid:{}, not found", uid);
            return;
        }
        final Account account = accountOptional.get();
        // 所有device的Version，进行比较
        int version = maxVersion;
        final int realMeetingVersion = account.getRealMeetingVersion();
        if (realMeetingVersion < version)
            version = realMeetingVersion;
        if (version == account.getMeetingVersion()) return;
        // 更新meeting version
        account.setPublicConfig(AccountExtend.FieldName.MeetingVersion,version);
        // 更新数据库
        update(account, account);
    }

    private void update(Account account,Account currentAccount) {
        long extId=Account.ExtType.DEFAULT.getId();
        account.setExtId(extId);
        // 更新数据库
        getAccounts().update(account);
        getInternalAccountsTable().update(account.getNumber(), account.getPlainName(), account.getPushType(),
                account.getPushToken(), account.getVcode(), account.isRegistered(), account.getInvitationPerDay(),
                account.isDisabled(),  account.getOktaId(), account.getOktaOrg(),account.isInactive(),account.getExtId(),
                account.isSupportTransfer());
        // 清理缓存
        final String keyAccount = getKey(account.getNumber());
        Long removeCnt = memCache.remove(keyAccount);
        if (memCache.exists(keyAccount)) {
            logger.error("update account, try more remove  account from memcache failed,still exists, uid:{},removeCnt:{}, key:{}",
                    account.getNumber(), removeCnt, keyAccount);
            removeCnt = memCache.remove(keyAccount);
            if (removeCnt == null || removeCnt == 0) {
                logger.error("update account, try more remove  account from memcache failed, uid:{},removeCnt:{}, key:{}",
                        account.getNumber(), removeCnt, keyAccount);
            }
        }
        final Account accountFromDB = getAccounts().get(account.getNumber());
        if(accountFromDB != null){
            logger.info("in update account,read from DB, uid:{},device size:{}, device 2 isPresent:{}",
                    accountFromDB.getNumber(), accountFromDB.getDevices().size(), accountFromDB.getDevice(2).isPresent());
            get(account.getNumber()).ifPresent(accFromMem -> {
                if (!accFromMem.getRealMsgEncVersion().equals(accountFromDB.getMsgEncVersion())){
                    logger.info("in update account,read from memcache, uid:{},getMsgEncVersion from Mem:{},from DB:{}",
                            accFromMem.getNumber(), accFromMem.getRealMsgEncVersion(), accountFromDB.getMsgEncVersion());
                }
            });
        }
        final String internalAccountKey = InternalAccountManager.getInternalAccountKey(account.getNumber());
        removeCnt = memCache.remove(internalAccountKey);
        if (removeCnt == null || removeCnt == 0) {
            logger.warn("update account, remove account from memcache failed, uid:{},removeCnt:{}, key:{}",
                    account.getNumber(), removeCnt, internalAccountKey);
        }

    }

    private void updateNoReloadTeam(Account account,Account currentAccount) {
        memCache.set(getKey(account.getNumber()), account);
        getAccounts().update(account);
        getInternalAccountsTable().update(account.getNumber(), account.getPlainName(), account.getPushType(),
                account.getPushToken(), account.getVcode(), account.isRegistered(), account.getInvitationPerDay(),
                account.isDisabled(),  account.getOktaId(), account.getOktaOrg(),account.isInactive(),account.getExtId(),account.isSupportTransfer());
        memCache.remove(InternalAccountManager.
                getInternalAccountKey(account.getNumber()));
        // reload teams cache
//        reloadTeamUserForUser(account,currentAccount);
        // remove email
    }

    //@Transaction(TransactionIsolationLevel.READ_COMMITTED)
    //private void update(Account accountNew) {
    //    try {
    //        Optional<Account> accountOptional = get(accountNew.getNumber(), false);
    //
    //        if (!accountOptional.isPresent()) {
    //            memCache.remove(accountNew.getNumber());
    //            return;
    //        }
    //
    //        Account account = accountOptional.get();
    //        memCache.set(getKey(accountNew.getNumber()), accountNew);
    //        getAccounts().update(accountNew);
    //        memCache.remove(InternalAccountManager.getInternalAccountKey(accountNew.getNumber()));
    //
    //        directoryNotifyManager.directoryNotifyHandle(DirectoryNotify.ChangeType.UPDATE,new Object[]{account,accountNew});
    //    } catch (Exception e){
    //        e.printStackTrace();
    //        logger.error("update account: {} error", accountNew.getNumber(), e);
    //    }
    //}


    private Optional<Account> get(String number, boolean forUpdate) {
        if (forUpdate) { // 更新数据从 数据库中获取
            final Account account = getAccounts().getForUpdate(number);
            if (null != account) {
                return Optional.of(mergeInternalAccount(account));
            }
            return Optional.empty();
        }

        final String key = getKey(number);
        // 先从Redis缓存中取
        Account account = (Account) memCache.get(key, Account.class);

        if (null == account) {
            account = getAccounts().get(number);
            if (null != account) {
                account = mergeInternalAccount(account);
                memCache.set(key, account);
                memCache.expire(key, 600);
            } else { // 检查是否已经删除
                InternalAccountsRow ia = getInternalAccountsTable().get(number);
                if (null != ia && ia.isDeleted()) {
                    account = new Account(number,true);
                    memCache.set(key, account);
                }
            }

        }

        return Optional.ofNullable(account);
    }

    public Optional<Account> get(String number) {
        return get(number, false);
    }

    // The return value could be partial, don't use it to update
    public Account getInfoWithPermissionCheck(String operator, String number) {
        Optional<Account> oAccount = get(operator);
        if (!oAccount.isPresent()) {
            return null;
        }
        return getInfoWithPermissionCheck(oAccount.get(), number);
    }

    // The return value could be partial, don't use it to update the account
    public Account getInfoWithPermissionCheck(Account operator, String number) {
        Optional<Account> oAccount = get(number);
        if (!oAccount.isPresent()) {
            return null;
        }
        return getInfoWithPermissionCheck(operator, oAccount.get());
    }

    public Account getInfoWithPermissionCheck(Account operator, Account account) {
        Account result = new Account();
        if (!operator.getNumber().equals(account.getNumber())) {
            result.setNumber(account.getNumber());
            result.setPlainName(account.getPlainName());
            result.setPublicConfigs(account.getPublicConfigs());
            result.setAvatar2(account.getAvatar2());
            result.setJoinedAt(account.getJoinedAt());
        } else {
            result = account;
        }

        return result;
    }

    public Optional<Account> reload(String number) {
        memCache.remove(getKey(number));
        return get(number);
    }

    public int renew(String number) throws NoSuchUserException {
        int code = 100000 + (new SecureRandom()).nextInt(999999 - 100000);
        Optional<Account> account = get(number);
        if (!account.isPresent()) {
            throw new NoSuchUserException("no such user: " + number);
        }

        Account account1 = account.get();
        account1.setRegistered(false);
        account1.setVcode(code);
        update(account1, null, false);

        return code;
    }

    // FIXME: more decent way
    private Account mergeInternalAccount(Account account) {
        InternalAccountsRow ia = getInternalAccountsTable().get(account.getNumber());
        if (null != ia) {
            account = mergeInternalAccount(account, ia);
        }
        return account;
    }


    private Account mergeOldAccountInfo(Account account) {
        Optional<Account> accountOptional=get(account.getNumber());
        if(accountOptional.isPresent()) {
            Account currentAccount=accountOptional.get();
            account.setSignature(currentAccount.getSignature());
            account.setTimeZone(currentAccount.getTimeZone());
            account.setDepartment(currentAccount.getDepartment());
            account.setSuperior(currentAccount.getSuperior());
            account.setAvatar2(currentAccount.getAvatar2());
            account.setGender(currentAccount.getGender());
            account.setAddress(currentAccount.getAddress());
            account.setFlag(currentAccount.getFlag());
            //account.setPin(currentAccount.getPin().isPresent()?currentAccount.getPin().get():null);
            account.setPrivateConfigs(currentAccount.getPrivateConfigs());
            account.setProtectedConfigs(currentAccount.getProtectedConfigs());
            account.setPublicConfigs(currentAccount.getPublicConfigs());
        }
        return account;
    }
    private Account mergeInternalAccount(Account account, InternalAccountsRow internalAccount) {
        account.setPlainName(internalAccount.getName());
        account.setPushType(internalAccount.getPush_type());
        account.setPushToken(internalAccount.getPush_token());
        //account.setVcode(internalAccount.getVcode());
        //account.setRegistered(internalAccount.isRegistered());
        account.setInvitationPerDay(internalAccount.getInvitation_per_day());
        account.setDisabled(internalAccount.isDisabled());
        account.setOktaId(internalAccount.getOktaId());
        account.setOktaOrg(internalAccount.getOkta_org());
        account.setPid(internalAccount.getPid());
        //account.setInactive(internalAccount.isInactive());
        account.setExtId(internalAccount.getExtId());
        account.setSupportTransfer(internalAccount.isSupportTransfer());
        account.setJoinedAt(internalAccount.getJoinedAt());
        return account;
    }

    // 只做断开操作，不做删除设备操作
    public void kickOffDevice(String number, long deviceId) {
        final WebsocketAddress address = new WebsocketAddress(number, deviceId);
        final PubSubProtos.PubSubMessage connectMessage = PubSubProtos.PubSubMessage.newBuilder().
                setType(PubSubProtos.PubSubMessage.Type.CLOSE).build();

        //  通知 package org.whispersystems.textsecuregcm.websocket 中关闭链接
        pubSubManager.publish(address, connectMessage);

    }

    public void kickOff(String number) {
        Account account = getAccounts().get(number);
        for (Device device : account.getDevices()) {
            kickOffDevice(number, device.getId());
        }
    }

    public boolean isRelayListed(String number) {
        byte[] token = Util.getContactToken(number);
        Optional<ClientContact> contact = directory.get(token);

        return contact.isPresent() && !Util.isEmpty(contact.get().getRelay());
    }


    private String getKey(String number) {
        return Account.class.getSimpleName() + Account.MEMCACHE_VERION + number;
    }

    private String getEmailKey(String oktaOrg, String email) {
        return Account.class.getSimpleName() +"oktaOrg"+"email"+Account.MEMCACHE_VERION + oktaOrg+email;
    }

    private String getEmailAndVerificationKey(String number){
        return Account.class.getSimpleName() + "verificationCode" + "email"+Account.MEMCACHE_VERION + number;
    }

    private String getLoginVerificationHashKey(String emailHash){
        return Account.class.getSimpleName() + "_emailHash_"+Account.MEMCACHE_VERION + emailHash;
    }
    private String getLoginVerificationKey(String email){
        return Account.class.getSimpleName() + "email"+Account.MEMCACHE_VERION + email;
    }

    public boolean createInternalAccount(String number, String name, String push_type, String push_token, int vcode, boolean registered, int invitation_per_day, boolean disabled, String email, String okta_id, String okta_org) {
        return getInternalAccountsTable().create(Optional.of(number), name, push_type, push_token, vcode, registered, invitation_per_day, disabled, email, okta_id,okta_org,this.getExtId(number,email));
    }
    public Optional<Account> getByOktaID(String okta_org,String oktaid) {
        InternalAccountsRow row = getInternalAccountsTable().getByOktaId(okta_org,oktaid);
        if (null == row) {
            return Optional.empty();
        }

        return get(row.getNumber());
    }



    //public Optional<Account> getByEmail(String oktaOrg, String email) {
    //    String number= (String) memCache.get(getEmailKey(oktaOrg, email),String.class);
    //    if(number!=null){
    //        return get(number);
    //    }else {
    //        InternalAccountsRow row = getInternalAccountsTable().getByEmail(oktaOrg, email);
    //        if (null == row) {
    //            return Optional.empty();
    //        }else{
    //            memCache.set(getEmailKey(oktaOrg, email),row.getNumber());
    //            return get(row.getNumber());
    //        }
    //    }
    //}

    //public Map<String,Account> getByEmails(List<String> emails) {
    //    Map<String,Account> accountMap=new HashMap<>();
    //    for(String email:emails){
    //        List<Optional<Account>> accountOptionalList=getByEmail(email);
    //        for(Optional<Account> accountOptional : accountOptionalList){
    //            if(accountOptional.isPresent()&&!accountOptional.get().isinValid()){
    //                accountMap.put(email,accountOptional.get());
    //            }else{
    //                accountMap.put(email,null);
    //            }
    //        }
    //    }
    //    return accountMap;
    //}

    //public Map<String, Account> getByEmailsForAvatar(List<String> emails) {
    //    Map<String, Account> accountMap = new HashMap<>();
    //    for (String email : emails) {
    //        List<Optional<Account>> accountOptionalList = getByEmail(email);
    //        for (Optional<Account> accountOptional : accountOptionalList) {
    //            if (!accountOptional.isPresent() || accountOptional.get().isinValid()) {
    //                continue;
    //            }
    //            final Account account = accountOptional.get();
    //            final String oktaOrg = account.getOktaOrg();
    //            // 只查公司okta和非okta登陆的【其他okta登陆的先忽略】
    //            if (oktaOrg != null && !oktaOrg.isEmpty() && !oktaOrg.equals("00o7lmeq0Snm25p1B356"))
    //                continue;
    //            accountMap.put(email, account);
    //        }
    //    }
    //    return accountMap;
    //}

    public List<Account> getByInviter(String inviter) {
       return null;
    }

    public void hiddenNotActiveAccount(){
        long since=System.currentTimeMillis()-TimeUnit.DAYS.toMillis(accountManagerConfiguration.getAccountExpireThreshold()+1)-1000*60*60*2;
        long to = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(accountManagerConfiguration.getAccountExpireThreshold());
        if(!memCache.exists("hiddenNotActiveAccountFirst")) {
            since = 0;
        }
        List<String> numbers=getAccounts().getAccountsByLastSeen(since,to);
        List<Account> accounts=new ArrayList<>();
        for(String number:numbers){
            Optional<Account> accountOptional=get(number);
            if(accountOptional.isPresent()&&!accountOptional.get().isinValid()&&!isBootAccount(accountOptional.get())){
                accounts.add(accountOptional.get());
            }
        }
        if(accounts.size()>0){
            directoryNotifyManager.directoryNotifyHandle(DirectoryNotify.ChangeType.NOT_ACTIVE,new Object[]{accounts});
        }
        if(since==0){
            memCache.set("hiddenNotActiveAccountFirst","");
        }
        teamsManager.reloadTeamMembers();
    }


    public List<String> inActiveAccountHandle(boolean isExecute,int accountExpireThreshold){
        int accountExpireThresholdTemp=accountManagerConfiguration.getAccountExpireThreshold();
        if(accountExpireThreshold!=0&&accountExpireThreshold>=0){
            accountExpireThresholdTemp=accountExpireThreshold;
        }
        long since = 0;

//        long since=System.currentTimeMillis()-TimeUnit.DAYS.toMillis(accountExpireThresholdTemp+1)-1000*60*60*2;
        long to = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(accountExpireThresholdTemp);
//        if(!memCache.exists("inActiveAccountHandleFirst")) {
//            since = 0;
//        }
        List<String> numbers=getAccounts().getAccountsByLastSeen(since,to);
        List<Account> accounts=new ArrayList<>();
        List<String> inactiveNumbers=new ArrayList<>();
        Set<String> reloadTeams=new HashSet<>();
        for(String number:numbers){
            Optional<Account> accountOptional=get(number);
            if(accountOptional.isPresent()&&!accountOptional.get().isinValid()&&!isBootAccount(accountOptional.get())){
                    //inactiveNumbers.add(accountOptional.get().getNumber());
            }
        }
        if(accounts.size()>0){
            logger.info("inActiveAccountHandle size:{},isExecute:{},number:{}",accounts.size(),isExecute,new Gson().toJson(inactiveNumbers));
            if(isExecute) {
//                long finalSince = since;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        inactive(accounts,reloadTeams);
//                        if(finalSince ==0){
//                            memCache.set("inActiveAccountHandleFirst","");
//                        }
                    }
                }).start();
            }
        }

        //teamsManager.reloadTeamMembers();
        return inactiveNumbers;
    }
    //public void oktaIdChangeHandle(Account account ,Account currentAccount){
    //    if(!objEquals(account.getExtId(),currentAccount.getExtId())){
    //        groupManager.changeGroupExtForUser(account);
    //    }
    //}

    public void reloadTeamUserForUser(Account account ,Account currentAccount){
        if (!objEquals(currentAccount.getPlainName(), account.getPlainName()) ||
                !objEquals(currentAccount.getAvatar2(), account.getAvatar2()) ||
                !objEquals(currentAccount.getSignature(), account.getSignature()) ||
                !objEquals(currentAccount.getTimeZone(), account.getTimeZone()) ||
                !objEquals(currentAccount.getGender(), account.getGender()) ||
                !objEquals(currentAccount.getAddress(), account.getAddress()) ||
                !objEquals(currentAccount.getSuperior(), account.getSuperior()) ||
                !objEquals(currentAccount.getDepartment(), account.getDepartment()) ||
                !objEquals(currentAccount.getPublicConfigs(), account.getPublicConfigs()) ||
                !objEquals(currentAccount.getProtectedConfigs(), account.getProtectedConfigs()) ||
                currentAccount.isinValid()!=account.isinValid() ||
                currentAccount.isInactive()!=account.isInactive() ||
                (currentAccount.isRegistered()==true&&currentAccount.isRegistered()!=account.isRegistered())) {

            //logger.info("update info! reloadTeamMembersForUser! account:{} ,currentAccount:{} ",new Gson().toJson(account),new Gson().toJson(currentAccount));
            //teamsManager.reloadTeamMembersForUser(account.getNumber());
        }
    }
    public boolean objEquals(Object o1,Object o2){
        if(o1!=null &&o1 instanceof String) o1=trimString((String)o1);
        if(o2!=null &&o2 instanceof String) o2=trimString((String)o2);
        return Objects.equals(o1,o2);
    }
    private String trimString(String str){
        if(StringUtil.isEmpty(str)){
            return null;
        }
        return str.trim();
    }

    public boolean isSignalActiveDevice(Device device) {
        boolean hasChannel = device.getFetchesMessages() || !Util.isEmpty(device.getApnId()) || !Util.isEmpty(device.getGcmId());
        return (device.getId() == Device.MASTER_ID && hasChannel && device.getSignedPreKey() != null) ||
                (device.getId() != Device.MASTER_ID && hasChannel && device.getSignedPreKey() != null
                        //暂时不加lastSeen判断
//                &&(device.getLastSeen()==0 || device.getLastSeen() > (System.currentTimeMillis() - TimeUnit.DAYS.toMillis(deivceExpireThreshold)))
                );
    }

    public boolean isActiveDevice(Device device,Account account) {
        boolean hasChannel = device.getFetchesMessages() || !Util.isEmpty(device.getApnId()) || !Util.isEmpty(device.getGcmId());
        boolean isActive=(device.getId() == Device.MASTER_ID && hasChannel ) ||
                (device.getId() != Device.MASTER_ID && hasChannel
                 //暂时不加lastSeen判断
//                &&(device.getLastSeen()==0 || device.getLastSeen() > (System.currentTimeMillis() - TimeUnit.DAYS.toMillis(deivceExpireThreshold)))
                );
        if(device.getId() != Device.MASTER_ID&&!isActive){
            logger.info("isActiveDevice mothed will delDevice! number:{} ,deviceId:{}",account.getNumber(),device.getId());
//            executor.submit(new Runnable() {
//                @Override
//                public void run() {
//                    update(account,device,true);
//                }
//            });
        }
        if (!isActive){
            logger.info("Account:{}, masterDeviceID:{} ,hasChannel:{},signedPrekey:{}",account.getNumber(),
                    device.getId() == Device.MASTER_ID,hasChannel,
                    device.getSignedPreKey() != null);
        }
        return isActive;
    }

    public boolean isVoiceSupported(Account account) {
        for (Device device : account.getDevices()) {
            if (isActiveDevice(device,account) && device.isVoiceSupported()) {
                return true;
            }
        }

        return false;
    }

    public boolean isVideoSupported(Account account) {
        for (Device device : account.getDevices()) {
            if (isActiveDevice(device,account) && device.isVideoSupported()) {
                return true;
            }
        }

        return false;
    }

    public boolean isActive(Account account) {
        return
                (account.getMasterDevice().isPresent() &&
                        isActiveDevice(account.getMasterDevice().get(),account)) ||isActiveForOpenApiAccount(account);
    }

    public boolean isActiveForOpenApiAccount(Account account) {
        for(Device device:account.getDevices()){
            if(isActiveDevice(device,account)){
                return true;
            }
        }
        return false;
    }
    public boolean isActiveByLastSeen(Account account) {
        if(isBootAccount(account)) return true;
        long lastSeen=0;
        for (Device device : account.getDevices()) {
            if(device.getLastSeen()>lastSeen){
                lastSeen=device.getLastSeen();
            }
        }
        long threshold = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(accountManagerConfiguration.getAccountExpireThreshold());
        return lastSeen>threshold;
    }

    public long getNextDeviceId(Account account) {
        long highestDevice = Device.MASTER_ID;

        for (Device device : account.getDevices()) {
            if (!isActiveDevice(device,account)) {
                return device.getId();
            } else if (device.getId() > highestDevice) {
                highestDevice = device.getId();
            }
        }

        return highestDevice + 1;
    }

    public int getActiveDeviceCount(Account account) {
        int count = 0;

        for (Device device : account.getDevices()) {
            if (isActiveDevice(device,account)) count++;
        }

        return count;
    }

    public boolean isBootAccount(Account account) {
        return account.getNumber().length()<=6;
    }

    public boolean isBootAccount(String number) {
        return !StringUtil.isEmpty(number)&&number.length()<=6;
    }

    private void updateLastSeen(Account account, Device device) {
        if (TimeUnit.MILLISECONDS.toDays(device.getLastSeen()) < TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())) {
            boolean isActive=isActiveByLastSeen(account);
            device.setLastSeen(System.currentTimeMillis());
            update(account, device, false);
            if(!isActive){//长时间未活跃用户，重新活跃时，加回通讯录
                logger.info("updateLastSeen! reloadTeamMembersForUser ! number:{} ",account.getNumber());
                //teamsManager.reloadTeamMembersForUser(account.getNumber());
                List<Account> accounts=new ArrayList<>();
                accounts.add(account);
                directoryNotifyManager.directoryNotifyHandle(DirectoryNotify.ChangeType.REACTIVATE,new Object[]{accounts});
            }
        }
    }
    //public void sendReactivate(Account existingAccount, Account account){
    //    if(existingAccount.getDevices().size()==0) return;//新注册用户,这里不通知
    //    boolean isActive=isActiveByLastSeen(existingAccount);
    //    if(!isActive){//长时间未活跃用户，重新注册时，加回通讯录
    //        teamsManager.reloadTeamMembersForUser(account.getNumber());
    //        logger.info("sendReactivate! reloadTeamMembersForUser! number:{} ",account.getNumber());
    //        List<Account> accounts=new ArrayList<>();
    //        accounts.add(account);
    //        directoryNotifyManager.directoryNotifyHandle(DirectoryNotify.ChangeType.REACTIVATE,new Object[]{accounts});
    //    }
    //}
    public GroupManagerWithTransaction getGroupManager() {
        return groupManager;
    }

    public void setGroupManager(GroupManagerWithTransaction groupManager) {
        this.groupManager = groupManager;
    }

    public boolean setNotRemindForMessage(String number){
        if(StringUtil.isEmpty(number)){
            return false;
        }else {
            try {
                AES aes=new AES(accountManagerConfiguration.getAlgorithm(),accountManagerConfiguration.getKey(),accountManagerConfiguration.getIv());
                String decryptNumber=aes.decryptString(number);
                Optional<Account> accountOptional=get(decryptNumber);
                if(accountOptional.isPresent()) {
                    memCache.set(this.msgNotRemindKey + decryptNumber, "");
                }
                ESLogger logger = new ESLogger("msgreminds");
                logger.withUID(decryptNumber);
                logger.withCustom("operator","unsubscribe");
                logger.withCustom("type","msgEmailRemind");
                logger.send();
                return true;
            } catch (Exception e) {
                logger.error("setNotRemindForMessage error! number:{} ,msg:{}",number,e.getMessage());
            }
        }
        return false;
    }
    public String getEncryptNumber(String number){
        if(StringUtil.isEmpty(number)){
            return null;
        }else {
            try {
                AES aes=new AES(accountManagerConfiguration.getAlgorithm(),accountManagerConfiguration.getKey(),accountManagerConfiguration.getIv());
                return aes.encryptString(number);
            } catch (Exception e) {
                logger.error("getEncryptNumber error! number:{} ,msg:{}",number,e.getMessage());
            }
            return null;
        }
    }

    public boolean isAcceptRemind(String number){
        if(StringUtil.isEmpty(number)){
            return false;
        }else {
            return !memCache.exists(this.msgNotRemindKey+number);
        }
    }

    public Set<String> getUserTeams(String number){
        return teamsManager.getUserTeams(number);
    }
    public boolean isActiveForTime(Account account,long timestamp){
        for(Device device:account.getDevices()){
            if(device.getLastSeen()>=timestamp){
                return true;
            }
        }
        return false;
    }

    public int getGlobalNotification(Account account){
        Integer globalNotification=(Integer) account.getGlobalNotification();
        if(globalNotification==null){
            globalNotification= accountManagerConfiguration.getDefaultGlobalNotification();
        }
        return globalNotification;
    }

    public AccountStringLookup getAccountStringLookup(){
        return new AccountStringLookup();
    }
    public class AccountStringLookup implements StringLookup {
        @Override
        public String lookup(String number) {
            Optional<Account> optionalAccount=get(number);
            if(optionalAccount.isPresent()){
                return optionalAccount.get().getPlainName();
            }
            return number;
        }
    }

    public Account getByPuid(String pid,String puid){
        return getAccounts().getByPuid(pid,puid);
    }

    public void setMessagesManager(MessagesManager messagesManager) {
        this.messagesManager = messagesManager;
    }

    //public void updateBatchForBu(List<Account> list) {
    //    Set<String> team = new HashSet<>();
    //    for(Account account : list){
    //        update(account);
    //        Set<String> userTeams = getUserTeams(account.getNumber());
    //        team.addAll(userTeams);
    //    }
    //
    //    Iterator<String> iterator = team.iterator();
    //    while(iterator.hasNext()){
    //        String teamName = iterator.next();
    //        teamsManager.reloadTeamMembers(teamName);
    //    }
    //}

    public long getExtId(String number,String email){
        //if(isBootAccount(number)){
        //    return Account.ExtType.BOT.getId();
        //}
        //JsonObject extConfig=whisperServerConfigurationApollo.getExtConfig();
        //if(StringUtil.isEmpty(email)){
        //    if(extConfig!=null&&extConfig.has(Account.ExtType.DEFAULT.name().toLowerCase())){
        //       return Account.ExtType.fromName(extConfig.get(Account.ExtType.DEFAULT.name().toLowerCase()).getAsString()).getId();
        //    }
        //    return Account.ExtType.DEFAULT.getId();
        //}
        //String emailDomain=email.trim().substring(email.indexOf("@")+1);
        //if(whisperServerConfigurationApollo.getExtConfig()!=null){
        //    if(whisperServerConfigurationApollo.getExtConfig().has(emailDomain)){
        //        return Account.ExtType.fromName(extConfig.get(emailDomain.trim()).getAsString()).getId();
        //    }
        //}
        return Account.ExtType.DEFAULT.getId();
    }

    private boolean checkEmailSendLimit(String emailHash) {
        final String cacheKey = getLoginVerificationHashKey(emailHash);
        String cacheData = memCache.get(cacheKey);
        long nowTime = System.currentTimeMillis();
        if (StringUtils.isNotEmpty(cacheData)) {
            VerificationCode cacheVerificationCode = new Gson().fromJson(cacheData, VerificationCode.class);
            long timestamp = cacheVerificationCode.getTimestamp();
            if (nowTime - timestamp >= whisperServerConfigurationApollo.getVerificationCodeSendInterval() * 1000) {
                cacheVerificationCode.setTimestamp(nowTime);
                memCache.set(cacheKey, new Gson().toJson(cacheVerificationCode));
            } else {
                return false;
            }
        } else {
            VerificationCode code = new VerificationCode(nowTime);
            memCache.set(cacheKey, new Gson().toJson(code));
        }
        return true;
    }
    public boolean sendEmailAndVerification(Account account, String email, String emailHash,String verificationCode) throws MessagingException {
        if (!checkEmailSendLimit(emailHash)) return false;

        List<String> emailList = new ArrayList<>();
        emailList.add(email);
        String template = whisperServerConfigurationApollo.getVerificationCodeTemplate().replace("#verificationCode#", verificationCode);
        SMTPClient.sendHtml(emailConfiguration.getServer(), emailConfiguration.getPort(), emailConfiguration.getFrom(), emailConfiguration.getUsername(), emailConfiguration.getPassword(), emailList,
                whisperServerConfigurationApollo.getVerificationCodeSubject(), template);
        // todo client 同时给email验证
        memCache.set("tmp_emailHash_" + account.getNumber(), emailHash);
        return true;
    }
    public String getTempEmailHash(String number){
        return (String) memCache.get("tmp_emailHash_" + number);
    }

    //public BaseResponse.STATUS bindEmail(Account account, String verificationCode) {
    //    String jsonMap = memCache.get(getEmailAndVerificationKey(account.getNumber()));
    //    if(StringUtils.isEmpty(jsonMap)){
    //        return BaseResponse.STATUS.EMAIL_VERIFICATION_CODE_ERROR;
    //    } else {
    //        VerificationCode code = new Gson().fromJson(jsonMap, VerificationCode.class);
    //        List<Optional<Account>> list=getByEmail(code.getEmail());
    //        if(list.size() > 0) {
    //            return BaseResponse.STATUS.EMAIL_OCCUPIED;
    //        }
    //        String redisVerificationCode = code.getVerificationCode();
    //        if(!verificationCode.equals(redisVerificationCode)){
    //            Long aLong = memCache.incrBy(getEmailAndVerificationKey(account.getNumber())+"Count", 1);
    //            if(aLong >= Long.valueOf(whisperServerConfigurationApollo.getVerificationCodeFailureCount())){
    //                return BaseResponse.STATUS.EMAIL_VERIFICATION_CODE_ERROR_MANY;
    //            } else {
    //                return BaseResponse.STATUS.EMAIL_VERIFICATION_CODE_ERROR;
    //            }
    //        } else {
    //            update(account, null, false);
    //            memCache.remove(getEmailAndVerificationKey(account.getNumber()));
    //            memCache.remove(getEmailAndVerificationKey(account.getNumber())+"Count");
    //            return BaseResponse.STATUS.OK;
    //        }
    //    }
    //}

    public boolean sendLoginVerificationCode(String email,String emailHash, String verificationCode) throws MessagingException {
        if (!checkEmailSendLimit(emailHash)) return false;

        List<String> emailList = new ArrayList<>();
        emailList.add(email);
        logger.info("send login emailHash {}, verification code {},", emailHash, verificationCode);
        String template = whisperServerConfigurationApollo.getVerificationCodeTemplate().replace("#verificationCode#", verificationCode);
        SMTPClient.sendHtml(emailConfiguration.getServer(), emailConfiguration.getPort(), emailConfiguration.getFrom(), emailConfiguration.getUsername(), emailConfiguration.getPassword(), emailList,
                whisperServerConfigurationApollo.getVerificationCodeSubject(), template);
        return true;
    }

    //public VerificationCode getVerificationCode(String email){
    //    String codeJson = memCache.get(getLoginVerificationKey(email));
    //    VerificationCode verificationCode = new Gson().fromJson(codeJson, VerificationCode.class);
    //    return verificationCode;
    //}

    public boolean supportV3Message(String uid){
        final Map<String, Boolean> v3MessageSupport = whisperServerConfigurationApollo.getV3MessageSupport();
        Boolean b = v3MessageSupport.get(uid);
        if (b != null && b) {
            return true;
        }
        b = v3MessageSupport.get("all");
        return  b != null && b;
    }

    public boolean supportV3Message(Set<String> uids){
        final Map<String, Boolean> v3MessageSupport = whisperServerConfigurationApollo.getV3MessageSupport();
        Boolean b = v3MessageSupport.get("all"); // 全部支持
        if (b != null && b) {
            return true;
        }
        for (String uid : uids) {
            b = v3MessageSupport.get(uid);
            if (b == null || !b) {
                return false;
            }
        }
        return true;
    }

    public boolean verificationCodeFailureCounts(String emailHash) {
        boolean b = true;
        String countCache = memCache.get(getLoginVerificationHashKey(emailHash) + "Count");
        if(StringUtils.isNotEmpty(countCache) && Long.valueOf(countCache) >= whisperServerConfigurationApollo.getVerificationCodeFailureCount()){
            b = false;
        }
        return b;
    }

    public void removeLoginVerificationCode(String emailHash) {
        memCache.remove(getLoginVerificationHashKey(emailHash));
        memCache.remove(getLoginVerificationHashKey(emailHash)+"Count");
    }

    public void increaseLoginVerificationCodeFailureCount(String emailHash) {
        memCache.incrBy(getLoginVerificationHashKey(emailHash)+"Count", 1);
    }

}
