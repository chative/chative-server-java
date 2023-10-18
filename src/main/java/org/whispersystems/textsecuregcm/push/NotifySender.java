package org.whispersystems.textsecuregcm.push;

import com.google.protobuf.ByteString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.MessageProtos;
import org.whispersystems.textsecuregcm.entities.Notification;
import org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest;
import org.whispersystems.textsecuregcm.storage.*;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public class NotifySender {
    private final Logger logger = LoggerFactory.getLogger(NotifySender.class);

    public final static String NOTIFY_REFRESH_DIRECTORY = "2lTzmnwXeR8RI5npSP41eAStQLnzYfU2";

    private final WebsocketSender mWebsocketSender;
    private final KafkaSender    kafkaSender;
    private final PushSender pushSender;
    private final AccountsManager accountsManager;
    private final MessagesManager messagesManager;

    public NotifySender(@NotNull WebsocketSender websocketSender, PushSender pushSender, @NotNull KafkaSender kafkaSender,AccountsManager accountsManager,MessagesManager messagesManager) {
        this.mWebsocketSender = websocketSender;
        this.pushSender = pushSender;
        this.kafkaSender=kafkaSender;
        this.accountsManager=accountsManager;
        this.messagesManager=messagesManager;
    }
    public void send(Account account, String content ) {
        this.send(account,content,null);
    }
    public void send(Account account,  String content , Notification notification) {
        //账号无消息接收权限直接返回（同步消息除外）
        if (account.getAccountMsgHandleType()!=null&&(account.getAccountMsgHandleType()==Account.MsgHandleType.ONLY_SEND.ordinal()||account.getAccountMsgHandleType()==Account.MsgHandleType.NOTHING.ordinal())) {
            return;
        }
        Long time=System.currentTimeMillis();
        Account operator=new Account("server",null);
        Device authDevice=new Device();
        authDevice.setId(0);
        operator.setAuthenticatedDevice(authDevice);
        // 增加消息的SeqNo
        Long seqNo = messagesManager.processMessageSeqInfo(operator, Optional.of(account.getNumber()), Optional.empty(), time,false,true);
        Long notifySeqNo = messagesManager.processMessageSeqInfo(operator, Optional.of(account.getNumber()), Optional.empty(), time,true,false);

        MessageProtos.Envelope.Builder messageBuilder = MessageProtos.Envelope.newBuilder();
        messageBuilder.setType(MessageProtos.Envelope.Type.NOTIFY)
                 .setSource(operator.getNumber())
                // .setSourceDevice((int) device.getId())
                .setMsgType(MessageProtos.Envelope.MsgType.MSG_NOTIFY)
                .setSystemShowTimestamp(time)
                .setTimestamp(time)
                .setSequenceId(seqNo)
                .setNotifySequenceId(notifySeqNo);
        messageBuilder.setContent(ByteString.copyFrom(content.getBytes()));
        MessageProtos.Envelope message=messageBuilder.build();
//        try {
//            SendMessageLogHandler.SendMessageLog sendMessageLog=new SendMessageLogHandler.SendMessageLog(SendMessageLogHandler.SendMessageAction.REVICE.getName(),account.getNumber(),device.getId(),message);
//            SendMessageLogHandler.send(sendMessageLog);
//        }catch (Exception e){
//            logger.error("SendMessageLog error!"+e.getMessage());
//        }
//        String conversation=messagesManager.getConversation(account.getNumber(),message.getSource(),null);
        long shardMsgId = 0;//=mWebsocketSender.insertForSharding(message,notification != null,conversation,null);
        for (Device device : account.getDevices()) {
            if (accountsManager.isActiveDevice(device,account)) {
                logger.info("websocketNotifySender send notify! account:" + account.getNumber() + " device:" + device.getId() + " msg:" + content);
                if(device.getReceiveType()== AccountCreateRequest.ReceiveType.KAFKA_VALUE){
                    kafkaSender.sendMessage(account,device,message,Optional.empty(),false);
                }else {
                    WebsocketSender.DeliveryStatus deliveryStatus = mWebsocketSender.sendMessage(account, device, message, WebsocketSender.Type.WEB, notification != null, notification,false,null,shardMsgId);
                    if (notification!=null&&  !deliveryStatus.isDelivered() &&device.shouldPushNotification()) {
                        pushSender.sendPushNotification(account, device, deliveryStatus.getMessageQueueDepth(), false, false,message,notification);
                    }
                }
            } else {
                logger.warn("websocketNotifySender send notify! account:{} device:{}, is NOT ActiveDevice ", account.getNumber(), device.getId());
            }
        }

    }

    public void sendForGroup(final Group group,  String content , Notification notification) {
        MessageProtos.Envelope.Builder messageBuilder = MessageProtos.Envelope.newBuilder();
        Long time=System.currentTimeMillis();
        Account operator=new Account("server",null);
        Device authDevice=new Device();
        authDevice.setId(0);
        operator.setAuthenticatedDevice(authDevice);
        // 增加消息的SeqNo
        Long seqNo = messagesManager.processMessageSeqInfo(operator, Optional.empty(), Optional.of(group.getId()), time,false,true);
        Long notifySeqNo = messagesManager.processMessageSeqInfo(operator, Optional.empty(), Optional.of(group.getId()), time,true,false);
        messageBuilder.setType(MessageProtos.Envelope.Type.NOTIFY)
                .setSource("server")
                // .setSourceDevice((int) device.getId())
                .setMsgType(MessageProtos.Envelope.MsgType.MSG_NOTIFY)
                .setSystemShowTimestamp(time)
                .setTimestamp(time)
                .setSequenceId(seqNo)
                .setNotifySequenceId(notifySeqNo);
        messageBuilder.setContent(ByteString.copyFrom(content.getBytes()));
        MessageProtos.Envelope message=messageBuilder.build();
        logger.info("sendNotifyForGroup, gid:{},timestamp:{},content:{}", group.getId(), time,content);
        pushSender.sendMessageForGroup(group, message, notification!=null, notification==null, null, notification);
    }
}
