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

import com.google.protobuf.ByteString;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.distributedlock.DistributedLock;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.ProducerKafka;
import org.whispersystems.textsecuregcm.util.StringUtil;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static org.whispersystems.textsecuregcm.entities.MessageProtos.Envelope;

public class KafkaSender {
  private static final Logger logger = LoggerFactory.getLogger(KafkaSender.class);
  private final MessagesManager messagesManager;
  private final AccountsManager accountsManager;
  private final ProducerKafka producerKafka;
  private MemCache memCache = null;


  public KafkaSender(AccountsManager accountsManager,MessagesManager messagesManager, ProducerKafka producerKafka,MemCache memCache) {
    this.accountsManager=accountsManager;
    this.messagesManager = messagesManager;
    this.producerKafka=producerKafka;
    this.memCache=memCache;
  }

  public void sendMessage(Account account, Device device, Envelope message,final Optional<StoredMessageInfo> storedMessageInfo,boolean requery) {
    byte[] msg=null;
    try {
      EncryptedOutgoingMessage encryptedMessage = null;
      encryptedMessage = new EncryptedOutgoingMessage(message, device.getSignalingKey());
      Optional<byte[]> body = Optional.ofNullable(encryptedMessage.toByteArray());
      msg= body.get();
      if(StringUtil.isEmpty(device.getReceiveChannel())){
        logger.error("KafkaSender.sendMessage error! device.receiveChannel is null!");
        return;
      }
      if(storedMessageInfo.isPresent()) {
        logger.info("kafkaSender.sendMessage sendStoreMsg! msgID:{},requery:{}",storedMessageInfo.get().id,requery);
      }
      Callback callback=new org.apache.kafka.clients.producer.Callback() {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
          if (null != e) {
            e.printStackTrace();
            logger.error("KafkaSender.sendMessage callback send to kafka error! isStore:{}, msgId:{} ,requery:{},msg:{}",storedMessageInfo.isPresent()?true:false,storedMessageInfo.isPresent()?storedMessageInfo.get().id:null,requery,e.getMessage());
            if(!storedMessageInfo.isPresent()) {
              storeMsg(account, device, message);
            }
          }else{
            logger.info("KafkaSender.sendMessage callback send to kafka success! isStore:{}, msgId:{} ,requery:{},msg:{}",storedMessageInfo.isPresent()?true:false,storedMessageInfo.isPresent()?storedMessageInfo.get().id:null,requery,null);
            new Thread(new Runnable() {
              @Override
              public void run() {
                processStoredMessagesToKafka(false);
              }
            }).start();
          }
        }
      };
//      try {
//        SendMessageLogHandler.SendMessageLog sendMessageLog=new SendMessageLogHandler.SendMessageLog(SendMessageLogHandler.SendMessageAction.SEND.getName(),account.getNumber(),device.getId(),message);
//        SendMessageLogHandler.send(sendMessageLog);
//      }catch (Exception e){
//        logger.error("KafkaSender.SendMessageLog error!"+e.getMessage());
//      }
      logger.info("before producerKafka.sendMessage getReceiveChannel:{}",device.getReceiveChannel());
      producerKafka.sendMessage(device.getReceiveChannel(),getKafkaMsgKey(account,device),msg,callback);
      logger.info("after producerKafka.sendMessage getReceiveChannel:{}",device.getReceiveChannel());
    } catch (CryptoEncodingException e) {
      logger.warn("Bad signaling key", e);
    } catch (Exception e){
      e.printStackTrace();
      if(!storedMessageInfo.isPresent()) {
        storeMsg(account, device, message);
      }
      logger.error("KafkaSender.sendMessage error!", e);
    }
  }

  public void sendMessageForStore(Account account, Device device, Envelope message,final Optional<StoredMessageInfo> storedMessageInfo,boolean requery) {
    byte[] msg=null;
    try {
      EncryptedOutgoingMessage encryptedMessage = null;
      encryptedMessage = new EncryptedOutgoingMessage(message, device.getSignalingKey());
      Optional<byte[]> body = Optional.ofNullable(encryptedMessage.toByteArray());
      msg= body.get();
      if(StringUtil.isEmpty(device.getReceiveChannel())){
        logger.error("KafkaSender.sendMessage error! device.receiveChannel is null!");
        return;
      }
      if(storedMessageInfo.isPresent()) {
        logger.info("kafkaSender.sendMessage sendStoreMsg! msgID:{},requery:{}",storedMessageInfo.get().id,requery);
      }
      Callback callback=new org.apache.kafka.clients.producer.Callback() {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
          if (null != e) {
            e.printStackTrace();
            logger.error("KafkaSender.sendMessage callback send to kafka error! isStore:{}, msgId:{} ,requery:{},msg:{}",storedMessageInfo.isPresent()?true:false,storedMessageInfo.isPresent()?storedMessageInfo.get().id:null,requery,e.getMessage());
            if(!storedMessageInfo.isPresent()) {
              storeMsg(account, device, message);
            }
          }else{
            logger.info("KafkaSender.sendMessage callback send to kafka success! isStore:{}, msgId:{} ,requery:{},msg:{}",storedMessageInfo.isPresent()?true:false,storedMessageInfo.isPresent()?storedMessageInfo.get().id:null,requery,null);
            if(storedMessageInfo.isPresent()){
              messagesManager.delete(account.getNumber(), device.getId(), storedMessageInfo.get().id, storedMessageInfo.get().cached);
            }
          }
        }
      };
//      try {
//        SendMessageLogHandler.SendMessageLog sendMessageLog=new SendMessageLogHandler.SendMessageLog(SendMessageLogHandler.SendMessageAction.SEND.getName(),account.getNumber(),device.getId(),message);
//        SendMessageLogHandler.send(sendMessageLog);
//      }catch (Exception e){
//        logger.error("KafkaSender.SendMessageLog error!"+e.getMessage());
//      }
      logger.info("before producerKafka.sendMessageForStore getReceiveChannel:{}",device.getReceiveChannel());
      producerKafka.sendMessage(device.getReceiveChannel(),getKafkaMsgKey(account,device),msg,callback);
      logger.info("after producerKafka.sendMessageForStore getReceiveChannel:{}",device.getReceiveChannel());
    } catch (CryptoEncodingException e) {
      logger.warn("Bad signaling key", e);
    } catch (Exception e){
      e.printStackTrace();
      if(!storedMessageInfo.isPresent()) {
        storeMsg(account, device, message);
      }
      logger.error("KafkaSender.sendMessage error!", e);
    }
  }

  private void storeMsg(Account account,Device device,Envelope message){
//    try {
//      SendMessageLogHandler.SendMessageLog sendMessageLog=new SendMessageLogHandler.SendMessageLog(SendMessageLogHandler.SendMessageAction.INSERT.getName(),account.getNumber(),device.getId(),message);
//      SendMessageLogHandler.send(sendMessageLog);
//    }catch (Exception exception){
//      logger.error("KafkaSender.SendMessageLog error!"+exception.getMessage());
//    }
    messagesManager.insertForReceiveType(account.getNumber(), device.getId(), message,false,device.getReceiveType());
    memCache.set("queryDBToKafka","true");
  }
  private String getKafkaMsgKey(Account account,Device device){
    return account.getNumber() + ":" +device.getId();
  }

  private void  processStoredMessagesToKafka(boolean requery){
    if(!memCache.exists("queryDBToKafka")){
      return;
    }
    if(!requery&&memCache.exists("queryDBToKafkaHandling")){
      return;
    }
    Lock processStoredMessagesLocker = DistributedLock.getLocker(new String[]{"KafkaSender.processStoredMessagesToKafka"},30, TimeUnit.SECONDS);

    if(processStoredMessagesLocker.tryLock()) {
      try {
        memCache.set("queryDBToKafkaHandling", "true");
        OutgoingMessageEntityList messages = messagesManager.getMessagesForKafka();
        Iterator<OutgoingMessageEntity> iterator = messages.getMessages().iterator();
        while (iterator.hasNext()) {
          OutgoingMessageEntityForKafka message = (OutgoingMessageEntityForKafka) iterator.next();
          Envelope.Builder builder = Envelope.newBuilder()
                  .setType(Envelope.Type.valueOf(message.getType()))
                  .setSourceDevice(message.getSourceDevice())
                  .setSource(message.getSource())
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
          Optional<Account> accountOptional = accountsManager.get(message.getDestination());
          boolean isDelete = false;
          if (!accountOptional.isPresent()) {
            isDelete = true;
            logger.warn("KafkaSender.processStoredMessagesToKafka destination is not exists! number:{}", message.getDestination());
          } else if (accountOptional.get().isinValid()) {
            isDelete = true;
            logger.warn("KafkaSender.processStoredMessagesToKafka destination is disabled! number:{}", message.getDestination());
          } else if (!accountOptional.get().getDevice(message.getDestinationDevice()).isPresent()) {
            isDelete = true;
            logger.warn("KafkaSender.processStoredMessagesToKafka destinationDevice is not exists! ! number:{} ,deviceId:{}", message.getDestination(), message.getDestination());
          }
          if (isDelete) {
            messagesManager.delete(message.getDestination(), message.getDestinationDevice(), message.getId(), message.isCached());
          } else {
            sendMessageForStore(accountOptional.get(), accountOptional.get().getDevice(message.getDestinationDevice()).get(), builder.build(), Optional.of(new StoredMessageInfo(message.getId(), message.isCached())), !iterator.hasNext() && messages.hasMore());
            if(!iterator.hasNext() && messages.hasMore()){
              processStoredMessagesToKafka(true);
            }
          }
        }
        if (!messages.hasMore()) {
          memCache.remove("queryDBToKafka");
          memCache.remove("queryDBToKafkaHandling");
        }
      }catch (Exception e){
        memCache.remove("queryDBToKafkaHandling");
        logger.error("KafkaSender processStoredMessagesToKafka error! msg:{}"+e.getMessage());
      }finally {
        processStoredMessagesLocker.unlock();
      }
    }else {
      logger.info("KafkaSender.processStoredMessagesToKafka not get lock!");
    }
  }


  public void queueMessage(Account account, Device device, Envelope message,boolean notify) {
    messagesManager.insertForReceiveType(account.getNumber(), device.getId(), message,notify,device.getReceiveType());
//    try {
//      SendMessageLogHandler.SendMessageLog sendMessageLog=new SendMessageLogHandler.SendMessageLog(SendMessageLogHandler.SendMessageAction.REQUEUE.getName(),account.getNumber(),device.getId(),message);
//      SendMessageLogHandler.send(sendMessageLog);
//    }catch (Exception e){
//      logger.error("SendMessageLog error!"+e.getMessage());
//    }

  }

  private static class StoredMessageInfo {
    private final long id;
    private final boolean cached;

    private StoredMessageInfo(long id, boolean cached) {
      this.id = id;
      this.cached = cached;
    }
  }
}
