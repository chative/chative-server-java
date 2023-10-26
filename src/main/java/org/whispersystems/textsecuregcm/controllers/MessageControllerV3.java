package org.whispersystems.textsecuregcm.controllers;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.dropwizard.auth.Auth;
import org.glassfish.jersey.message.internal.Statuses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.exceptions.NoPermissionException;
import org.whispersystems.textsecuregcm.exceptions.NoSuchGroupException;
import org.whispersystems.textsecuregcm.federation.FederatedClientManager;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.push.ApnFallbackManager;
import org.whispersystems.textsecuregcm.push.PushSender;
import org.whispersystems.textsecuregcm.push.ReceiptSender;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.Base64;
import org.whispersystems.textsecuregcm.util.StringUtil;
import org.whispersystems.textsecuregcm.util.Util;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.ToLongFunction;

@Path("/v3/messages")
public class MessageControllerV3 {
    private final Logger logger = LoggerFactory.getLogger(MessageControllerV3.class);

    private final RateLimiters rateLimiters;
    private final PushSender pushSender;
    private final ReceiptSender receiptSender;
    private final FederatedClientManager federatedClientManager;
    private final AccountsManager accountsManager;
    private final TeamsManagerCasbin teamsManager;
    private final MessagesManager messagesManager;
    private final ConversationManager conversationManager;
    private final ApnFallbackManager apnFallbackManager;
    private final ReadReceiptsManager readReceiptsManager;

