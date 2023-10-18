package org.whispersystems.textsecuregcm.storage;


import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.protobuf.ByteString;
import io.dropwizard.db.DataSourceFactory;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RAtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.distributedlock.DistributedLock;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.entities.MessageProtos.Envelope;
import org.whispersystems.textsecuregcm.util.Constants;
import org.whispersystems.textsecuregcm.util.Conversions;
import org.whispersystems.textsecuregcm.util.RedisConstants;
import org.whispersystems.textsecuregcm.util.StringUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.codahale.metrics.MetricRegistry.name;

public class MessagesManager {
  private final Logger logger = LoggerFactory.getLogger(MessagesManager.class);

  private static final MetricRegistry metricRegistry       = SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME);
  private static final Timer  deleteMeter    = metricRegistry.timer(name(MessagesManager.class, "deleteMsg"   ));
  private static final Timer  loadMsgMeter    = metricRegistry.timer(name(MessagesManager.class, "loadMsg"   ));

  private static final Meter          cacheHitByIdMeter    = metricRegistry.meter(name(MessagesManager.class, "cacheHitById"   ));
  private static final Meter          cacheMissByIdMeter   = metricRegistry.meter(name(MessagesManager.class, "cacheMissById"  ));
  private static final Meter          cacheHitByNameMeter  = metricRegistry.meter(name(MessagesManager.class, "cacheHitByName" ));
  private static final Meter          cacheMissByNameMeter = metricRegistry.meter(name(MessagesManager.class, "cacheMissByName"));
  ThreadPoolExecutor executor=new ThreadPoolExecutor(20,20,0, TimeUnit.SECONDS,new LinkedBlockingDeque<>(), new ThreadPoolExecutor.CallerRunsPolicy());

  private final Messages      messages;
  private final MessagesForSharding      messagesForSharding;
  private final PushConversationsTable   pushConversationsTable;
  private final PushMessagesTable pushMessagesTable;

  public ReadReceiptsManager getReadReceiptsManager() {
    return readReceiptsManager;
  }

  private final ReadReceiptsManager readReceiptsManager;
  private final MessagesCache messagesCache;
  private final Distribution  distribution;
  private final List<String> forbiddenMessage;
  private final AccountsManager accountsManager;
  private final MessagesV3 messagesV3;
  private final RecallMsgInfosTable recallMsgInfosTable;
  private final ConversationMsgsTable  conversationMsgsTable;
  private final GroupManagerWithTransaction groupManager;
  private final DataSourceFactory database;

  private MemCache memCache = null;

  public static final int PUSH_MSG_SIZE = 30;

  public MessagesManager(Messages messages, MessagesCache messagesCache, float cacheRate,MemCache memCache,List<String> forbiddenMessage,MessagesForSharding messagesForSharding,PushConversationsTable pushConversationsTable,PushMessagesTable pushMessagesTable,ReadReceiptsManager readReceiptsManager,AccountsManager accountsManager,MessagesV3 messagesV3,RecallMsgInfosTable recallMsgInfosTable,ConversationMsgsTable  conversationMsgsTable,GroupManagerWithTransaction groupManager,DataSourceFactory database) {
    this.messages      = messages;
    this.messagesForSharding=messagesForSharding;
    this.messagesCache = messagesCache;
    this.distribution  = new Distribution(cacheRate);
    this.memCache=memCache;
    this.pushConversationsTable=pushConversationsTable;
    this.pushMessagesTable=pushMessagesTable;
    this.forbiddenMessage=forbiddenMessage;
    this.readReceiptsManager=readReceiptsManager;
    this.accountsManager=accountsManager;
    this.messagesV3=messagesV3;
    this.recallMsgInfosTable=recallMsgInfosTable;
    this.conversationMsgsTable=conversationMsgsTable;
    this.groupManager=groupManager;
    this.database=database;
  }

  public int insert(String destination, long destinationDevice, Envelope message,boolean notify,boolean readReceipt,long shardMsgId,String gid,String realDestination,IncomingMessage.RealSource realSource) {
    // if (distribution.isQualified(destination, destinationDevice)) {
    //   messagesCache.insert(destination, destinationDevice, message);
    // } else {
//    boolean isSharding=this.isSharding(message);
    String conversation=this.getConversation(realDestination,message.getSource(),gid);
    int notifyMsgType=getNotifyMsgType(message);
    long msgId=messages.store(message, destination, destinationDevice, notify,getPriority(message.getSource(),destination,gid,readReceipt),conversation,notifyMsgType,
            message.hasSourceIdentityKey()?message.getSourceIdentityKey():null,
            message.hasPeerContext()? message.getPeerContext():null);
//    if(isSharding){
//      insertPush(destination,destinationDevice,message,this.getConversation(destination,message.getSource(),gid),msgId);
//    }

    insertPushOther(destination, destinationDevice, message, conversation, msgId,notify,realSource,shardMsgId);
    return 1;
    // }
  }

  private int getNotifyMsgType(Envelope message){
    if(message!=null&&message.hasMsgType()&&message.getMsgType().equals(Envelope.MsgType.MSG_NOTIFY)){
      if(message.hasContent()&&message.getContent().size()>0){
        try {
          String notifyMsg = new String(message.getContent().toByteArray());
          JsonObject notifyObj = new Gson().fromJson(notifyMsg, JsonObject.class);
          if(notifyObj!=null&&notifyObj.has("notifyType")){
            return notifyObj.get("notifyType").getAsInt();
          }
        }catch (Exception e){
          logger.error("MessagesManager getNotifyMsgType error! {}",e.getMessage());
        }
      }
    }
    return -1;
  }
  public long insertForSharding(Envelope message,boolean notify,String conversation) {
    long msgId=messagesForSharding.store(message,notify,conversation,getPushType(message).ordinal(),getNotifyMsgType(message));
    executor.submit(new Runnable() {
      @Override
      public void run() {
        insertOrUpdateConversationMsg(message,conversation,notify,msgId);
      }
    });
    return msgId;
  }

  public void insertOrUpdateConversationMsg(Envelope message,String conversation,boolean notify,long msgId){
    String conversationMsgKey=getConversationMsgKey(conversation);
    ConversationMsg conversationMsg= (ConversationMsg) memCache.get(conversationMsgKey,ConversationMsg.class);
    if(conversationMsg!=null){
      updateConversationMsg(conversationMsgKey,conversationMsg,message,notify,msgId);
    }else{
      conversationMsg=conversationMsgsTable.getByConversation(conversation);
      if(conversationMsg==null){
        Lock lock=DistributedLock.getLocker(new String[]{conversation+"msgInsert"});
        try {
          lock.lock();
          conversationMsg=getConversationMsg(conversation);
          if(conversationMsg==null){
            long lastVisibleMsgId=0;
            long lastVisibleMsgSequenceId=0;
            long lastVisibleMsgNotifySequenceId=0;
            long lastVisibleMsgServerTime=0;
            if(notify){
              lastVisibleMsgId=msgId;
              lastVisibleMsgSequenceId=message.getSequenceId();
              lastVisibleMsgNotifySequenceId=message.getNotifySequenceId();
              lastVisibleMsgServerTime=message.getSystemShowTimestamp();
            }
            conversationMsg = new ConversationMsg(conversation, msgId, lastVisibleMsgId, lastVisibleMsgSequenceId, lastVisibleMsgNotifySequenceId,message.getSystemShowTimestamp(),lastVisibleMsgServerTime, getPushType(message).ordinal(),0,message.getSequenceId());
            long id=conversationMsgsTable.insert(conversationMsg);
            conversationMsg.setId(id);
            memCache.set(conversationMsgKey, conversationMsg);
            return ;
          }
        }finally {
          lock.unlock();
        }
      }
      updateConversationMsg(conversationMsgKey,conversationMsg,message,notify,msgId);
    }
  }

  private void updateConversationMsg(String conversationMsgKey,ConversationMsg conversationMsg,Envelope message,boolean notify,long msgId){
    conversationMsg.setLastMsgId(msgId);
    conversationMsg.setLastMsgSequenceId(message.getSequenceId());
    conversationMsg.setLastMsgServerTime(message.getSystemShowTimestamp());
    if(notify){
      conversationMsg.setLastVisibleMsgId(msgId);
      conversationMsg.setLastVisibleMsgSequenceId(message.getSequenceId());
      conversationMsg.setLastVisibleMsgNotifySequenceId(message.getNotifySequenceId());
      conversationMsg.setLastVisibleMsgServerTime(message.getSystemShowTimestamp());
    }
    conversationMsgsTable.update(conversationMsg);
    memCache.set(conversationMsgKey, conversationMsg);
  }

  private ConversationMsg getConversationMsg(String conversation){
    String conversationMsgKey=getConversationMsgKey(conversation);
    ConversationMsg conversationMsg= (ConversationMsg) memCache.get(conversationMsgKey,ConversationMsg.class);
    if(conversationMsg!=null){
      return conversationMsg;
    }else{
      conversationMsg=conversationMsgsTable.getByConversation(conversation);
      if(conversationMsg!=null) {
        memCache.set(conversationMsgKey,conversationMsg);
        return conversationMsg;
      }
    }
    return null;
  }
  public void insertPushForSource(Envelope message,String conversation,boolean notify,long shardMsgId){
    PushConversationsTable.PUSH_TYPE pushType=getPushType(message);
    Optional<Account> accountOptional=accountsManager.get(message.getSource());
    if(accountOptional.isPresent()&&!accountOptional.get().isinValid()) {
      for(Device device:accountOptional.get().getDevices()) {
        if (accountsManager.isActiveDevice(device,accountOptional.get())) {
          this.insertPushConversationForSource(accountOptional.get().getNumber(), device.getId(), conversation, pushType, message.getNotifySequenceId(),notify,message.getSourceDevice(),message.getSequenceId(),shardMsgId,message.getSystemShowTimestamp());
        }
      }
    }
  }

  private PushConversation insertPushConversationForSource(String destination, long destinationDevice,String conversation,PushConversationsTable.PUSH_TYPE pushType,long lastSelfNotifySequenceId,boolean notify,long messageSourceDevice,long sequenceId,long shardMsgId,long systemShowTimestamp){
    String pushConversationKey=getPushConversationKey(destination,destinationDevice);
    PushConversation pushConversation= (PushConversation) memCache.hget(pushConversationKey,conversation,PushConversation.class);
    if(pushConversation!=null){
        return updatePushConversationForSource(pushConversationKey,pushConversation,lastSelfNotifySequenceId,notify,messageSourceDevice,sequenceId,shardMsgId,systemShowTimestamp);
    }else{
      pushConversation=pushConversationsTable.getByDestinationAndConversation(destination,destinationDevice,conversation);
      if(pushConversation==null){
        Lock lock=DistributedLock.getLocker(new String[]{destination+destinationDevice+conversation+"insert"});
        try {
          lock.lock();
          pushConversation=getPushConversation(destination,destinationDevice,conversation);
//          pushConversation=pushConversationsTable.getByDestinationAndConversation(destination,destinationDevice,conversation);
          if(pushConversation==null){
            ReadReceipt readReceipt=readReceiptsManager.getMaxReadReceipt(destination,conversation);
            long readNotifySequenceId=0;
            long lastVisibleMsgSequenceId=0;
            long lastVisibleMsgNotifySequenceId=0;
            if(readReceipt!=null){
              readNotifySequenceId=readReceipt.getMaxNotifySequenceId();
            }
            if(!notify){
              lastSelfNotifySequenceId=0;
            }else {
              lastVisibleMsgSequenceId=sequenceId;
              lastVisibleMsgNotifySequenceId=lastSelfNotifySequenceId;
            }
            String id = UUID.randomUUID().toString().replace("-", "");
            boolean isSend=false;
            if(destinationDevice!=messageSourceDevice){
              isSend=true;
            }
            pushConversation = new PushConversation(id,conversation, destination, destinationDevice, pushType.ordinal(), 0,readNotifySequenceId,lastSelfNotifySequenceId,isSend,-1,-1,lastVisibleMsgSequenceId,lastVisibleMsgNotifySequenceId,shardMsgId,shardMsgId==-1?0:systemShowTimestamp);
            pushConversationsTable.insert(pushConversation);
            memCache.hset(pushConversationKey, conversation, pushConversation);
            return pushConversation;
          }
        }finally {
          lock.unlock();
        }
      }
      return updatePushConversationForSource(pushConversationKey,pushConversation,lastSelfNotifySequenceId,notify,messageSourceDevice,sequenceId,shardMsgId,systemShowTimestamp);
    }
  }

  private PushConversation updatePushConversationForSource(String pushConversationKey,PushConversation pushConversation,long lastSelfNotifySequenceId,boolean notify,long messageSourceDevice,long sequenceId,long shardMsgId,long systemShowTimestamp ){
    if(lastSelfNotifySequenceId<=pushConversation.getLastSelfNotifySequenceId()||!notify) {
      if(shardMsgId!=-1){
        pushConversation.setLastShardMsgId(shardMsgId);
        pushConversation.setLastShardMsgServerTime(systemShowTimestamp);
        pushConversationsTable.updateForSource(pushConversation);
        memCache.hset(pushConversationKey, pushConversation.getConversation(), pushConversation);
      }
      return pushConversation;
    }
//    Lock lock=DistributedLock.getLocker(new String[]{pushConversationKey+pushConversation.getConversation()+"update"});
//    try {
//      lock.lock();
//      pushConversation=getPushConversation(pushConversation.getDestination(),pushConversation.getDestinationDevice(),pushConversation.getConversation());
      pushConversation.setLastSelfNotifySequenceId(lastSelfNotifySequenceId);
      pushConversation.setLastVisibleMsgSequenceId(sequenceId);
      pushConversation.setLastVisibleMsgNotifySequenceId(lastSelfNotifySequenceId);
      if(pushConversation.getDestinationDevice()==messageSourceDevice) {
        pushConversation.setSend(false);
      }
      if(shardMsgId!=-1){
        pushConversation.setLastShardMsgId(shardMsgId);
        pushConversation.setLastShardMsgServerTime(systemShowTimestamp);
      }
      pushConversationsTable.updateForSource(pushConversation);
      memCache.hset(pushConversationKey, pushConversation.getConversation(), pushConversation);
//    }finally {
//      lock.unlock();
//    }
    return pushConversation;
  }

  public void updatePushConversationForRead(String destination,String conversation,long readNotifySequenceId,long sourceDeviceId){
    Optional<Account> accountOptional=accountsManager.get(destination);
    if(accountOptional.isPresent()&&!accountOptional.get().isinValid()) {
      for (Device device : accountOptional.get().getDevices()) {
        if (accountsManager.isActiveDevice(device, accountOptional.get())) {
          String pushConversationKey = getPushConversationKey(destination, device.getId());
//          Lock lock=DistributedLock.getLocker(new String[]{pushConversationKey+conversation+"update"});
//          try {
//            lock.lock();
            PushConversation pushConversation=getPushConversation(destination,device.getId(),conversation);
            if (pushConversation != null && pushConversation.getMaxReadNotifySequenceId() < readNotifySequenceId) {
              pushConversation.setMaxReadNotifySequenceId(readNotifySequenceId);
              if(readNotifySequenceId==pushConversation.getLastVisibleMsgNotifySequenceId()&&device.getId()==sourceDeviceId){
                pushConversation.setSend(false);
              }
              pushConversationsTable.updateForRead(pushConversation);
              memCache.hset(pushConversationKey, pushConversation.getConversation(), pushConversation);
            }
//          }finally {
//            lock.unlock();
//          }
        }
      }
    }
  }

  public void updatePushConversationForSend(String destination,long destinationDevice,String conversation){
    String pushConversationKey = getPushConversationKey(destination, destinationDevice);
//    Lock lock=DistributedLock.getLocker(new String[]{pushConversationKey+conversation+"update"});
//    try {
//      lock.lock();
      PushConversation pushConversation=getPushConversation(destination,destinationDevice,conversation);
      if (pushConversation != null) {
        pushConversation.setSend(false);
        pushConversationsTable.updateForSend(pushConversation);
        memCache.hset(pushConversationKey, pushConversation.getConversation(), pushConversation);
      }
//    }finally {
//      lock.unlock();
//    }
  }

  public void insertPushOther(String destination, long destinationDevice, Envelope message,String conversation,long msgId,boolean notify,IncomingMessage.RealSource realSource,long shardMsgId) {
    PushConversationsTable.PUSH_TYPE pushType=getPushType(message);
    PushConversation pushConversation=this.insertPushConversationForOther(destination,destinationDevice,conversation,pushType,message,msgId,notify,realSource,shardMsgId);
//    if(pushConversation!=null) {
//      String pushMsgKey=getPushMsgKey(destination,destinationDevice,pushConversation.getId());
//      if(pushType.equals(PushConversationsTable.PUSH_TYPE.ALL)) {
//        pushMessagesTable.insert(new PushMessage(pushConversation.getId(),msgId,pushConversation.getPushType(),pushConversation.getPriority(),message.getSequenceId()));
//        memCache.zAdd(pushMsgKey, message.getSequenceId(), msgId+"");
//      }else{
//        memCache.zAdd(pushMsgKey,PUSH_MSG_SIZE ,message.getSequenceId(), msgId+"");
//      }
//    }
  }

  private PushConversation insertPushConversationForOther(String destination, long destinationDevice,String conversation,PushConversationsTable.PUSH_TYPE pushType,Envelope message,long msgId,boolean notify,IncomingMessage.RealSource realSource,long shardMsgId){
    String pushConversationKey=getPushConversationKey(destination,destinationDevice);
    PushConversation pushConversation= (PushConversation) memCache.hget(pushConversationKey,conversation,PushConversation.class);
    if(pushConversation!=null){
      return updatePushConversationForOther(pushConversationKey,pushConversation,message,msgId,notify,destination.equals(message.getSource()),realSource,shardMsgId);
    }else{
      pushConversation=pushConversationsTable.getByDestinationAndConversation(destination,destinationDevice,conversation);
      if(pushConversation==null){
       return insertPushConversationForOtherWithLock(destination,destinationDevice,conversation,pushType,message,msgId,notify,realSource,shardMsgId);
      }
      return updatePushConversationForOther(pushConversationKey,pushConversation,message,msgId,notify,destination.equals(message.getSource()),realSource,shardMsgId);
    }
  }

  private PushConversation insertPushConversationForOtherWithLock(String destination, long destinationDevice,String conversation,PushConversationsTable.PUSH_TYPE pushType,Envelope message,long msgId,boolean notify,IncomingMessage.RealSource realSource,long shardMsgId){
    Lock lock=DistributedLock.getLocker(new String[]{destination+destinationDevice+conversation+"insert"});
    try {
      lock.lock();
      String pushConversationKey=getPushConversationKey(destination,destinationDevice);
      PushConversation pushConversation=pushConversationsTable.getByDestinationAndConversation(destination,destinationDevice,conversation);
      if(pushConversation==null) {
        ReadReceipt readReceipt = readReceiptsManager.getMaxReadReceipt(destination, conversation);
        long readNotifySequenceId = 0;
        if (readReceipt != null) {
          readNotifySequenceId = readReceipt.getMaxNotifySequenceId();
        }
        String id = UUID.randomUUID().toString().replace("-", "");
        if (notify) {
          boolean isSend=true;
          if(("server").equals(message.getSource())) {
            isSend=false;
          }
          pushConversation = new PushConversation(id, conversation, destination, destinationDevice, pushType.ordinal(), 0, readNotifySequenceId, 0, isSend, msgId, msgId, message.getSequenceId(), message.getNotifySequenceId(),shardMsgId,shardMsgId==-1?0:message.getSystemShowTimestamp());
        } else {
          pushConversation = new PushConversation(id, conversation, destination, destinationDevice, pushType.ordinal(), 0, readNotifySequenceId, 0, false, msgId, -1, 0, 0,shardMsgId,shardMsgId==-1?0:message.getSystemShowTimestamp());
        }
        pushConversationsTable.insert(pushConversation);
        memCache.hset(pushConversationKey, conversation, pushConversation);
        return pushConversation;
      }else{
        return updatePushConversationForOther(pushConversationKey,pushConversation,message,msgId,notify,destination.equals(message.getSource()),realSource,shardMsgId);
      }
    }finally {
      lock.unlock();
    }
  }


  private PushConversation updatePushConversationForOther(String pushConversationKey,PushConversation pushConversation,Envelope message,long msgId,boolean notify,boolean isSelf,IncomingMessage.RealSource realSource,long shardMsgId){
//    Lock lock=DistributedLock.getLocker(new String[]{pushConversationKey+pushConversation.getConversation()+"update"});
//    try {
//      lock.lock();
//      pushConversation = getPushConversation(pushConversation.getDestination(), pushConversation.getDestinationDevice(), pushConversation.getConversation());
      pushConversation.setLastMsgId(msgId==-1?pushConversation.getLastShardMsgId():msgId);
      if(shardMsgId!=-1){
        pushConversation.setLastShardMsgId(shardMsgId);
        pushConversation.setLastShardMsgServerTime(message.getSystemShowTimestamp());
      }
      if (notify) {
        pushConversation.setLastVisibleMsgId(msgId==-1?pushConversation.getLastShardMsgId():msgId);
        pushConversation.setLastVisibleMsgSequenceId(message.getSequenceId());
        pushConversation.setLastVisibleMsgNotifySequenceId(message.getNotifySequenceId());
        if(("server").equals(message.getSource())) {
          pushConversation.setSend(false);
        }else{
          pushConversation.setSend(true);
        }
        pushConversationsTable.updateForOther(pushConversation);
      }else {
        if(message.getMsgType()==Envelope.MsgType.MSG_SYNC_NORMAL&&realSource!=null){
          pushConversation.setLastVisibleMsgId(msgId==-1?pushConversation.getLastShardMsgId():msgId);
          pushConversation.setLastVisibleMsgSequenceId(realSource.getSequenceId());
          pushConversation.setLastVisibleMsgNotifySequenceId(realSource.getNotifySequenceId());
          pushConversation.setSend(true);
          pushConversationsTable.updateForOther(pushConversation);
        }else {
          pushConversationsTable.updateForOtherForSilent(pushConversation);
        }
      }
      memCache.hset(pushConversationKey, pushConversation.getConversation(), pushConversation);
//    }finally {
//      lock.unlock();
//    }
    return pushConversation;
  }

  private PushConversation getPushConversation(String destination, long destinationDevice,String conversation){
    String pushConversationKey=getPushConversationKey(destination,destinationDevice);
    PushConversation pushConversation= (PushConversation) memCache.hget(pushConversationKey,conversation,PushConversation.class);
    if(pushConversation!=null){
      return pushConversation;
    }else{
      pushConversation=pushConversationsTable.getByDestinationAndConversation(destination,destinationDevice,conversation);
      if(pushConversation!=null) {
        memCache.hset(pushConversationKey, conversation, pushConversation);
        return pushConversation;
      }
    }
    return null;
  }

  private PushConversationsTable.PUSH_TYPE getPushType(Envelope message){
    if(message.getType().equals(Envelope.Type.NOTIFY)){
      return PushConversationsTable.PUSH_TYPE.ALL;
    }
    return PushConversationsTable.PUSH_TYPE.LATEST;
  }
  public boolean isSharding(Envelope message,IncomingMessage.Conversation conversation){
    if(message.getMsgType().equals(Envelope.MsgType.MSG_NOTIFY)||message.getMsgType().equals(Envelope.MsgType.MSG_NORMAL)||message.getMsgType().equals(Envelope.MsgType.MSG_RECALL)){
      return true;
    }
    if(message.getMsgType().equals(Envelope.MsgType.MSG_SYNC)||message.getMsgType().equals(Envelope.MsgType.MSG_SYNC_NORMAL)){
      if(conversation!=null&&message.getSource().equals(conversation.getNumber())){
        return true;
      }
    }
    return false;
  }

  public boolean isSharding(IncomingMessage message,String source){
    if(message.getMsgType()==Envelope.MsgType.MSG_NOTIFY_VALUE||message.getMsgType()==Envelope.MsgType.MSG_NORMAL_VALUE||message.getMsgType()==Envelope.MsgType.MSG_RECALL_VALUE){
      return true;
    }
    if(message.getMsgType()==Envelope.MsgType.MSG_SYNC_VALUE||message.getMsgType()==Envelope.MsgType.MSG_SYNC_NORMAL_VALUE){
      if(message.getConversation()!=null&& source.equals(message.getConversation().getNumber())){
        return true;
      }
    }
    return false;
  }

  public void insertForReceiveType(String destination, long destinationDevice, Envelope message,boolean notify,int receiveType) {
    messages.storeForReceiveType(message, destination, destinationDevice,notify,receiveType);
  }

  public void insertBatch(List<String> destination, List<Long> destinationDevice, List<Envelope> message,List<Boolean> notify,List<Integer> prioritys,List<String> conversations,long msgId,String conversation) {
//      if(this.isSharding(message.get(0))){
//        this.insertPushForBatch(destination,destinationDevice,message.get(0),conversation,msgId);
//      }
    List<Integer> notifyMsgTypes=new ArrayList<>();
    if(message!=null&&message.size()>0) {
      int notifyMsgType = getNotifyMsgType(message.get(0));
      for (Envelope envelope : message) {
        notifyMsgTypes.add(notifyMsgType);
      }
    }

      long[] msgIds=messagesV3.storeBatch(message, destination, destinationDevice,notify,prioritys,conversations,notifyMsgTypes);
      this.insertPushForBatch(destination,destinationDevice,message.get(0),conversation,msgIds,notify.get(0),msgId);
  }

  public void insertPushForBatch(List<String> destination, List<Long> destinationDevice, Envelope message,String conversation,long[] msgIds,boolean notify,long shardMsgId) {
    PushConversationsTable.PUSH_TYPE pushType=getPushType(message);
    List<PushConversation> pushConversations=insertPushConversationForBatchWithLock(destination,destinationDevice,conversation,pushType,msgIds,message,notify,shardMsgId);
//    List<PushMessage> pushMessages=new ArrayList<>();
//    for(int i=0;i<destination.size();i++) {
//      PushConversation pushConversation =pushConversations.get(i);
//      if (pushConversation != null) {
//        String pushMsgKey = getPushMsgKey(destination.get(i), destinationDevice.get(i), pushConversation.getId());
//        pushMessages.add(new PushMessage(pushConversation.getId(), msgId, pushConversation.getPushType(), pushConversation.getPriority(),message.getSequenceId()));
//        if (pushType.equals(PushConversationsTable.PUSH_TYPE.ALL)) {
//          memCache.zAdd(pushMsgKey, message.getSequenceId(), msgId + "");
//        } else {
////          memCache.zAdd(pushMsgKey, PUSH_MSG_SIZE, message.getSequenceId(), msgId + "");
//        }
//      }
//    }
//    if (pushType.equals(PushConversationsTable.PUSH_TYPE.ALL)) {
//      pushMessagesTable.insertBatch(pushMessages);
//    }
  }

  private List<PushConversation> insertPushConversationForBatchWithLock(List<String> destination, List<Long> destinationDevice,String conversation,PushConversationsTable.PUSH_TYPE pushType,long[] msgIds,Envelope message,boolean notify,long shardMsgId){
    List<PushConversation> pushConversations=new ArrayList<>();
    for(int i=0;i<destination.size();i++) {
      String pushConversationKey = getPushConversationKey(destination.get(i), destinationDevice.get(i));
      PushConversation pushConversation = (PushConversation) memCache.hget(pushConversationKey, conversation, PushConversation.class);
      if (pushConversation != null) {
        pushConversations.add(updatePushConversationForOther(pushConversationKey,pushConversation,message,msgIds==null?-1:msgIds[i],notify,false,null,shardMsgId));
      } else {
        pushConversation = pushConversationsTable.getByDestinationAndConversation(destination.get(i), destinationDevice.get(i), conversation);
        if (pushConversation == null) {
          pushConversations.add(insertPushConversationForOtherWithLock(destination.get(i),destinationDevice.get(i),conversation,pushType,message,msgIds==null?-1:msgIds[i],notify,null,shardMsgId));
        }else {
          pushConversations.add(updatePushConversationForOther(pushConversationKey,pushConversation,message,msgIds==null?-1:msgIds[i],notify,false,null,shardMsgId));
        }
      }
    }
    return pushConversations;
  }

  private List<PushConversation> insertPushConversationForBatch(List<String> destination, List<Long> destinationDevice,String conversation,PushConversationsTable.PUSH_TYPE pushType,long[] msgIds,Envelope message,boolean notify,long shardMsgId){
    List<PushConversation> pushConversations=new ArrayList<>();
    List<PushConversation> pushConversationsToInsert=new ArrayList<>();
    List<PushConversation> pushConversationsToUpdate=new ArrayList<>();
    for(int i=0;i<destination.size();i++) {
      String pushConversationKey = getPushConversationKey(destination.get(i), destinationDevice.get(i));
      PushConversation pushConversation = (PushConversation) memCache.hget(pushConversationKey, conversation, PushConversation.class);
      if (pushConversation != null) {
        pushConversation.setLastMsgId(msgIds[i]);
        if(notify) {
          pushConversation.setLastVisibleMsgId(msgIds[i]);
          pushConversation.setLastVisibleMsgSequenceId(message.getSequenceId());
          pushConversation.setLastVisibleMsgNotifySequenceId(message.getNotifySequenceId());
        }
        pushConversation.setSend(true);
        pushConversationsToUpdate.add(pushConversation);
        pushConversations.add(pushConversation);
        memCache.hset(pushConversationKey, conversation, pushConversation);
      } else {
        pushConversation = pushConversationsTable.getByDestinationAndConversation(destination.get(i), destinationDevice.get(i), conversation);

        if (pushConversation == null) {
          long readNotifySequenceId=0;
          ReadReceipt readReceipt=readReceiptsManager.getMaxReadReceipt(destination.get(i),conversation);
          if(readReceipt!=null){
            readNotifySequenceId=readReceipt.getMaxNotifySequenceId();
          }
          String id = UUID.randomUUID().toString().replace("-", "");
          if(notify) {
            pushConversation = new PushConversation(id,conversation, destination.get(i), destinationDevice.get(i), pushType.ordinal(), 0,readNotifySequenceId,0,true,msgIds[i],msgIds[i],message.getSequenceId(),message.getNotifySequenceId(),shardMsgId,shardMsgId==-1?0:message.getSystemShowTimestamp());
          }else{
            pushConversation = new PushConversation(id, conversation, destination.get(i), destinationDevice.get(i), pushType.ordinal(), 0, readNotifySequenceId, 0, true,msgIds[i],-1,0,0,shardMsgId,shardMsgId==-1?0:message.getSystemShowTimestamp());

          }
          pushConversationsToInsert.add(pushConversation);
          pushConversations.add(pushConversation);
        }else {
          pushConversation.setSend(true);
          pushConversation.setLastMsgId(msgIds[i]);
          if(notify) {
            pushConversation.setLastVisibleMsgId(msgIds[i]);
            pushConversation.setLastVisibleMsgSequenceId(message.getSequenceId());
            pushConversation.setLastVisibleMsgNotifySequenceId(message.getNotifySequenceId());
          }
          pushConversationsToUpdate.add(pushConversation);
          pushConversations.add(pushConversation);
        }
        memCache.hset(pushConversationKey, conversation, pushConversation);
      }
    }
    if(pushConversationsToInsert.size()>0){
      pushConversationsTable.insertBatch(pushConversationsToInsert);
    }
    if(pushConversationsToUpdate.size()>0){
      pushConversationsTable.updateBatch(pushConversationsToUpdate);
    }
    return pushConversations;
  }

  public OutgoingMessageEntityList getMessagesForDevice(String destination, long destinationDevice) {
    Timer.Context context=loadMsgMeter.time();
    try {
      List<OutgoingMessageEntity> messages = this.messages.load(destination, destinationDevice);
      if (messages.size() <= Messages.RESULT_SET_CHUNK_SIZE) {
        messages.addAll(this.messagesCache.get(destination, destinationDevice, Messages.RESULT_SET_CHUNK_SIZE - messages.size()));
      }
      return new OutgoingMessageEntityList(messages, messages.size() >= Messages.RESULT_SET_CHUNK_SIZE);
    }finally {
      context.stop();
    }
  }

  public OutgoingMessageEntityList getMessagesForDeviceByConversation(String destination, long destinationDevice,String conversation) {
    Timer.Context context=loadMsgMeter.time();
    try {
      List<OutgoingMessageEntity> messages = this.messages.loadByConversation(destination, destinationDevice,conversation);
      if (messages.size() <= Messages.RESULT_SET_CHUNK_SIZE) {
        messages.addAll(this.messagesCache.get(destination, destinationDevice, Messages.RESULT_SET_CHUNK_SIZE - messages.size()));
      }
      return new OutgoingMessageEntityList(messages, messages.size() >= Messages.RESULT_SET_CHUNK_SIZE);
    }finally {
      context.stop();
    }
  }

  public OutgoingMessageEntityList getMessagesForKafka() {
    List<OutgoingMessageEntity> messages = this.messages.loadForKafKa();

    return new OutgoingMessageEntityList(messages, messages.size() >= Messages.RESULT_SET_CHUNK_SIZE);
  }

  public OutgoingMessageEntityListForRemind getMessagesForTimeRange(long begin, long end , int offset) {
    List<OutgoingMessageEntityForRemind> messages = this.messages.loadByTimeRange(begin,end,offset);
    return new OutgoingMessageEntityListForRemind(messages, messages.size() >= Messages.RESULT_SET_CHUNK_SIZE);
  }

  public long notifyMerge(int notifyCountThreshold){
    return messages.removeForNotifyMerge(notifyCountThreshold);
  }
  public List<OutgoingMessageEntityForSharding> getMessagesByIds(String conversation, List<Long> ids) {
    Timer.Context context=loadMsgMeter.time();
    try {
      List<OutgoingMessageEntityForSharding> messages = this.messagesForSharding.loadByIds(conversation, ids);
      return messages;
    }finally {
      context.stop();
    }
  }

  public List<OutgoingMessageEntityForSharding> getMessages(String conversation,long beginTime) {
    Timer.Context context=loadMsgMeter.time();
    try {
      List<OutgoingMessageEntityForSharding> messages = this.messagesForSharding.load(conversation,beginTime);
      return messages;
    }finally {
      context.stop();
    }
  }

  public List<OutgoingMessageEntityForSharding> getMessagesBySequenceIds(String conversation,List<Long> sequenceIds,long beginTime) {
    Timer.Context context=loadMsgMeter.time();
    try {
      List<OutgoingMessageEntityForSharding> messages = this.messagesForSharding.loadBySequenceIds(conversation, sequenceIds,beginTime);
      return messages;
    }finally {
      context.stop();
    }
  }

  public List<OutgoingMessageEntityForSharding> getMessagesBySequenceRange(String conversation, Long minSequenceId,Long maxSequenceId,long beginTime) {
    Timer.Context context=loadMsgMeter.time();
    try {
      List<OutgoingMessageEntityForSharding> messages = this.messagesForSharding.loadBySequenceRange(conversation, minSequenceId, maxSequenceId,beginTime);
      return messages;
    }finally {
      context.stop();
    }
  }

  public List<OutgoingMessageEntityForSharding> getMessagesByMaxSequenceId(String conversation,Long maxSequenceId,long beginTime) {
    Timer.Context context=loadMsgMeter.time();
    try {
      List<OutgoingMessageEntityForSharding> messages = this.messagesForSharding.loadByMaxSequenceId(conversation, maxSequenceId,beginTime);
      return messages;
    }finally {
      context.stop();
    }
  }

  public List<OutgoingMessageEntityForSharding> getMessagesByMinSequenceId(String conversation,Long minSequenceId,long beginTime) {
    Timer.Context context=loadMsgMeter.time();
    try {
      List<OutgoingMessageEntityForSharding> messages = this.messagesForSharding.loadByMinSequenceId(conversation, minSequenceId,beginTime);
      return messages;
    }finally {
      context.stop();
    }
  }
  public long getMsgSurplus(Account account,String conversation) {
    PushConversation pushConversation=this.getPushConversation(account.getNumber(),account.getAuthenticatedDevice().get().getId(),conversation);
    if(pushConversation!=null){
      return pushMessagesTable.getCountByConversation(pushConversation.getId());
    }
    return 0;
  }

  public long deletePushMessages(Account account,String conversation,List<OutgoingMessageEntityForSharding> messages) {
    PushConversation pushConversation=this.getPushConversation(account.getNumber(),account.getAuthenticatedDevice().get().getId(),conversation);
    if(pushConversation!=null&&messages.size()>0){
      List<Long> msgIds=new ArrayList<>();
      for(OutgoingMessageEntityForSharding outgoingMessageEntityForSharding:messages){
        msgIds.add(outgoingMessageEntityForSharding.getId());
      }
      long surplus=pushMessagesTable.getCountByConversationForLoad(pushConversation.getId(),msgIds);
      executor.submit(new Runnable() {
        @Override
        public void run() {
          pushMessagesTable.deleteByPushConversationIdAndMsgIds(pushConversation.getId(),msgIds);
        }
      });
      return surplus;
    }
    if(pushConversation!=null){
     return pushMessagesTable.getCountByConversation(pushConversation.getId());
    }
    return 0;
  }

  public List<ConversationMsgInfos.ConversationMsgInfo> getConversationMsg(Account account) {
    long begin=System.currentTimeMillis();
    long created=account.getAuthenticatedDevice().get().getCreated();
    long startTime=created- TimeUnit.DAYS.toMillis(14);
    String link="dbname=accountdb user=:dbUser password=:dbPwd options=-csearch_path=";
    link=link.replace(":dbUser",database.getUser());
    link=link.replace(":dbPwd",database.getPassword());
    List<ConversationMsg> conversationMsgList =conversationMsgsTable.getByNumberForHot(account.getNumber(),startTime,link);
    logger.info("uid:{},did:{} getConversationMsg messagesManager.getConversationMsg getByNumberForHot conversationMsgList.size{} cost:{}",account.getNumber(),account.getAuthenticatedDevice().get().getId(),conversationMsgList.size(),System.currentTimeMillis()-begin);
    List<ConversationMsgInfos.ConversationMsgInfo> conversationPreviewList=new ArrayList<>();
    if(conversationMsgList!=null&&conversationMsgList.size()>0){
      for(ConversationMsg conversationMsg:conversationMsgList) {
        begin=System.currentTimeMillis();
        List<OutgoingMessageEntityForSharding> messageEntityForShardings= loadOldestAndLastestMsg(account,conversationMsg,startTime);
        logger.info("uid:{},did:{} getConversationMsg messagesManager.getConversationMsg conversation:{} loadOldestAndLastestMsg cost:{}",account.getNumber(),account.getAuthenticatedDevice().get().getId(),conversationMsg.getConversation(),System.currentTimeMillis()-begin);
        begin=System.currentTimeMillis();
        if(messageEntityForShardings==null||messageEntityForShardings.size()==0) continue;
        OutgoingMessageEntityForSharding oldestMsg= messageEntityForShardings.get(0);
        OutgoingMessageEntityForSharding lastestMsg= messageEntityForShardings.size()>1?messageEntityForShardings.get(1):oldestMsg;
        PushConversation pushConversation=getPushConversation(account.getNumber(),account.getAuthenticatedDevice().get().getId(),conversationMsg.getConversation());
        if(pushConversation==null) continue;
        OutgoingMessageEntityForSharding outgoingMessageEntityForSharding=messagesForSharding.loadById(conversationMsg.getConversation(),conversationMsg.getLastVisibleMsgId());
        logger.info("uid:{},did:{} getConversationMsg messagesManager.getConversationMsg conversation:{} msgId:{} loadById cost:{}",account.getNumber(),account.getAuthenticatedDevice().get().getId(),conversationMsg.getConversation(),conversationMsg.getLastVisibleMsgId(),System.currentTimeMillis()-begin);
        begin=System.currentTimeMillis();
        Envelope message=messageToEnvelope(outgoingMessageEntityForSharding);
        ConversationMsgInfos.ConversationMsgInfo.Builder builder=ConversationMsgInfos.ConversationMsgInfo.newBuilder();
        builder.setConversationId(getConversatinId(pushConversation));
        if(message!=null) {
          builder.setLastestMsg(message);
        }
        builder.setReadPosition(getReadPosition(pushConversation));
        logger.info("uid:{},did:{} getConversationMsg messagesManager.getConversationMsg conversation:{} getReadPosition cost:{}",account.getNumber(),account.getAuthenticatedDevice().get().getId(),conversationMsg.getConversation(),System.currentTimeMillis()-begin);
        begin=System.currentTimeMillis();
        builder.setMaxOutgoingNsId(pushConversation.getLastSelfNotifySequenceId());
        builder.setUnreadCorrection(getCorrectionValue(pushConversation.getConversation(),builder.getReadPosition().getMaxNotifySequenceId(),builder.getLastestMsg().getNotifySequenceId()));
        logger.info("uid:{},did:{} getConversationMsg messagesManager.getConversationMsg conversation:{} getCorrectionValue cost:{}",account.getNumber(),account.getAuthenticatedDevice().get().getId(),conversationMsg.getConversation(),System.currentTimeMillis()-begin);
        builder.setLastestMsgSId(lastestMsg.getSequenceId());
        builder.setOldestMsgSId(oldestMsg.getSequenceId());
        builder.setOldestMsgNsId(oldestMsg.getNotifySequenceId());
        conversationPreviewList.add(builder.build());
      }
    }
    return conversationPreviewList;
  }

  public List<OutgoingMessageEntityForSharding> loadOldestAndLastestMsg(Account account,ConversationMsg conversationMsg,long startTime){
    long begin=System.currentTimeMillis();
    boolean isGroup=isGroup(conversationMsg.getConversation());
    if(isGroup){
      GroupMember groupMember=groupManager.getMember(conversationMsg.getConversation(),account.getNumber());
      if (groupMember==null){
        return null;
      }
      startTime=Math.max(startTime,groupMember.getCreate_time());
    }
    logger.info("uid:{},did:{} getConversationMsg messagesManager.getConversationMsg conversation:{} loadOldestAndLastestMsg getGroup cost:{}",account.getNumber(),account.getAuthenticatedDevice().get().getId(),conversationMsg.getConversation(),System.currentTimeMillis()-begin);
    begin=System.currentTimeMillis();
    List<OutgoingMessageEntityForSharding> list=messagesForSharding.loadOldestAndLastest(conversationMsg.getConversation(),startTime,account.getAuthenticatedDevice().get().getCreated());
    logger.info("uid:{},did:{} getConversationMsg messagesManager.getConversationMsg conversation:{} loadOldestAndLastestMsg loadOldestAndLastest cost:{}",account.getNumber(),account.getAuthenticatedDevice().get().getId(),conversationMsg.getConversation(),System.currentTimeMillis()-begin);
    return list;
  }


  private boolean isGroup(String conversation){
    if(!StringUtil.isEmpty(conversation)){
      return conversation.indexOf(":")==-1;
    }
    return false;
  }

  public long count() {
    return this.messages.count();
  }

  public long countByEstimate() {
    this.messages.analyze();
    return this.messages.countByEstimate();
  }

  public void clear(String destination) {
    this.messagesCache.clear(destination);
    this.messages.clear(destination);
    memCache.remove(destination+Constants.MESSAGES_DEPTH);
  }

  public void clear(String destination, long deviceId) {
    this.messagesCache.clear(destination, deviceId);
    this.messages.clear(destination, deviceId);
    memCache.hdel(destination+Constants.MESSAGES_DEPTH,deviceId+"");
  }

  public void clear(String destination,String source,int priority) {
    this.messages.clear(destination,source,priority);
  }

  public void clearMessagesDepth(String destination, long deviceId) {
    memCache.hdel(destination+Constants.MESSAGES_DEPTH,deviceId+"");
  }
  public void subtractMessagesDepth(String destination, long deviceId,int count) {
    memCache.hincrBy(destination+Constants.MESSAGES_DEPTH,deviceId+"",-count);
  }

  public Optional<OutgoingMessageEntity> delete(String destination, long destinationDevice, String source, long timestamp)
  {
    Optional<OutgoingMessageEntity> removed = this.messagesCache.remove(destination, destinationDevice, source, timestamp);

    if (!removed.isPresent()) {
      removed = Optional.ofNullable(this.messages.remove(destination, destinationDevice, source, timestamp));
      cacheMissByNameMeter.mark();
    } else {
      cacheHitByNameMeter.mark();
    }

    return removed;
  }

  public void delete(String destination, long deviceId, long id, boolean cached) {
    if (cached) {
      this.messagesCache.remove(destination, deviceId, id);
      cacheHitByIdMeter.mark();
    } else {
      Timer.Context context=deleteMeter.time();
      try {
        this.messages.remove(destination, id);
      }finally {
        context.stop();
      }
      cacheMissByIdMeter.mark();
    }
  }

  public void deleteForSharding(PushConversation pushConversation, long id, boolean cached) {
    if (cached) {
      this.messagesCache.remove(pushConversation.getDestination(), pushConversation.getDestinationDevice(), id);
      cacheHitByIdMeter.mark();
    } else {
      Timer.Context context=deleteMeter.time();
      try {
        String pushMsgKey = getPushMsgKey(pushConversation.getDestination(), pushConversation.getDestinationDevice(), pushConversation.getId());
        memCache.zrem(pushMsgKey,id+"");
        pushMessagesTable.deleteByPushConversationIdAndMsgId(pushConversation.getId(),id);
      }finally {
        context.stop();
      }
      cacheMissByIdMeter.mark();
    }
  }

  public long trimMessage(int expireThreshold){
    long     timestamp = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(expireThreshold);
    logger.info("Trimming old messages: " + timestamp + "...");
    return messages.removeOld(timestamp);
  }

  public long trimMessage(String sourceRegex,int expireThreshold){
    long     timestamp = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(expireThreshold);
    logger.info("Trimming old messages: sourceRegex:{} ,timestamp:{} ..." ,sourceRegex,timestamp );
    return messages.removeOldBySource(sourceRegex,timestamp);
  }

  /**
   * 增加消息的SeqNo和系统时间等信息
   * @param destinationName
   * @param groupId
   */
  public Long processMessageSeqInfo(Account source, Optional<String> destinationName, Optional<String> groupId, long timestamp,boolean notify,boolean add) {
    Long msgSeqNo = -1L;
    // 单聊，每个单聊一个seqNo
    if(!groupId.isPresent()) {
      try {
        msgSeqNo = this.getIndividualMessageSeqNo(source, destinationName.get(), timestamp,notify,add);
      } catch (Exception e) {
        logger.error("individual chat lock error! msg:{}", e.getMessage());
        e.printStackTrace();
      }
    } else { // 群聊，每个群一个seqNo
      String msgGroupId = groupId.get();
      try {
        msgSeqNo = this.getGroupMessageSeqNo(msgGroupId, source, timestamp,notify,add);
      } catch (Exception e) {
        logger.error("group chat lock error! msg:{}", e.getMessage());
        e.printStackTrace();
      }
    }
    return msgSeqNo;
  }


  /**
   * 单聊消息SeqNo
   * @param source
   * @param destinationName
   * @param timestamp
   * @return
   */
  public Long getIndividualMessageSeqNo(Account source, String destinationName, long timestamp,boolean notify,boolean add) {
    Long msgSeqNo = null;
    String deviceId = source.getAuthenticatedDevice().isPresent() ? String.valueOf(source.getAuthenticatedDevice().get().getId()) : StringUtils.EMPTY;
    String cacheKey = RedisConstants.buildIndividualChatCacheKey(source.getNumber(), destinationName, deviceId, timestamp,notify);
    try {
      if (memCache.exists(cacheKey)) {
        String cacheSeqNo = memCache.get(cacheKey);
        msgSeqNo = Long.parseLong(cacheSeqNo);
      } else {
        RAtomicLong atomicLong = DistributedLock.getRedissonAtomicLong(RedisConstants.buildIndividualChatSeqNoKey(source.getNumber(), destinationName,notify));
        if(add) {
          msgSeqNo = atomicLong.incrementAndGet();
        }else{
          msgSeqNo=atomicLong.get();
        }
        memCache.setex(cacheKey, 86400, msgSeqNo.toString());
      }
    }catch (Exception e){
      logger.error("MessagesManager.getGroupMessageSeqNo error!",e);
      if(msgSeqNo==null){
        if(notify) {
          long maxSequenceId = messagesForSharding.getMaxNotifySequenceId(getConversation(destinationName, source.getNumber(), null));
          return maxSequenceId + 1;
        }else{
          long maxSequenceId = messagesForSharding.getMaxSequenceId(getConversation(destinationName, source.getNumber(), null));
          return maxSequenceId + 1;
        }
      }
    }

    return msgSeqNo;
  }

  /**
   * 群聊消息SeqNo
   * @param msgGroupId
   * @param source
   * @param timestamp
   * @return
   */
  public Long getGroupMessageSeqNo(String msgGroupId, Account source, long timestamp,boolean notify,boolean add) {
    Long msgSeqNo = null;
    String deviceId = source.getAuthenticatedDevice().isPresent() ? String.valueOf(source.getAuthenticatedDevice().get().getId()) : StringUtils.EMPTY;
    String cacheKey = RedisConstants.buildGroupChatCacheKey(msgGroupId, source.getNumber(), deviceId, timestamp,notify);
    try {
      if (memCache.exists(cacheKey)) {
        String cacheSeqNo = memCache.get(cacheKey);
        msgSeqNo = Long.parseLong(cacheSeqNo);
      } else {
        RAtomicLong atomicLong = DistributedLock.getRedissonAtomicLong(RedisConstants.buildGroupChatSeqNoKey(msgGroupId,notify));
        if(add) {
          msgSeqNo = atomicLong.incrementAndGet();
        }else{
          msgSeqNo = atomicLong.get();
        }
        memCache.setex(cacheKey, 86400 * 2, msgSeqNo.toString());
      }
    }catch (Exception e){
      logger.error("MessagesManager.getGroupMessageSeqNo error!",e);
      if(msgSeqNo==null){
        if(notify) {
          long maxSequenceId = messagesForSharding.getMaxNotifySequenceId(msgGroupId);
          return maxSequenceId + 1;
        }else{
          long maxSequenceId = messagesForSharding.getMaxSequenceId(msgGroupId);
          return maxSequenceId + 1;
        }
      }
    }
    return msgSeqNo;
  }

  public String getConversation(String destination,String source,String gid){
    if(StringUtil.isEmpty(gid)) {
      List<String> sortedList = Ordering.natural().sortedCopy(Lists.newArrayList(source, destination));
      return Joiner.on(":").join(sortedList);
    }else {
      return gid;
    }
  }

  public List<ConversationPreviews.ConversationPreview> getPushConversations(String destination, long destinationDevice){
    List<PushConversation> pushConversations =pushConversationsTable.getByDestination(destination,destinationDevice);
    List<Long> msgIds=new ArrayList<>();
    List<ConversationPreviews.ConversationPreview> conversationPreviewList=null;
    Map<Long,Envelope> messageEntityMap=new HashMap<>();
    if(pushConversations!=null&&pushConversations.size()>0){
      for(PushConversation pushConversation:pushConversations) {
        msgIds.add(pushConversation.getLastVisibleMsgId());
      }
      List<OutgoingMessageEntity> outgoingMessageEntityList=messages.loadByIds(msgIds);
      messageEntityMap=messageToEnvelope(outgoingMessageEntityList);
      conversationPreviewList=new ArrayList<>();
      for(PushConversation pushConversation:pushConversations) {
        ConversationPreviews.ConversationPreview.Builder builder=ConversationPreviews.ConversationPreview.newBuilder();
        builder.setConversationId(getConversatinId(pushConversation));
        Envelope message =messageEntityMap.get(pushConversation.getLastVisibleMsgId());
        if(message!=null) {
          builder.setLastestMsg(message);
        }else{
          builder.setLastestMsgNsId(pushConversation.getLastVisibleMsgNotifySequenceId());
        }
        builder.setReadPosition(getReadPosition(pushConversation));
        builder.setMaxOutgoingNsId(pushConversation.getLastSelfNotifySequenceId());
        builder.setUnreadCorrection(getCorrectionValue(pushConversation.getConversation(),builder.getReadPosition().getMaxNotifySequenceId(),builder.getLastestMsg().getNotifySequenceId()));
        conversationPreviewList.add(builder.build());
      }
    }
    return conversationPreviewList;
  }

  private int getCorrectionValue(String conversation,long start,long end){
    return recallMsgInfosTable.getUnreadCorrection(conversation,start,end);
  }

  private ConversationPreviews.ReadPosition getReadPosition(PushConversation pushConversation){
    ReadReceipt readReceipt=readReceiptsManager.getMaxReadReceipt(pushConversation.getDestination(),pushConversation.getConversation());
    ConversationPreviews.ReadPosition.Builder builder=ConversationPreviews.ReadPosition.newBuilder();
    if(readReceipt!=null){
      builder.setReadAt(readReceipt.getReadAt());
      builder.setMaxServerTime(readReceipt.getMaxServerTimestamp());
      builder.setMaxNotifySequenceId(readReceipt.getMaxNotifySequenceId());
      builder.setMaxSequenceId(messagesForSharding.loadByNsId(pushConversation.getConversation(),readReceipt.getMaxNotifySequenceId()));
    }else{
      builder.setReadAt(0);
      builder.setMaxServerTime(0);
      builder.setMaxNotifySequenceId(0);
    }
    return builder.build();
  }
  private ConversationPreviews.ConversationId getConversatinId(PushConversation pushConversation){
    ConversationPreviews.ConversationId.Builder builder=ConversationPreviews.ConversationId.newBuilder();
    if(pushConversation.getConversation().indexOf(pushConversation.getDestination())!=-1){
      //+71638272541:+79309462780
      String des="\\"+pushConversation.getDestination();
      String number= pushConversation.getConversation().replaceFirst(des,"").replace(":","");
      builder.setNumber(number);
    }else{
      if(!StringUtil.isEmpty(pushConversation.getConversation())) {
        builder.setGroupId(ByteString.copyFrom(pushConversation.getConversation().getBytes()));
      }
    }
    return builder.build();
  }

  private Map<Long,Envelope> messageToEnvelope(List<OutgoingMessageEntity> outgoingMessageEntityList){
    Map<Long,Envelope> messageEntityMap=new HashMap<>();
    if(outgoingMessageEntityList!=null&&outgoingMessageEntityList.size()>0) {
      for(OutgoingMessageEntity message:outgoingMessageEntityList){
        Envelope.Builder builder = Envelope.newBuilder()
                .setType(Envelope.Type.valueOf(message.getType()))
                .setSourceDevice(message.getSourceDevice())
                .setSource(message.getSource())
                .setSequenceId(message.getSequenceId())
                .setNotifySequenceId(message.getNotifySequenceId())
                .setMsgType(Envelope.MsgType.valueOf(message.getMsgType()))
                .setTimestamp(message.getTimestamp());

        if(message.getSystemShowTimestamp()!=null){
          builder.setSystemShowTimestamp(message.getSystemShowTimestamp());
        }
        if (message.getMessage() != null) {
          builder.setLegacyMessage(ByteString.copyFrom(message.getMessage()));
        }

        if (message.getContent() != null) {
          builder.setContent(ByteString.copyFrom(message.getContent()));
        }

        if (message.getRelay() != null && !message.getRelay().isEmpty()) {
          builder.setRelay(message.getRelay());
        }
        if (message.getSourceIK() != null && !message.getSourceIK().isEmpty()) {
          builder.setSourceIdentityKey(message.getSourceIK());
        }
        if (message.getPeerContext() != null && !message.getPeerContext().isEmpty()) {
          builder.setPeerContext(message.getPeerContext());
        }
        messageEntityMap.put(message.getId(),builder.build());
      }
    }
    return messageEntityMap;
  }

  public Envelope messageToEnvelope(OutgoingMessageEntityForSharding message){
    Envelope.Builder builder = Envelope.newBuilder()
            .setType(Envelope.Type.valueOf(message.getType()))
            .setSourceDevice(message.getSourceDevice())
            .setSource(message.getSource())
            .setSequenceId(message.getSequenceId())
            .setNotifySequenceId(message.getNotifySequenceId())
            .setMsgType(Envelope.MsgType.valueOf(message.getMsgType()))
            .setTimestamp(message.getTimestamp());
    if(message.getSystemShowTimestamp()!=null){
      builder.setSystemShowTimestamp(message.getSystemShowTimestamp());
    }
    if (message.getContent() != null) {
      builder.setContent(ByteString.copyFrom(message.getContent()));
    }
    return builder.build();
  }

  public List<OutgoingMessageEntityForSharding> getPushMessages(String destination, long destinationDevice,PushConversation pushConversation){
    List<Long> msgIds=this.getPushMessageIds(destination,destinationDevice,pushConversation);
    if(msgIds!=null&&msgIds.size()>0) {
      return messagesForSharding.loadByIds(pushConversation.getConversation(), msgIds);
    }
    return null;
  }
  private List<Long> getPushMessageIds(String destination, long destinationDevice,PushConversation pushConversation){
    String pushMsgKey = getPushMsgKey(destination, destinationDevice, pushConversation.getId());
    Set<String> msgIds=null;
    if(pushConversation.getPushType()==PushConversationsTable.PUSH_TYPE.ALL.ordinal()) {
      msgIds = memCache.zRange(pushMsgKey, Messages.RESULT_SET_CHUNK_SIZE);
    }else{
      msgIds=memCache.zRangeForLatest(pushMsgKey,PUSH_MSG_SIZE);
    }
    List<Long> list=null;
    if(msgIds==null||msgIds.size()==0){
      List<PushMessage> pushMessageList=pushMessagesTable.getByConversation(pushConversation.getId());
      Map<String,Double> members=new HashMap<>();
      if(pushMessageList!=null&&pushMessageList.size()>0){
        list=new ArrayList<>();
        for(PushMessage pushMessage:pushMessageList){
          members.put(pushMessage.getMsgId()+"",Double.parseDouble(pushMessage.getSequenceId()+""));
          list.add(pushMessage.getMsgId());
        }
        memCache.zAdd(pushMsgKey,members);
        if(pushConversation.getPushType()==PushConversationsTable.PUSH_TYPE.ALL.ordinal()) {
          int toIndex=list.size()>Messages.RESULT_SET_CHUNK_SIZE?Messages.RESULT_SET_CHUNK_SIZE:list.size();
          list = list.subList(0,toIndex);
        }else{
          int fromIndex=list.size()-PUSH_MSG_SIZE>0?list.size()-PUSH_MSG_SIZE:0;
          list = list.subList(fromIndex,list.size());
        }
      }
    }else {
      list=new ArrayList<>();
      Iterator iterator=msgIds.iterator();
      while (iterator.hasNext()){
        list.add(Long.parseLong((String)iterator.next()));
      }
    }
    return list;
  }


  private String getPushConversationKey(String destination, long destinationDevice){
    return String.join("_",MessagesManager.class.getSimpleName(), "PushConversation", String.valueOf(PushConversation.MEMCACHE_VERION), destination,destinationDevice+"");
  }

  private String getSendConversationMsgKey(String destination, long destinationDevice){
    return String.join("_",MessagesManager.class.getSimpleName(), "SendConversationMsg", String.valueOf(PushConversation.MEMCACHE_VERION), destination,destinationDevice+"");
  }

  private String getConversationMsgKey(String conversation){
    return String.join("_",MessagesManager.class.getSimpleName(), "ConversationMsg", String.valueOf(PushConversation.MEMCACHE_VERION), conversation);

  }
  private String getPushMsgKey(String destination, long destinationDevice ,String conversation){
    return String.join("_",MessagesManager.class.getSimpleName(), "PushMsg", String.valueOf(PushMessage.MEMCACHE_VERION),destination,destinationDevice+"", conversation);
  }
  public static class Distribution {

    private final float percentage;

    public Distribution(float percentage) {
      this.percentage = percentage;
    }

    public boolean isQualified(String address, long device) {
      if (percentage <= 0)   return false;
      if (percentage >= 100) return true;

      try {
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        digest.update(address.getBytes());
        digest.update(Conversions.longToByteArray(device));

        byte[] result = digest.digest();
        int hashCode = Conversions.byteArrayToShort(result);

        return hashCode <= 65535 * percentage;
      } catch (NoSuchAlgorithmException e) {
        throw new AssertionError(e);
      }
    }

  }

  public boolean isForbidden(String source,String destination){
    StringBuffer sb=new StringBuffer();
    sb.append(source).append("_").append(destination);
    if(forbiddenMessage.contains(sb.toString())){
      return true;
    }
    sb.delete(0,sb.length());
    sb.append(destination).append("_").append(source);
    if(forbiddenMessage.contains(sb.toString())){
      return true;
    }
    return false;
  }
