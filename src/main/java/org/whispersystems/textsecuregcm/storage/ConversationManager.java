package org.whispersystems.textsecuregcm.storage;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.SharedMetricRegistries;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.ConversationNotify;
import org.whispersystems.textsecuregcm.entities.GroupNotify;
import org.whispersystems.textsecuregcm.entities.Notification;
import org.whispersystems.textsecuregcm.entities.Notify;
import org.whispersystems.textsecuregcm.util.Constants;
import org.whispersystems.textsecuregcm.util.SystemMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.codahale.metrics.MetricRegistry.name;

public class ConversationManager {

  private final Logger logger = LoggerFactory.getLogger(ConversationManager.class);
  private MemCache memCache;
  private final ConversationsTable conversationsTable;
  private NotifyManager notifyManager;
  private MessagesManager messagesManager;
  ThreadPoolExecutor executor=new ThreadPoolExecutor(20,20,0, TimeUnit.SECONDS,new LinkedBlockingDeque<>(), new ThreadPoolExecutor.CallerRunsPolicy());
  public void registerMetrics(){
    SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME)
            .register(name(GroupManagerWithTransaction.class, "ConversationManager_executor_depth"),
                    (Gauge<Long>) ((ThreadPoolExecutor)executor)::getTaskCount);
  }
  public ConversationManager(ConversationsTable conversationsTable,MemCache memCache,MessagesManager messagesManager){
    this.conversationsTable=conversationsTable;
    this.memCache=memCache;
    this.messagesManager=messagesManager;
    registerMetrics();
  }

  public Conversation store(Conversation conversation) {
    //logger.info("in store conversation:{}",conversation);
    long id=conversationsTable.insert(conversation);
    conversation.setId(id);
    //logger.info("over store conversation:{}",conversation);
    memCache.hset(getAccountConversationKey(conversation.getNumber()),getConversationKey(conversation.getConversation()),conversation);
    return conversation;
  }

  public Conversation update(Conversation conversation,ConversationNotify.ChangeType changeType) {
    int version=conversationsTable.update(conversation);
    conversation.setVersion(version);
    memCache.hset(getAccountConversationKey(conversation.getNumber()),getConversationKey(conversation.getConversation()),conversation);
    if(changeType==ConversationNotify.ChangeType.BLOCK&&conversation.getBlockStatus()==ConversationsTable.STATUS.OPEN.getCode()){
      messagesManager.clear(conversation.getNumber(),conversation.getConversation(),Messages.PRIORITY.PERSONAL.getValue());
    }
    return conversation;
  }

  public Conversation get(String number,String conversationStr) {
    String conversationKey=getAccountConversationKey(number);
    boolean exists=memCache.exists(conversationKey);
    if(exists){
      return (Conversation) memCache.hget(conversationKey,conversationStr,Conversation.class);
    }else{
      Conversation returnConversation=null;
      List<Conversation> conversationList= conversationsTable.queryByNumber(number);
      Map<String, Object> conversationMap=new HashMap<>();
      if (conversationList!=null) {
        for(Conversation conversation:conversationList){
          conversationMap.put(conversation.getConversation(),conversation);
          if(conversation.getConversation().equals(conversationStr)){
            returnConversation=conversation;
          }
        }
      }
      if(conversationMap.size()==0){
        conversationMap.put("default",new Conversation());
      }
      memCache.hmset(conversationKey,conversationMap);

      return returnConversation;
    }
  }
  public List<Conversation> get(String number,List<String> conversations) {
    String conversationKey=getAccountConversationKey(number);
    boolean exists=memCache.exists(conversationKey);
    List<Conversation> returnList=new ArrayList<>();
    if (!exists) {
      List<Conversation> conversationList= conversationsTable.queryByNumber(number);
      Map<String, Object> conversationMap=new HashMap<>();
      if (conversationList!=null) {
        for(Conversation conversation:conversationList){
          conversationMap.put(conversation.getConversation(),conversation);
          if(conversations!=null&&conversations.size()>0&&conversations.contains(conversation.getConversation())){
            returnList.add(conversation);
          }
        }
        if(conversations==null||conversations.size()==0){
          returnList=conversationList;
        }else {
          return getConversations(conversations, conversationMap, new ArrayList<>());
        }
      }
      if(conversationMap.size()==0){
        conversationMap.put("default",new Conversation());
      }
      memCache.hmset(conversationKey,conversationMap);
    }else{
      if(conversations==null||conversations.size()==0||conversations.size()>5){
        Map<String, Object> conversationMap=memCache.hgetAll(conversationKey,Conversation.class);
        List<Conversation> conversationList= new ArrayList<>();
        if (conversations!=null && conversations.size()!=0) { // 如果有指定查询的conversation
          return getConversations(conversations, conversationMap, conversationList);
        }
        if(conversationMap!=null&&conversationMap.size()>0){
          for(String key:conversationMap.keySet()){
            if(key.equals("default")){
              continue;
            }
            Conversation conversation= (Conversation) conversationMap.get(key);
            conversationList.add(conversation);
            if(conversations!=null&&conversations.size()>0&&conversations.contains(conversation.getConversation())){
              returnList.add(conversation);
            }
          }
          if(conversations==null||conversations.size()==0){
            returnList=conversationList;
          }
        }
      }else{
        for(String conversation:conversations) {
          Conversation conversationTemp= (Conversation) memCache.hget(conversationKey,conversation,Conversation.class);
          if(conversationTemp!=null){
            returnList.add(conversationTemp);
          } else{
            returnList.add(new Conversation(conversation));
          }
        }
      }
    }

    return returnList;
  }

  private List<Conversation> getConversations(List<String> conversations, Map<String, Object> conversationMap, List<Conversation> conversationList) {
    for ( String conversation : conversations) {
      final Object cfg = conversationMap.get(conversation);
      if (cfg != null) {
        conversationList.add((Conversation) cfg);
      } else {
        conversationList.add(new Conversation(conversation));
      }
    }
    return conversationList;
  }

  private String getAccountConversationKey(String number) {
    return String.join("_", Conversation.class.getSimpleName(),"Conversation", String.valueOf(Conversation.MEMCACHE_VERION), number);
  }

  private String getConversationKey(String conversation) {
    return String.join("",conversation);
  }

  public void setMemCache(MemCache memCache) {
    this.memCache = memCache;
  }

  public void sendConversationNotify(int changeType,Account operator, Conversation conversation){
    executor.submit(new Runnable() {
      @Override
      public void run() {
        try {
          ConversationNotify.Conversation notifyConversation = new ConversationNotify.Conversation(conversation.getConversation(), conversation.getMuteStatus(),
                  conversation.getBlockStatus(),conversation.getConfidentialMode(), conversation.getRemark(), conversation.getVersion());
          ConversationNotify.NodifyData nodifyData = new ConversationNotify.NodifyData(operator.getNumber(), operator.getAuthenticatedDevice().get().getId(), changeType, notifyConversation);
          ConversationNotify conversationNotify = new ConversationNotify(System.currentTimeMillis(), nodifyData);
          sendNotify(operator, conversationNotify);
        }catch (Exception e){
          logger.error(String.format("sendConversationNotify error! changeType:%d,operator:s%,conversation:%s",changeType,operator.getNumber(),new Gson().toJson(conversation)));
        }
      }
    });
  }
  private void sendNotify(Account account,ConversationNotify conversationNotify){
    if(conversationNotify!=null){
      notifyManager.sendNotify(account,conversationNotify,null);
    }
  }

  public NotifyManager getNotifyManager() {
    return notifyManager;
  }

  public void setNotifyManager(NotifyManager notifyManager) {
    this.notifyManager = notifyManager;
  }
}
