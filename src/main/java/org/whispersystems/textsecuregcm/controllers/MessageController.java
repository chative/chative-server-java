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
package org.whispersystems.textsecuregcm.controllers;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.annotation.Timed;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.dropwizard.auth.Auth;
import org.glassfish.jersey.message.internal.Statuses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.directorynotify.DirectoryNotifyManager;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.entities.MessageProtos.Envelope;
import org.whispersystems.textsecuregcm.eslogger.loggerFilter;
import org.whispersystems.textsecuregcm.exceptions.NoPermissionException;
import org.whispersystems.textsecuregcm.exceptions.NoSuchGroupException;
import org.whispersystems.textsecuregcm.federation.FederatedClient;
import org.whispersystems.textsecuregcm.federation.FederatedClientManager;
import org.whispersystems.textsecuregcm.federation.NoSuchPeerException;
import org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.push.*;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.Base64;
import org.whispersystems.textsecuregcm.util.Constants;
import org.whispersystems.textsecuregcm.util.StringUtil;
import org.whispersystems.textsecuregcm.util.Util;
import org.whispersystems.textsecuregcm.websocket.WebSocketConnection;

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
import java.util.function.ToLongFunction;

import static com.codahale.metrics.MetricRegistry.name;

@Path("/v1/messages")
public class MessageController {

