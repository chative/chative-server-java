package org.whispersystems.textsecuregcm.storage;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.difft.factory.EnforcerFactory;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.directorynotify.DirectoryNotifyManager;
import org.whispersystems.textsecuregcm.entities.DirectoryNotify;
import org.whispersystems.textsecuregcm.rpcclient.FriendGRPC;
import redis.clients.jedis.JedisPubSub;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TeamsManagerCasbin {

    private final Logger logger = LoggerFactory.getLogger(AccountsManager.class);

    private final InternalTeamsDB internalTeamsDB;
    private final InternalTeamsAccountsTable internalTeamsAccountsTable;
    private final AccountsManager accountsManager;
    private final MemCache memCache;
    private DirectoryNotifyManager directoryNotifyManager;
    Cache<String, Object> caffeine= Caffeine.newBuilder().build();
    private EnforcerFactory enforcerFactory;

    private static final String BOT_TEAM = "botteam";
    private static final String TEAM_ID = "team:";
    private static final String DOMAIN = "domain:";
    private static final String ACCOUNT_NUMBER = "account:";
    FriendGRPC friendGRPC;

    public void setFriendGRPC(FriendGRPC friendGRPC) {
        this.friendGRPC = friendGRPC;
    }

    public TeamsManagerCasbin(InternalTeamsDB internalTeamsDB, InternalTeamsAccountsTable internalTeamsAccountsTable, AccountsManager accountsManager, MemCache memCache, EnforcerFactory enforcerFactory) {
        this.internalTeamsDB = internalTeamsDB;
        this.internalTeamsAccountsTable = internalTeamsAccountsTable;
        this.accountsManager = accountsManager;
        this.memCache = memCache;
        this.enforcerFactory = enforcerFactory;
        reloadTeamMembersToCaffeine();
    }

    public void create(String name) {
        internalTeamsDB.insert(name);
    }


    public void updateNew(long id, String name, long parentId, String ancestors, boolean status, int orderNum, String remark){
        internalTeamsDB.updateNew(id, name, parentId, ancestors, status, orderNum, remark);
        memCache.remove(getTeamKey(name));
        memCache.remove(getTeamIDKey(id+""));
    }

    public Team get(String name) {
        Team team = (Team)memCache.get(getTeamKey(name), Team.class);
        return team;
    }

    public Team getForEndWith(String name) {
        List<InternalGroupsRow> result = internalTeamsDB.getForEndWith(name);
        if (null == result||result.size()==0) {
            return null;
        }else{
            InternalGroupsRow row=result.get(0);
            return new Team(row.getId(), row.getName());
        }
    }


    public Team getById(int tid){
        Team team = (Team)memCache.get(getTeamIDKey(tid + ""), Team.class);
        if(null == team){
            InternalGroupsRow row = internalTeamsDB.getById(tid);
            if(null == row){
                return null;
            } else {
                team = new Team(row.getId(),row.getName(),row.getAppid(),row.getPid());
                memCache.set(getTeamIDKey(tid + ""), team);
            }
        }
        return team;
    }

    public List<Team> getTree(String name, List<Boolean> status){
        return  internalTeamsDB.getTree(name, status);
    }

    public List<Team> getAll(int offset, int limit, String pid) {
        List<Team> result = new ArrayList<>();
        for (InternalGroupsRow row : internalTeamsDB.getAll(offset, limit, pid)) {
            result.add(new Team(row.getId(),row.getName(), row.getAppid(), row.getPid()));
        }
        return result;
    }



    public Set<String> getUserTeams(String uid) {
        Set<String> teams = (Set<String>) memCache.get(getUserTeamsKey(uid), Set.class);
        if (null == teams || teams.isEmpty()) {
            teams = new HashSet<>();
            for (InternalGroupsAccountsRow g : internalTeamsAccountsTable.getAccountGroups(uid)) {
                teams.add(g.getGroup_name());
            }
            memCache.set(getUserTeamsKey(uid), teams);
        }

        return teams;
    }

    public boolean isFriend(String sourceNumber, String targetNumber) {
        if (sourceNumber.equals(targetNumber)) {
            return true;
        }
        return friendGRPC.isFriend(sourceNumber,targetNumber);
    }

    public Map<String, Account> getTeamMembers(String team) {
        Map<String, Account> result =(HashMap<String, Account>)  caffeine.getIfPresent(getTeamMembersKey(team));
        if (null == result) {
            result = (HashMap<String, Account>) memCache.get(getTeamMembersKey(team), Map.class);
            if(result==null) {
                result = new HashMap<>();
                for (InternalGroupsAccountsRow t : internalTeamsAccountsTable.getGroupAccounts(team)) {
                    // skip duplicates
                    if (null != result.get(t.getAccount_number())) {
                        continue;
                    }

                    Optional<Account> oaccount = accountsManager.get(t.getAccount_number());
                    if (!oaccount.isPresent()) {
                        logger.error("Account " + t.getAccount_number() + " is in team \"" + t.getGroup_name() + "\" but was not found in accountsManager");
                        continue;
                    }
                    Account account = oaccount.get();

                    // skip disabled accounts
                    if (account.isinValid()) {
                        continue;
                    }

                    result.put(account.getNumber(), account);
                }
                memCache.set(getTeamMembersKey(team), result);
                caffeinePut(team, result);
            } else {
                // Convert to Account
                for (String k : result.keySet()) {
                    result.put(k, (Account) memCache.convert(result.get(k), Account.class));
                }
                caffeinePut(team, result);
            }
        }else{
            logger.info("TeamsManager getTeamMembersFromCaffeine team:{} ",team);
        }
        return result;
    }

    public Map<String, Account> getTeamMembersForCaffeine(String team,boolean isInternal) {
        String caffeineKey=getTeamMembersKey(team);
        if(!isInternal){
            caffeineKey=getTeamMembersKeyForOutAccount(team);
        }
        Map<String, Account> result =(HashMap<String, Account>) caffeine.getIfPresent(caffeineKey);
        if (null == result) {
            result = (HashMap<String, Account>) memCache.get(getTeamMembersKey(team), Map.class);
            if(result==null) {
                result = new HashMap<>();
                for (InternalGroupsAccountsRow t : internalTeamsAccountsTable.getGroupAccounts(team)) {
                    // skip duplicates
                    if (null != result.get(t.getAccount_number())) {
                        continue;
                    }

                    Optional<Account> oaccount = accountsManager.get(t.getAccount_number());
                    if (!oaccount.isPresent()) {
                        logger.error("Account " + t.getAccount_number() + " is in team \"" + t.getGroup_name() + "\" but was not found in accountsManager");
                        continue;
                    }
                    Account account = oaccount.get();

                    // skip disabled accounts
                    if (account.isinValid()) {
                        continue;
                    }

                    result.put(account.getNumber(), account);
                }
                memCache.set(getTeamMembersKey(team), result);
                caffeinePut(team, result);
            } else {
                // Convert to Account
                for (String k : result.keySet()) {
                    result.put(k, (Account) memCache.convert(result.get(k), Account.class));
                }
                caffeinePut(team, result);
            }
            result=(HashMap<String, Account>) caffeine.getIfPresent(caffeineKey);
        }else{
            logger.info("TeamsManager getTeamMembersFromCaffeine team:{} ",team);
        }
        return result;
    }

    private void caffeinePut(String team,Map<String, Account> result){
        caffeine.put(getTeamMembersKey(team), result);
        HashMap<String,Account> resultForOutAccount=new HashMap<>();
        for (String k : result.keySet()) {
            Account accountTemp = new Account();
            accountTemp.setAvatar2(result.get(k).getAvatar2());
            accountTemp.setNumber(result.get(k).getNumber());
            accountTemp.setPlainName(result.get(k).getPlainName());
            accountTemp.setPublicConfigs(result.get(k).getPublicConfigs());
            accountTemp.setExtId(result.get(k).getExtId());
            resultForOutAccount.put(k, accountTemp);
        }
        caffeine.put(getTeamMembersKeyForOutAccount(team), resultForOutAccount);
        memCache.getJedisCluster().publish("caffeinePut",team);
        logger.info("TeamsManager caffeinePut team:{} ",team);
    }

    public void caffeinePutForSub(String team) {
        Map<String, Account> result = (HashMap<String, Account>) memCache.get(getTeamMembersKey(team), Map.class);
        if(result==null) {
            result = new HashMap<>();
            for (InternalGroupsAccountsRow t : internalTeamsAccountsTable.getGroupAccounts(team)) {
                // skip duplicates
                if (null != result.get(t.getAccount_number())) {
                    continue;
                }
                Optional<Account> oaccount = accountsManager.get(t.getAccount_number());
                if (!oaccount.isPresent()) {
                    logger.error("Account " + t.getAccount_number() + " is in team \"" + t.getGroup_name() + "\" but was not found in accountsManager");
                    continue;
                }
                Account account = oaccount.get();
                // skip disabled accounts
                if (account.isinValid()) {
                    continue;
                }
                result.put(account.getNumber(), account);
            }
            memCache.set(getTeamMembersKey(team), result);
            caffeinePutForSub(team, result);
        } else {
            // Convert to Account
            for (String k : result.keySet()) {
                result.put(k, (Account) memCache.convert(result.get(k), Account.class));
            }
            caffeinePutForSub(team, result);
        }
    }

    private void caffeinePutForSub(String team,Map<String, Account> result){
        caffeine.put(getTeamMembersKey(team), result);
        HashMap<String,Account> resultForOutAccount=new HashMap<>();
        for (String k : result.keySet()) {
            Account accountTemp = new Account();
            accountTemp.setAvatar2(result.get(k).getAvatar2());
            accountTemp.setNumber(result.get(k).getNumber());
            accountTemp.setPlainName(result.get(k).getPlainName());
            accountTemp.setPublicConfigs(result.get(k).getPublicConfigs());
            accountTemp.setExtId(result.get(k).getExtId());
            resultForOutAccount.put(k, accountTemp);
        }
        caffeine.put(getTeamMembersKeyForOutAccount(team), resultForOutAccount);
        logger.info("TeamsManager caffeinePutForSub team:{} ",team);
    }

    private void caffeineSub(){
        try {
            Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    memCache.getJedisCluster().subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String message) {
                            if(channel.equals("caffeinePut")){
                                caffeinePutForSub(message);
                            }
                        }
                    },"caffeinePut");
                }
            },0L,1000L, TimeUnit.MILLISECONDS).get();
        } catch (Exception e) {
            logger.error("TeamsManager.caffeineSub error!",e);
        }
    }

    public void  reloadTeamMembers(String team) {
        Map<String, Account> result = new HashMap<>();
        for (InternalGroupsAccountsRow t : internalTeamsAccountsTable.getGroupAccounts(team)) {
            // skip duplicates
            if (null != result.get(t.getAccount_number())) {
                continue;
            }
            Optional<Account> oaccount = accountsManager.get(t.getAccount_number());
            if (!oaccount.isPresent()) {
                logger.error("Account " + t.getAccount_number() + " is in team \"" + t.getGroup_name() + "\" but was not found in accountsManager");
                continue;
            }
            Account account = oaccount.get();
            // skip disabled accounts
            if (account.isinValid()) {
                continue;
            }
            result.put(account.getNumber(), account);
        }
        memCache.set(getTeamMembersKey(team), result);
        caffeinePut(team, result);
    }


    public Map<String, Account> getContacts(String uid) {
        Optional<Account> optionalAccount=accountsManager.get(uid);
        return optionalAccount.map(this::getContacts).orElse(null);
    }

    public Map<String, Account> getContacts(Account account) {
        Long begin=System.currentTimeMillis();
        final List<String> friends = friendGRPC.ListFriend(account.getNumber());
        Map<String, Account> contacts = new HashMap<>(friends.size());

        for (String friend : friends) {
            final Account member = accountsManager.getInfoWithPermissionCheck(account, friend);
            if (null == member) {
                continue;
            }
            contacts.put(member.getNumber(), member);
        }
        logger.info("number:{} ,TeamsManager.getContacts  used:{}",account.getNumber(),System.currentTimeMillis()-begin);
        return contacts;
    }

    public Map<String, Account> getContactsForCaffeine(Account account) {
        Long begin=System.currentTimeMillis();
        Map<String, Account> contacts = new HashMap<>();
        Set<String> teams = getUserTeams(account.getNumber());
        teams.add(BOT_TEAM); // 加入全员可见的team
        logger.info("number:{} ,TeamsManager.getContacts.getUserTeams used:{}",account.getNumber(),System.currentTimeMillis()-begin);
        for (String team : teams) {
            Long begin2=System.currentTimeMillis();
            Map<String, Account> members = getTeamMembersForCaffeine(team,account.isInternalAccount());
            logger.info("number:{} ,TeamsManager.getContacts.getTeamMembers , team:{} used:{}",account.getNumber(),team,System.currentTimeMillis()-begin2);
            if(members!=null) {
                contacts.putAll(members);
            }
        }
        if(!account.isInternalAccount()){
            contacts.put(account.getNumber(),account);
        }
        logger.info("number:{} ,TeamsManager.getContacts  used:{}",account.getNumber(),System.currentTimeMillis()-begin);
        return contacts;
    }

    public String getUserTeamsKey(String uid) {
        return String.join("_","Team", "userteams", String.valueOf(InternalTeamsAccountsTable.MEMCACHE_VERION), uid);
    }

    public String getTeamMembersKey(String team) {
        return String.join("_","Team", "teammembers", String.valueOf(InternalTeamsAccountsTable.MEMCACHE_VERION), team);
    }

    public String getTeamMembersKeyForOutAccount(String team) {
        return String.join("_","Team", "teammembers", String.valueOf(InternalTeamsAccountsTable.MEMCACHE_VERION), team,"out");
    }

    public String getTeamKey(String team){
        return String.join("_","Team", "teamname", String.valueOf(InternalTeamsAccountsTable.MEMCACHE_VERION), team);
    }

    public String getTeamIDKey(String teamId){
        return String.join("_","Team", "teamid", String.valueOf(InternalTeamsAccountsTable.MEMCACHE_VERION), teamId);
    }

    public DirectoryNotifyManager getDirectoryNotifyManager() {
        return directoryNotifyManager;
    }

    public void setDirectoryNotifyManager(DirectoryNotifyManager directoryNotifyManager) {
        this.directoryNotifyManager = directoryNotifyManager;
    }

    public void reloadTeamMemebers(String team, boolean isRemove){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isRemove){
                    memCache.remove(getTeamMembersKey(team));
                }
                reloadTeamMembers(team);
            }
        }).start();
    }

