package org.whispersystems.textsecuregcm.websocket;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.dispatch.DispatchChannel;
import org.whispersystems.textsecuregcm.controllers.MessageController;
import org.whispersystems.textsecuregcm.controllers.NoSuchUserException;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.push.NotPushRegisteredException;
import org.whispersystems.textsecuregcm.push.PushSender;
import org.whispersystems.textsecuregcm.push.ReceiptSender;
import org.whispersystems.textsecuregcm.push.TransientPushFailureException;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.Constants;
import org.whispersystems.textsecuregcm.util.StringUtil;
import org.whispersystems.websocket.WebSocketClient;
import org.whispersystems.websocket.messages.WebSocketResponseMessage;
import org.whispersystems.websocket.messages.protobuf.ProtobufWebSocketMessageFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.codahale.metrics.MetricRegistry.name;
import static org.whispersystems.textsecuregcm.entities.MessageProtos.Envelope;
import static org.whispersystems.textsecuregcm.storage.PubSubProtos.PubSubMessage;

public class WebSocketConnection implements DispatchChannel {

    private static final MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME);
    public static final Histogram messageTime = metricRegistry.histogram(name(MessageController.class, "message_delivery_duration"));

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConnection.class);

    private final ReceiptSender receiptSender;
    private final PushSender pushSender;
    private final MessagesManager messagesManager;

    private final Account account;
    private final Device device;
    private final WebSocketClient client;
    private final String connectionId;
    private final ConcurrentMap<String, Boolean> concurrentHashMap = new ConcurrentHashMap<String, Boolean>();

    public WebSocketConnection(
            PushSender pushSender,
            ReceiptSender receiptSender,
            MessagesManager messagesManager,
            Account account,
            Device device,
            WebSocketClient client,
            String connectionId) {
        this.pushSender = pushSender;
        this.receiptSender = receiptSender;
        this.messagesManager = messagesManager;
        this.account = account;
        this.device = device;
        this.client = client;
        this.connectionId = connectionId;
    }

    @Override
    public void onDispatchMessage(String channel, byte[] message) {
        try {
            PubSubMessage pubSubMessage = PubSubMessage.parseFrom(message);

            switch (pubSubMessage.getType().getNumber()) {
                case PubSubMessage.Type.QUERY_DB_VALUE:
                    processStoredMessages();
//                    processStoredMessagesForSharding();
                    break;
                case PubSubMessage.Type.DELIVER_VALUE:
                    sendMessage(Envelope.parseFrom(pubSubMessage.getContent()), Optional.empty(), false,pubSubMessage.getNotify(),pubSubMessage.getNotification(),pubSubMessage.getReadReceipt(),pubSubMessage.getMsgId(),pubSubMessage.getConversation(),pubSubMessage.getRealSource());
                    break;
                case PubSubMessage.Type.CONNECTED_VALUE:
                    if (pubSubMessage.hasContent() && !new String(pubSubMessage.getContent().toByteArray()).equals(connectionId)) {
                        client.hardDisconnectQuietly();
                    }
                    break;
                case PubSubMessage.Type.CLOSE_VALUE:
                    client.hardDisconnectQuietly();// 强制关闭
                    logger.info("CLOSE_VALUE channel:{},connectionId:{},Number:{},deviceID:{}",
                            channel, connectionId, account.getNumber(), device.getId());
                default:
                    logger.warn("Unknown pubsub message: " + pubSubMessage.getType().getNumber());
            }
        } catch (InvalidProtocolBufferException e) {
            logger.warn("Protobuf parse error", e);
        }
    }

    @Override
    public void onDispatchUnsubscribed(String channel) {
        client.close(1000, "OK");
    }

    public void onDispatchSubscribed(String channel) {
        processConversationMessages();
//        processStoredMessages();
        //processStoredMessagesForSharding();
    }

    private void sendMessage(final Envelope message,
                             final Optional<StoredMessageInfo> storedMessageInfo,
                             final boolean requery, boolean notify,PubSubProtos.Notification notification,boolean readReceipt,long msgId,PubSubProtos.Conversation conversation,PubSubProtos.RealSource realSource) {
        String key=storedMessageInfo.isPresent()?storedMessageInfo.get().key+"":"";
        try {
            logger.info("WebsocketConnection.sendMessage msgId:{}, pushConversation:{}",msgId,storedMessageInfo.isPresent()?storedMessageInfo.get().pushConversation==null?new Gson().toJson(storedMessageInfo.get().pushConversation) :null:null);
            EncryptedOutgoingMessage encryptedMessage = new EncryptedOutgoingMessage(message, device.getSignalingKey());
            Optional<byte[]> body = Optional.ofNullable(encryptedMessage.toByteArray());
            ListenableFuture<WebSocketResponseMessage> response = client.sendRequest("PUT", "/api/v1/message", null, Optional.of(body.get()));

            try {
                SendMessageLogHandler.SendMessageLog sendMessageLog=new SendMessageLogHandler.SendMessageLog(SendMessageLogHandler.SendMessageAction.SEND.getName(),account.getNumber(),device.getId(),message);
                logger.info("sendMessage.sendLog! connectionId:{}, msg.timeStamp:{},msg:{}",connectionId,message.getTimestamp(),sendMessageLog.toJsonObject().toString());
                SendMessageLogHandler.send(sendMessageLog);
            }catch (Exception e){
                logger.error("SendMessageLog error!"+e.getMessage());
            }
            Futures.addCallback(response, new FutureCallback<WebSocketResponseMessage>() {
                @Override
                public void onSuccess(@Nullable WebSocketResponseMessage response) {
                    concurrentHashMap.remove(key);

                    boolean isReceipt = message.getType() == Envelope.Type.RECEIPT;
                    boolean isNotify = message.getType() == Envelope.Type.NOTIFY;


                    try {
                        if (isSuccessResponse(response) && !isReceipt) {
                            if (!message.getSource().equals(account.getNumber()) && !readReceipt && conversation != null && !device.isMaster())
                            // 非同步消息,非已读,只关心次设备
                            {
                                final String conversationID = conversation.hasGid() ? conversation.getGid() :
                                        message.getSource();
                                pushSender.setDesktopSuccessSend(account.getNumber(), conversationID,
                                        message.getNotifySequenceId()); // 设置成功发送
                            }
                            messageTime.update(System.currentTimeMillis() - message.getTimestamp());
                        }
                    } catch (Exception e) {
                        try {
                            logger.error("setDesktopSuccessSend or messageTime.update error!,uid:{},getSource:{},getNotifySequenceId:{}",
                                    account.getNumber(), message.hasSource() ? message.getSource() : null,
                                    message.hasNotifySequenceId() ? message.getNotifySequenceId() : null,
                                    e);
                        } catch (Exception e1) {
                            logger.error("setDesktopSuccessSend or messageTime.update error!", e1);
                        }
                    }

                    sendLog(isSuccessResponse(response),response);
                    if (isSuccessResponse(response)) {
                        if (storedMessageInfo.isPresent()) {
                            if(storedMessageInfo.get().pushConversation!=null){
                                messagesManager.deleteForSharding(storedMessageInfo.get().pushConversation,storedMessageInfo.get().id,storedMessageInfo.get().cached);
                            }else {
                                messagesManager.delete(account.getNumber(), device.getId(), storedMessageInfo.get().id, storedMessageInfo.get().cached);
                            }
                        }
//                        if (!isReceipt && !isNotify) sendDeliveryReceiptFor(message);

                        if (requery) {
                            if (storedMessageInfo.isPresent()) {
                                if(storedMessageInfo.get().pushConversation!=null){
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            processStoredMessagesForSharding(storedMessageInfo.get().pushConversation);
                                        }
                                    }).start();
                                }else {
                                    processStoredMessages();
                                }
                            }
                        }
                    } else if (!isSuccessResponse(response) && !storedMessageInfo.isPresent()) {
                        requeueMessage(message,notify,notification,readReceipt,msgId,conversation,realSource);
                    }
                }

                @Override
                public void onFailure(@Nonnull Throwable throwable) {
                    concurrentHashMap.remove(key);
                    if (!storedMessageInfo.isPresent()) requeueMessage(message,notify,notification,readReceipt,msgId,conversation,realSource);
                    StringBuffer sb=new StringBuffer();
                    StackTraceElement[] trace = throwable.getStackTrace();
                    for (StackTraceElement traceElement : trace)
                        sb.append("\tat " + traceElement);

                    logger.warn("WebSocketConnection sendMsg onFailure! stack:",sb.toString());
                    sendLog(false,new ProtobufWebSocketMessageFactory().createResponse(1,-1,throwable.getMessage()==null?"throwable.getMessage is null":throwable.getMessage(),null, Optional.empty()).getResponseMessage());
                }

                private boolean isSuccessResponse(WebSocketResponseMessage response) {
                    boolean ok = response != null && response.getStatus() >= 200 && response.getStatus() < 300;
                    if (response != null && !ok) {
                        final Optional<byte[]> bodyRes = response.getBody();
                        bodyRes.ifPresent(body -> {
                            if (body.length > 0)
                                logger.warn("from {} to {},WebSocketConnection sendMsg, get Bad response:{}, body:{} ",
                                        message.getSource(), account.getNumber(),response.getStatus(), new String(body));
                        });
                    }
                    return ok;
                }
                private void sendLog(Boolean isSuccess,WebSocketResponseMessage response){
                    try {

                        SendMessageLogHandler.SendMessageLog sendMessageLog=new SendMessageLogHandler.SendMessageLog(SendMessageLogHandler.SendMessageAction.RESPONSE.getName(),account.getNumber(),device.getId(),message,isSuccess,response);
                        logger.info("sendMessage.sendLog! connectionId:{}, msg.timeStamp:{},msg:{}",connectionId,message.getTimestamp(),sendMessageLog.toJsonObject().toString());
                        SendMessageLogHandler.send(sendMessageLog);
                    }catch (Exception e){
                        logger.error("SendMessageLog error!"+e.getMessage());
                    }
                }

            }, MoreExecutors.directExecutor());
        } catch (CryptoEncodingException e) {
            concurrentHashMap.remove(key);
            logger.warn("Bad signaling key", e);
        }
    }

    private void sendConversationInfo(final ConversationPreviews.ConversationPreview conversationPreview,
                             final ConversationInfo conversationInfo,
                             final boolean requery) {
        try {
            logger.info("WebsocketConnection.sendConversationInfo , pushConversation:{}",new Gson().toJson(conversationPreview));
            Optional<byte[]> body = Optional.ofNullable(conversationPreview.toByteArray());
            ListenableFuture<WebSocketResponseMessage> response = client.sendRequest("PUT", "/api/v1/conversation", null, Optional.of(body.get()));
           String conversationLogId= UUID.randomUUID().toString().replace("-", "");
            try {
                SendMessageLogHandler.SendMessageLog sendMessageLog=new SendMessageLogHandler.SendMessageLog(SendMessageLogHandler.SendMessageAction.SEND.getName(),account.getNumber(),device.getId(),conversationPreview,conversationLogId);
                SendMessageLogHandler.send(sendMessageLog);
            }catch (Exception e){
                logger.error("WebSocketConnection sendConversationInfo log error!"+e.getMessage());
            }
            Futures.addCallback(response, new FutureCallback<WebSocketResponseMessage>() {
                @Override
                public void onSuccess(@Nullable WebSocketResponseMessage response) {
                    sendLog(isSuccessResponse(response),response);
                    if (isSuccessResponse(response)) {
                        if (conversationInfo!=null) {
                            messagesManager.updatePushConversationForSend(account.getNumber(),device.getId(),conversationInfo.conversation);
                        }
//                        if (!isReceipt && !isNotify) sendDeliveryReceiptFor(message);

                        if (requery) {
                            processConversationMessages();
                        }
                    }
                }

                @Override
                public void onFailure(@Nonnull Throwable throwable) {
                    StringBuffer sb=new StringBuffer();
                    StackTraceElement[] trace = throwable.getStackTrace();
                    for (StackTraceElement traceElement : trace)
                        sb.append("\tat " + traceElement);
                    sendLog(false,new ProtobufWebSocketMessageFactory().createResponse(1,-1,throwable.getMessage()==null?"throwable.getMessage is null":throwable.getMessage(),null, Optional.empty()).getResponseMessage());
                    logger.warn("WebSocketConnection sendConversationInfo onFailure! stack:",sb.toString());
                }

                private boolean isSuccessResponse(WebSocketResponseMessage response) {
                    return response != null && response.getStatus() >= 200 && response.getStatus() < 300;
                }
                private void sendLog(Boolean isSuccess,WebSocketResponseMessage response){
                    try {

                        SendMessageLogHandler.SendMessageLog sendMessageLog=new SendMessageLogHandler.SendMessageLog(SendMessageLogHandler.SendMessageAction.RESPONSE.getName(),account.getNumber(),device.getId(),null,isSuccess,response,conversationLogId);
//                        logger.warn("sendMessage.sendLog! connectionId:{}, msg.timeStamp:{},msg:{}",connectionId,message.getTimestamp(),sendMessageLog.toJsonObject().toString());
                        SendMessageLogHandler.send(sendMessageLog);
                    }catch (Exception e){
                        logger.error("WebSocketConnection sendConversationInfo log error!"+e.getMessage());
                    }
                }

            }, MoreExecutors.directExecutor());
        } catch (Exception e) {
            logger.warn("WebSocketConnection sendConversationInfo error!", e);
        }
    }

    private void requeueMessage(Envelope message,boolean notify,PubSubProtos.Notification notification,boolean readReceipt,long msgId,PubSubProtos.Conversation pspConversation,PubSubProtos.RealSource pspRealSource) {
        String gid=null;
        if(notification!=null&&notification.hasArgs()&&notification.getArgs()!=null&&notification.getArgs().hasGid()&&notification.getArgs().getGid()!=null) {
            gid=notification.getArgs().getGid();
        }
        String realDestination= account.getNumber();
        if(message.getMsgType()==Envelope.MsgType.MSG_SYNC||message.getMsgType()==Envelope.MsgType.MSG_SYNC_NORMAL||message.getMsgType()==Envelope.MsgType.MSG_SYNC_READ_RECEIPT){
            if(pspConversation!=null){
                IncomingMessage.Conversation conversation=new IncomingMessage.Conversation();
                conversation.setNumber(pspConversation.getNumber());
                conversation.setGid(pspConversation.getGid());
                if(conversation.getType()==IncomingMessage.Conversation.Type.GROUP){
                    gid=conversation.getGid();
                }
                if(conversation.getType()==IncomingMessage.Conversation.Type.PRIVATE){
                    realDestination=conversation.getNumber();
                }
            }
        }
        IncomingMessage.RealSource realSource=null;
        if(message.getMsgType()==Envelope.MsgType.MSG_SYNC_NORMAL&&pspRealSource!=null){
            realSource=new IncomingMessage.RealSource();
            realSource.setSource(pspRealSource.getSource());
            realSource.setSourceDevice(pspRealSource.getSourceDevice());
            realSource.setTimestamp(pspRealSource.getTimestamp());
            realSource.setServerTimestamp(pspRealSource.getServerTimestamp());
            realSource.setSequenceId(pspRealSource.getSequenceId());
            realSource.setNotifySequenceId(pspRealSource.getNotifySequenceId());
        }
        pushSender.getWebSocketSender().queueMessage(account, device, message,notify,readReceipt,msgId,gid,realDestination,realSource);

        try {
            if(notify) {
                pushSender.sendQueuedNotification(account, device, message,notification,pspConversation);
            }
        } catch (NotPushRegisteredException e) {
            logger.warn("requeueMessage", e);
        }
    }

    //
    private void sendDeliveryReceiptFor(Envelope message) {
        try {
            receiptSender.sendReceipt(account, message.getSource(), message.getTimestamp(),
                    message.hasRelay() ? Optional.of(message.getRelay()) :
                            Optional.empty());
        } catch (NoSuchUserException | NotPushRegisteredException e) {
            logger.info("No longer registered " + e.getMessage());
        } catch (IOException | TransientPushFailureException e) {
            logger.warn("Something wrong while sending receipt", e);
        } catch (WebApplicationException e) {
            logger.warn("Bad federated response for receipt: " + e.getResponse().getStatus());
        }
    }

    private void processConversationMessages() {
       List<ConversationPreviews.ConversationPreview> conversationPreviews=messagesManager.getPushConversations(account.getNumber(),device.getId());
       if(conversationPreviews!=null) {
           Iterator<ConversationPreviews.ConversationPreview> iterator = conversationPreviews.iterator();
           boolean hasMore = conversationPreviews.size() >= PushConversationsTable.RESULT_SET_CHUNK_SIZE;
           while (iterator.hasNext()) {
               boolean requery = !iterator.hasNext() && hasMore;
               ConversationPreviews.ConversationPreview preview = iterator.next();
               ConversationInfo conversationInfo = new ConversationInfo(messagesManager.getConversation(account.getNumber(), preview.getConversationId().getNumber(), preview.getConversationId().getGroupId().toStringUtf8()));
               sendConversationInfo(preview, conversationInfo, requery);
           }
           if (!hasMore) {
               messagesManager.removeSendConversationMsgLock(account.getNumber(), device.getId());
               client.sendRequest("PUT", "/api/v1/queue/conversation/empty", null, Optional.empty());
               processStoredMessages();
           }
       }else {
           messagesManager.removeSendConversationMsgLock(account.getNumber(), device.getId());
           client.sendRequest("PUT", "/api/v1/queue/conversation/empty", null, Optional.empty());
           processStoredMessages();

       }
    }
    private void processStoredMessages() {
        if(messagesManager.isExistsSendConversationMsgLock(account.getNumber(),device.getId())){
            return;
        }
        String priorConversation=messagesManager.getPriorConversation(account.getNumber(),device.getId());
        OutgoingMessageEntityList messages=null;
        boolean isConversation=false;

        if(!StringUtil.isEmpty(priorConversation)){
            isConversation=true;
            messages = messagesManager.getMessagesForDeviceByConversation(account.getNumber(), device.getId(),priorConversation);
            if(!messages.hasMore()){
                messagesManager.removePriorConversation(account.getNumber(),device.getId(),priorConversation);
            }
            if(messages.getMessages().size()==0){
                messages = messagesManager.getMessagesForDevice(account.getNumber(), device.getId());
                isConversation=false;
            }
        }else {
            messages = messagesManager.getMessagesForDevice(account.getNumber(), device.getId());
        }
        messagesManager.clearMessagesDepth(account.getNumber(), device.getId());
        Iterator<OutgoingMessageEntity> iterator = messages.getMessages().iterator();

        while (iterator.hasNext()) {
            OutgoingMessageEntity message = iterator.next();
            Envelope.Builder builder = Envelope.newBuilder()
                    .setType(Envelope.Type.valueOf(message.getType()))
                    .setSourceDevice(message.getSourceDevice())
                    .setSource(message.getSource())
                    .setSequenceId(message.getSequenceId())
                    .setNotifySequenceId(message.getNotifySequenceId())
                    .setMsgType(Envelope.MsgType.valueOf(message.getMsgType()))
                    .setTimestamp(message.getTimestamp());
            if (message.hasSourceIK()){
                builder.setSourceIdentityKey(message.getSourceIK());
            }
            if (message.hasPeerContext()){
                builder.setPeerContext(message.getPeerContext());
            }

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


            String msgInfo=message.getSource()+"_"+message.getSourceDevice()+"_"+message.getTimestamp();
            String key=message.getId()+"";
            boolean requery=!iterator.hasNext() && messages.hasMore();
            if(isConversation){
                requery=!iterator.hasNext();
            }
            //已经发送，并且不是requery就不再发送,或者当前记录已经发送且requery，不再发送。
            if(concurrentHashMap.containsKey(key)&&!requery||concurrentHashMap.containsKey(key)&&concurrentHashMap.get(key)){
                logger.info("msgId:{} is send! continue! msgInfo:{}",message.getId(),msgInfo);
                continue;
            }else{
                concurrentHashMap.put(key,requery);
                sendMessage(builder.build(), Optional.of(new StoredMessageInfo(message.getId(), message.isCached())), requery,false,null,false,message.getId(),null,null);
            }
        }

        if (!messages.hasMore()&&!isConversation) {
            client.sendRequest("PUT", "/api/v1/queue/empty", null, Optional.empty());
        }
    }

