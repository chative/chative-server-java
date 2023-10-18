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

import com.codahale.metrics.annotation.Timed;
import com.google.protobuf.ByteString;
import io.dropwizard.auth.Auth;
import org.glassfish.jersey.message.internal.Statuses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.entities.MessageProtos.Envelope;
import org.whispersystems.textsecuregcm.exceptions.NoPermissionException;
import org.whispersystems.textsecuregcm.exceptions.NoSuchGroupException;
import org.whispersystems.textsecuregcm.federation.FederatedClient;
import org.whispersystems.textsecuregcm.federation.FederatedClientManager;
import org.whispersystems.textsecuregcm.federation.NoSuchPeerException;
import org.whispersystems.textsecuregcm.internal.accounts.AccountCreateRequest;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.push.ApnFallbackManager;
import org.whispersystems.textsecuregcm.push.NotPushRegisteredException;
import org.whispersystems.textsecuregcm.push.PushSender;
import org.whispersystems.textsecuregcm.push.ReceiptSender;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.Base64;
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
import java.util.function.ToLongFunction;

@Path("/v1/readReceipt")
public class ReadReceiptController {

    private final Logger logger = LoggerFactory.getLogger(ReadReceiptController.class);

    private final RateLimiters rateLimiters;
    private final ReadReceiptsManager readReceiptsManager;
    private final MessagesManager messagesManager;
    // edited by guolilei

    public ReadReceiptController(RateLimiters rateLimiters,
                                 ReadReceiptsManager readReceiptsManager,
                                 MessagesManager messagesManager) {
        this.rateLimiters = rateLimiters;
        this.readReceiptsManager=readReceiptsManager;
        this.messagesManager=messagesManager;
    }

    @Timed
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse uploadReadReceipt(@Auth Account source,
                                           @Valid UploadReadReceiptRequest requst)
            throws RateLimitExceededException {
        rateLimiters.getCustomizeLimiter("limiter_uploadReadReceipt", 300, 300).validate(source.getNumber()+"_"+source.getAuthenticatedDevice().get().getId());
        if(requst==null||StringUtil.isEmpty(requst.getConversation())||requst.getConversationType()==null||requst.getMaxServerTimestamp()==null||requst.getReadAt()==null||requst.getNotifySequenceId()==null){
            BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param ",logger);
        }
        if(GetShardingMessagesRequst.DESTINATION_TYPE.fromOrdinal(requst.getConversationType())==null){
            BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param ",logger);
        }
        String conversation=null;
        if(GetShardingMessagesRequst.DESTINATION_TYPE.fromOrdinal(requst.getConversationType()).equals(GetShardingMessagesRequst.DESTINATION_TYPE.PRIVATE)) {
            conversation=messagesManager.getConversation(requst.getConversation(),source.getNumber(),null);
        }else{
            conversation=messagesManager.getConversation(null,null,requst.getConversation());
        }
        readReceiptsManager.insert(new ReadReceipt(conversation,source.getNumber(),source.getAuthenticatedDevice().get().getId(),requst.getMaxServerTimestamp(),requst.getReadAt(),requst.getNotifySequenceId(),System.currentTimeMillis()));
        return BaseResponse.ok(null);
    }

}