    private final Logger logger = LoggerFactory.getLogger(MessageController.class);

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
    ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 20, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new ThreadPoolExecutor.CallerRunsPolicy());

    // edited by guolilei

    public MessageController(RateLimiters rateLimiters,
                             PushSender pushSender,
                             ReceiptSender receiptSender,
                             AccountsManager accountsManager,
                             MessagesManager messagesManager,
                             FederatedClientManager federatedClientManager,
                             ApnFallbackManager apnFallbackManager,TeamsManagerCasbin teamsManager,
                             ConversationManager conversationManager,ReadReceiptsManager readReceiptsManager) {
        this.rateLimiters = rateLimiters;
        this.pushSender = pushSender;
        this.receiptSender = receiptSender;
        this.accountsManager = accountsManager;
        this.messagesManager = messagesManager;
        this.federatedClientManager = federatedClientManager;
        this.apnFallbackManager = apnFallbackManager;
        this.teamsManager=teamsManager;
        this.conversationManager=conversationManager;
        this.readReceiptsManager=readReceiptsManager;
        SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME)
                .register(name(DirectoryNotifyManager.class, "MessageController_executor_depth"),
                        (Gauge<Long>) ((ThreadPoolExecutor)executor)::getTaskCount);
    }

    @Timed
    @Path("/{destination}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SendMessageResponse sendMessage(@Auth Account source,
                                           @PathParam("destination") String destinationName,
                                           @Valid IncomingMessageList messages)
            throws IOException, RateLimitExceededException {
        boolean isSyncMessage = source.getNumber().equals(destinationName);

        if (!source.getNumber().equals(destinationName)) {
            rateLimiters.getMessagesLimiter().validate(source.getNumber() + "__" + destinationName);
        }
        //账号无消息发送权限直接拒绝
        if(source.getAccountMsgHandleType()!=null&&(source.getAccountMsgHandleType()==Account.MsgHandleType.ONLY_RECEIVE.ordinal()||source.getAccountMsgHandleType()==Account.MsgHandleType.NOTHING.ordinal())){
            throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
        }

        boolean readReceipt=false;
        if(messages.getMessages()!=null&&messages.getMessages().size()>0) {
            readReceipt= messages.getMessages().get(0).isReadReceipt();
        }

        Optional<Account> destination = accountsManager.get(destinationName);
        if (!destination.isPresent()) {
            if(readReceipt){
                return new SendMessageResponse(!isSyncMessage && accountsManager.getActiveDeviceCount(source) > 1);
            }
            throw new WebApplicationException(Response.status(404).build());
        }

        final Account destAccount = destination.get();
        if (destAccount.isDisabled() ) {
            if(readReceipt){
                return new SendMessageResponse(!isSyncMessage &&  accountsManager.getActiveDeviceCount(source) > 1);
            }
            throw new WebApplicationException(Response.status(404).build());
        }
        if (!destAccount.isRegistered()) {
            if (readReceipt) {
                return new SendMessageResponse(!isSyncMessage && accountsManager.getActiveDeviceCount(source) > 1);
            }
            logger.info("Destination account {} not registered ", destAccount.getNumber());
            throw new WebApplicationException(Response.status(404).entity(new BaseResponse(1, 10105,
                    "This account has logged out and messages can not be reached.", null)).build());
        }
        if (destAccount.isInactive()) {
            if(readReceipt){
                return new SendMessageResponse(!isSyncMessage &&  accountsManager.getActiveDeviceCount(source) > 1);
            }
            throw new WebApplicationException(Response.status(431).build());
        }

        //账号无消息接收权限直接返回（同步消息除外）
        if (!isSyncMessage&& destAccount.getAccountMsgHandleType()!=null&&(destAccount.getAccountMsgHandleType()==Account.MsgHandleType.ONLY_SEND.ordinal()|| destAccount.getAccountMsgHandleType()==Account.MsgHandleType.NOTHING.ordinal())) {
            return new SendMessageResponse(!isSyncMessage &&  accountsManager.getActiveDeviceCount(source) > 1);
        }
        // log
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(messages.getTimestamp()));
        String log = "[signal] sendMessage: [" + timestamp + "] " + source.getNumber() + " -> " + destinationName + " ，silent:" + messages.isSilent() + " ，readReceipt:" + readReceipt;
        for (IncomingMessage incomingMessage : messages.getMessages()) {
            log += "\n-> device: " + incomingMessage.getDestination() + "." + incomingMessage.getDestinationDeviceId() +
                    ", type: " + incomingMessage.getType();
        }
        logger.info(log);

        try {
            if(!readReceipt) {
                Notification notification = null;
                String conversationStr = null;
                if (messages.getMessages() != null && messages.getMessages().size() > 0) {
                    notification = messages.getMessages().get(0).getNotification();

                    if (notification == null || notification.getArgs() == null || StringUtil.isEmpty(notification.getArgs().getGid())) {
                        conversationStr = source.getNumber();
                    } else {
                        conversationStr = notification.getArgs().getGid();
                    }
                    Conversation conversation = conversationManager.get(destinationName, conversationStr);
                    if (conversation != null && conversation.getBlockStatus()!=null&& conversation.getBlockStatus() == ConversationsTable.STATUS.OPEN.getCode()) {
                        return new SendMessageResponse(!isSyncMessage && accountsManager.getActiveDeviceCount(source) > 1, -1L, System.currentTimeMillis());
                        //throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
                        //throw new WebApplicationException(Response.status(404).entity(new BaseResponse(1, 10106,
                        //        "Rejected by the receiver.", null)).build());

                    }
                }
            }
            if (Util.isEmpty(messages.getRelay())) {
                sendLocalMessage(source, destinationName, messages, isSyncMessage);
            } else {
                sendRelayMessage(source, destinationName, messages, isSyncMessage);
            }

            Long seqNo = messages.getMessages()!= null && messages.getMessages().size() > 0 ? messages.getMessages().get(0).getSequenceId() : null;
            Long notifySeqNo = messages.getMessages()!= null && messages.getMessages().size() > 0 ? messages.getMessages().get(0).getNotifySequenceId() : null;
            Long msgSysTimestamp = messages.getMessages()!= null &&messages.getMessages().size() > 0 ? messages.getMessages().get(0).getSystemShowTimestamp() : 0;
            logger.info("[signal]sendMessage: [" + timestamp + "] " + source.getNumber() + " -> " + destinationName + ".seqNo:" + seqNo+ ".notifySeqNo:" + notifySeqNo+ ".msgSysTimestamp:" + msgSysTimestamp);
            return new SendMessageResponse(!isSyncMessage && accountsManager.getActiveDeviceCount(source) > 1, seqNo,
                    msgSysTimestamp, notifySeqNo != null ? notifySeqNo : 0);
        } catch (NoSuchUserException e) {
            if(readReceipt){
                return new SendMessageResponse(!isSyncMessage &&  accountsManager.getActiveDeviceCount(source) > 1);
            }
            throw new WebApplicationException(Response.status(404).build());
        } catch (MismatchedDevicesException e) {
            logger.warn("in sendMessage catch MismatchedDevicesException source.Number:{},destinationName:{},missingDevices:{},extraDevices:{}",
                    source.getNumber(), destinationName, e.getMissingDevices(), e.getExtraDevices());
            throw new WebApplicationException(Response.status(409)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new MismatchedDevices(e.getMissingDevices(),
                            e.getExtraDevices()))
                    .build());
        } catch (StaleDevicesException e) {
            throw new WebApplicationException(Response.status(410)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new StaleDevices(e.getStaleDevices()))
                    .build());
        } catch (InvalidDestinationException e) {
            throw new WebApplicationException(Response.status(400).build());
        } catch (NoPermissionException e) {
            throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
        } catch (NoSuchGroupException e) {
            throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
        }
    }

    @Timed
    @Path("/destination/{destination}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SendMessageResponse sendMessageForDestination(@Auth Account source,
                                           @PathParam("destination") String destinationName,
                                           @Valid IncomingMessageList messages)
            throws RateLimitExceededException {
        boolean isSyncMessage = source.getNumber().equals(destinationName);

        if (!source.getNumber().equals(destinationName)) {
            rateLimiters.getMessagesLimiter().validate(source.getNumber() + "__" + destinationName);
        }
        //账号无消息发送权限直接拒绝
        if(source.getAccountMsgHandleType()!=null&&(source.getAccountMsgHandleType()==Account.MsgHandleType.ONLY_RECEIVE.ordinal()||source.getAccountMsgHandleType()==Account.MsgHandleType.NOTHING.ordinal())){
            throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
        }

        boolean readReceipt=false;
        if(messages.getMessages()!=null&&messages.getMessages().size()>0) {
            readReceipt= messages.getMessages().get(0).isReadReceipt();
        }

        Optional<Account> destination = accountsManager.get(destinationName);
        if (!destination.isPresent()) {
            if(readReceipt){
                return new SendMessageResponse(!isSyncMessage && accountsManager.getActiveDeviceCount(source) > 1,-1L, System.currentTimeMillis());
            }
            throw new WebApplicationException(Response.status(404).build());
        }

        final Account destAccount = destination.get();
        if (destAccount.isDeleted()) {
            throw new WebApplicationException(Response.status(404).
                    entity(new BaseResponse(1, 10110,
                            "Operation denied. This account is already unregistered.", null)).build());
        }
        if (destAccount.isDisabled()) {
            if(readReceipt){
                return new SendMessageResponse(!isSyncMessage &&  accountsManager.getActiveDeviceCount(source) > 1);
            }
            throw new WebApplicationException(Response.status(404).build());
        }
        if (!destAccount.isRegistered()) {
            if (readReceipt) {
                return new SendMessageResponse(!isSyncMessage && accountsManager.getActiveDeviceCount(source) > 1);
            }
            logger.info("Destination account {} not registered ", destAccount.getNumber());
            throw new WebApplicationException(Response.status(404).entity(new BaseResponse(1, 10105,
                    "This account has logged out and messages can not be reached.", null)).build());
        }

        if (destAccount.isInactive()) {
            if(readReceipt){
                return new SendMessageResponse(!isSyncMessage &&  accountsManager.getActiveDeviceCount(source) > 1);
            }
            throw new WebApplicationException(Response.status(431).build());
        }
        //账号无消息接收权限直接返回（同步消息除外）
        if (!isSyncMessage&& destAccount.getAccountMsgHandleType()!=null&&
                (destAccount.getAccountMsgHandleType()==Account.MsgHandleType.ONLY_SEND.ordinal()||
                        destAccount.getAccountMsgHandleType()==Account.MsgHandleType.NOTHING.ordinal())) {
            return new SendMessageResponse(!isSyncMessage &&  accountsManager.getActiveDeviceCount(source) > 1,-1L, System.currentTimeMillis());
        }
        // log
        String timestamp = messages.getTimestamp()+"";
        String log = "sendMessageForDestination: [" + timestamp + "] " + source.getNumber() + " -> " + destinationName + " ，silent:" + messages.isSilent() + " ，readReceipt:" + readReceipt;
        for (IncomingMessage incomingMessage : messages.getMessages()) {
            log += ", type: " + incomingMessage.getType();
        }
        logger.info(log);

        try {
            if(!readReceipt) {
                Notification notification = null;
                String conversationStr = null;
                if (messages.getMessages() != null && messages.getMessages().size() > 0) {
                    notification = messages.getMessages().get(0).getNotification();

                    if (notification == null || notification.getArgs() == null || StringUtil.isEmpty(notification.getArgs().getGid())) {
                        conversationStr = source.getNumber();
                    } else {
                        conversationStr = notification.getArgs().getGid();
                    }
                    Conversation conversation = conversationManager.get(destinationName, conversationStr);
                    if (conversation != null && conversation.getBlockStatus()!=null&& conversation.getBlockStatus() == ConversationsTable.STATUS.OPEN.getCode()) {
                        return new SendMessageResponse(!isSyncMessage && accountsManager.getActiveDeviceCount(source) > 1, -1L, System.currentTimeMillis());
                        //throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
                        //throw new WebApplicationException(Response.status(404).entity(new BaseResponse(1, 10106,
                        //        "Rejected by the receiver.", null)).build());
                    }
                }
            }
            Long seqNo =  sendLocalMessageForDestination(source, destAccount, messages, isSyncMessage);
            Long notifySeqNo = messages.getMessages()!= null && messages.getMessages().size() > 0 ? messages.getMessages().get(0).getNotifySequenceId() : null;
            long msgSysTimestamp = messages.getMessages()!= null &&messages.getMessages().size() > 0 ? messages.getMessages().get(0).getSystemShowTimestamp() : 0;
            logger.info("sendMessage: [" + timestamp + "] " + source.getNumber() + " -> " + destinationName + ".seqNo:" + seqNo+ ".notifySeqNo:" + notifySeqNo+ ".msgSysTimestamp:" + msgSysTimestamp);
            return new SendMessageResponse(!isSyncMessage &&  accountsManager.getActiveDeviceCount(source) > 1,
                    seqNo, msgSysTimestamp,notifySeqNo != null ? notifySeqNo : 0L);
        } catch (NoSuchUserException e) {
            if(readReceipt){
                return new SendMessageResponse(!isSyncMessage &&  accountsManager.getActiveDeviceCount(source) > 1,-1L, System.currentTimeMillis());
            }
            throw new WebApplicationException(Response.status(404).build());
        } catch (NoPermissionException e) {
            throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
        }
    }

    @Timed
    @Path("/destinations")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SendMessageResponseForDestinations sendMessageForDestinations(@Auth Account source,
                                                                         @Valid IncomingMessageForDestinations message)
            throws RateLimitExceededException {
        rateLimiters.getMessagesForDestinationsLimiter().validate(source.getNumber() + "_sendMessageForDestinations");
        //账号无消息发送权限直接拒绝
        if(source.getAccountMsgHandleType()!=null&&(source.getAccountMsgHandleType()==Account.MsgHandleType.ONLY_RECEIVE.ordinal()||source.getAccountMsgHandleType()==Account.MsgHandleType.NOTHING.ordinal())){
            throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
        }
        boolean readReceipt=message.isReadReceipt();
        message.setSystemShowTimestamp(System.currentTimeMillis());
        List<SendMessageResponseForDestinations.SendMessageResponseWithDestination> sendMessageResponseWithDestinationList=new ArrayList<>();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(message.getTimestamp()));
        for(String number:message.getDestinations()) {
            Optional<Account> destination = accountsManager.get(number);
            String log = "sendMessage: [" + timestamp + "] " + source.getNumber() + " -> " + number + " ，silent:" + message.isSilent() + " ，readReceipt:" + readReceipt;
            log += ", type: " + message.getType();
            //目标账号不存在，被禁用
            if (!destination.isPresent()||destination.get().isinValid()) {
                if(!readReceipt) {
                    sendMessageResponseWithDestinationList.add(new SendMessageResponseForDestinations.SendMessageResponseWithDestination(number, BaseResponse.STATUS.NO_SUCH_USER.getState(), -1L, "Account is not exists or disabled !"));
                    // log
                    logger.info(log + " ,sendFailed! msg:{}", "Account is not exists or disabled !");
                }else{
                    sendMessageResponseWithDestinationList.add(new SendMessageResponseForDestinations.SendMessageResponseWithDestination(number,BaseResponse.STATUS.OK.getState(), -1L,null));
                }
            //无接受消息权限
            }else if(destination.get().getAccountMsgHandleType()!=null&&(destination.get().getAccountMsgHandleType()==Account.MsgHandleType.ONLY_SEND.ordinal()||destination.get().getAccountMsgHandleType()==Account.MsgHandleType.NOTHING.ordinal())){
                if(!readReceipt) {
                    sendMessageResponseWithDestinationList.add(new SendMessageResponseForDestinations.SendMessageResponseWithDestination(number, BaseResponse.STATUS.NO_PERMISSION.getState(), -1L, "Account no permission receive msg !"));
                    logger.info(log + " ,sendFailed! msg:{}", "Account no permission receive msg !");
                }else{
                    sendMessageResponseWithDestinationList.add(new SendMessageResponseForDestinations.SendMessageResponseWithDestination(number,BaseResponse.STATUS.OK.getState(), -1L,null));
                }
            }else{
                try {
                    if(!readReceipt) {
                        if (!destination.get().isRegistered()) {
                            sendMessageResponseWithDestinationList.add(new SendMessageResponseForDestinations.SendMessageResponseWithDestination(number,
                                    BaseResponse.STATUS.NO_SUCH_USER.getState(), -1L, "Account is not Registered !"));
                            logger.info(log + " ,sendFailed! msg:{}", "Account is not Registered !");
                            continue;
                        }
                        Notification notification = null;
                        String conversationStr = null;
                        notification = message.getNotification();
                        if (notification == null || notification.getArgs() == null || StringUtil.isEmpty(notification.getArgs().getGid())) {
                            conversationStr = source.getNumber();
                        } else {
                            conversationStr = notification.getArgs().getGid();
                        }
                        Conversation conversation = conversationManager.get(number, conversationStr);
                        if (conversation != null && conversation.getBlockStatus()!=null&&conversation.getBlockStatus() == ConversationsTable.STATUS.OPEN.getCode()) {
                            sendMessageResponseWithDestinationList.add(new SendMessageResponseForDestinations.SendMessageResponseWithDestination(number, BaseResponse.STATUS.OK.getState(), -1L, null));
                            continue;
                        }
                    }
                    long seqNo = sendLocalMessageForDestinations(source,destination.get(),message);
                    Long notifySeqNo = message.getNotifySequenceId();

                    logger.info(log+ " .seqNo:" + seqNo+".notifySeqNo:" + notifySeqNo);
                    sendMessageResponseWithDestinationList.add(new SendMessageResponseForDestinations.SendMessageResponseWithDestination(number,BaseResponse.STATUS.OK.getState(), seqNo,null,notifySeqNo));
                } catch (NoSuchUserException e) {
                    sendMessageResponseWithDestinationList.add(new SendMessageResponseForDestinations.SendMessageResponseWithDestination(number,BaseResponse.STATUS.NO_SUCH_USER.getState(), -1L,"Account is not exists!"));
                    logger.warn(log+" ,sendFailed! msg:{}",e.getMessage());
                }catch (NoPermissionException e) {
                    sendMessageResponseWithDestinationList.add(new SendMessageResponseForDestinations.SendMessageResponseWithDestination(number,BaseResponse.STATUS.NO_PERMISSION.getState(), -1L,e.getMessage()));
                    logger.warn(log+" ,sendFailed! msg:{}",e.getMessage());
                }
            }
        }

        return new SendMessageResponseForDestinations(sendMessageResponseWithDestinationList, message.getSystemShowTimestamp());
    }

    @Timed
    @Path("/group/{gid}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SendMessageResponse sendMessageForGroup(@Auth Account source,
                                           @PathParam("gid") String gid,
                                           @Valid IncomingMessageList messages)
            throws RateLimitExceededException {
        if (!source.getNumber().equals(gid)) {
            rateLimiters.getMessagesForGroupLimiter().validate(source.getNumber() + "__" + gid);
        }
        //账号无消息发送权限直接拒绝
        if(source.getAccountMsgHandleType()!=null&&(source.getAccountMsgHandleType()==Account.MsgHandleType.ONLY_RECEIVE.ordinal()||source.getAccountMsgHandleType()==Account.MsgHandleType.NOTHING.ordinal())){
            throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
        }
        for(IncomingMessage incomingMessage:messages.getMessages()){
            if(incomingMessage.getType()!=Envelope.Type.PLAINTEXT.getNumber()){
                throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
            }
        }
        try {
            Group group=null;
            Object unavailableUsers = null;
            if(!StringUtil.isEmpty(gid)) {//发群组消息时，判断群组的有效性及是否在群组中，是否有发消息权限
                int detailMessageType=0;
                if(messages!=null&&messages.getMessages().size()>0){
                    detailMessageType=messages.getMessages().get(0).getDetailMessageType();
                }
                final GroupManagerWithTransaction groupManager = accountsManager.getGroupManager();
                group= groupManager.getGroupWithPermissionCheckForSendMsg(source, gid,detailMessageType);
                groupManager.updateGroupActiveTime(group);
                final List<GroupManagerWithTransaction.UnavailableAccount> unavailableAccounts =
                        groupManager.getUnavailableAccounts(gid);
                if( unavailableAccounts != null && !unavailableAccounts.isEmpty()){
                    unavailableUsers = unavailableAccounts;
                }
            }

            // log
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(messages.getTimestamp()));
            String log = "sendMessage: [" + timestamp + "] " + source.getNumber() + " -> " + gid;
            for (IncomingMessage incomingMessage : messages.getMessages()) {
                log += ", type: " + incomingMessage.getType();
            }
            logger.info(log);

            if (Util.isEmpty(messages.getRelay())) {
                sendLocalMessageForGroup(source, group, messages);
            }
            Long seqNo = messages.getMessages()!= null && messages.getMessages().size() > 0 ? messages.getMessages().get(0).getSequenceId() : null;
            Long notifySeqNo = messages.getMessages()!= null && messages.getMessages().size() > 0 ? messages.getMessages().get(0).getNotifySequenceId() : null;
            Long msgSysTimestamp = messages.getMessages()!= null &&messages.getMessages().size() > 0 ? messages.getMessages().get(0).getSystemShowTimestamp() : 0;
            logger.info("sendMessage: [" + timestamp + "] " + source.getNumber() + " -> " + gid + ".seqNo:" + seqNo+".notifySeqNo:" + notifySeqNo+ ".msgSysTimestamp:" + msgSysTimestamp);
            final SendMessageResponse messageResponse = new SendMessageResponse(false, seqNo, msgSysTimestamp, notifySeqNo);
            messageResponse.setUnavailableUsers(unavailableUsers);
            return messageResponse;
        } catch (NoPermissionException e) {
            throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
        } catch (NoSuchGroupException e) {
            throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
        }
    }
    

    @Timed
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public OutgoingMessageEntityList getPendingMessages(@Auth Account account) {
        assert account.getAuthenticatedDevice().isPresent();

        if (!Util.isEmpty(account.getAuthenticatedDevice().get().getApnId())) {
//      RedisOperation.unchecked(() -> apnFallbackManager.cancel(account, account.getAuthenticatedDevice().get()));
        }

        OutgoingMessageEntityList list=messagesManager.getMessagesForDevice(account.getNumber(),
         account.getAuthenticatedDevice().get().getId());
        return list;
    }

    @Timed
    @POST
    @Path("/getMsg")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse getPendingMessages(@Auth Account account,@Valid  GetShardingMessagesRequst requst) {
        if(requst==null||(StringUtil.isEmpty(requst.getGid())&&StringUtil.isEmpty(requst.getNumber()))){
            BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param ",logger);
        }

        String conversation=null;
        long beginTime=0;
        if(StringUtil.isEmpty(requst.getGid())) {
            conversation=messagesManager.getConversation(requst.getNumber(),account.getNumber(),null);
        }else{
            conversation=messagesManager.getConversation(null,null,requst.getGid());
            try{
                GroupMember groupMember=accountsManager.getGroupManager().getMemberWithPermissionCheck(account,conversation,account.getNumber());
                if(groupMember==null){
                    throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
                }
                beginTime=groupMember.getCreate_time();
            } catch (NoPermissionException e) {
                throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
            } catch (NoSuchGroupException e) {
                throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
            }
        }
        if(requst.getSequenceIds()!=null&&requst.getSequenceIds().size()>0){
            List<OutgoingMessageEntityForSharding> list=messagesManager.getMessagesBySequenceIds(conversation,requst.getSequenceIds(),beginTime);
            long surplus=messagesManager.deletePushMessages(account,conversation,list);
            return BaseResponse.ok(new GetShardingMessagesResponse(messageToString(list),surplus));
        }
        if(requst.getMaxSequenceId()!=null){
            if(requst.getMinSequenceId()!=null){
                if((requst.getMaxSequenceId()-requst.getMinSequenceId())>Messages.RESULT_SET_CHUNK_SIZE){
                    BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param ",logger);
                }
                List<OutgoingMessageEntityForSharding> list=messagesManager.getMessagesBySequenceRange(conversation,requst.getMinSequenceId(),requst.getMaxSequenceId(),beginTime);
                long surplus=messagesManager.deletePushMessages(account,conversation,list);
                return BaseResponse.ok(new GetShardingMessagesResponse(messageToString(list),surplus));
            }

            List<OutgoingMessageEntityForSharding> list=messagesManager.getMessagesByMaxSequenceId(conversation,requst.getMaxSequenceId(),beginTime);
            long surplus=messagesManager.deletePushMessages(account,conversation,list);
            return BaseResponse.ok(new GetShardingMessagesResponse(messageToString(list),surplus));

        }
        List<OutgoingMessageEntityForSharding> list=messagesManager.getMessages(conversation,beginTime);
        list.sort( Comparator.comparingLong(new ToLongFunction<OutgoingMessageEntityForSharding>() {
            @Override
            public long applyAsLong(OutgoingMessageEntityForSharding value) {
                return value.getSequenceId();
            }
        }));
        long surplus=messagesManager.deletePushMessages(account,conversation,list);
        return BaseResponse.ok(new GetShardingMessagesResponse(messageToString(list),surplus));
    }


    //@Timed
    //@POST
    //@Path("/getHotMsg")
    //@Produces(MediaType.APPLICATION_JSON)
    //public BaseResponse getHotMessages(@Auth Account account,@Valid  GetShardingMessagesRequst requst) {
    //    //if(requst==null||(StringUtil.isEmpty(requst.getGid())&&StringUtil.isEmpty(requst.getNumber()))){
    //    //    BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param ",logger);
    //    //}
    //    //
    //    //String conversation=null;
    //    //long beginTime=0;
    //    //if(StringUtil.isEmpty(requst.getGid())) {
    //    //    conversation=messagesManager.getConversation(requst.getNumber(),account.getNumber(),null);
    //    //}else{
    //    //    conversation=messagesManager.getConversation(null,null,requst.getGid());
    //    //    try{
    //    //        GroupMember groupMember=accountsManager.getGroupManager().getMemberWithPermissionCheck(account,conversation,account.getNumber());
    //    //        if(groupMember==null){
    //    //            throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
    //    //        }
    //    //        beginTime=groupMember.getCreate_time();
    //    //    } catch (NoPermissionException e) {
    //    //        throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
    //    //    } catch (NoSuchGroupException e) {
    //    //        throw new WebApplicationException("NoPermission",Response.status(Statuses.from(430,"NoPermission")).build());
    //    //    }
    //    //}
    //    //if(requst.getSequenceIds()!=null&&requst.getSequenceIds().size()>0){
    //    //    List<OutgoingMessageEntityForSharding> list=messagesManager.getMessagesBySequenceIds(conversation,requst.getSequenceIds(),beginTime);
    //    //    return BaseResponse.ok(new GetShardingMessagesResponse(messageToString(list),0));
    //    //}
    //    //if(requst.getMaxSequenceId()!=null){
    //    //    if(requst.getMinSequenceId()!=null){
    //    //        if((requst.getMaxSequenceId()-requst.getMinSequenceId())>Messages.RESULT_SET_CHUNK_SIZE){
    //    //            BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param ",logger);
    //    //        }
    //    //        List<OutgoingMessageEntityForSharding> list=messagesManager.getMessagesBySequenceRange(conversation,requst.getMinSequenceId(),requst.getMaxSequenceId(),beginTime);
    //    //        return BaseResponse.ok(new GetShardingMessagesResponse(messageToString(list),0));
    //    //    }
    //    //
    //    //    List<OutgoingMessageEntityForSharding> list=messagesManager.getMessagesByMaxSequenceId(conversation,requst.getMaxSequenceId(),beginTime);
    //    //    return BaseResponse.ok(new GetShardingMessagesResponse(messageToString(list),0));
    //    //
    //    //}
    //    //if(requst.getMinSequenceId()!=null){
    //    //    List<OutgoingMessageEntityForSharding> list=messagesManager.getMessagesByMinSequenceId(conversation,requst.getMinSequenceId(),beginTime);
    //    //    return BaseResponse.ok(new GetShardingMessagesResponse(messageToString(list),0));
    //    //}
    //    //List<OutgoingMessageEntityForSharding> list=messagesManager.getMessages(conversation,beginTime);
    //    //list.sort( Comparator.comparingLong(new ToLongFunction<OutgoingMessageEntityForSharding>() {
    //    //    @Override
    //    //    public long applyAsLong(OutgoingMessageEntityForSharding value) {
    //    //        return value.getSequenceId();
    //    //    }
    //    //}));
    //    //return BaseResponse.ok(new GetShardingMessagesResponse(messageToString(list),0));
    //}
    private List<String> messageToString(List<OutgoingMessageEntityForSharding> list){
        List<String> returns=new ArrayList<>();
        for(OutgoingMessageEntityForSharding outgoingMessageEntityForSharding:list){
            Optional<byte[]> body = Optional.ofNullable(messagesManager.messageToEnvelope(outgoingMessageEntityForSharding).toByteArray());
            if(body.isPresent()){
                returns.add(Base64.encodeBytes(body.get()));
            }
        }
        return returns;
    }

    @Timed
    @GET
    @Path("/getConversationMsg")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse getConversationMsg(@Auth Account account) {
        //long begin=System.currentTimeMillis();
        List<String> returns=new ArrayList<>();
        //List<ConversationMsgInfos.ConversationMsgInfo> conversationMsgInfos=messagesManager.getConversationMsg(account);
        //logger.info("uid:{},did:{} getConversationMsg messagesManager.getConversationMsg cost:{}",account.getNumber(),account.getAuthenticatedDevice().get().getId(),System.currentTimeMillis()-begin);
        //begin=System.currentTimeMillis();
        //for(ConversationMsgInfos.ConversationMsgInfo conversationMsgInfo:conversationMsgInfos){
        //    Optional<byte[]> body = Optional.ofNullable(conversationMsgInfo.toByteArray());
        //    if(body.isPresent()){
        //        returns.add(Base64.encodeBytes(body.get()));
        //    }
        //}
        //logger.info("uid:{},did:{} getConversationMsg encode cost:{}",account.getNumber(),account.getAuthenticatedDevice().get().getId(),System.currentTimeMillis()-begin);

        return BaseResponse.ok(new GetConversationMsgResponse(returns,false));
    }


    @Timed
    @POST
    @Path("/getMsgSurplus")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse getMessageSurplus(@Auth Account account,@Valid  GetShardingMessagesRequst requst) {
        if(requst==null||(StringUtil.isEmpty(requst.getGid())&&StringUtil.isEmpty(requst.getNumber()))){
            BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param ",logger);
        }

        String conversation=null;
        if(StringUtil.isEmpty(requst.getGid())) {
            conversation=messagesManager.getConversation(requst.getNumber(),account.getNumber(),null);
        }else{
            conversation=messagesManager.getConversation(null,null,requst.getGid());
        }
        long surplus=messagesManager.getMsgSurplus(account,conversation);
        return BaseResponse.ok(new GetShardingMessagesResponse(null,surplus));
    }

    @Timed
    @POST
    @Path("/setPriorConversation")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse setPriorConversation(@Auth Account account, @Valid SetPriorConversationRequest setPriorConversationRequest) {
        if(setPriorConversationRequest==null||(StringUtil.isEmpty(setPriorConversationRequest.getNumber())&&StringUtil.isEmpty(setPriorConversationRequest.getGid()))){
            messagesManager.clearPriorConversation(account.getNumber(), account.getAuthenticatedDevice().get().getId());
        }else {
            messagesManager.setPriorConversation(account.getNumber(), account.getAuthenticatedDevice().get().getId(), setPriorConversationRequest);
        }
        return BaseResponse.ok();
    }

    @Timed
    @DELETE
    @Path("/{source}/{timestamp}")
    public void removePendingMessage(@Auth Account account,
                                     @PathParam("source") String source,
                                     @PathParam("timestamp") long timestamp)
            throws IOException {
//        try {
            WebSocketConnection.messageTime.update(System.currentTimeMillis() - timestamp);

            Optional<OutgoingMessageEntity> message = messagesManager.delete(account.getNumber(),
                    account.getAuthenticatedDevice().get().getId(),
                    source, timestamp);

//            if (message.isPresent() && message.get().getType() != Envelope.Type.RECEIPT_VALUE) {
//                receiptSender.sendReceipt(account,
//                        message.get().getSource(),
//                        message.get().getTimestamp(),
//                        Optional.ofNullable(message.get().getRelay()));
//            }
//        } catch (NotPushRegisteredException e) {
//            logger.info("User no longer push registered for delivery receipt: " + e.getMessage());
//        } catch (NoSuchUserException | TransientPushFailureException e) {
//            logger.warn("Sending delivery receipt", e);
//        }
    }


    private void sendLocalMessage(Account source,
                                  String destinationName,
                                  IncomingMessageList messages,
                                  boolean isSyncMessage)
            throws NoSuchUserException, MismatchedDevicesException, StaleDevicesException {
        Account destination;

        if (!isSyncMessage) destination = getDestinationAccount(destinationName);
        else destination = source;

        validateCompleteDeviceList(source, destination, messages.getMessages(), isSyncMessage);
        validateRegistrationIds(destination, messages.getMessages());
        Optional<String> gidOptional=Optional.empty();
        if(messages.getMessages()!=null&&messages.getMessages().size()>0) {
            if (messages.getMessages().get(0).isReadReceipt()) {
                gidOptional = getMsgGid(messages.getMessages().get(0));
            }
        }
        if(messages.getTimestamp() == 0) messages.setTimestamp(System.currentTimeMillis());
        // 增加消息的SeqNo和系统时间等信息
        Long seqNo = messagesManager.processMessageSeqInfo(source, Optional.of(destinationName),gidOptional, messages.getTimestamp(),false,true);
        long systemTimestamp = System.currentTimeMillis();
        Map<Long,Integer> messageMap=new HashMap<>();
        for (IncomingMessage incomingMessage : messages.getMessages()) {
            if(messageMap.containsKey(incomingMessage.getDestinationDeviceId())){
                messageMap.put(incomingMessage.getDestinationDeviceId(),messageMap.get(incomingMessage.getDestinationDeviceId())+1);
            }else{
                messageMap.put(incomingMessage.getDestinationDeviceId(),1);
            }
        }
        Long notifySeqNo=null;
        if(!messages.isSilent()){
            notifySeqNo = messagesManager.processMessageSeqInfo(source, Optional.of(destinationName), gidOptional, messages.getTimestamp(),true,true);
        }else{
            notifySeqNo = messagesManager.processMessageSeqInfo(source, Optional.of(destinationName), gidOptional, messages.getTimestamp(),true,false);
        }
        for (IncomingMessage incomingMessage : messages.getMessages()) {
            incomingMessage.setSequenceId(seqNo);
            incomingMessage.setSystemShowTimestamp(systemTimestamp);
            incomingMessage.setNotifySequenceId(notifySeqNo);
            boolean checkFriend = false;
            if(!incomingMessage.isReadReceipt()) {//非已读回执消息判断权限
                checkFriend = true;
                Notification notification = incomingMessage.getNotification();
                if (notification != null && notification.getArgs() != null) {
                    String gid=incomingMessage.getNotification().getArgs().getGid();
                    if(!StringUtil.isEmpty(gid)) {//发群组消息时，判断群组的有效性及是否在群组中
                        checkFriend = false; //群组消息不判断好友关系
                        Group group= accountsManager.getGroupManager().getGroupWithPermissionCheck(source, gid);
                        accountsManager.getGroupManager().updateGroupActiveTime(group);
                    }
                }
            }
            //if (checkFriend && !teamsManager.isFriend(source.getNumber(), destinationName) &&
            //        !accountsManager.getGroupManager().inSameGroup(source, destination)) {
            //    throw new NoPermissionException("no permission!");
            //}
            if (messagesManager.isForbidden(source.getNumber(),destinationName)) {
                throw new NoPermissionException("no permission!");
            }
            Optional<Device> destinationDevice = destination.getDevice(incomingMessage.getDestinationDeviceId());

            if (destinationDevice.isPresent()) {
                boolean slient=messages.isSilent();
                if(destinationDevice.get().getReceiveType() == AccountCreateRequest.ReceiveType.KAFKA_VALUE&&messageMap.get(incomingMessage.getDestinationDeviceId())>1
                &&incomingMessage.getType()==7){
                    continue;
                }
                if(messageMap.get(incomingMessage.getDestinationDeviceId())>1&&incomingMessage.getType()!=7){
                    logger.warn("[signal] {} to {},msgType:{},set  slient true,when multiMsg",
                            source.getNumber(), destinationName, incomingMessage.getType());
                    slient=true;
                }
                if (source.getAuthenticatedDevice().isPresent() && source.getAuthenticatedDevice().get().isMaster()){
                    slient = true;
                }
                logger.info("[signal] {} to {},msgType:{},force slient:{} ",
                        source.getNumber(), destinationName, incomingMessage.getType(), slient);
                sendLocalMessage(source, destination, destinationDevice.get(), messages.getTimestamp(), incomingMessage, !slient);
            }
        }
    }

    private long sendLocalMessageForDestination(Account source,
                                                Account destination,IncomingMessageList messages,boolean isSyncMessage)
            throws NoSuchUserException{
        if(messages==null||messages.getMessages()==null||messages.getMessages().size()==0) return -1;
        Optional<String> gidOptional=Optional.empty();
        if(messages.getMessages()!=null&&messages.getMessages().size()>0) {
            if (messages.getMessages().get(0).isReadReceipt()) {
                gidOptional = getMsgGid(messages.getMessages().get(0));
            }
        }
        Long seqNo=null;
        // 增加消息的SeqNo和系统时间等信息
        if(messagesManager.isSharding(messages.getMessages().get(0),source.getNumber())){
            seqNo  = messagesManager.processMessageSeqInfo(source, Optional.of(destination.getNumber()), gidOptional, messages.getTimestamp(),false,true);
        }else{
            seqNo  = messagesManager.processMessageSeqInfo(source, Optional.of(destination.getNumber()), gidOptional, messages.getTimestamp(),false,false);
        }

        Long notifySeqNo=null;
        if(messagesManager.isSharding(messages.getMessages().get(0),source.getNumber())&&!messages.isSilent()){
            notifySeqNo = messagesManager.processMessageSeqInfo(source, Optional.of(destination.getNumber()), gidOptional, messages.getTimestamp(),true,true);
        }else{
            notifySeqNo = messagesManager.processMessageSeqInfo(source, Optional.of(destination.getNumber()), gidOptional, messages.getTimestamp(),true,false);
        }
        long systemTimestamp = System.currentTimeMillis();
        for (IncomingMessage incomingMessage : messages.getMessages()) {
            incomingMessage.setSequenceId(seqNo);
            incomingMessage.setNotifySequenceId(notifySeqNo);
            incomingMessage.setSystemShowTimestamp(systemTimestamp);
            if(!incomingMessage.isReadReceipt()) {//非已读回执消息判断权限
                logger.info("sendLocalMessageForDestination source:{},destination:{},not ReadReceipt",source.getNumber(),destination.getNumber());
                //if (!teamsManager.isFriend(source.getNumber(), destination.getNumber()) &&
                //        !accountsManager.getGroupManager().inSameGroup(source, destination)) {
                //    logger.warn("sendLocalMessageForDestination source:{},destination:{},not Friend",source.getNumber(),destination.getNumber());
                //    throw new NoPermissionException("no permission!");
                //}
                if (messagesManager.isForbidden(source.getNumber(),destination.getNumber())) {
                    throw new NoPermissionException("no permission!");
                }
            }

            sendLocalMessage(source, destination, messages.getTimestamp(), incomingMessage,  !messages.isSilent(),isSyncMessage);

            if(incomingMessage.getMsgType()==Envelope.MsgType.MSG_RECALL_VALUE){
                recallMsgHandle(incomingMessage,source,destination.getNumber(),null);
            }
        }
        if(isSyncMessage&&messages.getMessages()!=null&&messages.getMessages().size()>0){
            syncReadReceiptHandle(messages,source);
        }
        return seqNo;
    }

    private void syncReadReceiptHandle(IncomingMessageList messages,Account source){
        executor.submit(new Runnable() {
            @Override
            public void run() {
                IncomingMessage incomingMessage=messages.getMessages().get(0);
                final Optional<Device> authenticatedDevice = source.getAuthenticatedDevice();
                final Device device = authenticatedDevice.get();
                if(incomingMessage.getMsgType()==Envelope.MsgType.MSG_SYNC_READ_RECEIPT_VALUE){
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
                }else {
                    Optional<byte[]> messageContent = getMessageContent(incomingMessage);
                    if(messageContent.isPresent()) {
                        MessageContent.Content content=null;
                        try {
                            content = MessageContent.Content.parseFrom(messageContent.get());
                        } catch (InvalidProtocolBufferException e) {
                            logger.error("syncMessage parse error! ",e);
                        }
                        if(content!=null&&content.hasSyncMessage()&&content.getSyncMessage().getReadCount()>0){
                           List<MessageContent.SyncMessage.Read> readList= content.getSyncMessage().getReadList();
                            List<ReadReceipt> readReceipts=new ArrayList<>();
                            Long now=System.currentTimeMillis();
                            for(MessageContent.SyncMessage.Read read:readList){
                               if(read.hasReadPosition()){
                                   String gid=null;
                                   String sender=null;
                                   if(read.getReadPosition().hasGroupId()&&!StringUtil.isEmpty(read.getReadPosition().getGroupId().toStringUtf8())){
                                       gid=read.getReadPosition().getGroupId().toStringUtf8();
                                   }else {
                                       sender=read.getSender();
                                   }
                                   String conversation=messagesManager.getConversation(sender,source.getNumber(),gid);
                                   ReadReceipt readReceipt = new ReadReceipt(conversation, source.getNumber(), device.getId(),read.getReadPosition().getMaxServerTimestamp(),read.getReadPosition().getReadAt(),0,now);
                                   readReceipts.add(readReceipt);
                               }
                            }
                            if(readReceipts.size()>0){
                                readReceiptsManager.insertBatch(readReceipts);
                            }
                        }
                    }
                }
            }
        });
    }

    private void recallMsgHandle(IncomingMessage incomingMessage,Account source,String destination,String gid){
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
    private long sendLocalMessageForDestinations(Account source,
                                  Account destination,IncomingMessageForDestinations incomingMessage)
            throws NoSuchUserException{
        if(incomingMessage.getTimestamp() == 0) incomingMessage.setTimestamp(System.currentTimeMillis());
        Optional<String> gidOptional=Optional.empty();
        if(incomingMessage.isReadReceipt()){
            gidOptional=getMsgGid(incomingMessage);
        }
        Long seqNo=null;
        // 增加消息的SeqNo和系统时间等信息
        if(messagesManager.isSharding(incomingMessage,source.getNumber())){
            seqNo  = messagesManager.processMessageSeqInfo(source, Optional.of(destination.getNumber()), gidOptional, incomingMessage.getTimestamp(),false,true);
        }else{
            seqNo  = messagesManager.processMessageSeqInfo(source, Optional.of(destination.getNumber()), gidOptional, incomingMessage.getTimestamp(),false,false);
        }

        incomingMessage.setSequenceId(seqNo);
        Long notifySeqNo=null;
        if(!incomingMessage.isSilent()){
            notifySeqNo = messagesManager.processMessageSeqInfo(source, Optional.of(destination.getNumber()),gidOptional, incomingMessage.getTimestamp(),true,true);
        }else{
            notifySeqNo = messagesManager.processMessageSeqInfo(source, Optional.of(destination.getNumber()),gidOptional, incomingMessage.getTimestamp(),true,false);
        }
        incomingMessage.setNotifySequenceId(notifySeqNo);

        //if(!incomingMessage.isReadReceipt()) {//非已读回执消息判断权限
        //    if (!teamsManager.isFriend(source.getNumber(), destination.getNumber())&&
        //            !accountsManager.getGroupManager().inSameGroup(source, destination)) {
        //        throw new NoPermissionException("no permission!");
        //    }
        //}

        sendLocalMessage(source, destination, incomingMessage.getTimestamp(), incomingMessage, !incomingMessage.isSilent(),false);

        return seqNo;
    }

    private void sendLocalMessageForGroup(Account source,
                                  Group group,
                                  IncomingMessageList messages) {
        if(messages.getTimestamp() == 0) messages.setTimestamp(System.currentTimeMillis());
        if(messages==null||messages.getMessages()==null||messages.getMessages().size()==0) return;
        Long seqNo=null;
        // 增加消息的SeqNo和系统时间等信息
        if(messagesManager.isSharding(messages.getMessages().get(0),source.getNumber())){
            seqNo  = messagesManager.processMessageSeqInfo(source, Optional.empty(), Optional.of(group.getId()), messages.getTimestamp(),false,true);
        }else{
            seqNo  = messagesManager.processMessageSeqInfo(source, Optional.empty(), Optional.of(group.getId()), messages.getTimestamp(),false,false);
        }
        Long notifySeqNo=null;
        if(messagesManager.isSharding(messages.getMessages().get(0),source.getNumber())&&!messages.isSilent()){
            notifySeqNo = messagesManager.processMessageSeqInfo(source, Optional.empty(), Optional.of(group.getId()), messages.getTimestamp(),true,true);
        }else{
            notifySeqNo = messagesManager.processMessageSeqInfo(source, Optional.empty(), Optional.of(group.getId()), messages.getTimestamp(),true,false);
        }
        long systemTimestamp = System.currentTimeMillis();
        for (IncomingMessage incomingMessage : messages.getMessages()) {
            incomingMessage.setSequenceId(seqNo);
            incomingMessage.setSystemShowTimestamp(systemTimestamp);
            incomingMessage.setNotifySequenceId(notifySeqNo);
            sendLocalMessageForGroup(source, group, messages.getTimestamp(), incomingMessage, !messages.isSilent());
            if(incomingMessage.getMsgType()==Envelope.MsgType.MSG_RECALL_VALUE){
                recallMsgHandle(incomingMessage,source,null,group.getId());
            }
        }
    }

    private void sendLocalMessageForGroup(Account source,
                                  Group group,
                                  long timestamp,
                                  IncomingMessage incomingMessage,
                                  boolean notify) {
        Optional<byte[]> messageBody = getMessageBody(incomingMessage);
        Optional<byte[]> messageContent = getMessageContent(incomingMessage);
        Envelope.Builder messageBuilder = Envelope.newBuilder();
        messageBuilder.setType(Envelope.Type.valueOf(incomingMessage.getType()))
                .setSource(source.getNumber())
                .setTimestamp(timestamp == 0 ? System.currentTimeMillis() : timestamp)
                .setSourceDevice((int) source.getAuthenticatedDevice().get().getId())
                .setSequenceId(incomingMessage.getSequenceId())
                .setNotifySequenceId(incomingMessage.getNotifySequenceId())
                .setMsgType(Envelope.MsgType.valueOf(incomingMessage.getMsgType()))
                .setSystemShowTimestamp(incomingMessage.getSystemShowTimestamp());
        if (messageBody.isPresent()) {
            messageBuilder.setLegacyMessage(ByteString.copyFrom(messageBody.get()));
        }
        if (messageContent.isPresent()) {
            messageBuilder.setContent(ByteString.copyFrom(messageContent.get()));
        }
        if (source.getRelay().isPresent()) {
            messageBuilder.setRelay(source.getRelay().get());
        }


        Envelope envelope = messageBuilder.build();
            try {
                SendMessageLogHandler.SendMessageLog sendMessageLog = new SendMessageLogHandler.SendMessageLog(SendMessageLogHandler.SendMessageAction.REVICE.getName(), group.getId(), -1, notify, false, envelope);
                SendMessageLogHandler.send(sendMessageLog);
            } catch (Exception e) {
                logger.error("SendMessageLog error!" + e.getMessage());
            }
        if(incomingMessage.getNotification()!=null&&incomingMessage.getNotification().getArgs()!=null) {
            incomingMessage.getNotification().getArgs().setSource(source);
        }
        pushSender.sendMessageForGroup(group, envelope, notify, false, incomingMessage, incomingMessage.getNotification());
    }

    private void sendLocalMessage(Account source,
                                  Account destinationAccount,
                                  Device destinationDevice,
                                  long timestamp,
                                  IncomingMessage incomingMessage,
                                  boolean notify)
            throws NoSuchUserException {
        try {
            logger.info("[signal] in sendLocalMessage, source:{},dst:{},notify:{}",
                    source.getNumber(), destinationAccount.getNumber(), notify);
            Optional<byte[]> messageBody = getMessageBody(incomingMessage);
            Optional<byte[]> messageContent = getMessageContent(incomingMessage);
            Envelope.Builder messageBuilder = Envelope.newBuilder();
            messageBuilder.setType(Envelope.Type.valueOf(incomingMessage.getType()))
                    .setSource(source.getNumber())
                    .setTimestamp(timestamp == 0 ? System.currentTimeMillis() : timestamp)
                    .setSourceDevice((int) source.getAuthenticatedDevice().get().getId())
                    .setSequenceId(incomingMessage.getSequenceId())
                    .setNotifySequenceId(incomingMessage.getNotifySequenceId())
                    .setMsgType(Envelope.MsgType.valueOf(incomingMessage.getMsgType()))
                    .setSystemShowTimestamp(incomingMessage.getSystemShowTimestamp());
            if (messageBody.isPresent()) {
                messageBuilder.setLegacyMessage(ByteString.copyFrom(messageBody.get()));
            }

            if (messageContent.isPresent()) {
                messageBuilder.setContent(ByteString.copyFrom(messageContent.get()));
            }

            if (source.getRelay().isPresent()) {
                messageBuilder.setRelay(source.getRelay().get());
            }


            boolean silent = false;

            Envelope envelope = messageBuilder.build();
            try {
                SendMessageLogHandler.SendMessageLog sendMessageLog = new SendMessageLogHandler.SendMessageLog(SendMessageLogHandler.SendMessageAction.REVICE.getName(), destinationAccount.getNumber(), destinationDevice.getId(), notify, silent, envelope);
                SendMessageLogHandler.send(sendMessageLog);
            } catch (Exception e) {
                logger.error("SendMessageLog error!" + e.getMessage());
            }
            if(incomingMessage.getNotification()!=null&&incomingMessage.getNotification().getArgs()!=null) {
                incomingMessage.getNotification().getArgs().setSource(source);
            }
            pushSender.sendMessage(destinationAccount, destinationDevice, envelope, notify, silent, incomingMessage.getNotification(),incomingMessage.isReadReceipt(),incomingMessage,-1);
        } catch (NotPushRegisteredException e) {
            if (destinationDevice.isMaster()) throw new NoSuchUserException(e);
            else logger.debug("Not registered", e);
        }
    }

    private void sendLocalMessage(Account source,
                                  Account destination,
                                  long timestamp,
                                  IncomingMessage incomingMessage,
                                  boolean notify,
                                  boolean isSyncMessage)
            throws NoSuchUserException {
            Optional<byte[]> messageBody = getMessageBody(incomingMessage);
            Optional<byte[]> messageContent = getMessageContent(incomingMessage);
            Envelope.Builder messageBuilder = Envelope.newBuilder();
            messageBuilder.setType(Envelope.Type.valueOf(incomingMessage.getType()))
                    .setSource(source.getNumber())
                    .setTimestamp(timestamp == 0 ? System.currentTimeMillis() : timestamp)
                    .setSourceDevice((int) source.getAuthenticatedDevice().get().getId())
                    .setSequenceId(incomingMessage.getSequenceId())
                    .setNotifySequenceId(incomingMessage.getNotifySequenceId())
                    .setMsgType(Envelope.MsgType.valueOf(incomingMessage.getMsgType()))
                    .setSystemShowTimestamp(incomingMessage.getSystemShowTimestamp());
            if (messageBody.isPresent()) {
                messageBuilder.setLegacyMessage(ByteString.copyFrom(messageBody.get()));
            }

            if (messageContent.isPresent()) {
                messageBuilder.setContent(ByteString.copyFrom(messageContent.get()));
            }

            if (source.getRelay().isPresent()) {
                messageBuilder.setRelay(source.getRelay().get());
            }


            boolean silent = false;

            Envelope envelope = messageBuilder.build();

            //String conversation=messagesManager.getConversation(destination.getNumber(),source.getNumber(),null);
            long shardMsgId = 0;//pushSender.getWebSocketSender().insertForSharding(envelope,notify,conversation,incomingMessage.getConversation());

            Set<Device> devices=destination.getDevices();
            logger.info("in sendLocalMessage,dest:{},deviceSize :{},isSyncMessage:{}",destination.getNumber(),devices.size(),isSyncMessage);
            for(Device destinationDevice:devices) {
                if (accountsManager.isActiveDevice(destinationDevice,destination)&&(!isSyncMessage||
                        // 同步消息时，deviceID不能相同
                        (isSyncMessage&&destinationDevice.getId()!=source.getAuthenticatedDevice().get().getId()))) {
                    try {
                        SendMessageLogHandler.SendMessageLog sendMessageLog = new SendMessageLogHandler.SendMessageLog(SendMessageLogHandler.SendMessageAction.REVICE.getName(), destination.getNumber(), destinationDevice.getId(), notify, silent, envelope);
                        SendMessageLogHandler.send(sendMessageLog);
                    } catch (Exception e) {
                        logger.error("SendMessageLog error!" + e.getMessage());
                    }
                    if(incomingMessage.getNotification()!=null&&incomingMessage.getNotification().getArgs()!=null) {
                        incomingMessage.getNotification().getArgs().setSource(source);
                    }
                    try {
                        final int detailMessageType = incomingMessage.getDetailMessageType();
                        notify = !incomingMessage.isReadReceipt() &&
                                detailMessageType != IncomingMessage.DetailMessageType.CARD.getCode() &&
                                detailMessageType != IncomingMessage.DetailMessageType.REACTION.getCode() &&
                                !isSyncMessage;
                        silent = !notify;
                        pushSender.sendMessage(destination, destinationDevice, envelope, notify, silent, incomingMessage.getNotification(),incomingMessage.isReadReceipt(),incomingMessage,shardMsgId);
                    } catch (NotPushRegisteredException e) {
                        if (destinationDevice.isMaster()) throw new NoSuchUserException(e);
                        else logger.debug("Not registered", e);
                    }
                }else {
                    logger.warn("do not pushSender.sendMessage,dest:{}, deviceID:{}, isActiveDevice:{},isSyncMessage:{},sourceDeviceID:{} ",
                            destination.getNumber(), destinationDevice.getId(), accountsManager.isActiveDevice(destinationDevice, destination),
                            isSyncMessage, source.getAuthenticatedDevice().get().getId());
                }
            }

    }

    private void sendRelayMessage(Account source,
                                  String destinationName,
                                  IncomingMessageList messages,
                                  boolean isSyncMessage)
            throws IOException, NoSuchUserException, InvalidDestinationException {
        if (isSyncMessage) throw new InvalidDestinationException("Transcript messages can't be relayed!");

        try {
            FederatedClient client = federatedClientManager.getClient(messages.getRelay());
            client.sendMessages(source.getNumber(), source.getAuthenticatedDevice().get().getId(),
                    destinationName, messages);
        } catch (NoSuchPeerException e) {
            throw new NoSuchUserException(e);
        }
    }

    private Account getDestinationAccount(String destination)
            throws NoSuchUserException {
        Optional<Account> account = accountsManager.get(destination);

        if (!account.isPresent() || ! accountsManager.isActive(account.get())) {
            throw new NoSuchUserException(destination);
        }

        return account.get();
    }

    private void validateRegistrationIds(Account account, List<IncomingMessage> messages)
            throws StaleDevicesException {
        List<Long> staleDevices = new LinkedList<>();

        for (IncomingMessage message : messages) {
            Optional<Device> device = account.getDevice(message.getDestinationDeviceId());

            if (device.isPresent() &&
                    message.getDestinationRegistrationId() > 0 &&
                    message.getDestinationRegistrationId() != device.get().getRegistrationId()) {
                staleDevices.add(device.get().getId());
            }
        }

        if (!staleDevices.isEmpty()) {
            throw new StaleDevicesException(staleDevices);
        }
    }

    private void validateCompleteDeviceList(Account source,
                                            Account destination,
                                            List<IncomingMessage> messages,
                                            boolean isSyncMessage)
            throws MismatchedDevicesException {
        Set<Long> messageDeviceIds = new HashSet<>();
        Set<Long> accountDeviceIds = new HashSet<>();

        List<Long> missingDeviceIds = new LinkedList<>();
        List<Long> extraDeviceIds = new LinkedList<>();

        for (IncomingMessage message : messages) {
            messageDeviceIds.add(message.getDestinationDeviceId());
        }

        for (Device device : destination.getDevices()) {
            if (accountsManager.isSignalActiveDevice(device) &&
                    !(isSyncMessage && device.getId() == destination.getAuthenticatedDevice().get().getId())) {
                accountDeviceIds.add(device.getId());

                if (!messageDeviceIds.contains(device.getId())) {
                    missingDeviceIds.add(device.getId());
                }
            }
        }

        for (IncomingMessage message : messages) {
            if (!accountDeviceIds.contains(message.getDestinationDeviceId())) {
                extraDeviceIds.add(message.getDestinationDeviceId());
            }
        }
        Optional<Device> authenticatedDevice = source.getAuthenticatedDevice();
        if (!missingDeviceIds.isEmpty() || !extraDeviceIds.isEmpty()) {
            if (authenticatedDevice.isPresent() && authenticatedDevice.get().getId() == 1 &&
                    !source.getNumber().equals(destination.getNumber())) { // 主设备先不报错 , 忽略自己给自己发消息的情况
                logger.warn("Mismatched devices , {} to account: {}, missing: {}, extra: {}",
                        source.getNumber(), destination.getNumber(),missingDeviceIds, extraDeviceIds);
            } else
                throw new MismatchedDevicesException(missingDeviceIds, extraDeviceIds);
        }
    }

    private Optional<byte[]> getMessageBody(IncomingMessage message) {
        if (Util.isEmpty(message.getBody())) return Optional.empty();

        try {
            return Optional.of(Base64.decode(message.getBody()));
        } catch (IOException ioe) {
            logger.debug("Bad B64", ioe);
            return Optional.empty();
        }
    }

    private Optional<byte[]> getMessageContent(IncomingMessage message) {
        if (Util.isEmpty(message.getContent())) return Optional.empty();

        try {
            return Optional.of(Base64.decode(message.getContent()));
        } catch (IOException ioe) {
            logger.debug("Bad B64", ioe);
            return Optional.empty();
        }
    }
    private Optional<String> getMsgGid(IncomingMessage incomingMessage){
//        if(incomingMessage.getMsgType()==Envelope.MsgType.MSG_READ_RECEIPT_VALUE&&incomingMessage.getConversation()!=null){
//            if(!StringUtil.isEmpty(incomingMessage.getConversation().getGid())){
//                return Optional.of(incomingMessage.getConversation().getGid());
//            }
//        }
//        Notification notification = incomingMessage.getNotification();
//        if (notification != null && notification.getArgs() != null) {
//            String gid = incomingMessage.getNotification().getArgs().getGid();
//            if(!StringUtil.isEmpty(gid)){
//                return Optional.of(gid);
//            }
//        }
        return Optional.empty();
    }

    public static void main(String[] args) throws IOException {
        String str="Cg4KDCs3Mzg5NTc3MDI2MRISEK+frOS8MBi1nqzkvDAgLChEGAAiQQgHEgwrNzM4OTU3NzAyNjEo1vTX+LwwOAFCGQoXCgVq4oCGZCiAmp4BONb01/i8MFIAYABYR2D59Nf4vDBoAXAvMC44PkAnSEc=";
        byte[] a=Base64.decode(str);
        ConversationMsgInfos.ConversationMsgInfo conversationMsgInfo=ConversationMsgInfos.ConversationMsgInfo.parseFrom(a);
        System.out.println(conversationMsgInfo.getLastestMsgSId());

        Envelope envelope= Envelope.parseFrom(Base64.decode("CAcSDCs3Mzg5NTc3MDI2MSiR3KCuvDA4AUJDCkEKCWTigIZz4oCGcxokCiAwOGMwNWIzNDMxNTY0ZDJhYTBlYmU0OGY4NTE1ZmY5YxACKICangE4kdygrrwwUgBgAFgMYITgoK68MGgBcAM="));

        System.out.println(envelope.getSource());

    }

}
