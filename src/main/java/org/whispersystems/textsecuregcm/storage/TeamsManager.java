package org.whispersystems.textsecuregcm.storage;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.directorynotify.DirectoryNotifyManager;
import org.whispersystems.textsecuregcm.entities.DirectoryNotify;
import redis.clients.jedis.JedisPubSub;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TeamsManager {

  private final Logger logger = LoggerFactory.getLogger(AccountsManager.class);

  private final InternalTeamsDB internalTeamsDB;
  private final InternalTeamsAccountsTable internalTeamsAccountsTable;
  private final AccountsManager accountsManager;
  private final MemCache memCache;
  private DirectoryNotifyManager directoryNotifyManager;
  Cache<String, Object> caffeine= Caffeine.newBuilder().build();

  private static final String BOT_TEAM = "botteam";

  public TeamsManager(InternalTeamsDB internalTeamsDB, InternalTeamsAccountsTable internalTeamsAccountsTable, AccountsManager accountsManager, MemCache memCache) {
    this.internalTeamsDB = internalTeamsDB;
    this.internalTeamsAccountsTable = internalTeamsAccountsTable;
    this.accountsManager = accountsManager;
    this.memCache = memCache;
    reloadTeamMembersToCaffeine();
  }

  public void create(String name) {
    internalTeamsDB.insert(name);
  }

  public void createNew(String name, long parentId, String ancestors, boolean status, int orderNum, String appid, String pid){
    Team team = get(pid);
    if(team != null){
      parentId = team.getId();
    }
    internalTeamsDB.insertNew(name, parentId, ancestors, status, orderNum, appid, pid, null);
  }

  public void updateNew(long id, String name, long parentId, String ancestors, boolean status, int orderNum){
    internalTeamsDB.updateNew(id, name, parentId, ancestors, status, orderNum, null);
  }

  public Team get(String name) {
    InternalGroupsRow row = internalTeamsDB.get(name);
    if (null == row) {
      return null;
    }

    return new Team(row.getId(), row.getName());
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
    InternalGroupsRow row = internalTeamsDB.getById(tid);
    if(null == row){
      return null;
    }
    return new Team(row.getId(),row.getName(),row.getAppid(),row.getPid());
  }

  public List<Team> getTree(String name, List<Boolean> status){
    return  internalTeamsDB.getTree(name, status);
  }

  public List<Team> getAll(int offset, int limit, String pid) {
    List<Team> result = new ArrayList<>();
    for (InternalGroupsRow row : internalTeamsDB.getAll(offset, limit, pid)) {
      result.add(new Team(row.getId(),row.getName()));
    }
    return result;
  }

  public void join(String uid, String team) {
    // TODO: validate parameters
    internalTeamsAccountsTable.insert(team, uid);
    memCache.remove(getUserTeamsKey(uid));
//    memCache.remove(getTeamMembersKey(team));
//    reloadTeamMemebers(team,false);
    // TODO: rebuild cache asynchronously (beware of multiple threads)
  }
  public void joinTeam(List<Account> accounts, String team) {
    for(Account account:accounts) {
      internalTeamsAccountsTable.insert(team, account.getNumber());
      memCache.remove(getUserTeamsKey(account.getNumber()));
    }
//    memCache.remove(getTeamMembersKey(team));
    reloadTeamMemebers(team,false);
    directoryNotifyManager.directoryNotifyHandle(DirectoryNotify.ChangeType.JOIN_TEAM,new Object[]{accounts,team});
  }

  public void leaveTeam(List<Account> accounts, String team) {
    for(Account account:accounts) {
      internalTeamsAccountsTable.delete(team,account.getNumber());
      memCache.remove(getUserTeamsKey(account.getNumber()));
    }
//    memCache.remove(getTeamMembersKey(team));
    reloadTeamMemebers(team,false);
    directoryNotifyManager.directoryNotifyHandle(DirectoryNotify.ChangeType.LEAVE_TEAM,new Object[]{accounts,team});
  }

  public Set<String> getUserTeams(String uid) {
    Set<String> teams = (Set<String>) memCache.get(getUserTeamsKey(uid), Set.class);
    if (null == teams) {
      teams = new HashSet<>();
      for (InternalGroupsAccountsRow g : internalTeamsAccountsTable.getAccountGroups(uid)) {
        teams.add(g.getGroup_name());
      }

      memCache.set(getUserTeamsKey(uid), teams);
    }

    return teams;
  }

  public boolean isHasSameTeam(String sourceNumber, String targetNumber) {
    Set<String> accountTeams = this.getUserTeams(sourceNumber);
    Set<String> numTeams = this.getUserTeams(targetNumber);

    // 放开 BOT_TEAM 的限制
    if (accountTeams.contains(BOT_TEAM) || numTeams.contains(BOT_TEAM)) return true;

    Set<String> result = new HashSet<String>();
    result.addAll(accountTeams);
    result.retainAll(numTeams);
    if (result.size() > 0) {
      return true;
    }
    return false;
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
      accountTemp.setNumber(result.get(k).getNumber());
      accountTemp.setPlainName(result.get(k).getPlainName());
      accountTemp.setPublicConfigs(result.get(k).getPublicConfigs());
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
      accountTemp.setNumber(result.get(k).getNumber());
      accountTemp.setPlainName(result.get(k).getPlainName());
      accountTemp.setPublicConfigs(result.get(k).getPublicConfigs());
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
    if(!optionalAccount.isPresent()){
      return null;
    }
    return getContacts(optionalAccount.get());
  }

  public Map<String, Account> getContacts(Account account) {
    Long begin=System.currentTimeMillis();
    Map<String, Account> contacts = new HashMap<>();
    Set<String> teams = getUserTeams(account.getNumber());

    teams.add(BOT_TEAM); // 加入全员可见的team
    logger.info("number:{} ,TeamsManager.getContacts.getUserTeams used:{}",account.getNumber(),System.currentTimeMillis()-begin);
    for (String team : teams) {
      Long begin2=System.currentTimeMillis();
      Map<String, Account> members = getTeamMembers(team);
      logger.info("number:{} ,TeamsManager.getContacts.getTeamMembers , team:{} used:{}",account.getNumber(),team,System.currentTimeMillis()-begin2);
      for (Account member : members.values()) {
        // if (!member.isActive()) continue; // 排除非Active
//        if (!accountsManager.isActiveByLastSeen(member)) {
//         continue;
//        }
        if (contacts.containsKey(member)) {
          continue;
        }

        member = accountsManager.getInfoWithPermissionCheck(account, member);
        if (null == member) {
            continue;
        }

        contacts.put(member.getNumber(), member);
      }
    }
    // remove self
    // contacts.remove(uid);
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

  public void reloadTeamMembersForUser(String number) {
    StackTraceElement[] sts=Thread.currentThread().getStackTrace();
    for(StackTraceElement stackTraceElement:sts){
      logger.info(stackTraceElement.toString());
    }
    new Thread(new Runnable() {
      @Override
      public void run() {
        Set<String> teams = getUserTeams(number);
        if(teams!=null&&teams.size()>0) {
          for (String team : teams) {
//            memCache.remove(getTeamMembersKey(team));
            reloadTeamMembers(team);
          }
        }
      }
    }).start();
  }
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
}
