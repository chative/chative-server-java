/*
 * Copyright (C) 2013 Open WhisperSystems
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

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;


import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.apache.commons.text.lookup.StringLookupFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.configuration.PushConfiguration;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.eslogger.loggerFilter;
import org.whispersystems.textsecuregcm.exceptions.NoPermissionException;
import org.whispersystems.textsecuregcm.exceptions.NoSuchGroupException;
import org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest;
import org.whispersystems.textsecuregcm.push.WebsocketSender.DeliveryStatus;
import org.whispersystems.textsecuregcm.redis.RedisOperation;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.BlockingThreadPoolExecutor;
import org.whispersystems.textsecuregcm.util.Constants;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;
import io.dropwizard.lifecycle.Managed;
import org.whispersystems.textsecuregcm.util.StringUtil;

import static org.whispersystems.textsecuregcm.entities.MessageProtos.Envelope;

public class PushSender implements Managed , DelayNotification.DelayNotificationCallback {

  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(PushSender.class);

  private final ApnFallbackManager         apnFallbackManager;
//  private final GCMSender                  gcmSender;
  private final APNSender                  apnSender;
  private final TpnSender                  tpnSender;
  private final WebsocketSender            webSocketSender;
  private final KafkaSender                kafkaSender;
  private final BlockingThreadPoolExecutor executor;
  private final BlockingThreadPoolExecutor executorForGroup;
  private final ThreadPoolExecutor apnExecutor;
  private final int                        queueSize;
  private final PushConfiguration         pushConfiguration;
  private MemCache memCache = null;
  final private AccountsManager accountsManager;
  private ThirdPartyPush mThirdPartyPush = null;
  private GroupManagerWithTransaction groupManagerWithTransaction=null;
  private final MessagesManager messagesManager;
  private final ConversationManager conversationManager;
  private static final MetricRegistry metricRegistry    = SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME);

  private static final Timer sendSynchronousMessageMeter    = metricRegistry.timer(name(MessagesManager.class, "sendSynchronousMessage"   ));
  private static final Timer sendWebsocketMessageMeter    = metricRegistry.timer(name(MessagesManager.class, "sendWebsocketMessage"   ));
  private static final Timer webSocketSenderSendMessageMeter    = metricRegistry.timer(name(MessagesManager.class, "webSocketSenderSendMessage"   ));

  private static final Timer sendSynchronousMessageMeterForGroup    = metricRegistry.timer(name(MessagesManager.class, "sendSynchronousMessageForGroup"   ));
  private static final Timer sendWebsocketMessageMeterForGroup    = metricRegistry.timer(name(MessagesManager.class, "sendWebsocketMessageForGroup"   ));
  private static final Timer apnAndDBHanderMeterForGroup    = metricRegistry.timer(name(MessagesManager.class, "apnAndDBHanderMeterForGroup"   ));


  public PushSender(ApnFallbackManager apnFallbackManager,
                    TpnSender tpnSender,
                   /* GCMSender gcmSender,*/ APNSender apnSender,
                    WebsocketSender websocketSender, PushConfiguration pushConfiguration,
                    AccountsManager accountsManager,
                    MemCache memCache,
                    GroupManagerWithTransaction groupManagerWithTransaction,
                    KafkaSender kafkaSender, MessagesManager messagesManager, ConversationManager conversationManager
  )
  {
    this.apnFallbackManager = apnFallbackManager;
    this.tpnSender          = tpnSender;
    this.apnSender          = apnSender;
    this.webSocketSender    = websocketSender;
    this.pushConfiguration = pushConfiguration;
    this.queueSize = pushConfiguration.getQueueSize();
    this.executor           = new BlockingThreadPoolExecutor(50, queueSize);
    this.executorForGroup   = new BlockingThreadPoolExecutor(100, queueSize);
    this.apnExecutor        =new ThreadPoolExecutor(20,20,0, TimeUnit.SECONDS,new LinkedBlockingDeque<>(), new ThreadPoolExecutor.CallerRunsPolicy());

    this.memCache = memCache;
    this.groupManagerWithTransaction=groupManagerWithTransaction;
    this.accountsManager = accountsManager;
    this.kafkaSender=kafkaSender;
    this.messagesManager=messagesManager;
    this.conversationManager=conversationManager;
    mThirdPartyPush = ThirdPartyPush.getInstance(accountsManager, memCache);

    SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME)
                          .register(name(PushSender.class, "send_queue_depth"),
                                    (Gauge<Integer>) executor::getSize);
    SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME)
            .register(name(PushSender.class, "sendForGroup_queue_depth"),
                    (Gauge<Integer>) executorForGroup::getSize);
  }

  public void sendMessage(final Account account, final Device device, final Envelope message, boolean notify, boolean silent,
                          Notification notification, boolean readReceipt, IncomingMessageBase incomingMessage, long shardMsgId)
      throws NotPushRegisteredException
  {
    if (device.getGcmId() == null && device.getApnId() == null && !device.getFetchesMessages()) {
      throw new NotPushRegisteredException("No delivery possible!");
    }

    if (queueSize > 0) {
      executor.execute(() -> sendSynchronousMessage(account, device, message, notify, silent, notification, readReceipt,incomingMessage,shardMsgId));
    } else {
      sendSynchronousMessage(account, device, message, notify, silent, notification, readReceipt,incomingMessage,shardMsgId);
    }
  }

  public void sendMessageForGroup(final Group group, final Envelope message, boolean notify, boolean silent,IncomingMessageBase incomingMessage, Notification notification)
  {
    if (queueSize > 0) {
      executorForGroup.execute(() -> sendSynchronousMessageForGroup(group, message, notify, silent,incomingMessage, notification));
    } else {
      sendSynchronousMessageForGroup(group, message, notify, silent,incomingMessage, notification);
    }
  }

  public void sendQueuedNotification(Account account, Device device,Envelope message,PubSubProtos.Notification notification,PubSubProtos.Conversation pspConversation)
      throws NotPushRegisteredException
  {
    Notification apnNotification=null;
    if(notification!=null&&notification.hasType()&&notification.hasArgs()){
      apnNotification=new Notification();
      apnNotification.setType(notification.getType());
      Notification.Args args=new Notification.Args();
      if(notification.getArgs().hasGid()) {
        args.setGid(notification.getArgs().getGid());
      }
      if(notification.getArgs().hasGname()) {
        args.setGname(notification.getArgs().getGname());
      }
      if(notification.getArgs().hasCollapseId()) {
        args.setCollapseId(notification.getArgs().getCollapseId());
      }
      if(notification.getArgs().hasPassthrough()) {
        args.setPassthrough(notification.getArgs().getPassthrough());
      }
      if(notification.getArgs().getMentionedPersonsCount()!=0) {
        args.setMentionedPersons(notification.getArgs().getMentionedPersonsList());
      }
      Optional<Account> source=accountsManager.get(message.getSource());
      if(source.isPresent()) {
        args.setSource(source.get());
      }
      apnNotification.setArgs(args);
    }
    int queueDepth=1;
    int addDepth=1;
    queueDepth=memCache.hincrBy(account.getNumber()+Constants.MESSAGES_DEPTH,device.getId()+"",addDepth).intValue();

//    if      (device.getGcmId() != null)  {}  //sendGcmNotification(account, device);
    if (device.shouldPushNotification())   {
      sendPushNotificationCheck(account,device,queueDepth,true,false,message,apnNotification,
              new IncomingMessage.Conversation(pspConversation.hasNumber()?pspConversation.getNumber():null,
                      pspConversation.hasGid()?pspConversation.getGid():null));
    }
    else if (!device.getFetchesMessages()) throw new NotPushRegisteredException("No notification possible!");
  }
  public void sendQueuedNotification(Account account, Device device)
          throws NotPushRegisteredException
  {

//    if      (device.getGcmId() != null)  {}  //sendGcmNotification(account, device);
    if (device.getApnId() != null)   sendPushNotification(account, device, 1,true, false);
    else if (!device.getFetchesMessages()) throw new NotPushRegisteredException("No notification possible!");
  }
  public WebsocketSender getWebSocketSender() {
    return webSocketSender;
  }

  private void sendSynchronousMessageForGroup(Group group, Envelope message, boolean notify, boolean silent,IncomingMessageBase incomingMessage,Notification notification) {
    Timer.Context context=sendSynchronousMessageMeterForGroup.time();
    try {
      List<GroupMember> groupMembers = groupManagerWithTransaction.getGroupMembers(group.getId());
//    logger.info("sendSynchronousMessageForGroup! source:{},group:{},groupMemberSize:{}",message.getSource(),group.getId(),groupMembers.size());
      String source = message.getSource();
      long sourceDeviceId = message.getSourceDevice();
      List<Envelope> messages = new ArrayList<>();
      List<String> destinations = new ArrayList<>();
      List<Long> destinationDeviceIds = new ArrayList<>();
      List<Boolean> notifys = new ArrayList<>();
      List<Account> destinationAccounts = new ArrayList<>();
      List<Device> destinationDevices = new ArrayList<>();
      List<Notification> notifications = new ArrayList<>();
      List<Integer> prioritys = new ArrayList<>();
      List<String> conversations = new ArrayList<>();
      this.setMentionedStr(notification);
//      long msgId=-1;
      long shardMsgId = 0;//webSocketSender.insertForSharding(message,notify,group.getId(),null);
      //messagesManager.insertPushForSource(message,group.getId(),notify,shardMsgId);
      for (GroupMember groupMember : groupMembers) {
        Optional<Account> accountOptional = accountsManager.get(groupMember.getUid());
//      logger.info("sendSynchronousMessageForGroup! source:{},group:{},groupMember:{},accountOptional:{}",message.getSource(),group.getId(),new Gson().toJson(groupMember),new Gson().toJson(accountOptional.get()));
        if (accountOptional.isPresent() && !accountOptional.get().isinValid()) {
          final Account account = accountOptional.get();
          //非消息发送账号，无消息接收权限直接返回
          if (!account.getNumber().equals(source) && account.getAccountMsgHandleType() != null && (account.getAccountMsgHandleType() == Account.MsgHandleType.ONLY_SEND.ordinal() || account.getAccountMsgHandleType() == Account.MsgHandleType.NOTHING.ordinal())) {
            continue;
          }
          //禁止发送消息，直接跳过
          if (messagesManager.isForbidden(message.getSource(), groupMember.getUid())) {
            continue;
          }
          // unregistered accounts don't get messages
          if (!account.isRegistered() || account.isInactive()) {
            logger.info("Not registered or inactive: {},isRegistered:{},inactive:{}", account.getNumber(),
                    account.isRegistered(), account.isInactive());
            continue;
          }
          Notification notificationTemp = reviseNotification(account, notification);
          for (Device device : account.getDevices()) {
            if (accountsManager.isActiveDevice(device, account) &&
                    (!account.getNumber().equals(source)
                            //|| (accountOptional.get().getNumber().equals(source)&&device.getId() != sourceDeviceId)
                    )) {
              if (message.getType() == MessageProtos.Envelope.Type.NOTIFY)
                logger.info("sendSynchronousMessageForGroup! source:{},group:{},timestamp:{},des:{},desDeviceId:{}",
                        message.getSource(), group.getId(), message.getTimestamp(), accountOptional.get().getNumber(), device.getId());
              if (!sendSynchronousMessageForGroup(account, device, message, notify, silent, incomingMessage,notificationTemp)) {
                destinations.add(account.getNumber());
                destinationDeviceIds.add(device.getId());
                notifys.add(notify);
                messages.add(message);
                destinationAccounts.add(account);
                destinationDevices.add(device);
                notifications.add(notificationTemp);
                prioritys.add(messagesManager.getPriority(message.getSource(), null, group.getId(), false));
                conversations.add(group.getId());
              }
            }
          }
        }
      }
      if (messages.size() > 0) {
        apnAndDBHanderForGroup(destinations, destinationDeviceIds, messages, notifys, destinationAccounts, destinationDevices, notifications,prioritys,conversations,shardMsgId,group.getId());
      }
    }finally {
      context.stop();
    }
  }

  private void apnAndDBHanderForGroup(List<String> destinations,List<Long> destinationDeviceIds,List<Envelope> messages,List<Boolean> notifys,List<Account> destinationAccounts, List<Device> destinationDevices,List<Notification> notifications,List<Integer> prioritys,List<String> conversations,long shardMsgId,String gid){
    Timer.Context context=apnAndDBHanderMeterForGroup.time();
    try {
      webSocketSender.insertForBatch(destinations, destinationDeviceIds, messages, notifys,prioritys,conversations,shardMsgId,gid);
      for (int i = 0; i < destinationAccounts.size(); i++) {
        int finalI = i;
        apnExecutor.execute(new Runnable() {
          @Override
          public void run() {
            if (!notifys.get(finalI)) {
              return;
            }
            int addDepth = 1;
            final Device device = destinationDevices.get(finalI);
            int queueDepth = memCache.hincrBy(destinationAccounts.get(finalI).getNumber() + Constants.MESSAGES_DEPTH, device.getId() + "", addDepth).intValue();
            if (device.shouldPushNotification()) {
              if (messages.get(finalI).getType() != Envelope.Type.RECEIPT) {
                sendPushNotificationCheck(destinationAccounts.get(finalI), device, queueDepth, false, false, messages.get(finalI), notifications.get(finalI),
                        new IncomingMessage.Conversation(null,gid));
              }
            }
          }
        });
      }
    }finally {
      context.stop();
    }
  }

  private void setMentionedStr(Notification notification){
    if(notification==null){
      return;
    }
    if(notification.getType()==Notification.Type.GROUP_MENTIONS_DESTINATION.getCode()
            ||notification.getType()==Notification.Type.GROUP_MENTIONS_OTHER.getCode()
            ||notification.getType()==Notification.Type.GROUP_REPLY_DESTINATION.getCode()
            ||notification.getType()==Notification.Type.GROUP_REPLY_OTHER.getCode()) {
      StringBuffer stringBuffer=new StringBuffer();
      List<String> mentionedPeople = notification.getArgs().getMentionedPersons();
      int i = 0;
      if (mentionedPeople != null && mentionedPeople.size() > 0) {
        for (String number : mentionedPeople) {
          if (StringUtil.isEmpty(number)) {
            continue;
          }
          i++;
          if (i > 2) {
            stringBuffer = stringBuffer.append("...");
            break;
          }

          Optional<Account> mentionedAccount = accountsManager.get(number);
          if (mentionedAccount.isPresent() && !StringUtil.isEmpty(mentionedAccount.get().getPlainName())) {
            stringBuffer.append(mentionedAccount.get().getPlainName()).append(",");
          } else {
            stringBuffer.append(number).append(",");
          }
        }
      }
      if (0 < i && i <= 2) {
        stringBuffer = stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());
      }
      if(stringBuffer.length()>0){
        notification.getArgs().setMentionedStr(stringBuffer.toString());
      }
    }
  }
  private Notification reviseNotification(Account account,Notification notification){
    try {
      if (notification == null || (notification.getArgs() == null&&StringUtil.isEmpty(notification.getPayload()))) {
        logger.info("reviseNotification notification is null!");
        return null;
      }
      Notification notificationTemp = (Notification) notification.clone();
      if (notification.getType() == Notification.Type.GROUP_MENTIONS_DESTINATION.getCode()
              || notification.getType() == Notification.Type.GROUP_MENTIONS_OTHER.getCode()
      ) {
        if (notification.getArgs().getMentionedPersons() != null && notification.getArgs().getMentionedPersons().contains(account.getNumber())) {
          notificationTemp.setType(Notification.Type.GROUP_MENTIONS_DESTINATION.getCode());
        } else {
          notificationTemp.setType(Notification.Type.GROUP_MENTIONS_OTHER.getCode());
        }
      }
      if (notification.getType() == Notification.Type.GROUP_REPLY_DESTINATION.getCode()
              || notification.getType() == Notification.Type.GROUP_REPLY_OTHER.getCode()) {
        if (notification.getArgs().getMentionedPersons() != null && notification.getArgs().getMentionedPersons().contains(account.getNumber())) {
          notificationTemp.setType(Notification.Type.GROUP_REPLY_DESTINATION.getCode());
        } else {
          notificationTemp.setType(Notification.Type.GROUP_REPLY_OTHER.getCode());
        }
      }
      if (notification.getType() == Notification.Type.RECALL_MSG.getCode()
              || notification.getType() == Notification.Type.RECALL_MENTIONS_MSG.getCode()) {
        if (notification.getArgs().getMentionedPersons() != null && (notification.getArgs().getMentionedPersons().contains("MENTIONS_ALL") || notification.getArgs().getMentionedPersons().contains(account.getNumber()))) {
          notificationTemp.setType(Notification.Type.RECALL_MENTIONS_MSG.getCode());
        } else {
          notificationTemp.setType(Notification.Type.RECALL_MSG.getCode());
        }
      }
      return notificationTemp;
    }catch (Exception e){
      logger.error("reviseNotification error! msg:{}",e.toString());
    }
    return null;
  }

  private boolean sendSynchronousMessageForGroup(Account account, Device device, Envelope message, boolean notify, boolean silent,IncomingMessageBase incomingMessage,Notification notification) {
//    if      (device.getGcmId() != null) sendGcmMessage(account, device, message);
//    else
    if (device.getReceiveType() == AccountCreateRequest.ReceiveType.KAFKA_VALUE) {
      kafkaSender.sendMessage(account, device, message, Optional.empty(), false);
      return true;
    } else {
      if (device.shouldPushNotification() || device.getFetchesMessages()) {
        return sendWebSocketMessageForGroup(account, device, message, notify,incomingMessage, notification);
      } else {
        logger.warn("sendSynchronousMessageForGroup error! device not active!");
        return true;
      }
    }
  }

  private void sendSynchronousMessage(Account account, Device device, Envelope message, boolean notify, boolean silent,
                                      Notification notification,boolean readReceipt,IncomingMessageBase incomingMessage,long shardMsgId) {
//    if      (device.getGcmId() != null) sendGcmMessage(account, device, message);
//    else
    Timer.Context context=sendSynchronousMessageMeter.time();
    try {
      if (device.getReceiveType() == AccountCreateRequest.ReceiveType.KAFKA_VALUE) {
        kafkaSender.sendMessage(account, device, message, Optional.empty(), false);
      } else {
        // mobile
        if (device.shouldPushNotification()) sendPushNotification(account, device, message, notify, silent, notification, readReceipt,incomingMessage,shardMsgId);
        // desktop
        else if (device.getFetchesMessages()) sendWebSocketMessage(account, device, message, notify, silent, notification,readReceipt,incomingMessage,shardMsgId);
        else throw new AssertionError();
      }
    }finally {
      context.stop();
    }
  }