    private static final int mismatchedUsers = 11001; // 不匹配的用户:多人、少人、没有更新的
    private static final int notSupportThisEncLevel = 11002; // 不支持的加密等级
    //private static final int noSuchUserCode //= 11002;
    //private static final int inActiveUserCode = 11003;
    //private static final int blockedCode = 11004;
    ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 20, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new ThreadPoolExecutor.CallerRunsPolicy());

    public MessageControllerV3(RateLimiters rateLimiters, PushSender pushSender, ReceiptSender receiptSender, AccountsManager accountsManager,
                               MessagesManager messagesManager,
                               FederatedClientManager federatedClientManager,
                               ApnFallbackManager apnFallbackManager, TeamsManagerCasbin teamsManager,
                               ConversationManager conversationManager, ReadReceiptsManager readReceiptsManager) {
        this.rateLimiters = rateLimiters;
        this.pushSender = pushSender;
        this.receiptSender = receiptSender;
        this.federatedClientManager = federatedClientManager;
        this.accountsManager = accountsManager;
        this.teamsManager = teamsManager;
        this.messagesManager = messagesManager;
        this.conversationManager = conversationManager;
        this.apnFallbackManager = apnFallbackManager;
        this.readReceiptsManager = readReceiptsManager;
    }

    @Timed
    @Path("/group/{gid}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse sendMessageForGroup(@Auth Account source,
                                                   @PathParam("gid") String gid,
                                                   @Valid  IncomingMessageV2 message)
            throws RateLimitExceededException {
        rateLimiters.getMessagesForGroupLimiter().validate(source.getNumber() + "__" + gid);
        //账号无消息发送权限直接拒绝
        if (source.getAccountMsgHandleType() != null && (source.getAccountMsgHandleType() == Account.MsgHandleType.ONLY_RECEIVE.ordinal() || source.getAccountMsgHandleType() == Account.MsgHandleType.NOTHING.ordinal())) {
            throw new WebApplicationException("NoPermission", Response.status(Statuses.from(430, "NoPermission")).build());
        }
        if (message.getType() != MessageProtos.Envelope.Type.NEW_CIPHERTEXT.getNumber()) {
            throw new WebApplicationException("NoPermission", Response.status(Statuses.from(430, "NoPermission")).build());
        }

        //
        try {
            Group group = null;
            Object unavailableUsers = null;
            boolean hasLegacy = message.hasLegacyContent();
            GroupManagerWithTransaction.GroupSendAccounts groupSendAccounts = null;
            if (!StringUtil.isEmpty(gid)) {//发群组消息时，判断群组的有效性及是否在群组中，是否有发消息权限
                int detailMessageType = message.getDetailMessageType();
                final GroupManagerWithTransaction groupManager = accountsManager.getGroupManager();
                group = groupManager.getGroupWithPermissionCheckForSendMsg(source, gid, detailMessageType);
                groupManager.updateGroupActiveTime(group);
                groupSendAccounts = groupManager.getGroupSendAccounts(gid);
                final List<GroupManagerWithTransaction.UnavailableAccount> unavailableAccounts =
                        groupSendAccounts.getUnavailableAccounts();
                if (unavailableAccounts != null && !unavailableAccounts.isEmpty()) {
                    unavailableUsers = unavailableAccounts;
                }
            }
            if (groupSendAccounts == null)
                throw new NoSuchGroupException("No such group: " + gid);

            // log
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(message.getTimestamp()));
            String log = "sendMessageForGroup sendMessage: [" + timestamp + "] " + source.getNumber() + " -> " + gid;
            log += ", type: " + message.getType();
            logger.info(log);
            Long seqNo = messagesManager.processMessageSeqInfo(source, Optional.empty(),
                    Optional.of(gid), message.getTimestamp(), false, false);
            Long notifySeqNo = messagesManager.processMessageSeqInfo(source, Optional.empty(),
                    Optional.of(gid), message.getTimestamp(), true, false);

            final Map<String, Account> validAccounts = groupSendAccounts.getValidAccounts();
            // 检查是否都支持加密等级V2
            AtomicBoolean onlyLegacy = new AtomicBoolean(hasLegacy && !accountsManager.supportV3Message(source.getNumber()));
            if (hasLegacy && !onlyLegacy.get()) validAccounts.forEach((uid, account) -> {
                if (uid.equals(source.getNumber())) // 不检查自己
                    return;
                if (account.getMsgEncVersion() < 2) {
                    logger.warn("sendMessageForGroup notSupportThisEncLevel gid:{},uid:{},", gid, uid);
                    onlyLegacy.set(true);
                    //throw new WebApplicationException(Response.status(200).entity(new BaseResponse(1, notSupportThisEncLevel,
                    //        "Not Supported", null)).build());
                }
            });
            if (hasLegacy && !onlyLegacy.get() && !accountsManager.supportV3Message(validAccounts.keySet())) {
                onlyLegacy.set(true);
                logger.warn("sendMessageForGroup notSupportThisEncLevel gid:{},not in support list", gid);
            }

            // 检查老设备
            List<SendMessageResponseV2.ExceptionRecipient> staleRecipients = new ArrayList<>();
            for (IncomingMessageV2.Recipient recipient : message.getRecipients()) {
                // 群里面是否有对应的人
                final Account destAccount = validAccounts.get(recipient.getUid());
                if (destAccount == null){
                    continue;
                }
                final StaleRecipientException staleRecipientException = checkNotStale(destAccount, recipient);
                if (staleRecipientException != null) {
                    staleRecipients.add(new SendMessageResponseV2.ExceptionRecipient(
                            destAccount.getNumber(),
                            destAccount.getIdentityKey(),
                            destAccount.getMainDeviceRegistrationId()));
                }
            }
            final SendMessageResponseV2 resp = new SendMessageResponseV2(false,
                    0L, 0, 0);
            if (staleRecipients.size() > 0) {
                resp.setStale(staleRecipients);
                final BaseResponse mismatchedUserState = new BaseResponse(1, mismatchedUsers, "Mismatched User State", resp);
                logger.info("sendMessageForGroup src:{},gid:{}, staleRecipients Res:{}",
                        source.getNumber(),gid, new Gson().toJson(mismatchedUserState));
                return  mismatchedUserState;
            }

            // 检查缺失的人
            final List<SendMessageResponseV2.ExceptionRecipient> missingRecipients =
                    listMissingRecipient(groupSendAccounts, message.getRecipients(),source.getNumber());
            if (!missingRecipients.isEmpty()) {
                resp.setMissing(missingRecipients);
                return new BaseResponse(1, mismatchedUsers, "Mismatched User State", resp);
            }
            int detailMessageType = message.getDetailMessageType();
            long systemTimestamp = System.currentTimeMillis();
            boolean notify = !message.isReadReceipt() &&
                    //detailMessageType != IncomingMessage.DetailMessageType.CARD.getCode() &&
                    detailMessageType != IncomingMessage.DetailMessageType.REACTION.getCode() ;
            message.setSequenceId(seqNo);
            message.setNotifySequenceId(notifySeqNo);
            message.setSystemShowTimestamp(systemTimestamp);
            // 发送消息
            for (IncomingMessageV2.Recipient recipient : message.getRecipients()) {
                // 群里面是否有对应的人
                final Account destAccount = validAccounts.get(recipient.getUid());
                if (destAccount == null){
                    continue;
                }
                if (hasLegacy)
                sendLocalMessage(source, destAccount, message.getTimestamp(), message, recipient,
                        true, notify && onlyLegacy.get());
                if (!hasLegacy || !onlyLegacy.get())
                    sendLocalMessage(source, destAccount, message.getTimestamp(), message, recipient, false, notify);
            }
            if (message.getMsgType() == MessageProtos.Envelope.MsgType.MSG_RECALL_VALUE) {
                recallMsgHandle(message, source, null, gid);
            }
            final SendMessageResponseV2 sendMessageResponseV2 = new SendMessageResponseV2(
                    accountsManager.getActiveDeviceCount(source) > 1,
                    seqNo, systemTimestamp, notifySeqNo);
            sendMessageResponseV2.setUnavailableUsers(unavailableUsers);
            final List<SendMessageResponseV2.ExceptionRecipient> extraRecipient =
                    listExtraRecipient(groupSendAccounts, message.getRecipients());
            BaseResponse okRes = new BaseResponse(1, 0, "OK", sendMessageResponseV2);
            if (!extraRecipient.isEmpty()) {
                sendMessageResponseV2.setExtra(extraRecipient);
                okRes = new BaseResponse(1, mismatchedUsers, "Mismatched User State", sendMessageResponseV2);
            }
            if (okRes.getStatus() == 0 && onlyLegacy.get()) {
                okRes = new BaseResponse(1, notSupportThisEncLevel,
                        "Not Supported", sendMessageResponseV2);
            }
            logger.info("sendMessageForGroup src:{},gid:{}, okRes:{}", source.getNumber(),gid, new Gson().toJson(okRes));
            return okRes;
        } catch (NoSuchGroupException exception){
            throw new WebApplicationException("NoPermission", Response.status(Statuses.from(430, "NoPermission")).build());
        } catch (NoPermissionException exception){
            throw new WebApplicationException("NoPermission", Response.status(Statuses.from(430, "NoPermission")).build());
        }
        }


    // 列举出不在群组里面的人，不应该给他们发消息
    private List<SendMessageResponseV2.ExceptionRecipient> listExtraRecipient(GroupManagerWithTransaction.GroupSendAccounts groupSendAccounts,
                                                                               List<IncomingMessageV2.Recipient> recipients) {
        List<SendMessageResponseV2.ExceptionRecipient> extraRecipients = new ArrayList<>();
        if (groupSendAccounts == null || groupSendAccounts.getAllAccounts().isEmpty() ||recipients.isEmpty()) {
            return extraRecipients;
        }
        Map<String, Boolean> allMembersMap = groupSendAccounts.getAllAccounts();
        for (IncomingMessageV2.Recipient recipient : recipients) {
            if (allMembersMap.get(recipient.getUid()) == null) {
                SendMessageResponseV2.ExceptionRecipient extraRecipient = new SendMessageResponseV2.ExceptionRecipient(
                        recipient.getUid(), null, 0 );
                extraRecipients.add(extraRecipient);
            }
        }
        return extraRecipients;
    }

    // 列举出缺失的人
    private List<SendMessageResponseV2.ExceptionRecipient> listMissingRecipient(GroupManagerWithTransaction.GroupSendAccounts groupSendAccounts,
                                                                                List<IncomingMessageV2.Recipient> recipients,String srcUID) {
        List<SendMessageResponseV2.ExceptionRecipient> missingRecipients = new ArrayList<>();
        Map<String, Boolean> sendingUids = new HashMap<>();
        for (IncomingMessageV2.Recipient recipient : recipients) {
            sendingUids.put(recipient.getUid(), true);
        }
        groupSendAccounts.getValidAccounts().forEach((uid,account)->{
            if (uid.equals(srcUID)) {
                return;
            }
            final Boolean aBoolean = sendingUids.get(uid);
            if (aBoolean == null || !aBoolean) {
                SendMessageResponseV2.ExceptionRecipient missingRecipient = new SendMessageResponseV2.ExceptionRecipient(
                        uid,account.getIdentityKey(),account.getMainDeviceRegistrationId() );
                missingRecipients.add(missingRecipient);
            }
        });

            return missingRecipients;
    }

    private void recallMsgHandle(IncomingMessageBase incomingMessage,Account source,String destination,String gid){
        executor.submit(new Runnable() {
            @Override
            public void run() {
                if(incomingMessage.getRealSource()!=null) {
                    String conversation = messagesManager.getConversation(destination, source.getNumber(), gid);
                    RecallMsgInfo recallMsgInfo = new RecallMsgInfo(conversation, incomingMessage.getSequenceId(), incomingMessage.getNotifySequenceId(),incomingMessage.getRealSource().getSequenceId(),incomingMessage.getRealSource().getNotifySequenceId());
                    messagesManager.storeRecallMsg(recallMsgInfo);
                    if(!StringUtil.isEmpty(incomingMessage.getRealSource().getSource())&&incomingMessage.getRealSource().getSourceDevice()!=null&&incomingMessage.getRealSource().getTimestamp()!=null) {
                        messagesManager.updateMsgForRecall(incomingMessage.getRealSource().getSource(), incomingMessage.getRealSource().getSourceDevice(), incomingMessage.getRealSource().getTimestamp());
                    }
                }
            }
        });
    }

    @Timed
    @Path("/{destination}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse sendMessage(@Auth Account source,
                                    @PathParam("destination") String destinationName,
                                    @Valid IncomingMessageV2 message)
            throws IOException, RateLimitExceededException {
        //if (!checkSender(source,message)){
        //    throw new WebApplicationException("Bad Body", Response.status(Statuses.from(400,"Bad Body")).build());
        //}

        boolean isSyncMessage = source.getNumber().equals(destinationName);

        if (!isSyncMessage) {
            if(message.isReadReceipt()){
                rateLimiters.getReadReceiptsLimiter().validate(source.getNumber() + "__" + destinationName);
            }else
                rateLimiters.getMessagesLimiter().validate(source.getNumber() + "__" + destinationName);
        }
        //账号无消息发送权限直接拒绝
        if(source.getAccountMsgHandleType()!=null&&(source.getAccountMsgHandleType()==Account.MsgHandleType.ONLY_RECEIVE.ordinal()||
                source.getAccountMsgHandleType()==Account.MsgHandleType.NOTHING.ordinal())){
            throw new WebApplicationException("NoPermission", Response.status(Statuses.from(430,"NoPermission")).build());
        }

        boolean readReceipt=message.isReadReceipt();

        Optional<Account> destination = accountsManager.get(destinationName);
        if (!destination.isPresent()) {
            if(readReceipt){
                return  BaseResponse.ok(new SendMessageResponseV2(!isSyncMessage &&  accountsManager.getActiveDeviceCount(source) > 1));
            }
            //return new BaseResponse(1, noSuchUserCode, "No such user", null);
            throw new WebApplicationException(Response.status(404).build());
        }

        final Account destAccount = destination.get();
        if (destAccount.isDeleted()) {
            throw new WebApplicationException(Response.status(404).
                    entity(new BaseResponse(1, 10110,
                            "Operation denied. This account is already unregistered.", null)).build());
        }
        if (destAccount.isDisabled() ) {
            if(readReceipt){
                return  BaseResponse.ok(new SendMessageResponseV2(!isSyncMessage &&  accountsManager.getActiveDeviceCount(source) > 1));
            }
            //return new BaseResponse(1, noSuchUserCode, "No such user", null);
            throw new WebApplicationException(Response.status(404).build());
        }
        if (!destAccount.isRegistered()) {
            if(readReceipt){
                return  BaseResponse.ok(new SendMessageResponseV2(!isSyncMessage &&  accountsManager.getActiveDeviceCount(source) > 1));
            }
            logger.info("Destination account {} not registered ", destAccount.getNumber());
            throw new WebApplicationException(Response.status(404).entity(new BaseResponse(1, 10105,
                    "This account has logged out and messages can not be reached.", null)).build());
            //return  new BaseResponse(1, 10105,
            //        "This account has logged out and messages can not be reached.", null);
        }
        if (destAccount.isInactive()) {
            if(readReceipt){
                return  BaseResponse.ok(new SendMessageResponseV2(!isSyncMessage &&  accountsManager.getActiveDeviceCount(source) > 1));
            }
            //return new BaseResponse(1, inActiveUserCode, "the recipient is inactive ", null);
            throw new WebApplicationException(Response.status(404).build());
        }

        //账号无消息接收权限直接返回（同步消息除外）
        if (!isSyncMessage&& destAccount.getAccountMsgHandleType()!=null&&(destAccount.getAccountMsgHandleType()==Account.MsgHandleType.ONLY_SEND.ordinal()|| destAccount.getAccountMsgHandleType()==Account.MsgHandleType.NOTHING.ordinal())) {
            //return new SendMessageResponse(!isSyncMessage &&  accountsManager.getActiveDeviceCount(source) > 1);
            return  BaseResponse.ok(new SendMessageResponseV2( accountsManager.getActiveDeviceCount(source) > 1));
        }


        // 检查是否支持v2加密协议
        boolean hasLegacy = message.hasLegacyContent();
        boolean onlyLegacy = hasLegacy && !accountsManager.supportV3Message(source.getNumber());
        if (onlyLegacy ){
            logger.warn("notSupportThisEncLevel 1v1, source uid:{} can not send v3 message,destination  uid:{}",
                    source.getNumber(), destinationName);
        } else if(hasLegacy && (destAccount.getMsgEncVersion() < 2 || !accountsManager.supportV3Message(destinationName))) {
            onlyLegacy = true;
            logger.warn("notSupportThisEncLevel 1v1,destination  uid:{} with MsgEncVersion: {},",
                    destinationName, destAccount.getMsgEncVersion());
        }

        message.setSystemShowTimestamp(System.currentTimeMillis());

        // log
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(message.getTimestamp()));
        String log = "[newv3] sendMessage: [" + timestamp + "] " + source.getNumber() + " -> " + destinationName +  " ，readReceipt:" + readReceipt +
                ", type: " + message.getType()+",detailMessageType:"+message.getDetailMessageType();

        logger.info(log);

        try {
            if (!readReceipt) {
                Notification notification = null;
                String conversationStr = null;
                notification = message.getNotification();

                if (notification == null || notification.getArgs() == null || StringUtil.isEmpty(notification.getArgs().getGid())) {
                    conversationStr = source.getNumber();
                } else {
                    conversationStr = notification.getArgs().getGid();
                }
                Conversation conversation = conversationManager.get(destinationName, conversationStr);
                if (conversation != null && conversation.getBlockStatus() != null && conversation.getBlockStatus() == ConversationsTable.STATUS.OPEN.getCode()) {
                    return BaseResponse.ok(
                            new SendMessageResponse(!isSyncMessage && accountsManager.getActiveDeviceCount(source) > 1,
                                    -1L, System.currentTimeMillis()));

                    //throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
                    //throw new WebApplicationException(Response.status(404).entity(new BaseResponse(1, 10106,
                    //        "Rejected by the receiver.", null)).build());
                    //return new BaseResponse(1, blockedCode, "NoPermission", null);
                }
                //非已读回执消息判断权限
                logger.info("[newv3] sendmessage source:{},destination:{},not ReadReceipt",source.getNumber(),destinationName);
                //if (!teamsManager.isFriend(source.getNumber(), destinationName) &&
                //        !accountsManager.getGroupManager().inSameGroup(source, destAccount)) {
                //    logger.warn("sendLocalMessageForDestination source:{},destination:{},not Friend & noSameGroup",source.getNumber(),destinationName);
                //    throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
                //}
                if (messagesManager.isForbidden(source.getNumber(),destinationName)) {
                    throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
                }
            }

            if (message.getNotification() != null && message.getNotification().getArgs() != null) {
                message.getNotification().getArgs().setSource(source);
            }
            int detailMessageType = message.getDetailMessageType();
            boolean notify = !readReceipt &&
                    //detailMessageType != IncomingMessage.DetailMessageType.CARD.getCode() &&
                    detailMessageType != IncomingMessage.DetailMessageType.REACTION.getCode() &&
                    !isSyncMessage;
            for (IncomingMessageV2.Recipient recipient : message.getRecipients()) {
                recipient.setUid(destinationName);
                if (checkNotStale(destAccount, recipient) != null) {
                    SendMessageResponseV2 resData = new SendMessageResponseV2(
                            false, 0L, 0, 0L, null, null,
                            Collections.singletonList(new SendMessageResponseV2.ExceptionRecipient(
                                    destAccount.getNumber(),
                                    destAccount.getIdentityKey(),
                                    destAccount.getMainDeviceRegistrationId())));

                    return new BaseResponse(1, mismatchedUsers, "Mismatched User State", resData);
                }

                Long seqNo = messagesManager.processMessageSeqInfo(source, Optional.of(destinationName), Optional.empty(), message.getTimestamp(), false, false);

                message.setSequenceId(seqNo);
                Long notifySeqNo = messagesManager.processMessageSeqInfo(source, Optional.of(destinationName), Optional.empty(), message.getTimestamp(), true, false);
                message.setNotifySequenceId(notifySeqNo);

                if (hasLegacy)
                sendLocalMessage(source, destAccount, message.getTimestamp(), message, recipient, true,
                        notify && onlyLegacy);
                if (!hasLegacy || !onlyLegacy){
                    sendLocalMessage(source, destAccount, message.getTimestamp(), message, recipient, false, notify);
                }
                if (message.getMsgType() == MessageProtos.Envelope.MsgType.MSG_RECALL_VALUE) {
                    recallMsgHandle(message, source, destinationName, null);
                }
                if (isSyncMessage) {
                    syncReadReceiptHandle(message, source);
                }
                break;
            }

            Long seqNo = message.getSequenceId();
            Long notifySeqNo = message.getNotifySequenceId();//messages.getMessages()!= null && messages.getMessages().size() > 0 ? messages.getMessages().get(0).getNotifySequenceId() : null;
            long msgSysTimestamp = message.getSystemShowTimestamp();//messages.getMessages()!= null &&messages.getMessages().size() > 0 ? messages.getMessages().get(0).getSystemShowTimestamp() : 0;
            final SendMessageResponseV2 sendMessageResponseV2 = new SendMessageResponseV2(
                    !isSyncMessage && accountsManager.getActiveDeviceCount(source) > 1, seqNo != null ? seqNo : 0,
                    msgSysTimestamp, notifySeqNo != null ? notifySeqNo : 0);
            BaseResponse okRes;
            if (onlyLegacy)
                okRes = new BaseResponse(1, notSupportThisEncLevel,
                        "Not Supported", sendMessageResponseV2);
            else
                okRes = BaseResponse.ok(sendMessageResponseV2);

            logger.info("[newv3]sendMessage: [ {} ] {} -> {}, seqNo:{},notifySeqNo: {},msgSysTimestamp:{},response: {}",
                    timestamp, source.getNumber(), destinationName, seqNo, notifySeqNo, msgSysTimestamp, new Gson().toJson(okRes));
            return okRes;
        } catch (Exception e) {
            logger.error("[newv3]sendMessage {} -> {} ,error: {}",source.getNumber(),destinationName, e.getMessage(), e);
            throw e;//new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
        }
    }

    // 检查sender的pubIK和registrationId是否准确
    //private boolean checkSender(Account sender, IncomingMessageV2 message) {
    //    if (!sender.getIdentityKey().equals(message.getSenderPubIK()))return false;
    //    return  sender.getMainDeviceRegistrationId() == message.getSenderRegistrationId();
    //
    //}
    private StaleRecipientException checkNotStale(Account destinationAccount, IncomingMessageV2.Recipient recipientInfo) {
        if (destinationAccount.getMainDeviceRegistrationId() != recipientInfo.getRegistrationId()) {
            return new StaleRecipientException(destinationAccount.getNumber(),
                    destinationAccount.getIdentityKey(),destinationAccount.getMainDeviceRegistrationId());
        }
        return null;
    }

    private void sendLocalMessage(Account source,
                                  Account destinationAccount,
                                  long timestamp,
                                  IncomingMessageV2 incomingMessage,
                                  IncomingMessageV2.Recipient recipientInfo,
                                  boolean legacyMessage,
                                  boolean notify) {
        String conversationId = null;
        final IncomingMessage.Conversation conversation = incomingMessage.getConversation();
        if (conversation != null) {
            conversationId = conversation.getId();
        }
        logger.info("[newv3] in sendLocalMessage,tye legacyMessage :{}, hasLegacyContent:{}, source:{},dst:{},conversationId:{},notify:{}",
                legacyMessage, incomingMessage.hasLegacyContent(), source.getNumber(), destinationAccount.getNumber(), conversationId, notify);
        if (legacyMessage && !incomingMessage.hasLegacyContent()) {
            return;
        }
        // 检查接收人是否stale
        //if ( destinationAccount.getMainDeviceRegistrationId() != recipientInfo.getMainRegistrationId()) {
        //    throw new StaleRecipientException(destinationAccount.getNumber(),
        //            destinationAccount.getIdentityKey(),destinationAccount.getMainDeviceRegistrationId());
        //}
        boolean isSyncMessage = source.getNumber().equals(destinationAccount.getNumber());

        try {
            //Optional<byte[]> messageContent = getMessageContent(incomingMessage);
            MessageProtos.Envelope.Builder messageBuilder = MessageProtos.Envelope.newBuilder();
            messageBuilder.setType(MessageProtos.Envelope.Type.forNumber(incomingMessage.getType()))
                    .setSource(source.getNumber())
                    .setTimestamp(timestamp == 0 ? System.currentTimeMillis() : timestamp)
                    .setSourceDevice((int) source.getAuthenticatedDevice().get().getId())
                    .setSequenceId(incomingMessage.getSequenceId())
                    .setNotifySequenceId(incomingMessage.getNotifySequenceId())
                    .setMsgType(MessageProtos.Envelope.MsgType.forNumber(incomingMessage.getMsgType()))
                    .setSystemShowTimestamp(incomingMessage.getSystemShowTimestamp());

            if (!legacyMessage)
                messageBuilder.setSourceIdentityKey(source.getIdentityKey());
            else
                messageBuilder.setType(MessageProtos.Envelope.Type.PLAINTEXT);

            getMessageContent(incomingMessage,legacyMessage).ifPresent(bytes -> messageBuilder.setContent(ByteString.copyFrom(bytes)));

            if (source.getRelay().isPresent()) {
                messageBuilder.setRelay(source.getRelay().get());
            }
            if (!legacyMessage && recipientInfo.getPeerContext() != null && !recipientInfo.getPeerContext().isEmpty()) {
                messageBuilder.setPeerContext(recipientInfo.getPeerContext());
            }

            boolean silent = !notify;

            MessageProtos.Envelope envelope = messageBuilder.build();
            for (Device destinationDevice : destinationAccount.getDevices()) {
                if (!accountsManager.isActiveDevice(destinationDevice, destinationAccount))
                    continue;
                if (destinationDevice.getMsgEncVersion()< 2 && !legacyMessage)
                {
                    logger.warn("notSupportThisEncLevel, advise the ppl update app,destination  uid:{}, did:{}, with MsgEncVersion: {},",
                            destinationAccount.getNumber(),destinationDevice.getId(), destinationDevice.getMsgEncVersion());
                    continue;
                }
                if (isSyncMessage &&
                        // 同步消息时，deviceID不能相同
                        destinationDevice.getId() == source.getAuthenticatedDevice().get().getId()) {
                    continue;
                }

                try {
                    SendMessageLogHandler.SendMessageLog sendMessageLog = new SendMessageLogHandler.SendMessageLog(SendMessageLogHandler.SendMessageAction.REVICE.getName(), destinationAccount.getNumber(), destinationDevice.getId(), notify, silent, envelope);
                    SendMessageLogHandler.send(sendMessageLog);
                    pushSender.sendMessage(destinationAccount, destinationDevice, envelope, notify, silent,
                            incomingMessage.getNotification(), incomingMessage.isReadReceipt(), incomingMessage, -1);
                } catch (Exception e) {
                    logger.error("SendMessageLog error!" + e.getMessage());
                }
            }
        //} catch ()
        } catch (Exception e) {
            logger.warn("sendLocalMessage {} -> {} , error:{}", source.getNumber(), destinationAccount.getNumber(), e.getMessage(), e);
        }
    }
    private Optional<byte[]> getMessageContent(IncomingMessageV2 message, boolean legacyMessage) {
        String content;
        content = legacyMessage ?message.getLegacyContent() :  message.getContent();
        if (Util.isEmpty(content)) return Optional.empty();
        try {
            return Optional.of(Base64.decode(content));
        } catch (IOException ioe) {
            logger.debug("Bad B64", ioe);
            return Optional.empty();
        }
    }

    private void syncReadReceiptHandle(IncomingMessageV2 incomingMessage,Account source){
        executor.submit(() -> {
            final Optional<Device> authenticatedDevice = source.getAuthenticatedDevice();
            final Device device = authenticatedDevice.get();
            if(incomingMessage.getMsgType()== MessageProtos.Envelope.MsgType.MSG_SYNC_READ_RECEIPT_VALUE){
                String realDestination=source.getNumber();
                String gid=null;
                if(incomingMessage.getConversation()!=null&&incomingMessage.getReadPositions()!=null&&incomingMessage.getReadPositions().size()>0){
                    if(incomingMessage.getConversation().getType()==IncomingMessage.Conversation.Type.GROUP){
                        gid=incomingMessage.getConversation().getGid();
                    }
                    if(incomingMessage.getConversation().getType()==IncomingMessage.Conversation.Type.PRIVATE){
                        realDestination=incomingMessage.getConversation().getNumber();
                    }
                    String conversation=messagesManager.getConversation(realDestination,source.getNumber(),gid);
                    List<IncomingMessage.ReadPosition> readPositions=incomingMessage.getReadPositions();
                    readPositions.sort(Comparator.comparingLong(new ToLongFunction<IncomingMessage.ReadPosition>() {
                        @Override
                        public long applyAsLong(IncomingMessage.ReadPosition value) {
                            return value.getMaxServerTime();
                        }
                    }).reversed());
                    Long now=System.currentTimeMillis();
                    List<ReadReceipt> readReceipts=new ArrayList<>();
                    for(IncomingMessage.ReadPosition readPosition:readPositions) {
                        ReadReceipt readReceipt = new ReadReceipt(conversation, source.getNumber(), device.getId(),readPosition.getMaxServerTime(),readPosition.getReadAt(),readPosition.getMaxNotifySequenceId(),now);
                        readReceipts.add(readReceipt);
                    }
                    if(readReceipts.size()>0){
                        pushSender.recallNotification(source, device, incomingMessage, readReceipts.get(0));
                        readReceiptsManager.insertBatch(readReceipts);
                    }
                }
            }
        });
    }

}