//  PERSONAL(1),
//  SYNC(2),
//  READ_RECEIPT(3),
//  NOTIFY(4),
//  GROUP(5),
//  BOT(6);
  public int getPriority(String source,String destination,String gid,boolean readReceipt){
    if(!StringUtils.isEmpty(gid)){
      return Messages.PRIORITY.GROUP.getValue();
    }
    if(readReceipt){
      return Messages.PRIORITY.READ_RECEIPT.getValue();
    }
    if(source.equals("server")){
      return Messages.PRIORITY.NOTIFY.getValue();
    }
    return Messages.PRIORITY.PERSONAL.getValue();
  }

  public void setSendConversationMsgLock(String destination, long destinationDevice){
    memCache.set(getSendConversationMsgKey(destination,destinationDevice),"");
  }

  public boolean isExistsSendConversationMsgLock(String destination, long destinationDevice){
    return memCache.exists(getSendConversationMsgKey(destination,destinationDevice));
  }

  public void removeSendConversationMsgLock(String destination, long destinationDevice){
    memCache.remove(getSendConversationMsgKey(destination,destinationDevice));
  }


  public void setPriorConversation(String destination, long destinationDevice,SetPriorConversationRequest setPriorConversationRequest){
    String realConversation=getConversation(setPriorConversationRequest.getNumber(),destination,setPriorConversationRequest.getGid());
    memCache.set(getPriorConversationKey(destination,destinationDevice),realConversation);
  }

  public String getPriorConversation(String destination, long destinationDevice) {
    return  memCache.get(getPriorConversationKey(destination,destinationDevice));
  }

  public void clearPriorConversation(String destination, long destinationDevice) {
    memCache.remove(getPriorConversationKey(destination,destinationDevice));
  }

  public void removePriorConversation(String destination, long destinationDevice,String realConversation) {
    if(realConversation.equals(memCache.get(getPriorConversationKey(destination,destinationDevice)))){
      memCache.remove(getPriorConversationKey(destination,destinationDevice));
    }
  }

  private String getPriorConversationKey(String destination, long destinationDevice){
    return String.join("_",MessagesManager.class.getSimpleName(), "PriorConversation", String.valueOf(PushConversation.MEMCACHE_VERION), destination,destinationDevice+"");
  }

  public void storeRecallMsg(RecallMsgInfo recallMsgInfo){
    recallMsgInfosTable.insert(recallMsgInfo);
  }

  public void updateMsgForRecall(String source,long sourceDevice,long timestamp){
    messages.updateForRecall(source,sourceDevice,timestamp);
  }

  public static void main(String[] args) {
    PushConversation pushConversation=new PushConversation();
    pushConversation.setConversation("+71638272541:+71638272541");
    pushConversation.setDestination("+71638272541");
    String des="\\"+pushConversation.getDestination();
    String number= pushConversation.getConversation().replaceFirst(des,"").replace(":","");
    System.out.println(number);
  }
}
