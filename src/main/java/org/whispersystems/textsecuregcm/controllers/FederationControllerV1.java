/**
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
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.federation.FederatedPeer;
import org.whispersystems.textsecuregcm.federation.NonLimitedAccount;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.util.Util;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/v1/federation")
public class FederationControllerV1 extends FederationController {

  private final Logger logger = LoggerFactory.getLogger(FederationControllerV1.class);

  private static final int ACCOUNT_CHUNK_SIZE = 10000;

  public FederationControllerV1(AccountsManager accounts,
                                AttachmentController attachmentController,
                                MessageController messageController)
  {
    super(accounts, attachmentController, messageController);
  }

  @Timed
  @GET
  @Path("/attachment/{attachmentId}")
  @Produces(MediaType.APPLICATION_JSON)
  public AttachmentUri getSignedAttachmentUri(@Auth                      FederatedPeer peer,
                                              @PathParam("attachmentId") long attachmentId)
          throws IOException, RateLimitExceededException {
    return attachmentController.redirectToAttachment(new NonLimitedAccount("Unknown", -1, peer.getName()),
                                                     attachmentId, Optional.empty());
  }

  @Timed
  @PUT
  @Path("/messages/{source}/{sourceDeviceId}/{destination}")
  public void sendMessages(@Auth                        FederatedPeer peer,
                           @PathParam("source")         String source,
                           @PathParam("sourceDeviceId") long sourceDeviceId,
                           @PathParam("destination")    String destination,
                           @Valid                       IncomingMessageList messages)
      throws IOException
  {
    try {
      messages.setRelay(null);
      messageController.sendMessage(new NonLimitedAccount(source, sourceDeviceId, peer.getName()), destination, messages);
    } catch (RateLimitExceededException e) {
      logger.warn("Rate limiting on federated channel", e);
      throw new IOException(e);
    }
  }

  @Timed
  @GET
  @Path("/user_count")
  @Produces(MediaType.APPLICATION_JSON)
  public AccountCount getUserCount(@Auth FederatedPeer peer) {
    return new AccountCount((int)accounts.getCount());
  }

  @Timed
  @GET
  @Path("/user_tokens/{offset}")
  @Produces(MediaType.APPLICATION_JSON)
  public ClientContacts getUserTokens(@Auth                FederatedPeer peer,
                                      @PathParam("offset") int offset)
  {
    Map<String, Account> accountList    = accounts.getAll(offset, ACCOUNT_CHUNK_SIZE);
    List<ClientContact> clientContacts = new LinkedList<>();

    for (Account account : accountList.values()) {
      byte[]        token         = Util.getContactToken(account.getNumber());
      ClientContact clientContact = new ClientContact(token, null, accounts.isVoiceSupported(account), accounts.isVideoSupported(account));

      if (!accounts.isActive(account)) {
        clientContact.setInactive(true);
      }

      clientContacts.add(clientContact);
    }

    return new ClientContacts(clientContacts);
  }
}
