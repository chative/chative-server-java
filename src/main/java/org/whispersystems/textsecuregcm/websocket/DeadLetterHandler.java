package org.whispersystems.textsecuregcm.websocket;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.dispatch.DispatchChannel;
import org.whispersystems.textsecuregcm.entities.IncomingMessage;
import org.whispersystems.textsecuregcm.entities.MessageProtos.Envelope;
import org.whispersystems.textsecuregcm.storage.MessagesManager;
import org.whispersystems.textsecuregcm.storage.PubSubProtos.PubSubMessage;

public class DeadLetterHandler implements DispatchChannel {

  private final Logger logger = LoggerFactory.getLogger(DeadLetterHandler.class);

  private final MessagesManager messagesManager;

  public DeadLetterHandler(MessagesManager messagesManager) {
    this.messagesManager = messagesManager;
  }

  @Override
  public void onDispatchMessage(String channel, byte[] data) {
    try {
      logger.info("Handling dead letter to: " + channel);

      WebsocketAddress address       = new WebsocketAddress(channel);
      PubSubMessage    pubSubMessage = PubSubMessage.parseFrom(data);

      switch (pubSubMessage.getType().getNumber()) {
        case PubSubMessage.Type.DELIVER_VALUE:
          Envelope message = Envelope.parseFrom(pubSubMessage.getContent());
          String gid=null;
          if(pubSubMessage.hasNotification()&&pubSubMessage.getNotification()!=null&&
                  pubSubMessage.getNotification().hasArgs()&&pubSubMessage.getNotification().getArgs()!=null&&
                  pubSubMessage.getNotification().getArgs().hasGid()&&pubSubMessage.getNotification().getArgs().getGid()!=null) {
            gid=pubSubMessage.getNotification().getArgs().getGid();
          }
          String realDestination=address.getNumber();
          if(message.getMsgType()==Envelope.MsgType.MSG_SYNC||message.getMsgType()==Envelope.MsgType.MSG_SYNC_NORMAL||message.getMsgType()==Envelope.MsgType.MSG_SYNC_READ_RECEIPT){
            if(pubSubMessage.hasConversation()&&pubSubMessage.getConversation()!=null){
              IncomingMessage.Conversation conversation=new IncomingMessage.Conversation();
              conversation.setNumber(pubSubMessage.getConversation().getNumber());
              conversation.setGid(pubSubMessage.getConversation().getGid());
              if(conversation.getType()==IncomingMessage.Conversation.Type.GROUP){
                gid=conversation.getGid();
              }
              if(conversation.getType()==IncomingMessage.Conversation.Type.PRIVATE){
                realDestination=conversation.getNumber();
              }
            }
          }
          IncomingMessage.RealSource realSource=null;
          if(message.getMsgType()==Envelope.MsgType.MSG_SYNC_NORMAL&&pubSubMessage.hasRealSource()&&pubSubMessage.getRealSource()!=null){
            realSource=new IncomingMessage.RealSource();
            realSource.setSource(pubSubMessage.getRealSource().getSource());
            realSource.setSourceDevice(pubSubMessage.getRealSource().getSourceDevice());
            realSource.setTimestamp(pubSubMessage.getRealSource().getTimestamp());
            realSource.setServerTimestamp(pubSubMessage.getRealSource().getServerTimestamp());
            realSource.setSequenceId(pubSubMessage.getRealSource().getSequenceId());
            realSource.setNotifySequenceId(pubSubMessage.getRealSource().getNotifySequenceId());
          }

          messagesManager.insert(address.getNumber(), address.getDeviceId(), message,pubSubMessage.getNotify(),pubSubMessage.getReadReceipt(),pubSubMessage.getMsgId(),gid,realDestination,realSource);
          break;
      }
    } catch (InvalidProtocolBufferException e) {
      logger.warn("Bad pubsub message", e);
    } catch (InvalidWebsocketAddressException e) {
      logger.warn("Invalid websocket address", e);
    }
  }

  @Override
  public void onDispatchSubscribed(String channel) {
    logger.warn("DeadLetterHandler subscription notice! " + channel);
  }

  @Override
  public void onDispatchUnsubscribed(String channel) {
    logger.warn("DeadLetterHandler unsubscribe notice! " + channel);
  }
}
