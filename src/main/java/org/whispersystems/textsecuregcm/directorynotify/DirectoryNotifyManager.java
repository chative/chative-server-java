package org.whispersystems.textsecuregcm.directorynotify;


import com.codahale.metrics.Gauge;
import com.codahale.metrics.SharedMetricRegistries;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.configuration.DirectoryNotifyConfiguration;
import org.whispersystems.textsecuregcm.distributedlock.DistributedLock;
import org.whispersystems.textsecuregcm.entities.Contacts;
import org.whispersystems.textsecuregcm.entities.DirectoryNotify;
import org.whispersystems.textsecuregcm.redis.ReplicatedJedisPool;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.Constants;
import org.whispersystems.textsecuregcm.util.StringUtil;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.codahale.metrics.MetricRegistry.name;

public class DirectoryNotifyManager {
    final private Logger logger = LoggerFactory.getLogger(DirectoryNotifyManager.class);

    private MemCache memCache;
    private TeamsManagerCasbin teamsManager;
    private NotifyManager notifyManager;
    private AccountsManager accountsManager;

    public void setConversationManager(ConversationManager conversationManager) {
        this.conversationManager = conversationManager;
    }

    private ConversationManager conversationManager ;
    private DirectoryNotifyConfiguration directoryNotifyConfiguration;
    private GroupManagerWithTransaction groupManagerWithTransaction;
    private ReplicatedJedisPool redisPool;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 20, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new ThreadPoolExecutor.CallerRunsPolicy());


    public DirectoryNotifyManager(MemCache memCache, TeamsManagerCasbin teamsManager, NotifyManager notifyManager, AccountsManager accountsManager,DirectoryNotifyConfiguration directoryNotifyConfiguration,GroupManagerWithTransaction groupManagerWithTransaction,ReplicatedJedisPool redisPool) {
        this.memCache = memCache;
        this.teamsManager = teamsManager;
        this.notifyManager = notifyManager;
        this.accountsManager = accountsManager;
        this.directoryNotifyConfiguration=directoryNotifyConfiguration;
        this.groupManagerWithTransaction=groupManagerWithTransaction;
        this.redisPool=redisPool;
        SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME)
                .register(name(DirectoryNotifyManager.class, "DirectoryNotifyManager_executor_depth"),
                        (Gauge<Long>) ((ThreadPoolExecutor)executor)::getTaskCount);
    }


    public void directoryNotifyHandle(String changeType, Object[] args) {
        if (StringUtil.isEmpty(changeType)) {
            logger.warn("changeType is null!");
            return;
        }
        if (!directoryNotifyConfiguration.isSendDirectoryNotify()) {
            logger.warn("close directoryNotify!");
            return;
        }
        logger.info("DirectoryNotifyManager directoryNotifyHandle type:{}",changeType);
        switch (changeType) {
            case DirectoryNotify.ChangeType.REGISTER:
                handleRegister(args);
                break;
            case DirectoryNotify.ChangeType.UPDATE:
                handleAccountBasicInfoChange(args);
                break;
            case DirectoryNotify.ChangeType.JOIN_TEAM:
                handleJoinTeam(args);
                break;
            case DirectoryNotify.ChangeType.LEAVE_TEAM:
                handleLeaveTeam(args);
                break;
            case DirectoryNotify.ChangeType.DISABLE:
                handleDisable(args);
                break;
            case DirectoryNotify.ChangeType.ENABLE:
                handleRegister(args);
                break;
            case DirectoryNotify.ChangeType.DELETE:
                handleDelete(args);
                break;
            case DirectoryNotify.ChangeType.NOT_ACTIVE:
                handleDisable(args);
                break;
            case DirectoryNotify.ChangeType.REACTIVATE:
                handleRegister(args);
                break;
            default:
        }
    }


    private void handleAccountBasicInfoChange(Object[] args) {
        executor.submit(() -> {
            try {
                Account currentAccount = (Account) args[0];
                Account newAccount = (Account) args[1];
                AccountChangeInfo accountChangeInfo=accountBasicInfoIsChange(currentAccount, newAccount);

                if (!accountChangeInfo.hasChanged()) {
                    logger.info("DirectoryNotifyManager handleAccountBasicInfoChange noChanged! number:{}",currentAccount.getNumber());
                    return;
                }
                logger.info("accountBasicInfoIsChange!!! msg:{},",new Gson().toJson(accountChangeInfo));
                //个人修改私有配置，立即发送通讯录通知
                if(accountChangeInfo.isImmediateNotify()){
                    Map<String,AccountChangeInfo> accountChangeInfos=new HashMap<>();
                    accountChangeInfos.put(accountChangeInfo.getNumber(),accountChangeInfo);
                    handleAccountBasicInfoChangeSend(accountChangeInfos);
                }else {
                    setBasicChangeInfo(accountChangeInfo);
                    if (newAccount.getMasterDevice().isPresent()) {
                        if (!StringUtil.isEmpty(currentAccount.getPlainName()) && currentAccount.getPlainName().equals(newAccount.getNumber())||currentAccount.isInactive()!=newAccount.isInactive()) {
                            if (System.currentTimeMillis() - newAccount.getMasterDevice().get().getCreated() < 300000) {
                                logger.info("DirectoryNotifyManager handleAccountBasicInfoChange register!! number:{}", currentAccount.getNumber());
                                handleAccountBasicInfoChangeSend(null);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("DirectoryNotifyManager handleAccountBasicInfoChange error! msg:{}", e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void handleAccountBasicInfoChangeSend(Map<String,AccountChangeInfo> accountChangeInfos) {
        try {
            Set<String> changeNumbers = null;
            Long count = memCache.scard(DirectoryNotify.DIRECTORY_NOTIFY_CHANGE_SET_KEY);
            changeNumbers = memCache.spop(DirectoryNotify.DIRECTORY_NOTIFY_CHANGE_SET_KEY, count==null?0:count);
            if(accountChangeInfos==null) {
                accountChangeInfos = getBasicChangeInfo();
            }

            if ((changeNumbers == null || changeNumbers.size() == 0)&&(accountChangeInfos==null||accountChangeInfos.size()==0)) {
                logger.info(" handleAccountBasicInfoChangeSend no changes!");
                return;
            }
            String changeType = DirectoryNotify.ChangeType.UPDATE;
            List<Account> accounts = new ArrayList<>();
            if(changeNumbers!=null&&changeNumbers.size()>0) {
                for (String number : changeNumbers) {
                    Optional<Account> accountOptional = accountsManager.get(number);
                    if (accountOptional.isPresent() && !accountOptional.get().isinValid()) {
                        accounts.add(accountOptional.get());
                    }
                }
            }
            if(accountChangeInfos!=null&&accountChangeInfos.size()>0) {
                for (String number : accountChangeInfos.keySet()) {
                    Optional<Account> accountOptional = accountsManager.get(number);
                    if (accountOptional.isPresent() && !accountOptional.get().isinValid()) {
                        accounts.add(accountOptional.get());
                    }
                }
            }
            if (accounts.size() == 0) {
                logger.info(" handleAccountBasicInfoChangeSend no changes!");
                return;
            }
            //找到受影响的人
            Map<Account, Set<DirectoryNotify.DirectoryMember>> affectedMembers = getAffectedNumbers(accounts, null, changeType,accountChangeInfos);
            if (affectedMembers == null || affectedMembers.size() == 0) {
                return;
            }
            //获取接收人的锁，增加版本号，并推送
            for (Account accountTemp : affectedMembers.keySet()) {
                //组织notify内容
                DirectoryNotify directoryNotify = createNotify(affectedMembers.get(accountTemp));
                Lock notifyLocker = DistributedLock.getLocker(new String[]{DirectoryNotify.DIRECTORY_CHANGE_NOTIFY_LOCK_KEY + accountTemp.getNumber()});
                try {
                    notifyLocker.lock();
                    if (((DirectoryNotify.NodifyData)directoryNotify.getData()).getMembers().size()==0) {
                        notifyManager.sendNotifySingle(accountTemp, addVersion(directoryNotify, accountTemp.getNumber(), true), null);
                    }else{
                        notifyManager.sendNotifySingle(accountTemp, addVersion(directoryNotify, accountTemp.getNumber(), false), null);
                    }
                } catch (Exception e) {
                    logger.error("handleAccountBasicInfoChangeSend sendNotifySingle error! msg:{}", e.getMessage());
                    e.printStackTrace();
                } finally {
                    notifyLocker.unlock();
                }
            }
        } catch (Exception e) {
            logger.error("DirectoryNotifyManager handleAccountBasicInfoChangeSend error! msg:{}", e.getMessage());
            e.printStackTrace();
        }

    }

    private void handleJoinTeam(Object[] args) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                Lock locker = null;
                try {
                    String changeType = DirectoryNotify.ChangeType.JOIN_TEAM;
                    // 一次添加多个人进team
                    List<Account> accounts = (List<Account>) args[0];
                    String team = (String) args[1];
                    if (StringUtil.isEmpty(team)) {
                        return;
                    }
                    if (accounts == null || accounts.size() == 0) {
                        return;
                    }
                    //找到受影响的人
                    Map<Account, Set<DirectoryNotify.DirectoryMember>> affectedMembers = getAffectedNumbers(accounts, args, changeType,null);
                    if (affectedMembers == null || affectedMembers.size() == 0) {
                        return;
                    }
                    locker = DistributedLock.getLocker(new String[]{DirectoryNotify.DIRECTORY_CHANGE_LOCK_KEY + team});

                    locker.lock();
                    //获取接收人的锁，增加版本号，并推送
                    for (Account accountTemp : affectedMembers.keySet()) {
                        //组织notify内容
                        DirectoryNotify directoryNotify = createNotify(affectedMembers.get(accountTemp));
                        Lock notifyLocker = DistributedLock.getLocker(new String[]{DirectoryNotify.DIRECTORY_CHANGE_NOTIFY_LOCK_KEY + accountTemp.getNumber()});
                        try {
                            notifyLocker.lock();
                            if (((DirectoryNotify.NodifyData)directoryNotify.getData()).getMembers().size()==0) {
                                notifyManager.sendNotifySingle(accountTemp, addVersion(directoryNotify, accountTemp.getNumber(), true), null);
                            }else{
                                notifyManager.sendNotifySingle(accountTemp, addVersion(directoryNotify, accountTemp.getNumber(), false), null);
                            }
                        } catch (Exception e) {
                            logger.error("handleJoinTeam sendNotifySingle error! msg:{}", e.getMessage());
                            e.printStackTrace();
                        } finally {
                            notifyLocker.unlock();
                        }
                    }
                } catch (Exception e) {
                    logger.error("DirectoryNotifyManager handleJoinTeam error! msg:{}", e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (locker != null) {
                        locker.unlock();
                    }
                }
            }
        });
    }

    private void handleLeaveTeam(Object[] args) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                Lock locker = null;
                try {
                    String changeType = DirectoryNotify.ChangeType.LEAVE_TEAM;
                    // 一次多个人离开tream
                    List<Account> accounts = (List<Account>) args[0];
                    String team = (String) args[1];
                    if (StringUtil.isEmpty(team)) {
                        return;
                    }
                    if (accounts == null || accounts.size() == 0) {
                        return;
                    }
                    //找到受影响的人
                    Map<Account, Set<DirectoryNotify.DirectoryMember>> affectedMembers = getAffectedNumbers(accounts, args, changeType,null);
                    if (affectedMembers == null || affectedMembers.size() == 0) {
                        return;
                    }
                    locker = DistributedLock.getLocker(new String[]{DirectoryNotify.DIRECTORY_CHANGE_LOCK_KEY + team});

                    locker.lock();

                    //获取接收人的锁，增加版本号，并推送
                    for (Account accountTemp : affectedMembers.keySet()) {
                        //组织notify内容
                        DirectoryNotify directoryNotify = createNotify(affectedMembers.get(accountTemp));
                        Lock notifyLocker = DistributedLock.getLocker(new String[]{DirectoryNotify.DIRECTORY_CHANGE_NOTIFY_LOCK_KEY + accountTemp.getNumber()});
                        try {
                            notifyLocker.lock();
                            if (((DirectoryNotify.NodifyData)directoryNotify.getData()).getMembers().size()==0) {
                                notifyManager.sendNotifySingle(accountTemp, addVersion(directoryNotify, accountTemp.getNumber(), true), null);
                            }else{
                                notifyManager.sendNotifySingle(accountTemp, addVersion(directoryNotify, accountTemp.getNumber(), false), null);
                            }
                        } catch (Exception e) {
                            logger.error("handleLeaveTeam sendNotifySingle error! msg:{}", e.getMessage());
                            e.printStackTrace();
                        } finally {
                            notifyLocker.unlock();
                        }
                    }
                } catch (Exception e) {
                    logger.error("DirectoryNotifyManager handleLeaveTeam error! msg:{}", e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (locker != null) {
                        locker.unlock();
                    }
                }
            }
        });
    }

    private void handleDelete(Object[] args) {
        //
        executor.submit(() -> {
            Account account = (Account) args[0];
            final List<String> contactList = (List<String>) args[1];
            //
            DirectoryNotify.DirectoryMember.Builder builder=new DirectoryNotify.DirectoryMember.Builder();
            builder.setAction(getAction(DirectoryNotify.ChangeType.DELETE)).setNumber(account.getNumber()).build();
            DirectoryNotify directoryNotify = createNotify(Collections.singleton(builder.build()));

            for (String uid : contactList) {
                final Optional<Account> contact = accountsManager.get(uid);
                notifyManager.sendNotifySingle(contact.get(), addVersion(directoryNotify, uid, false), null);
            }
        });
    }
    private void handleDisable(Object[] args) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                Lock locker = null;
                try {
                    String changeType = DirectoryNotify.ChangeType.DISABLE;
                    //todo 一次禁用多个人
                    List<Account> accounts = (List<Account>) args[0];
                    if (accounts == null || accounts.size() == 0) {
                        return;
                    }
                    //找到受影响的人
                    Map<Account, Set<DirectoryNotify.DirectoryMember>> affectedMembers = getAffectedNumbers(accounts, args, changeType,null);
                    if (affectedMembers == null || affectedMembers.size() == 0) {
                        return;
                    }
//                    locker = DistributedLock.getLocker(new String[]{DirectoryNotify.DIRECTORY_CHANGE_STATUS_LOCK_KEY});
//                    locker.lock();
                    //获取接收人的锁，增加版本号，并推送
                    for (Account accountTemp : affectedMembers.keySet()) {
                        //组织notify内容
                        DirectoryNotify directoryNotify = createNotify(affectedMembers.get(accountTemp));
                        Lock notifyLocker = DistributedLock.getLocker(new String[]{DirectoryNotify.DIRECTORY_CHANGE_NOTIFY_LOCK_KEY + accountTemp.getNumber()});
                        try {
                            notifyLocker.lock();
                            if (((DirectoryNotify.NodifyData)directoryNotify.getData()).getMembers().size()==0) {
                                notifyManager.sendNotifySingle(accountTemp, addVersion(directoryNotify, accountTemp.getNumber(), true), null);
                            }else{
                                notifyManager.sendNotifySingle(accountTemp, addVersion(directoryNotify, accountTemp.getNumber(), false), null);
                            }
                        } catch (Exception e) {
                            logger.error("handleLeaveTeam handleDisable error! msg:{}", e.getMessage());
                            e.printStackTrace();
                        } finally {
                            notifyLocker.unlock();
                        }
                    }
                } catch (Exception e) {
                    logger.error("DirectoryNotifyManager handleDisable error! msg:{}", e.getMessage());
                    e.printStackTrace();
                } finally {
//                    if (locker != null) {
//                        locker.unlock();
//                    }
                }
            }
        });
    }

    private void handleRegister(Object[] args) {
        executor.submit(() -> {
            Lock locker = null;
            try {
                String changeType = DirectoryNotify.ChangeType.REGISTER;
                //一次启用用多个人
                List<Account> accounts = (List<Account>) args[0];
                //找到受影响的人
                Map<Account, Set<DirectoryNotify.DirectoryMember>> affectedMembers = getAffectedNumbers(accounts, args, changeType,null);
                if (affectedMembers == null || affectedMembers.size() == 0) {
                    return;
                }
//                    locker = DistributedLock.getLocker(new String[]{DirectoryNotify.DIRECTORY_CHANGE_STATUS_LOCK_KEY});
//
//                    locker.lock();
                //获取接收人的锁，增加版本号，并推送
                for (Account accountTemp : affectedMembers.keySet()) {
                    //组织notify内容
                    DirectoryNotify directoryNotify = createNotify(affectedMembers.get(accountTemp));
                    Lock notifyLocker = DistributedLock.getLocker(new String[]{DirectoryNotify.DIRECTORY_CHANGE_NOTIFY_LOCK_KEY + accountTemp.getNumber()});
                    try {
                        notifyLocker.lock();
                        if (accounts.contains(accountTemp)||((DirectoryNotify.NodifyData)directoryNotify.getData()).getMembers().size()==0) {
                            // 通知自己
                            notifyManager.sendNotifySingle(accountTemp, addVersion(directoryNotify, accountTemp.getNumber(), true), null);
                        } else {
                            // 通知好友
                            notifyManager.sendNotifySingle(accountTemp, addVersion(directoryNotify, accountTemp.getNumber(), false), null);
                        }
                    } catch (Exception e) {
                        logger.error("handleLeaveTeam handleEnable error! msg:{}", e.getMessage());
                        e.printStackTrace();
                    } finally {
                        notifyLocker.unlock();
                    }
                }
            } catch (Exception e) {
                logger.error("DirectoryNotifyManager handleEnable error! msg:{}", e.getMessage());
                e.printStackTrace();
            } finally {
//                    if (locker != null) {
//                        locker.unlock();
//                    }
            }
        });
    }

    public DirectoryNotify addVersion(DirectoryNotify directoryNotify, String recipientNumber, boolean skip) {
        DirectoryNotify.NodifyData nodifyData = (DirectoryNotify.NodifyData) directoryNotify.getData();
        int addVersion = 1;
        if (skip) {
            addVersion = 2;
        }
        Long newVersion=memCache.incrBy(DirectoryNotify.PERSONAL_DIRECTORY_VERSION_KEY + recipientNumber,addVersion);
        //todo 运行一段时间后，可将查询旧缓存通讯录版本逻辑去除
        try (Jedis jedis = redisPool.getWriteResource()) {
            Long version=jedis.incrBy(DirectoryNotify.PERSONAL_DIRECTORY_VERSION_KEY + recipientNumber, addVersion);
            if(newVersion<version){
                newVersion=memCache.incrBy(DirectoryNotify.PERSONAL_DIRECTORY_VERSION_KEY + recipientNumber,version);
            }
        }
        nodifyData.setDirectoryVersion(newVersion);
        directoryNotify.setData(nodifyData);
        return directoryNotify;
    }

    public long getDirectoryVersion(String number) {
        if (StringUtil.isEmpty(number)) {
            logger.error("DirectoryNotifyManager getDirectoryVersion error, number is null!");
            return 0;
        }
        return memCache.incrBy(DirectoryNotify.PERSONAL_DIRECTORY_VERSION_KEY + number, 0);
    }

    public DirectoryNotify createNotify(Set<DirectoryNotify.DirectoryMember> members) {
        DirectoryNotify.NodifyData nodifyData = new DirectoryNotify.NodifyData(-1, new ArrayList(members));
        DirectoryNotify directoryNotify = new DirectoryNotify(System.currentTimeMillis(), nodifyData);
        return directoryNotify;
    }

    // 每个人的通讯录，增加了哪些人
    // 返回的key里面存放了被影响的人
    private Map<Account, Set<DirectoryNotify.DirectoryMember>> getAffectedNumbers(List<Account> accounts, Object[] args, String changeType,Map<String,AccountChangeInfo> accountChangeInfos) {
        Map<Account, Set<DirectoryNotify.DirectoryMember>> numbers = new HashMap<>();
        int action = getAction(changeType);
        if (DirectoryNotify.ChangeType.UPDATE.equals(changeType) || DirectoryNotify.ChangeType.DISABLE.equals(changeType) ||
                DirectoryNotify.ChangeType.ENABLE.equals(changeType)||  DirectoryNotify.ChangeType.REGISTER.equals(changeType)) {
            for (Account account : accounts) {
                Map<String, Account> affectedMembers = teamsManager.getContacts(account);
                affectedMembers.put(account.getNumber(), account);//加入自己，不为disable时，需通知自己
                DirectoryNotify.BasicInfoChangeType basicInfoChangeType=null;
                if(DirectoryNotify.ChangeType.UPDATE.equals(changeType)) {
                    basicInfoChangeType = accountBasicInfoIsChangeType(accountChangeInfos.get(account.getNumber()));
                }
                for (String number : affectedMembers.keySet()) {
                    Optional<Account> affectedMemberOptional =accountsManager.get(number);
                    if(!affectedMemberOptional.isPresent()){
                        continue;
                    }
                    Account affectedMember=affectedMemberOptional.get();
                    if(DirectoryNotify.ChangeType.UPDATE.equals(changeType)){
                        if(!basicInfoChangeIsNotify(account,affectedMember,accountChangeInfos,basicInfoChangeType)){
                            continue;
                        }
                    }
                    if (DirectoryNotify.ChangeType.DISABLE.equals(changeType) && accounts.contains(affectedMember)) {//被禁用的不通知
                        continue;
                    }
                    if(accounts.size()>50){
                        numbers.put(affectedMember, new HashSet<>());
                    }else {
                        if (numbers.keySet().contains(affectedMember)) {
                            numbers.get(affectedMember).add(createDirectoryMember(account, affectedMember, action, number.equals(account.getNumber())));
                        } else {
                            Set<DirectoryNotify.DirectoryMember> directoryMemberSet = new HashSet<>();
                            directoryMemberSet.add(createDirectoryMember(account, affectedMember, action, number.equals(account.getNumber())));
                            numbers.put(affectedMember, directoryMemberSet);
                        }
                    }
                }
            }
        } else if (DirectoryNotify.ChangeType.JOIN_TEAM.equals(changeType)) {
            //todo 修正team参数
            String team = (String) args[1];
            Map<String, Account> affectedMembers = teamsManager.getTeamMembers(team);
            for (Account account : accounts) {//新增加成员列表directoryMemberSet，用于通知team中其他人
                for (String number : affectedMembers.keySet()) {
                    Optional<Account> affectedMemberOptional =accountsManager.get(number);
                    if(!affectedMemberOptional.isPresent()){
                        continue;
                    }
                    Account affectedMember=affectedMemberOptional.get();
                    if(accounts.size()>50){
                        numbers.put(affectedMember, new HashSet<>());
                    }else {
                        if (accounts.contains(affectedMember)) {//新加入的人
                        } else {//其他人员，通知添加新加入的人
                            if (numbers.keySet().contains(affectedMember)) {
                                numbers.get(affectedMember).add(createDirectoryMember(account, affectedMember, action, number.equals(account.getNumber())));
                            } else {
                                Set<DirectoryNotify.DirectoryMember> directoryMemberSet = new HashSet<>();
                                directoryMemberSet.add(createDirectoryMember(account, affectedMember, action, number.equals(account.getNumber())));
                                numbers.put(affectedMember, directoryMemberSet);
                            }
                        }
                    }
                }

            }
            Set<DirectoryNotify.DirectoryMember> directoryMemberSetForNew = new HashSet<>();

            for (Account account : accounts) {//通知本次新加入的成员
                if(affectedMembers.size()>50){
                    numbers.put(account, new HashSet<>());
                }else {
                    for (String number : affectedMembers.keySet()) {
                        if (account.getNumber().equals(number)) {
                            continue;
                        }
                        Account affectedMember = affectedMembers.get(number);
                        if (numbers.keySet().contains(account)) {
                            numbers.get(account).add(createDirectoryMember(affectedMember, account, action, number.equals(account.getNumber())));
                        } else {
                            Set<DirectoryNotify.DirectoryMember> directoryMemberSet = new HashSet<>();
                            directoryMemberSet.add(createDirectoryMember(affectedMember, account, action, number.equals(account.getNumber())));
                            numbers.put(account, directoryMemberSet);
                        }
                    }
                }
            }
        } else if (DirectoryNotify.ChangeType.LEAVE_TEAM.equals(changeType)) {
            ////todo 修正team参数
            //String team = (String) args[1];
            ////当前组成员
            //Map<String, Account> affectedMembers = teamsManager.getTeamMembers(team);
            //for (Account account : accounts) {
            //    for (String number : affectedMembers.keySet()) {//为组里其他人员构建删除通知
            //        Optional<Account> affectedMemberOptional =accountsManager.get(number);
            //        if(!affectedMemberOptional.isPresent()){
            //            continue;
            //        }
            //        Account affectedMember=affectedMemberOptional.get();
            //        boolean isHasSameTeam = teamsManager.isFriend(account.getNumber(), affectedMember.getNumber());
            //        if (!isHasSameTeam) {
            //            if(accounts.size()>50){
            //                numbers.put(affectedMember, new HashSet<>());
            //            }else {
            //                if (numbers.keySet().contains(affectedMember)) {
            //                    numbers.get(affectedMember).add(createDirectoryMember(account, affectedMember, action, false));
            //                } else {
            //                    Set<DirectoryNotify.DirectoryMember> directoryMemberSet = new HashSet<>();
            //                    directoryMemberSet.add(createDirectoryMember(account, affectedMember, action, false));
            //                    numbers.put(affectedMember, directoryMemberSet);
            //                }
            //            }
            //        }
            //    }
            //}
            //for (Account account : accounts) {//为这次删除的人员构建删除通知
            //    for (String number : affectedMembers.keySet()) {//添加群里的现有成员
            //        Account affectedMember = affectedMembers.get(number);
            //        boolean isHasSameTeam = teamsManager.isFriend(account.getNumber(), affectedMember.getNumber());
            //        if (!isHasSameTeam) {
            //            if((accounts.size()+affectedMembers.size())>50) {
            //                numbers.put(account, new HashSet<>());
            //            }else{
            //                if (numbers.keySet().contains(account)) {
            //                    numbers.get(account).add(createDirectoryMember(affectedMember, account, action, false));
            //                } else {
            //                    Set<DirectoryNotify.DirectoryMember> directoryMemberSet = new HashSet<>();
            //                    directoryMemberSet.add(createDirectoryMember(affectedMember, account, action, false));
            //                    numbers.put(account, directoryMemberSet);
            //                }
            //            }
            //        }
            //    }
            //    for (Account accountTemp : accounts) {//添加本次离开team的其他成员
            //        if (account.equals(accountTemp)) {
            //            continue;
            //        }
            //        boolean isHasSameTeam = teamsManager.isFriend(account.getNumber(), accountTemp.getNumber());
            //        if (!isHasSameTeam) {
            //            if((accounts.size()+affectedMembers.size())>50) {
            //                numbers.put(account, new HashSet<>());
            //            }else {
            //                if (numbers.keySet().contains(account)) {
            //                    numbers.get(account).add(createDirectoryMember(accountTemp, account, action, false));
            //                } else {
            //                    Set<DirectoryNotify.DirectoryMember> directoryMemberSet = new HashSet<>();
            //                    directoryMemberSet.add(createDirectoryMember(accountTemp, account, action, false));
            //                    numbers.put(account, directoryMemberSet);
            //                }
            //            }
            //        }
            //    }
            //}
        }
        return numbers;
    }

    public DirectoryNotify.DirectoryMember createDirectoryMember(Account source,Account affecte,Integer action,boolean isSelf){
        final Conversation conversation = conversationManager.get(affecte.getNumber(), source.getNumber());
        DirectoryNotify.DirectoryMember.Builder builder=new DirectoryNotify.DirectoryMember.Builder();
        builder.setNumber(source.getNumber());
        builder.setName(source.getPlainName());
        //builder.setExtId(source.getExtId());
        builder.setPublicConfigs(source.getPublicConfigs());
        builder.setAvatar(source.getAvatar2());
        if (conversation != null) {
            builder.setRemark(conversation.getRemark());
        }
        if(affecte.isInternalAccount()||source.getNumber().equals(affecte.getNumber())) {
            builder.setSignature(source.getSignature());
            builder.setTimeZone(source.getTimeZone());
            builder.setDepartment(source.getDepartment());
            builder.setSuperior(source.getSuperior());
            builder.setGender(source.getGender());
            builder.setAddress(source.getAddress());
            builder.setProtectedConfigs(source.getProtectedConfigs());
        }
        if(isSelf) {
            //builder.setEmail(source.getEmail());
            builder.setFlag(source.getFlag());
            builder.setPrivateConfigs(source.getPrivateConfigs());
        }
        builder.setAction(action);
        return builder.build();
    }

    private boolean basicInfoChangeIsNotify(Account account, Account affectedMember, Map<String,AccountChangeInfo> accountChangeInfos, DirectoryNotify.BasicInfoChangeType basicInfoChangeType) {
        //改的仅有自己私有信息时，不通知其他人
        if (DirectoryNotify.BasicInfoChangeType.PRIVATE.equals(basicInfoChangeType)) {
            if (!account.getNumber().equals(affectedMember.getNumber())) {
                return false;
            }
        }
        //protected变更时，不通知其他的外部账号
        if (DirectoryNotify.BasicInfoChangeType.PROTECTED.equals(basicInfoChangeType)) {
            if (!account.getNumber().equals(affectedMember.getNumber()) && !affectedMember.isInternalAccount()) {
                return false;
            }
        }
        return true;
    }
    private int getAction(String changeType) {
        int action = -1;
        switch (changeType) {
            case DirectoryNotify.ChangeType.UPDATE:
            case DirectoryNotify.ChangeType.REGISTER:
            case DirectoryNotify.ChangeType.ENABLE:
                action = DirectoryNotify.ActionType.UPDATE.ordinal();
                break;
            case DirectoryNotify.ChangeType.JOIN_TEAM:
                action = DirectoryNotify.ActionType.ADD.ordinal();
                break;
            case DirectoryNotify.ChangeType.LEAVE_TEAM:
            case DirectoryNotify.ChangeType.DISABLE:
                action = DirectoryNotify.ActionType.DELETE.ordinal();
                break;

            case DirectoryNotify.ChangeType.DELETE:
                action = DirectoryNotify.ActionType.PERMANENT_DELETE.ordinal();

            default:
        }
        return action;
    }

    private void setBasicChangeInfo(AccountChangeInfo accountChangeInfo){
//          edis.sadd(DirectoryNotify.DIRECTORY_NOTIFY_CHANGE_SET_KEY, number);
        Lock locker = DistributedLock.getLocker(new String[]{DirectoryNotify.DIRECTORY_BASIC_INFO_CHANGE_LOCK_KEY});
        try {
            locker.lock();
            String number=accountChangeInfo.getNumber();
            String accountChangeInfoStr = memCache.hget(DirectoryNotify.DIRECTORY_NOTIFY_CHANGE_MAP_KEY,number);
            if (StringUtil.isEmpty(accountChangeInfoStr)) {
                memCache.hset(DirectoryNotify.DIRECTORY_NOTIFY_CHANGE_MAP_KEY, number, objectMapper.writeValueAsString(accountChangeInfo));
            } else {
                AccountChangeInfo currentChangeInfo = objectMapper.readValue(accountChangeInfoStr, AccountChangeInfo.class);
                currentChangeInfo.addChangeFields(accountChangeInfo.getFields());
                memCache.hset(DirectoryNotify.DIRECTORY_NOTIFY_CHANGE_MAP_KEY, number, objectMapper.writeValueAsString(currentChangeInfo));
            }
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } finally {
            locker.unlock();
        }
    }

    private Map<String,AccountChangeInfo> getBasicChangeInfo(){
        Lock locker = DistributedLock.getLocker(new String[]{DirectoryNotify.DIRECTORY_BASIC_INFO_CHANGE_LOCK_KEY});
        try {
            locker.lock();
            Map<String,AccountChangeInfo> accountChangeInfos=new HashMap<>();
            Map<String,String> accountChangeInfoStrs=memCache.hgetAll(DirectoryNotify.DIRECTORY_NOTIFY_CHANGE_MAP_KEY);
            if (accountChangeInfoStrs!=null&&accountChangeInfoStrs.size()>0) {
                memCache.remove(DirectoryNotify.DIRECTORY_NOTIFY_CHANGE_MAP_KEY);
                for(String key:accountChangeInfoStrs.keySet()) {
                    accountChangeInfos.put(key,objectMapper.readValue(accountChangeInfoStrs.get(key), AccountChangeInfo.class));
                }
            }
            return accountChangeInfos;
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } finally {
            locker.unlock();
        }
        return null;
    }
    private DirectoryNotify.BasicInfoChangeType accountBasicInfoIsChangeType(AccountChangeInfo accountChangeInfo){
        if(accountChangeInfo==null){
            return null;
        }
        int allCount=accountChangeInfo.getFields().size();
        int privateInfoChangeCount=0;
        int publicInfoChangeCount=0;
        if(accountChangeInfo.hasChangeField("flag")){
            privateInfoChangeCount+=1;
        }
        if(accountChangeInfo.hasChangeField("privateConfigs")){
            privateInfoChangeCount+=1;
        }
        if(accountChangeInfo.hasChangeField("publicConfigs")){
            publicInfoChangeCount+=1;
        }
        if(accountChangeInfo.hasChangeField("plainName")){
            publicInfoChangeCount+=1;
        }
        if(accountChangeInfo.hasChangeField("inactive")){
            publicInfoChangeCount+=1;
        }
        if(accountChangeInfo.hasChangeField("extId")){
            publicInfoChangeCount+=1;
        }
        if(allCount==privateInfoChangeCount){
            return DirectoryNotify.BasicInfoChangeType.PRIVATE;
        }
        if(publicInfoChangeCount>0){
            return DirectoryNotify.BasicInfoChangeType.PUBLIC;
        }

        return DirectoryNotify.BasicInfoChangeType.PROTECTED;
    }
    private AccountChangeInfo accountBasicInfoIsChange(Account currentAccount, Account newAccount) {
        AccountChangeInfo accountChangeInfo=new AccountChangeInfo();
        accountChangeInfo.setNumber(currentAccount.getNumber());
        if (!objEquals(currentAccount.getPlainName(), newAccount.getPlainName())) {
            accountChangeInfo.addChangeField("plainName");
        }
        if (!objEquals(currentAccount.getAvatar2(), newAccount.getAvatar2())) {
            accountChangeInfo.addChangeField("avatar");
        }
        if (!objEquals(currentAccount.getSignature(), newAccount.getSignature())) {
            accountChangeInfo.addChangeField("signature");
        }
        if (!objEquals(currentAccount.getExtId(), newAccount.getExtId())) {
            accountChangeInfo.addChangeField("extId");
        }
        if (!objEquals(currentAccount.getTimeZone(), newAccount.getTimeZone())) {
            accountChangeInfo.addChangeField("timeZone");
        }
        if (!objEquals(currentAccount.getPrivateConfigs(), newAccount.getPrivateConfigs())) {
            accountChangeInfo.addChangeField("privateConfigs");
            //兼容老版本，修改全局配置时，将使用全局配置的群的notification字段级联变更下，并通知自己
            if(!objEquals(currentAccount.getGlobalNotification(),newAccount.getGlobalNotification())){
                groupManagerWithTransaction.setGroupNotificationForChangeGlobalNotification(newAccount);
            }
            accountChangeInfo.setImmediateNotify(true);
        }
        if (!objEquals(currentAccount.getProtectedConfigs(), newAccount.getProtectedConfigs())) {
            accountChangeInfo.addChangeField("protectedConfigs");
        }
        if (!objEquals(currentAccount.getPublicConfigs(), newAccount.getPublicConfigs())) {
            accountChangeInfo.addChangeField("publicConfigs");
            if (newAccount.getPublicConfigs() != null && (
                    newAccount.getPublicConfigs().containsKey(AccountExtend.FieldName.MeetingVersion) )
            ) {
                accountChangeInfo.setImmediateNotify(true);
            }
        }
        if (!objEquals(currentAccount.isInactive(), newAccount.isInactive())) {
            accountChangeInfo.addChangeField("inactive");
        }
        return accountChangeInfo;
    }

    public boolean objEquals(Object o1,Object o2){
        if(o1!=null &&o1 instanceof String) o1=trimString((String)o1);
        if(o2!=null &&o2 instanceof String) o2=trimString((String)o2);
        return Objects.equals(o1,o2);
    }
    private String trimString(String str){
        if(StringUtil.isEmpty(str.trim())){
            return null;
        }
        return str.trim();
    }
    public static class AccountChangeInfo{
        private String number;
        private Set<String> fields=new HashSet<>();

        public boolean isImmediateNotify() {
            return immediateNotify;
        }

        public void setImmediateNotify(boolean immediateNotify) {
            this.immediateNotify = immediateNotify;
        }

        public boolean immediateNotify=false;
        public AccountChangeInfo(){};
        public void addChangeField(String field){
            fields.add(field);
        }

        public void addChangeFields(Set<String> fields){
            this.fields.addAll(fields);
        }

        public boolean hasChangeField(String field){
            return fields.contains(field);
        }

        public boolean hasChanged(){
            return fields.size()>0;
        }
        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public Set<String> getFields() {
            return fields;
        }

        public void setFields(Set<String> fields) {
            this.fields = fields;
        }
    }

}
