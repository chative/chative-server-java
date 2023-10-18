/*
 * Copyright (C) 2014 Open WhisperSystems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.whispersystems.textsecuregcm.push;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.IncomingMessage;
import org.whispersystems.textsecuregcm.entities.IncomingMessageBase;
import org.whispersystems.textsecuregcm.entities.Notification;
import org.whispersystems.textsecuregcm.entities.SendMessageLogHandler;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.Constants;
import org.whispersystems.textsecuregcm.util.StringUtil;
import org.whispersystems.textsecuregcm.websocket.ProvisioningAddress;
import org.whispersystems.textsecuregcm.websocket.WebsocketAddress;

import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.codahale.metrics.MetricRegistry.name;
import static org.whispersystems.textsecuregcm.entities.MessageProtos.Envelope;
import static org.whispersystems.textsecuregcm.storage.PubSubProtos.PubSubMessage;

public class WebsocketSender {

  public enum Type {
    APN,
    GCM,
    WEB
  }

  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(WebsocketSender.class);

  private final MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME);

  private final Meter websocketRequeueMeter = metricRegistry.meter(name(getClass(), "ws_requeue"));
  private final Meter websocketOnlineMeter  = metricRegistry.meter(name(getClass(), "ws_online"  ));
  private final Meter websocketOfflineMeter = metricRegistry.meter(name(getClass(), "ws_offline" ));

  private final Meter apnOnlineMeter        = metricRegistry.meter(name(getClass(), "apn_online" ));
  private final Meter apnOfflineMeter       = metricRegistry.meter(name(getClass(), "apn_offline"));

  private final Meter gcmOnlineMeter        = metricRegistry.meter(name(getClass(), "gcm_online" ));
  private final Meter gcmOfflineMeter       = metricRegistry.meter(name(getClass(), "gcm_offline"));

  private final Meter provisioningOnlineMeter  = metricRegistry.meter(name(getClass(), "provisioning_online" ));
  private final Meter provisioningOfflineMeter = metricRegistry.meter(name(getClass(), "provisioning_offline"));
  private final Timer insertForBatchMeter    = metricRegistry.timer(name(WebsocketSender.class, "insertForBatchMeter"   ));
  private final Timer insertForBatchInsertMeter    = metricRegistry.timer(name(WebsocketSender.class, "insertForBatchInsertMeter"   ));

  private final MessagesManager messagesManager;
  private final PubSubManager   pubSubManager;
  private MemCache memCache = null;
  private final ConcurrentMap<WebsocketAddress, Boolean> queryDbMap = new ConcurrentHashMap<WebsocketAddress, Boolean>();



  public WebsocketSender(MessagesManager messagesManager, PubSubManager pubSubManager,MemCache memCache ) {
    this.messagesManager = messagesManager;
    this.pubSubManager   = pubSubManager;
    this.memCache=memCache;
    startQueryDbTimer();
  }

  public DeliveryStatus sendMessage(Account account, Device device, Envelope message, Type channel, boolean notify, Notification notification, boolean readReceipt, IncomingMessageBase incomingMessage, long shardMsgId) {
    WebsocketAddress address       = new WebsocketAddress(account.getNumber(), device.getId());
    PubSubProtos.Notification.Builder notificationBuilder=PubSubProtos.Notification.newBuilder();
    String gid=null;
    if(notification!=null){
      notificationBuilder.setType(notification.getType());
      if(notification.getArgs()!=null){
        PubSubProtos.Args.Builder argsBuilder=PubSubProtos.Args.newBuilder();
        if(notification.getArgs().getGid()!=null) {
          argsBuilder.setGid(notification.getArgs().getGid());
//          gid=notification.getArgs().getGid();
        }
        if(notification.getArgs().getGname()!=null) {
          argsBuilder.setGname(notification.getArgs().getGname());
        }
        if(notification.getArgs().getPassthrough()!=null) {
          argsBuilder.setPassthrough(notification.getArgs().getPassthrough());
        }
        if(notification.getArgs().getCollapseId()!=null) {
          argsBuilder.setCollapseId(notification.getArgs().getCollapseId());
        }
        if(notification.getArgs().getMentionedPersons()!=null&&notification.getArgs().getMentionedPersons().size()>0) {
          argsBuilder.addAllMentionedPersons(notification.getArgs().getMentionedPersons());
        }
        notificationBuilder.setArgs(argsBuilder.build());
      }
    }
    String realDestination=account.getNumber();
    if(incomingMessage!=null&&(incomingMessage.getMsgType()==Envelope.MsgType.MSG_SYNC_VALUE||(incomingMessage.getMsgType()==Envelope.MsgType.MSG_SYNC_NORMAL_VALUE||incomingMessage.getMsgType()==Envelope.MsgType.MSG_SYNC_READ_RECEIPT_VALUE||incomingMessage.getMsgType()==Envelope.MsgType.MSG_READ_RECEIPT_VALUE))){
      if(incomingMessage.getConversation()!=null){
        if(incomingMessage.getConversation().getType()==IncomingMessage.Conversation.Type.GROUP){
          gid=incomingMessage.getConversation().getGid();
        }
        if(incomingMessage.getConversation().getType()==IncomingMessage.Conversation.Type.PRIVATE){
          realDestination=incomingMessage.getConversation().getNumber();
        }
      }
    }
//    boolean isSharding=messagesManager.isSharding(message);
//    long shardMsgId=-1L;
    String conversation=messagesManager.getConversation(realDestination,message.getSource(),gid);
//    shardMsgId=this.insertForSharding(message,notify,conversation);
//    if(isSharding){
//      msgId=messagesManager.insertForSharding(message,notify,conversation);
//    }

    //messagesManager.insertPushForSource(message,conversation,notify,shardMsgId);
    PubSubProtos.Conversation.Builder conversationBuilder=PubSubProtos.Conversation.newBuilder();
    if(incomingMessage!=null&&incomingMessage.getConversation()!=null){
      if(!StringUtil.isEmpty(incomingMessage.getConversation().getGid())){
        conversationBuilder.setGid(incomingMessage.getConversation().getGid());
      }
      if(!StringUtil.isEmpty(incomingMessage.getConversation().getNumber())){
        conversationBuilder.setNumber(incomingMessage.getConversation().getNumber());
      }
    }
    PubSubProtos.RealSource.Builder realSourceBuilder=PubSubProtos.RealSource.newBuilder();
    if(incomingMessage!=null&&incomingMessage.getRealSource()!=null){
      if(!StringUtil.isEmpty(incomingMessage.getRealSource().getSource())){
        realSourceBuilder.setSource(incomingMessage.getRealSource().getSource());
      }
      realSourceBuilder.setSourceDevice(incomingMessage.getRealSource().getSourceDevice());
      realSourceBuilder.setServerTimestamp(incomingMessage.getRealSource().getServerTimestamp());
      realSourceBuilder.setTimestamp(incomingMessage.getRealSource().getTimestamp());
      realSourceBuilder.setSequenceId(incomingMessage.getRealSource().getSequenceId());
      realSourceBuilder.setNotifySequenceId(incomingMessage.getRealSource().getNotifySequenceId());
    }
    PubSubMessage    pubSubMessage = PubSubMessage.newBuilder()
            .setType(PubSubMessage.Type.DELIVER)
            .setContent(message.toByteString())
            .setNotify(notify)
            .setNotification(notificationBuilder)
            .setReadReceipt(readReceipt)
            .setMsgId(shardMsgId)
            .setConversation(conversationBuilder.build())
            .setRealSource(realSourceBuilder.build())
            .build();


    // The publish could be successful when the client suddenly go offline without disconnecting.
    // Then, the client will never receive the message.
    // So, we keep the message whether the publish is successful or not.
    // The message will be removed from the database when it is received.

     if (pubSubManager.publish(address, pubSubMessage)) {
       if      (channel == Type.APN) apnOnlineMeter.mark();
//       else if (channel == Type.GCM) gcmOnlineMeter.mark();
       else                          websocketOnlineMeter.mark();

       return new DeliveryStatus(true, 0);
     } else {
       if      (channel == Type.APN) apnOfflineMeter.mark();
//       else if (channel == Type.GCM) gcmOfflineMeter.mark();
       else                          websocketOfflineMeter.mark();


       messagesManager.insert(account.getNumber(), device.getId(), message,notify,readReceipt,shardMsgId,gid,realDestination,incomingMessage==null?null:incomingMessage.getRealSource());
       int addDepth=0;
       if(notify){
         addDepth=1;
       }
       int queueDepth=memCache.hincrBy(account.getNumber()+Constants.MESSAGES_DEPTH,device.getId()+"",addDepth).intValue();
       pubSubManager.publish(address, PubSubMessage.newBuilder()
               .setType(PubSubMessage.Type.QUERY_DB)
               .build());
       try {
         SendMessageLogHandler.SendMessageLog sendMessageLog=new SendMessageLogHandler.SendMessageLog(SendMessageLogHandler.SendMessageAction.INSERT.getName(),account.getNumber(),device.getId(),message);
         SendMessageLogHandler.send(sendMessageLog);
       }catch (Exception e){
         logger.error("SendMessageLog error!"+e.getMessage());
       }

       return new DeliveryStatus(false, queueDepth);
     }

//    pubSubManager.publish(address, pubSubMessage);
//
//    if      (channel == Type.APN) apnOfflineMeter.mark();
//    else if (channel == Type.GCM) gcmOfflineMeter.mark();
//    else                          websocketOfflineMeter.mark();
//
//    int queueDepth = messagesManager.insert(account.getNumber(), device.getId(), message);
//    pubSubManager.publish(address, PubSubMessage.newBuilder()
//            .setType(PubSubMessage.Type.QUERY_DB)
//            .build());
//
//    return new DeliveryStatus(false, queueDepth);
  }

  public void insertForBatch(List<String> destinations,List<Long> destinationDevices,List<Envelope> messages,List<Boolean> notifys,List<Integer> prioritys,List<String> conversations,long msgId,String gid){
    Timer.Context context=insertForBatchMeter.time();
    try {
      Timer.Context context1=insertForBatchInsertMeter.time();
      try {
        messagesManager.insertBatch(destinations,destinationDevices,messages,notifys,prioritys,conversations,msgId,gid);
      }finally {
        context1.stop();
      }
      for(int i=0;i<destinations.size();i++) {
        WebsocketAddress address= new WebsocketAddress(destinations.get(i),destinationDevices.get(i));
        pubSubManager.publish(address, PubSubProtos.PubSubMessage.newBuilder()
                .setType(PubSubProtos.PubSubMessage.Type.QUERY_DB)
                .build());
//        try {
//          SendMessageLogHandler.SendMessageLog sendMessageLog=new SendMessageLogHandler.SendMessageLog(SendMessageLogHandler.SendMessageAction.INSERT.getName(),destinations.get(i),destinationDevices.get(i),messages.get(i));
//          SendMessageLogHandler.send(sendMessageLog);
//        }catch (Exception e){
//          logger.error("SendMessageLog error!"+e.getMessage());
//        }
      }
    }finally {
      context.stop();
    }

  }

  public boolean sendMessageForGroup(Account account, Device device, Envelope message, boolean notify,IncomingMessageBase incomingMessage, Notification notification) {
    WebsocketAddress address       = new WebsocketAddress(account.getNumber(), device.getId());
    PubSubProtos.Notification.Builder notificationBuilder=PubSubProtos.Notification.newBuilder();;
    if(notification!=null){
      notificationBuilder.setType(notification.getType());
      if(notification.getArgs()!=null){
        PubSubProtos.Args.Builder argsBuilder=PubSubProtos.Args.newBuilder();
        if(notification.getArgs().getGid()!=null) {
          argsBuilder.setGid(notification.getArgs().getGid());
        }
        if(notification.getArgs().getGname()!=null) {
          argsBuilder.setGname(notification.getArgs().getGname());
        }
        if(notification.getArgs().getPassthrough()!=null) {
          argsBuilder.setPassthrough(notification.getArgs().getPassthrough());
        }
        if(notification.getArgs().getCollapseId()!=null) {
          argsBuilder.setCollapseId(notification.getArgs().getCollapseId());
        }
        if(notification.getArgs().getMentionedPersons()!=null&&notification.getArgs().getMentionedPersons().size()>0) {
          argsBuilder.addAllMentionedPersons(notification.getArgs().getMentionedPersons());
        }
        notificationBuilder.setArgs(argsBuilder.build());
      }
    }
    PubSubProtos.Conversation.Builder conversationBuilder=PubSubProtos.Conversation.newBuilder();
    if(incomingMessage!=null&&incomingMessage.getConversation()!=null){
      if(!StringUtil.isEmpty(incomingMessage.getConversation().getGid())){
        conversationBuilder.setGid(incomingMessage.getConversation().getGid());
      }
      if(!StringUtil.isEmpty(incomingMessage.getConversation().getNumber())){
        conversationBuilder.setNumber(incomingMessage.getConversation().getNumber());
      }
    }

    PubSubMessage    pubSubMessage = PubSubMessage.newBuilder()
            .setType(PubSubMessage.Type.DELIVER)
            .setContent(message.toByteString())
            .setNotify(notify)
            .setNotification(notificationBuilder)
            .setConversation(conversationBuilder.build())
            .build();

    // The publish could be successful when the client suddenly go offline without disconnecting.
    // Then, the client will never receive the message.
    // So, we keep the message whether the publish is successful or not.
    // The message will be removed from the database when it is received.

    if (pubSubManager.publish(address, pubSubMessage)) {
      return true;
    } else {
      return false;
    }

  }

  public void queueMessage(Account account, Device device, Envelope message,boolean notify,boolean readReceipt,long msgId,String gid,String realDestination,IncomingMessage.RealSource realSource) {
    websocketRequeueMeter.mark();

    WebsocketAddress address = new WebsocketAddress(account.getNumber(), device.getId());

    messagesManager.insert(account.getNumber(), device.getId(), message,notify,readReceipt,msgId,gid,realDestination,realSource);
    try {
      SendMessageLogHandler.SendMessageLog sendMessageLog=new SendMessageLogHandler.SendMessageLog(SendMessageLogHandler.SendMessageAction.REQUEUE.getName(),account.getNumber(),device.getId(),message);
      SendMessageLogHandler.send(sendMessageLog);
    }catch (Exception e){
      logger.error("SendMessageLog error!"+e.getMessage());
    }
    queryDbMap.put(address,true);
//    pubSubManager.publish(address, PubSubMessage.newBuilder()
//                                                .setType(PubSubMessage.Type.QUERY_DB)
//                                                .build());
  }

  public boolean sendProvisioningMessage(ProvisioningAddress address, byte[] body) {
    PubSubMessage    pubSubMessage = PubSubMessage.newBuilder()
                                                  .setType(PubSubMessage.Type.DELIVER)
                                                  .setContent(ByteString.copyFrom(body))
                                                  .build();

    if (pubSubManager.publish(address, pubSubMessage)) {
      provisioningOnlineMeter.mark();
      return true;
    } else {
      provisioningOfflineMeter.mark();
      return false;
    }
  }

  public long insertForSharding(Envelope message,boolean notify ,String conversation, IncomingMessage.Conversation incomingConversation){
    if(messagesManager.isSharding(message,incomingConversation)) {
      return messagesManager.insertForSharding(message, notify, conversation);
    }
    return -1;
  }



  public static class DeliveryStatus {

    private final boolean delivered;
    private final int     messageQueueDepth;

    public DeliveryStatus(boolean delivered, int messageQueueDepth) {
      this.delivered = delivered;
      this.messageQueueDepth = messageQueueDepth;
    }

    public boolean isDelivered() {
      return delivered;
    }

    public int getMessageQueueDepth() {
      return messageQueueDepth;
    }
  }

  public void startQueryDbTimer(){
    logger.info("websocketSender startQueryDbTimer!");
    java.util.Timer queryDbTimer = new java.util.Timer();
    queryDbTimer.scheduleAtFixedRate(new TimerTask(){
      @Override
      public void run() {
        sendQueryDb();
      }
    },10000,10000);
  }

  public void sendQueryDb(){
    logger.info("websocketSender sendQueryDb!");
    if(queryDbMap.size()>0) {
      Iterator<WebsocketAddress> iterator=queryDbMap.keySet().iterator();
      while (iterator.hasNext()){
        WebsocketAddress address=iterator.next();
        pubSubManager.publish(address, PubSubMessage.newBuilder()
                .setType(PubSubMessage.Type.QUERY_DB)
                .build());
        logger.info("websocketSender sendQueryDb address:{}",address.toString());
        iterator.remove();
      }
    }
  }
}