//    public void reloadTeamMembersForUser(String number) {
//        StackTraceElement[] sts=Thread.currentThread().getStackTrace();
//        for(StackTraceElement stackTraceElement:sts){
//            logger.info(stackTraceElement.toString());
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Set<String> teams = getUserTeams(number);
//                if(teams!=null&&teams.size()>0) {
//                    for (String team : teams) {
////            memCache.remove(getTeamMembersKey(team));
//                        reloadTeamMembers(team);
//                    }
//                }
//            }
//        }).start();
//    }
    public void reloadTeamMembers() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Boolean> booleanList=new ArrayList<>();
                booleanList.add(false);
                List<Team> teams = getTree("",booleanList);
                if (teams != null && teams.size() > 0) {
                    for (Team team : teams) {
//            memCache.remove(getTeamMembersKey(team));
                        reloadTeamMembers(team.getName());
                    }
                }

            }
        }).start();
    }

    public void reloadTeamMembersToCaffeine() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                caffeineSub();
                List<Boolean> booleanList=new ArrayList<>();
                booleanList.add(false);
                List<Team> teams = getTree("",booleanList);
                if (teams != null && teams.size() > 0) {
                    for (Team team : teams) {
//            memCache.remove(getTeamMembersKey(team));
                        getTeamMembers(team.getName());
                    }
                }

            }
        }).start();
    }

    public boolean isInTeam(Account account,String team){
        Set<String> teams=getUserTeams(account.getNumber());
        return teams.contains(team);
    }

    private String getTeamKeyValue(String prefixKey){
        return prefixKey.substring(prefixKey.indexOf(":")+1, prefixKey.length());
    }

}