//    private void processStoredMessagesForSharding(){
//        List<PushConversation>  conversations=messagesManager.getPushConversations(account.getNumber(), device.getId());
//        if(conversations!=null&&conversations.size()>0){
//            conversations.sort(new Comparator<PushConversation>() {
//                @Override
//                public int compare(PushConversation o1, PushConversation o2) {
//                    return o1.getPriority()-o2.getPriority();
//                }
//            });
//            for(PushConversation pushConversation:conversations) {
//                List<OutgoingMessageEntityForSharding> pushMessages = messagesManager.getPushMessages(account.getNumber(), device.getId(),pushConversation);
//                if(pushMessages==null||pushMessages.size()==0){
//                    continue;
//                }
//                Iterator<OutgoingMessageEntityForSharding> iterator = pushMessages.iterator();
//                while (iterator.hasNext()) {
//                    OutgoingMessageEntityForSharding message = iterator.next();
//                    Envelope.Builder builder = Envelope.newBuilder()
//                            .setType(Envelope.Type.valueOf(message.getType()))
//                            .setSourceDevice(message.getSourceDevice())
//                            .setSource(message.getSource())
//                            .setTimestamp(message.getTimestamp())
//                            .setSequenceId(message.getSequenceId())
//                            .setNotifySequenceId(message.getNotifySequenceId())
//                            .setMsgType(Envelope.MsgType.valueOf(message.getMsgType()))
//                            .setMsgType(Envelope.MsgType.forNumber(message.getMsgType()));
//
//                    if (message.getContent() != null) {
//                        builder.setContent(ByteString.copyFrom(message.getContent()));
//                    }
//
//                    String msgInfo=message.getSource()+"_"+message.getSourceDevice()+"_"+message.getTimestamp();
//                    String key=pushConversation.getId()+message.getId();
//                    boolean requery=!iterator.hasNext() && pushMessages.size()>=Messages.RESULT_SET_CHUNK_SIZE;
//                    //已经发送，并且不是requery就不再发送,或者当前记录已经发送且requery，不再发送。
//                    if(concurrentHashMap.containsKey(key)&&!requery||concurrentHashMap.containsKey(key)&&concurrentHashMap.get(key)){
//                        logger.info("msgId:{} is send! continue! msgInfo:{}",message.getId(),msgInfo);
//                        continue;
//                    }else{
//                        concurrentHashMap.put(key,requery);
//                        sendMessage(builder.build(), Optional.of(new StoredMessageInfo(message.getId(), message.isCached(),key,pushConversation)), requery,false,null,false,message.getId(),null);
//                    }
//                }
//            }
//        }
//    }

    private void processStoredMessagesForSharding(PushConversation pushConversation){
        if(pushConversation==null) return;
        List<OutgoingMessageEntityForSharding> pushMessages = messagesManager.getPushMessages(account.getNumber(), device.getId(),pushConversation);
        if(pushMessages==null||pushMessages.size()==0){
            return;
        }
        Iterator<OutgoingMessageEntityForSharding> iterator = pushMessages.iterator();
        while (iterator.hasNext()) {
            OutgoingMessageEntityForSharding message = iterator.next();
            Envelope.Builder builder = Envelope.newBuilder()
                    .setType(Envelope.Type.valueOf(message.getType()))
                    .setSourceDevice(message.getSourceDevice())
                    .setSource(message.getSource())
                    .setTimestamp(message.getTimestamp())
                    .setSequenceId(message.getSequenceId())
                    .setNotifySequenceId(message.getNotifySequenceId())
                    .setMsgType(Envelope.MsgType.valueOf(message.getMsgType()))
                    .setMsgType(Envelope.MsgType.forNumber(message.getMsgType()));

            if (message.getContent() != null) {
                builder.setContent(ByteString.copyFrom(message.getContent()));
            }
            String msgInfo = message.getSource() + "_" + message.getSourceDevice() + "_" + message.getTimestamp();
            String key = pushConversation.getId() + message.getId();
            boolean requery = !iterator.hasNext() && pushMessages.size() >= Messages.RESULT_SET_CHUNK_SIZE;
            //已经发送，并且不是requery就不再发送,或者当前记录已经发送且requery，不再发送。
            if (concurrentHashMap.containsKey(key) && !requery || concurrentHashMap.containsKey(key) && concurrentHashMap.get(key)) {
                logger.info("msgId:{} is send! continue! msgInfo:{}", message.getId(), msgInfo);
                continue;
            } else {
                concurrentHashMap.put(key, requery);
                sendMessage(builder.build(), Optional.of(new StoredMessageInfo(message.getId(), message.isCached(), key, pushConversation)), requery, false, null,false, message.getId(),null,null);
            }
        }
    }

    private static class StoredMessageInfo {
        private final long id;
        private final boolean cached;
        private PushConversation pushConversation;
        private String key;

        private StoredMessageInfo(long id, boolean cached) {
            this.id = id;
            this.cached = cached;
            this.key=id+"";
        }
        private StoredMessageInfo(long id, boolean cached,String key,PushConversation pushConversation) {
            this.id = id;
            this.cached = cached;
            this.key=key;
            this.pushConversation= pushConversation;
        }
    }

    private static class ConversationInfo {
        private String conversation;

        private ConversationInfo(String conversation) {
            this.conversation=conversation;
        }

    }
}