//
//  private void sendGcmMessage(Account account, Device device, Envelope message) {
//    DeliveryStatus deliveryStatus = webSocketSender.sendMessage(account, device, message, WebsocketSender.Type.GCM);
//
//    if (!deliveryStatus.isDelivered()) {
//      sendGcmNotification(account, device);
//    }
//  }

//  private void sendGcmNotification(Account account, Device device) {
//    GcmMessage gcmMessage = new GcmMessage(device.getGcmId(), account.getNumber(),
//                                           (int)device.getId(), false);
//
//    gcmSender.sendMessage(gcmMessage);
//  }
//
private void sendPushNotification(Account account, Device device, Envelope outgoingMessage, boolean notify, boolean silent, Notification notification, boolean readReceipt, IncomingMessageBase incomingMessage, long shardMsgId) {
  Timer.Context context = webSocketSenderSendMessageMeter.time();
  DeliveryStatus deliveryStatus = null;
  try {
    deliveryStatus = webSocketSender.sendMessage(account, device, outgoingMessage, WebsocketSender.Type.APN, notify, notification, readReceipt, incomingMessage, shardMsgId);
  } catch (Exception e) {
    logger.error("in sendPushNotification sendWebSocketMessage {} to {} ,notify:{},Exception error!",
            outgoingMessage.getSource(), account.getNumber(), notify, e);
  } finally {
    context.stop();
  }
  logger.info("in sendPushNotification {} to {},notify:{},after sendWebSocketMessage, deliveryStatus.isDelivered:{}",
          outgoingMessage.getSource(), account.getNumber(), notify, deliveryStatus != null && deliveryStatus.isDelivered());
  if (!notify) {
    return;
  }

//    if (memCache.exists("userStatus_DND_" + account.getNumber())) {
//      return;
//    }
  if (deliveryStatus != null && !deliveryStatus.isDelivered() && outgoingMessage.getType() != Envelope.Type.RECEIPT) {
    sendPushNotificationCheck(account,device, deliveryStatus.getMessageQueueDepth(),false,
            silent,outgoingMessage,notification,incomingMessage.getConversation());
  }
}

  public void sendPushNotificationCheck(Account account, Device device, int depth, boolean newOnly, boolean silent, Envelope outgoingMessage,
                                        Notification notification, IncomingMessage.Conversation conversation) {
    sendPushNotification(account, device, depth, newOnly, silent, outgoingMessage, notification);

    //if (account.getDevices().size() == 1 || device.getApnId() == null || //  不是 apn的直接发
    //        (notification != null &&
    //                (notification.getType()== Notification.Type.GROUP_CALL.getCode() ||
    //                        notification.getType() == Notification.Type.PERSONAL_CALL.getCode()))) {
    //  logger.info("in sendPushNotificationCheck, {} send to {}, Devices.size() {},getApnId:{}, send apn now",
    //          outgoingMessage.getSource(), account.getNumber(), account.getDevices().size(), device.getApnId());
    //  sendPushNotification(account, device, depth, newOnly, silent, outgoingMessage, notification);
    //  return;
    //}
    //// If we have multiple devices, and will send apn if no read in 15 seconds, push to the queue
    //try {
    //  DelayNotification.addNotification(DelayNotification.checkSent,
    //          new SendNotificationParam(account.getNumber(), device.getId(),
    //                  depth, false, silent, outgoingMessage, notification,
    //                  conversation.getType().equals(IncomingMessage.Conversation.Type.GROUP) ? conversation :
    //                          new IncomingMessage.Conversation(outgoingMessage.getSource(), null)).toJson(), 3000);
    //  logger.info("in sendPushNotificationCheck, {} send to {}, add to DelayNotification.checkSent", outgoingMessage.getSource(), account.getNumber());
    //} catch (JsonProcessingException e) {
    //  logger.error("in sendPushNotificationCheck, {} send to {}, add to DelayNotification.checkSent JsonProcessingException!", outgoingMessage.getSource(), account.getNumber(), e);
    //}

  }
  protected void sendPushNotification(Account account, Device device, int depth, boolean newOnly, boolean silent, Envelope outgoingMessage, Notification notification) {

    if (notification == null ){
      logger.warn("in sendPushNotification, {} send to {}, no notification ",outgoingMessage.getSource(),account.getNumber());
    } else {
      logger.info("in sendPushNotification, {} send to {}, notificationType:{} ",
              outgoingMessage.getSource(),account.getNumber(),  notification.getType());
    }
    if (newOnly && RedisOperation.unchecked(() -> apnFallbackManager.isScheduled(account, device))) {
      logger.info("in sendPushNotification, {} send to {}, newOnly and isScheduled,do not send apn  ",outgoingMessage.getSource(),account.getNumber());
      return;
    }
    String conversationStr=null;
    if(notification==null||notification.getArgs()==null||StringUtil.isEmpty(notification.getArgs().getGid())){
      conversationStr=outgoingMessage.getSource();
    }else{
      conversationStr=notification.getArgs().getGid();
    }
    Conversation conversation=conversationManager.get(account.getNumber(),conversationStr);
    if (conversation != null) {
      final Integer muteStatus = conversation.getMuteStatus();
      logger.info("in sendApnNotification, {} send to {}, muteStatus:{} ",outgoingMessage.getSource(),account.getNumber(), muteStatus);
      if (muteStatus != null && muteStatus == ConversationsTable.STATUS.OPEN.getCode()) {
        logger.info("in sendApnNotification, {} send to {}, is mute, do not send apn ", outgoingMessage.getSource(), account.getNumber());
        return;
      }
    }
    final Device.AndroidNotify androidNotify = device.getAndroidNotify();
    if (androidNotify != null){
      tpnSender.sendNotification(account, device, depth, newOnly, silent, outgoingMessage, notification);
      return;
    }
//    if (!Util.isEmpty(device.getVoipApnId())) {
//      apnMessage = new ApnMessage(device.getVoipApnId(), account.getNumber(), device.getId(), true);
//      RedisOperation.unchecked(() -> apnFallbackManager.schedule(account, device));
//    } else {
//      apnMessage = new ApnMessage(device.getApnId(), account.getNumber(), device.getId(), false);
//    }
//    String gid=outgoingMessage.getGid();
    String payload =null;
    if(notification!=null&&!StringUtil.isEmpty(notification.getPayload())) {
      payload=replacePayload(notification.getPayload(),depth);
      logger.info("payload:"+payload);
    }else{
      payload = this.bulidPayload(account, depth, silent, outgoingMessage, notification, device);
    }
    if (payload != null && payload.contains("APN_Message")){
      logger.warn("in sendApnNotification, {} send to {}, get APN_Message ,notification:{}",
              outgoingMessage.getSource(),account.getNumber(),
              notification != null ? new Gson().toJson(notification) : "null");
      //如果客户端要推apn，但是相关参数不对，就不推了
      payload=null;
    }
      if(!StringUtil.isEmpty(payload)) {
        String collapseId = getCollapseId(notification,outgoingMessage);
        if(notification!=null) {

          ApnMessage apnMessage;
          if(!StringUtil.isEmpty(device.getVoipApnId())&&notification.getType()==Notification.Type.PERSONAL_CALL.getCode()){
            apnMessage = new ApnMessage(device.getVoipApnId(), account.getNumber(), device.getId(), payload, true, collapseId);
            apnSender.sendMessage(device.getUserAgent(), apnMessage);
          }else {
            apnMessage = new ApnMessage(device.getApnId(), account.getNumber(), device.getId(), payload, false, collapseId);
            apnSender.sendMessage(device.getUserAgent(), apnMessage);
          }
        }
      }

//        payload = String.format("{\"aps\":{\"sound\":\"default\",\"badge\":%d,\"alert\":{\"loc-key\":\"APN_Message\"}}}", depth);
//        if (silent) {
//          payload = String.format("{\"aps\":{\"sound\":{\"volume\":\"0.0\"},\"badge\":%d,\"alert\":{\"loc-key\":\"APN_Message\"}}}", depth);
//        }
//      } else {
//        payload = String.format("{\"aps\":{\"sound\":\"default\",\"badge\":%d,\"alert\":{\"title\":\"Difft\",\"subtitle\":\"\",\"body\":\"%s\"}}}", depth, body);
//        if (silent) {
//          payload = String.format("{\"aps\":{\"sound\":{\"volume\":\"0.0\"},\"badge\":%d,\"alert\":{\"title\":\"Difft\",\"subtitle\":\"\",\"body\":\"%s\"}}}", depth, body);
//        }
//      }
  }

  private String getCollapseId(Notification notification,Envelope outgoingMessage){
    if(notification!=null&&notification.getArgs()!=null&&!StringUtil.isEmpty(notification.getArgs().getCollapseId())) {
      return notification.getArgs().getCollapseId();
    }else{
      return outgoingMessage.getSource()+"_"+outgoingMessage.getSourceDevice()+"_"+outgoingMessage.getTimestamp();
    }
  }

  static public String getEncPushMsg(Envelope outgoingMessage,Device device){
    String msg=null;
    try {
      EncryptedOutgoingMessage encryptedMessage = null;
      encryptedMessage = new EncryptedOutgoingMessage(outgoingMessage, device.getSignalingKey());
      Optional<byte[]> body = Optional.ofNullable(encryptedMessage.toByteArray());
      msg= Base64.getEncoder().encodeToString(body.get());
    } catch (CryptoEncodingException e) {
      e.printStackTrace();
    }
    return msg;
  }
  private String bulidPayload(Account account, int depth, boolean silent,Envelope outgoingMessage,Notification notification,Device device){
    String msg=getEncPushMsg(outgoingMessage,device);

    int notificationType=-1;
    if(notification!=null) {
      notificationType = notification.getType();
    }
    StringBuffer body = new StringBuffer();
    String lockey="APN_Message";
    List<String> logArgs=null;
    boolean isNotification=true;
    boolean isSilent=silent;
    int volume=1;
    int critical=0;
    String threadId=null;
    String name="default";
    String title="";
    Optional<Account> a=null;
    if(notification!=null&&notification.getArgs()!=null&&notification.getArgs().getSource()!=null){
      a=Optional.of(notification.getArgs().getSource());
    }else {
      a=accountsManager.get(outgoingMessage.getSource());
    }
    String sourceName=outgoingMessage.getSource();
    if(a!=null&&a.isPresent()&&!StringUtil.isEmpty(a.get().getPlainName())){
      sourceName=a.get().getPlainName();
    }
    if (notificationType >=Notification.Type.GROUP_NORMAL.getCode() && notificationType<= Notification.Type.GROUP_UPDATE_ANNOUNCEMENT.getCode()) {
      String gid = notification.getArgs().getGid();
      String gname = notification.getArgs().getGname();
      threadId=gname;
      if (!StringUtil.isEmpty(gid)||!StringUtil.isEmpty(gname)) {
        Group group =null;
        GroupMember groupMember = null;
        if(!StringUtil.isEmpty(gid)) {
          threadId=gid;
          group = groupManagerWithTransaction.getGroup(gid);
          try {
            groupMember = groupManagerWithTransaction.getMemberWithPermissionCheck(account, gid, account.getNumber());
          } catch (NoPermissionException e) {
            e.printStackTrace();
          } catch (NoSuchGroupException e) {
            e.printStackTrace();
          }
        }
        boolean oldGroup=false;
        if((StringUtil.isEmpty(gid)||group==null)&&!StringUtil.isEmpty(gname)){
          oldGroup=true;
        }
        if((!StringUtil.isEmpty(gid)&&group!=null&&groupMember!=null)||oldGroup){
          String groupName=gname;
          if(group!=null){
            groupName= group.getName();
          }
          title=groupName;
          logArgs = new ArrayList<String>();
          logArgs.add(groupName);
          logArgs.add(sourceName);
          int notificationConfig=this.getNotificationConfig(account,groupMember);
          if (Notification.Type.GROUP_MENTIONS_DESTINATION.getCode() == notificationType) {//@DESTINATION
            if ((groupMember != null && notificationConfig <= GroupMembersTable.NOTIFICATION.MENTION.ordinal())||oldGroup) {
              body.append(String.format(Notification.NotificationTermTemplate.GROUP_MENTIONS_DESTINATION, sourceName));
              isSilent = false;
              critical = 1;
              lockey = Notification.Type.GROUP_MENTIONS_DESTINATION.name();

            }else{
              isNotification=false;
            }
          } else if (Notification.Type.GROUP_MENTIONS_OTHER.getCode() == notificationType) {//@other
            if ((groupMember != null && notificationConfig <= GroupMembersTable.NOTIFICATION.ALL.ordinal())||oldGroup) {
              StringBuffer stringBuffer = new StringBuffer();
              if(StringUtil.isEmpty(notification.getArgs().getMentionedStr())) {
                List<String> mentionedPeople = notification.getArgs().getMentionedPersons();
                int i = 0;
                if (mentionedPeople != null && mentionedPeople.size() > 0) {
                  for (String number : mentionedPeople) {
                    if (StringUtil.isEmpty(number)) {
                      continue;
                    }
                    i++;
                    if (i > 2) {
                      stringBuffer = stringBuffer.append("...");
                      break;
                    }

                    Optional<Account> mentionedAccount = accountsManager.get(number);
                    if (mentionedAccount.isPresent() && !StringUtil.isEmpty(mentionedAccount.get().getPlainName())) {
                      stringBuffer.append(mentionedAccount.get().getPlainName()).append(",");
                    } else {
                      stringBuffer.append(number).append(",");
                    }
                  }
                }
                if (0 < i && i <= 2) {
                  stringBuffer = stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());
                }
              }else{
                stringBuffer=stringBuffer.append(notification.getArgs().getMentionedStr());
              }
              body.append(String.format(Notification.NotificationTermTemplate.GROUP_MENTIONS_OTHER, sourceName, stringBuffer.toString()));
              isSilent = false;
              critical = 1;
              lockey = Notification.Type.GROUP_MENTIONS_OTHER.name();
              logArgs.add(stringBuffer.toString());
            }else{
              isNotification=false;
            }
          } else if (Notification.Type.GROUP_MENTIONS_ALL.getCode() == notificationType) {//@all
            if ((groupMember != null && notificationConfig <= GroupMembersTable.NOTIFICATION.MENTION.ordinal())||oldGroup) {
              body.append(String.format(Notification.NotificationTermTemplate.GROUP_MENTIONS_ALL, sourceName));
              isSilent = false;
              critical = 1;
              lockey = Notification.Type.GROUP_MENTIONS_ALL.name();
            }else{
              isNotification=false;
              }
          } else if (Notification.Type.GROUP_REPLY_DESTINATION.getCode() == notificationType) { //回复
            if ((groupMember != null && notificationConfig <= GroupMembersTable.NOTIFICATION.MENTION.ordinal())||oldGroup) {
              body.append(String.format(Notification.NotificationTermTemplate.GROUP_REPLY_DESTINATION, sourceName));
              isSilent = false;
              critical = 1;
              lockey = Notification.Type.GROUP_REPLY_DESTINATION.name();
            }else{
              isNotification=false;
            }
          }else if (Notification.Type.GROUP_REPLY_OTHER.getCode() == notificationType) { //回复
            if ((groupMember != null && notificationConfig <= GroupMembersTable.NOTIFICATION.ALL.ordinal())||oldGroup) {
              StringBuffer stringBuffer = new StringBuffer();
              if(StringUtil.isEmpty(notification.getArgs().getMentionedStr())) {
                List<String> mentionedPeople = notification.getArgs().getMentionedPersons();
                int i = 0;
                if (mentionedPeople != null && mentionedPeople.size() > 0) {
                  for (String number : mentionedPeople) {
                    if (StringUtil.isEmpty(number)) {
                      continue;
                    }
                    i++;
                    if (i > 2) {
                      stringBuffer = stringBuffer.append("...");
                      break;
                    }

                    Optional<Account> mentionedAccount = accountsManager.get(number);
                    if (mentionedAccount.isPresent() && !StringUtil.isEmpty(mentionedAccount.get().getPlainName())) {
                      stringBuffer.append(mentionedAccount.get().getPlainName()).append(",");
                    } else {
                      stringBuffer.append(number).append(",");
                    }
                  }
                }
                if (0 < i && i <= 2) {
                  stringBuffer = stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());
                }
              }else{
                stringBuffer=stringBuffer.append(notification.getArgs().getMentionedStr());
              }
              body.append(String.format(Notification.NotificationTermTemplate.GROUP_REPLY_OTHER, sourceName,stringBuffer.toString()));
              isSilent = false;
              critical = 1;
              lockey = Notification.Type.GROUP_REPLY_OTHER.name();
              logArgs.add(stringBuffer.toString());
            }else{
              isNotification=false;
            }
          } else if (Notification.Type.GROUP_CALL.getCode() == notificationType) {//call
            body.append(String.format(Notification.NotificationTermTemplate.GROUP_CALL,sourceName));
            isSilent=false;
            critical=1;
            name="CallPassive.wav";
            lockey=Notification.Type.GROUP_CALL.name();
          } else if (Notification.Type.GROUP_FILE.getCode() == notificationType){
            if ((groupMember != null && notificationConfig <= GroupMembersTable.NOTIFICATION.ALL.ordinal())||oldGroup) {
              body.append(String.format(Notification.NotificationTermTemplate.GROUP_FILE, sourceName));
              lockey = Notification.Type.GROUP_FILE.name();
//              logArgs=new ArrayList<String>();
//              logArgs.add(groupName);
//              logArgs.add(sourceName);
            }else{
              isNotification=false;
            }
          } else if (Notification.Type.GROUP_CALL_COLSE.getCode() == notificationType){
            if ((groupMember != null && notificationConfig <= GroupMembersTable.NOTIFICATION.ALL.ordinal())||oldGroup) {
              body.append(String.format(Notification.NotificationTermTemplate.GROUP_CALL_COLSE, sourceName));
              isSilent = false;
              critical = 1;
              lockey = Notification.Type.GROUP_CALL_COLSE.name();
            }else{
              isNotification=false;
            }
          }  else if (Notification.Type.GROUP_CALL_OVER.getCode() == notificationType) {
            if ((groupMember != null && notificationConfig <= GroupMembersTable.NOTIFICATION.ALL.ordinal()) || oldGroup) {
              body.append(Notification.NotificationTermTemplate.GROUP_CALL_OVER);
              isSilent = false;
              critical = 1;
              lockey = Notification.Type.GROUP_CALL_OVER.name();
              logArgs.remove(sourceName);
            } else {
              isNotification = false;
            }
          }else if (Notification.Type.GROUP_ADD_ANNOUNCEMENT.getCode() == notificationType){
            if ((groupMember != null && notificationConfig <= GroupMembersTable.NOTIFICATION.ALL.ordinal())||oldGroup) {
              body.append(String.format(Notification.NotificationTermTemplate.GROUP_ADD_ANNOUNCEMENT, sourceName));
              isSilent = false;
              critical = 1;
              lockey = Notification.Type.GROUP_ADD_ANNOUNCEMENT.name();
            }else{
              isNotification=false;
            }
          } else if (Notification.Type.GROUP_UPDATE_ANNOUNCEMENT.getCode() == notificationType){
            if ((groupMember != null && notificationConfig <= GroupMembersTable.NOTIFICATION.ALL.ordinal())||oldGroup) {
              body.append(String.format(Notification.NotificationTermTemplate.GROUP_UPDATE_ANNOUNCEMENT, sourceName));
              isSilent = false;
              critical = 1;
              lockey = Notification.Type.GROUP_UPDATE_ANNOUNCEMENT.name();
            }else{
              isNotification=false;
            }
          }else{
            if ((groupMember != null && notificationConfig <= GroupMembersTable.NOTIFICATION.ALL.ordinal())||oldGroup) {
              lockey=Notification.Type.GROUP_NORMAL.name();
              body.append(String.format(Notification.NotificationTermTemplate.GROUP_NORMAL,sourceName));
            }else{
              isNotification=false;
            }
          }
        }
      }
    } else if (notificationType >= Notification.Type.PERSONAL_NORMAL.getCode() && notificationType <= Notification.Type.PERSONAL_CALL_TIMEOUT.getCode()&&a!=null) {
      threadId=outgoingMessage.getSource();
      logArgs=new ArrayList<String>();
      logArgs.add(sourceName);
      if (Notification.Type.PERSONAL_CALL.getCode()== notificationType) {//1v1 call
        isSilent=false;
        critical=1;
        name="calling.caf";
        lockey=Notification.Type.PERSONAL_CALL.name();
        body.append(String.format(Notification.NotificationTermTemplate.PERSONAL_CALL,sourceName));
      }else if(Notification.Type.PERSONAL_FILE.getCode()== notificationType) {//1v1 file
        lockey=Notification.Type.PERSONAL_FILE.name();
        body.append(String.format(Notification.NotificationTermTemplate.PERSONAL_FILE,sourceName));
      }else if(Notification.Type.PERSONAL_REPLY.getCode()== notificationType) {//1v1 reply
        lockey = Notification.Type.PERSONAL_REPLY.name();
        body.append(String.format(Notification.NotificationTermTemplate.PERSONAL_REPLY, sourceName));
      }else if(Notification.Type.PERSONAL_CALL_CANCEL.getCode()== notificationType) {//1v1 file
        isSilent=false;
        critical=1;
        lockey=Notification.Type.PERSONAL_CALL_CANCEL.name();
        body.append(String.format(Notification.NotificationTermTemplate.PERSONAL_CALL_CANCEL,sourceName));
      }else if(Notification.Type.PERSONAL_CALL_TIMEOUT.getCode()== notificationType) {//1v1 file
        isSilent=false;
        critical=1;
        lockey=Notification.Type.PERSONAL_CALL_TIMEOUT.name();
        body.append(String.format(Notification.NotificationTermTemplate.PERSONAL_CALL_TIMEOUT,sourceName));
      }else{
        lockey=Notification.Type.PERSONAL_NORMAL.name();
        body.append(String.format(Notification.NotificationTermTemplate.PERSONAL_NORMAL,sourceName));
      }
    }else if (notificationType==Notification.Type.RECALL_MSG.getCode()||notificationType==Notification.Type.RECALL_MENTIONS_MSG.getCode()||notificationType==Notification.Type.TASK_MSG.getCode()) {
      String gid = notification.getArgs().getGid();
      logArgs = new ArrayList<String>();
      if (!StringUtil.isEmpty(gid)) {
        Group group = null;
        GroupMember groupMember = null;
        if (!StringUtil.isEmpty(gid)) {
          threadId = gid;
          group = groupManagerWithTransaction.getGroup(gid);
          try {
            groupMember = groupManagerWithTransaction.getMemberWithPermissionCheck(account, gid, account.getNumber());
          } catch (NoPermissionException e) {
            e.printStackTrace();
          } catch (NoSuchGroupException e) {
            e.printStackTrace();
          }
        }
        if (group != null && groupMember != null) {
          title = group.getName();
          logArgs.add(title);
          if ((notificationType==Notification.Type.RECALL_MSG.getCode()||notificationType==Notification.Type.TASK_MSG.getCode())&&this.getNotificationConfig(account,groupMember) > GroupMembersTable.NOTIFICATION.ALL.ordinal()) {
            isNotification = false;
          }
          if (notificationType==Notification.Type.RECALL_MENTIONS_MSG.getCode()&&this.getNotificationConfig(account,groupMember) > GroupMembersTable.NOTIFICATION.MENTION.ordinal()) {
            isNotification = false;
          }
        } else {
          isNotification = false;
        }
      }
      logArgs.add(sourceName);
      if(notificationType==Notification.Type.RECALL_MSG.getCode()||notificationType==Notification.Type.RECALL_MENTIONS_MSG.getCode()) {
        lockey = Notification.Type.RECALL_MSG.name();
        body.append(String.format(Notification.NotificationTermTemplate.RECALL_MSG, sourceName));
      }
      if(notificationType==Notification.Type.TASK_MSG.getCode()){
        lockey = Notification.Type.TASK_MSG.name();
        body.append(String.format(Notification.NotificationTermTemplate.TASK_MSG, sourceName));
      }
    }

    Notification.Type type=Notification.Type.fromCode(notificationType);
    if(isNotification&&body.length()==0&&type!=null){
      lockey=type.name();
      if(type.name().indexOf("GROUP")!=-1){
        String gid = notification.getArgs()==null?null:notification.getArgs().getGid();
        if (!StringUtil.isEmpty(gid)) {
          Group group = null;
          GroupMember groupMember = null;
          if (!StringUtil.isEmpty(gid)) {
            group = groupManagerWithTransaction.getGroup(gid);
            try {
              groupMember = groupManagerWithTransaction.getMemberWithPermissionCheck(account, gid, account.getNumber());
            } catch (NoPermissionException e) {
              e.printStackTrace();
            } catch (NoSuchGroupException e) {
              e.printStackTrace();
            }
          }
          if (group != null && groupMember != null) {
            if (this.getNotificationConfig(account,groupMember) <= GroupMembersTable.NOTIFICATION.ALL.ordinal()) {
              logArgs=new ArrayList<String>();
              title = group.getName();
              logArgs.add(title);
              logArgs.add(sourceName);
              body.append(String.format(type.getTemplate(), sourceName));
            }else{
              isNotification = false;
            }
          } else {
            isNotification = false;
          }

        }
      }else{
        logArgs=new ArrayList<String>();
        logArgs.add(sourceName);
        body.append(String.format(type.getTemplate(), sourceName));
      }
    }

    if(isNotification) {
      JsonObject payloadJson=new JsonObject();
      JsonObject apsJson = new JsonObject();
      JsonObject alertJson = new JsonObject();
      JsonArray logArgsJson = new JsonArray();
      apsJson.addProperty("badge", depth);
      //按会话折叠apn，目前不启用
//      if(!StringUtil.isEmpty(threadId)){
//        apsJson.addProperty("thread-id" , threadId);
//      }
      apsJson.add("alert", alertJson);
      if (logArgs != null) {
        for (String arg : logArgs) {
          logArgsJson.add(arg);
        }
      }
      alertJson.addProperty("loc-key", lockey);
      alertJson.add("loc-args", logArgsJson);
      alertJson.addProperty("title", title);
      if (!StringUtil.isEmpty(body.toString())) {
        alertJson.addProperty("body", body.toString());
      }

      JsonObject soundJson = new JsonObject();
      if (isSilent) {
        soundJson.addProperty("volume", "0");
      }else {
        soundJson.addProperty("volume", volume);
      }
      if(!name.equals("default")) {
        soundJson.addProperty("name", name);
      }
      soundJson.addProperty("critical", critical+"");
      apsJson.add("sound", soundJson);
      if(notification!=null&&notification.getArgs()!=null&&!StringUtil.isEmpty(notification.getArgs().getPassthrough())) {
        apsJson.addProperty("passthrough", notification.getArgs().getPassthrough());
      }
      if(!StringUtil.isEmpty(msg)&&msg.length()<3596){
        apsJson.addProperty("msg",msg);
      }
      apsJson.addProperty("mutable-content",1);

      payloadJson.add("aps",apsJson);
      return payloadJson.toString();
    }
    return null;
  }

  private void sendPushNotification(Account account, Device device, int depth, boolean newOnly, boolean silent) {
    ApnMessage apnMessage;

    if (newOnly && RedisOperation.unchecked(() -> apnFallbackManager.isScheduled(account, device))) {
      return;
    }

//    if (!Util.isEmpty(device.getVoipApnId())) {
//      apnMessage = new ApnMessage(device.getVoipApnId(), account.getNumber(), device.getId(), true);
//      RedisOperation.unchecked(() -> apnFallbackManager.schedule(account, device));
//    } else {
//      apnMessage = new ApnMessage(device.getApnId(), account.getNumber(), device.getId(), false);
//    }
    String payload = String.format("{\"aps\":{\"sound\":\"default\",\"badge\":%d,\"alert\":{\"loc-key\":\"APN_Message\"}}}", depth);
    if (silent) {
      payload = String.format("{\"aps\":{\"sound\":{\"volume\":\"0.0\"},\"badge\":%d,\"alert\":{\"loc-key\":\"APN_Message\"}}}", depth);
    }

    apnMessage = new ApnMessage(device.getApnId(), account.getNumber(), device.getId(), payload, false);

    apnSender.sendMessage(device.getUserAgent(),apnMessage);
  }

  private int getNotificationConfig(Account account,GroupMember groupMember){
    if(groupMember==null){
      return -1;
    }
    if(groupMember.isUseGlobal()) {
      return accountsManager.getGlobalNotification(account);
    }
    return groupMember.getNotification();
  }

  private boolean sendWebSocketMessageForGroup(Account account, Device device, Envelope outgoingMessage, boolean notify,IncomingMessageBase incomingMessage,Notification notification)
  {
    Timer.Context context=sendWebsocketMessageMeterForGroup.time();
    try {
      return webSocketSender.sendMessageForGroup(account, device, outgoingMessage, notify, incomingMessage, notification);
    }finally {
      context.stop();
    }
  }

  private void sendWebSocketMessage(Account account, Device device, Envelope outgoingMessage, boolean notify, boolean silent,Notification notification,boolean readReceipt,IncomingMessageBase incomingMessage,long shardMsgId)
  {
    Timer.Context context=sendWebsocketMessageMeter.time();
    try {
      Timer.Context context2=webSocketSenderSendMessageMeter.time();
      DeliveryStatus deliveryStatus=null;
      try {
        deliveryStatus = webSocketSender.sendMessage(account, device, outgoingMessage, WebsocketSender.Type.WEB, notify, notification,readReceipt,incomingMessage,shardMsgId);
      }catch (Exception e){
        logger.error("sendWebSocketMessage error!",e);
      }finally {
        context2.stop();
      }
      if (!notify) {
        return;
      }

      // push notification if the device is offline
      if (outgoingMessage.hasSource()) {
        logger.debug(String.format("sendWebSocketMessage() %s -> %s, type: %s", outgoingMessage.getSource(), account.getNumber(), outgoingMessage.getType().toString()));
      }
      if (deliveryStatus!=null&&!deliveryStatus.isDelivered() && // websocket failed
              !account.getNumber().equalsIgnoreCase(outgoingMessage.getSource()) && // not self, avoid multi devices sync messages
              outgoingMessage.getType() != Envelope.Type.RECEIPT
      ) {
        // TODO: follow the parameter silent
        String addr = account.getNumber();
        mThirdPartyPush.push(account, "Title", "New Message");
      }
    }finally {
      context.stop();
    }
  }

  @Override
  public void start() throws Exception {
    apnSender.start();
//    gcmSender.start();
  }

  @Override
  public void stop() throws Exception {
    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.MINUTES);

    apnSender.stop();
//    gcmSender.stop();
  }
  public DepthStringLookup getDepthStringLookup(int depth){
    return new DepthStringLookup(depth);
  }

  @Override
  public void notificationDelayCallback(String type, String obj) throws JsonProcessingException, InvalidProtocolBufferException {
    // 解析数据
    final SendNotificationParam sendNotificationParam = SendNotificationParam.fromJson(obj);
    final Envelope outgoingMessage = sendNotificationParam.getOutgoingMessage();
    final long notifySequenceId = outgoingMessage.getNotifySequenceId();
    // 检查是否发送成功
    //boolean shouldNotification = false;
    final IncomingMessage.Conversation conversation = sendNotificationParam.getConversation();
    if (type.equals(DelayNotification.checkSent)) {
      final String checkSentKey = desktopSuccessSendKey(sendNotificationParam.getUid(),
              conversation.getId(), notifySequenceId);
      final String isSuccess = memCache.get(checkSentKey);
      logger.info("in notificationDelayCallback,checkSentKey:{}, isSuccess:{}", checkSentKey, isSuccess);
      if (isSuccess != null && isSuccess.equals("1")) {
        DelayNotification.addNotification(DelayNotification.checkRead, obj, 7000); // 7秒后检查是否已读
        return;
      }
    } else {
      // 检查是否已读
      final ReadReceiptsManager readReceiptsManager = messagesManager.getReadReceiptsManager();
      final String conversationID = conversation.getType().equals(IncomingMessage.Conversation.Type.GROUP)?
              conversation.getId():
              Joiner.on(":").join(Ordering.natural().sortedCopy(Lists.newArrayList(conversation.getId(), sendNotificationParam.getUid())));
      ReadReceipt readReceipt=readReceiptsManager.getMaxReadReceipt(sendNotificationParam.getUid(), conversationID);
      if (readReceipt != null && readReceipt.getMaxServerTimestamp() >= outgoingMessage.getSystemShowTimestamp()) { // 已读,先用ServerTimestamp
        logger.info("in notificationDelayCallback, uid:{},has read conversation:{},getSystemShowTimestamp:{},getMaxServerTimestamp:{}",
                sendNotificationParam.getUid(), conversationID,
                outgoingMessage.getSystemShowTimestamp(),readReceipt.getMaxServerTimestamp());
        return;
      } else if (readReceipt != null) {
        logger.info("in notificationDelayCallback, uid:{},not read conversation:{},getSystemShowTimestamp:{},getMaxServerTimestamp:{}",
                sendNotificationParam.getUid(), conversationID,
                outgoingMessage.getSystemShowTimestamp(),readReceipt.getMaxServerTimestamp());
      } else {
        logger.warn("in notificationDelayCallback, uid:{},not found when read conversation:{}",
                sendNotificationParam.getUid(), conversationID);
      }
      // 发送notification
      //shouldNotification = true;
    }
    //if (shouldNotification)return;
    // 发送notification
    final Optional<Account> accountOptional = accountsManager.get(sendNotificationParam.getUid());
    logger.info("in notificationDelayCallback,type:{},try send notification, exists {} account?:{}",
            type, sendNotificationParam.getUid(), accountOptional.isPresent());
    if (!accountOptional.isPresent()){
      logger.error("in notificationDelayCallback account not found, uid: {}", sendNotificationParam.getUid());
      return;
    }
    final Account account = accountOptional.get();
    final Optional<Device> optionalDevice = account.getDevice(sendNotificationParam.getDid());
    if (!optionalDevice.isPresent()){
      logger.error("in notificationDelayCallback optionalDevice not found, uid: {}, did: {}", sendNotificationParam.getUid(), sendNotificationParam.getDid());
      return;
    }
    sendPushNotification(account,optionalDevice.get(),sendNotificationParam.getDepth(),
            sendNotificationParam.isNewOnly(),sendNotificationParam.isSilent(), outgoingMessage, sendNotificationParam.getNotification());
  }

  public void setDesktopSuccessSend(String uid, String conversationID, long notifySequenceId) {
    final String checkSentKey = desktopSuccessSendKey(uid, conversationID, notifySequenceId);
    final String result = memCache.setex(checkSentKey, 20, "1");
    logger.info("setDesktopSuccessSend, checkSentKey:{}, result:{}", checkSentKey, result);
    //
  }
  private String desktopSuccessSendKey(String uid, String conversationID, long notifySequenceId) {
    return "destktopsendmsg" + uid +":"+ conversationID+ ":" + notifySequenceId;
  }

  private String recallNotificationKey(String uid, String conversationID) {
    return "recallNotification" + uid +":"+ conversationID;
  }

  public void recallNotification(Account account, Device device, IncomingMessageBase incomingMessage, ReadReceipt readReceipt){
    try {
      IncomingMessage.Conversation conversation = incomingMessage.getConversation();
      final String recallPosKey = recallNotificationKey(account.getNumber(), conversation.getId());
      if (device.isMaster()) { // 主设备,仅仅更新标记
        logger.info("in recallNotification , master device,recallPosKey:{},update MaxServerTimestamp:{}, maxNotifySequenceId:{}",
                recallPosKey, readReceipt.getMaxServerTimestamp(), readReceipt.getMaxNotifySequenceId());
        memCache.set(recallPosKey, String.valueOf(readReceipt.getMaxServerTimestamp()));
        return;
      }
      // 查是否支持recall
      final String mobVersion = memCache.get(loggerFilter.cliVersionCacheKey(account.getNumber()));
      final boolean skip = mobVersion == null || mobVersion.compareTo(pushConfiguration.getApnRecallMinVer()) < 0;
      logger.info("in recallNotification mobVersion:{},skip:{}", mobVersion, skip);
      if(skip)
        return;
      // 查询已经recall的id
      final String recallPos = memCache.get(recallPosKey);
      if (recallPos != null && !recallPos.isEmpty() && readReceipt.getMaxServerTimestamp() <= Long.parseLong(recallPos)){
        logger.info("in recallNotification readReceipt.getMaxNotifySequenceId() <= Long.parseLong(recallPos), recallPosKey:{}, recallPos:{},maxServerTimestamp:{},getMaxNotifySequenceId:{}",
                recallPosKey,recallPos,readReceipt.getMaxServerTimestamp(), readReceipt.getMaxNotifySequenceId());
        return;
      }
      // 发送recall notification
      device = account.getMasterDevice().get();
      String payload = "{\"aps\": {\"alert\": {\"loc-key\": \"RECALL_NOTIFICATION\", \"loc-args\": [" +
              "\""+conversation.getId()+"\","+
              readReceipt.getMaxServerTimestamp()+","+readReceipt.getMaxNotifySequenceId()+
              "]},\"mutable-content\":1}}";
      ApnMessage apnMessage = new ApnMessage(device.getApnId(), account.getNumber(), device.getId(), payload,
              false,conversation.getId()+readReceipt.getMaxServerTimestamp());
      //apnMessage.setBackground(false);
      logger.info("in recallNotification ,account:{}, payload:{}", account.getNumber(), payload);
      Device finalDevice = device;
      executor.execute(()->{
      apnSender.sendMessage(finalDevice.getUserAgent(),apnMessage);});

      //
      memCache.set(recallPosKey, String.valueOf(readReceipt.getMaxServerTimestamp()));
    } catch (Exception e) {
      logger.error("in recallNotification uid::{}, Exception", account.getNumber(), e);
    }
  }
  public class DepthStringLookup implements StringLookup {
    long depth;
    public DepthStringLookup(int depth){
      this.depth=depth;
    }
    @Override
    public String lookup(String number) {
      return depth + "";
    }
  }

  public String replacePayload(String payload,int depth){
    GroupManagerWithTransaction.GidStringLookup gidStringLookup=accountsManager.getGroupManager().getGidStringLookup();
    AccountsManager.AccountStringLookup accountStringLookup=accountsManager.getAccountStringLookup();
    DepthStringLookup depthStringLookup=getDepthStringLookup(depth);
    Map<String,StringLookup> stringLookupMap=new HashMap<>();
    stringLookupMap.put("gid",gidStringLookup);
    stringLookupMap.put("uid",accountStringLookup);
    stringLookupMap.put("badge",depthStringLookup);
    StringLookup stringLookup= StringLookupFactory.INSTANCE.interpolatorStringLookup(stringLookupMap,null,false);
    StringSubstitutor stringSubstitutor=new StringSubstitutor(stringLookup);
    return stringSubstitutor.replace(payload);
  }

}
