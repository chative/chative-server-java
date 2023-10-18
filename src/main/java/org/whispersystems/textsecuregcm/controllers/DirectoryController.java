/**
 * Copyright (C) 2013 Open WhisperSystems
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.whispersystems.textsecuregcm.controllers;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.annotation.Timed;
import io.dropwizard.auth.Auth;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.directorynotify.DirectoryNotifyManager;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.push.WebsocketSender;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.Base64;
import org.whispersystems.textsecuregcm.util.Constants;
import org.whispersystems.textsecuregcm.util.ParameterValidator;
import org.whispersystems.textsecuregcm.util.RandomString;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.codahale.metrics.MetricRegistry.name;

@Path("/v1/directory")
public class DirectoryController {

    private final Logger logger = LoggerFactory.getLogger(DirectoryController.class);
    private final MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME);
    private final Histogram contactsHistogram = metricRegistry.histogram(name(getClass(), "contacts"));

    private final RateLimiters rateLimiters;
    private final DirectoryManager directory;
    private final AccountsManager mAccounts;
    private final TeamsManagerCasbin teamsManager;
    private final DirectoryNotifyManager directoryNotifyManager;
    private final InternalAccountsInvitationTable mInternalAccountsInvitationTable;
    private final InteractManager interactManager;
    private final ConversationManager conversationManager;

    private final RandomString mRandomString = new RandomString(32);

    public DirectoryController(){
        rateLimiters=null;
        directory=null;
        mAccounts=null;
        teamsManager=null;
        mInternalAccountsInvitationTable=null;
        directoryNotifyManager=null;
        interactManager=null;
        conversationManager=null;
    };
    public DirectoryController(RateLimiters rateLimiters,
                               DirectoryManager directory,
                               AccountsManager accounts,
                               TeamsManagerCasbin teamsManager,
                               WebsocketSender websocketSender,
                               InternalAccountsInvitationTable internalAccountsInvitationTable,
                               DirectoryNotifyManager directoryNotifyManager,
                               InteractManager interactManager,
                                 ConversationManager conversationManager) {
        this.directory = directory;
        this.rateLimiters = rateLimiters;
        this.mAccounts = accounts;
        this.teamsManager = teamsManager;
        this.mInternalAccountsInvitationTable = internalAccountsInvitationTable;
        directoryNotifyManager.setConversationManager(conversationManager);
        this.directoryNotifyManager=directoryNotifyManager;
        this.interactManager=interactManager;
        this.conversationManager=conversationManager;
    }

    @Timed
    @GET
    @Path("/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTokenPresence(@Auth Account account, @PathParam("token") String token)
            throws RateLimitExceededException {
        rateLimiters.getContactsLimiter().validate(account.getNumber());

        try {
            Optional<ClientContact> contact = directory.get(decodeToken(token));

            if (contact.isPresent()) return Response.ok().entity(contact.get()).build();
            else return Response.status(404).build();

        } catch (IOException e) {
            logger.info("Bad token", e);
            return Response.status(404).build();
        }
    }

    @Timed
    @PUT
    @Path("/tokens")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ClientContacts getContactIntersection(@Auth Account account, @Valid ClientContactTokens contacts)
            throws RateLimitExceededException {
        rateLimiters.getContactsLimiter().validate(account.getNumber(), contacts.getContacts().size());
        contactsHistogram.update(contacts.getContacts().size());

        try {
            List<byte[]> tokens = new LinkedList<>();

            for (String encodedContact : contacts.getContacts()) {
                tokens.add(decodeToken(encodedContact));
            }

            List<ClientContact> intersection = directory.get(tokens);
            return new ClientContacts(intersection);
        } catch (IOException e) {
            logger.info("Bad token", e);
            throw new WebApplicationException(Response.status(400).build());
        }
    }

    @Timed
    @GET
    @Path("/internal/accounts{number : (/\\+\\d{5,11})?}")
    @Produces(MediaType.APPLICATION_JSON)
    public InternalAccounts getInternalAccounts(@Auth Account account, @PathParam("number") String number) {
        List<InternalAccount> result = new ArrayList<>();

        Map<String, Account> contacts = teamsManager.getContacts(account);
        if (!number.isEmpty()) {
            // remove path separator
            number = number.substring(1);
            Account a = contacts.get(number);
            if (null == a) {
                throw new WebApplicationException(Response.status(404).build());
            }
            result.add(new InternalAccount(a.getNumber(), a.getPlainName(),  null, null, null));
        } else {
            for (Map.Entry<String, Account> contact : contacts.entrySet()) {
                result.add(new InternalAccount(contact.getKey(), contact.getValue().getPlainName(),  null, null, null));
            }
            logger.info("getContacts,return Number:{},size:{}", account.getNumber(), result.size());
        }

        return new InternalAccounts(result,directoryNotifyManager.getDirectoryVersion(account.getNumber()));
    }

    @Timed
    @GET
    @Path("/extInfo/{number : (\\+\\d{5,11})?}")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse getAccountExtInfo(@Auth Account account, @PathParam("number") String number) {
        if (!number.isEmpty()) {
            Optional<Account> accountOptional=mAccounts.get(number);
            if(accountOptional.isPresent()&&!accountOptional.get().isinValid()){
                return BaseResponse.ok(new AccountExtInfoResponse(accountOptional.get().getExtId()));
            }
        } else {
            BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "Invalid param" , logger);
        }
        BaseResponse.err(BaseResponse.STATUS.NO_SUCH_USER, "Account is not exists or inValid !" , logger);
        return null;
    }

    @Timed
    @POST
    @Path("/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse getContacts(@Auth Account account,
                                    @HeaderParam("Accept-Language") String lang,
                                    @QueryParam("properties") String properties,
                                    @Valid GetContactsRequest request) {
        List<Contacts.Contact> result = new ArrayList<>();
        Long contactsVer = null;
        Long begin=System.currentTimeMillis();
        Map<String, Account> contacts = teamsManager.getContacts(account);//teamsManager.getContactsForCaffeine(account);
        final Map<String, Conversation> conversations = getConversations(account.getNumber(), request);
        if (null == request || null == request.getUids()) {
            logger.info("number:{} ,DirectoryController.teamsManagerGetContactsForUid used:{}",account.getNumber(),System.currentTimeMillis()-begin);
            Long begin2=System.currentTimeMillis();
            for (Map.Entry<String, Account> contact : contacts.entrySet()) {
                result.add(buildContact(account, contact.getValue(),lang,conversations, properties,null).build());
            }
            contactsVer = directoryNotifyManager.getDirectoryVersion(account.getNumber());
            logger.info("number:{} ,DirectoryController.teamsManagerGetContactsForUid buildContact used:{}",account.getNumber(),System.currentTimeMillis()-begin2);

        } else {
            logger.info("number:{} ,DirectoryController.teamsManagerGetContactsForAll used:{}",account.getNumber(),System.currentTimeMillis()-begin);
            Long begin2=System.currentTimeMillis();
            // validate parameters
            request.validate(logger);

            for (String uid : request.getUids()) {
                // in at least one same team
                Account contact = contacts.get(uid);
                if (null == contact) {
                    //BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, "no permission to get the user's info: " + uid, logger);
                    final Optional<Account> accountOptional = mAccounts.get(uid);
                    if (!accountOptional.isPresent())continue;
                    contact = accountOptional.get();
                } else if (account.getNumber().equals(uid)) {//解决自己更新信息后，立即获取自己的个人信息时，因team缓存未刷新，不能获取最新信息的问题
                    contact = mAccounts.get(uid).get();
                }
                GiveInteractResponse thumbsUp = interactManager.get(uid, InteractsTable.TYPE.THUMBS_UP.getCode());
                result.add(buildContact(account, contact,lang, conversations,properties, thumbsUp).build());
            }
            logger.info("number:{} ,DirectoryController.teamsManagerGetContactsForAll buildContact used:{}",account.getNumber(),System.currentTimeMillis()-begin2);
        }

        logger.info("number:{} ,DirectoryController.getContacts , size:{}, used:{}", account.getNumber(),result.size(),System.currentTimeMillis()-begin);
        return BaseResponse.ok(new Contacts(contactsVer, result));
    }

    private Map<String,Conversation> getConversations(String uid,GetContactsRequest request){
        final List<Conversation> conversations = conversationManager.get(uid, request != null ? request.getUids() : null);
        return conversations.stream().collect(Collectors.toMap(Conversation::getConversation, Function.identity()));
    }

    private Contacts.Contact.Builder buildContact(Account operator, Account account,String lang, final Map<String, Conversation> conversations,String properties,GiveInteractResponse thumbsUp) {
        Contacts.Contact.Builder builder = new Contacts.Contact.Builder();
        if (conversations != null && !conversations.isEmpty()) {
            final Conversation conversation = conversations.get(account.getNumber());
            if (conversation != null) {
                builder.setRemark(conversation.getRemark());
            }
        }
        builder.setNumber(account.getNumber())
                .setJoinedAt(account.formatJoinedDate(lang))
                .setName(account.getPlainName())
                //.setSignature(account.getSignature())
                .setAvatar(account.getAvatar2())
                //.setThumbsUp(thumbsUp)
                .setPublicConfigs(account.getPublicConfigs());
        //builder.setExtId(account.getExtId());
        if (operator.getNumber().equals(account.getNumber())) {
            // properties=all
            builder.setTimeZone(account.getTimeZone())
                    .setDepartment(account.getDepartment())
                    .setSuperior(account.getSuperior())
                    .setGender(account.getGender())
                    .setAddress(account.getAddress())
                    .setProtectedConfigs(account.getProtectedConfigs());
        }
        // all properties if is self
        if (operator.getNumber().equals(account.getNumber())) {
            Optional<Account> accountOptional=mAccounts.get(account.getNumber());
            if(accountOptional.isPresent()) {
                account =accountOptional.get();
            }
            //builder.setEmail(account.getEmail());
            setDefaultValue(account);
            builder.setFlag(account.getFlag())
                    .setPrivateConfigs(account.getPrivateConfigs());
        }
        return builder;
    }
    private void setDefaultValue(Account account){
        if(account.getGlobalNotification()==null){
            account.setPrivateConfig(AccountExtend.FieldName.GLOBAL_NOTIFICATION, mAccounts.getGlobalNotification(account));
        }

    }

    @Timed
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/internal/name/{nameurl}")
    public void setInternalName(@Auth Account account, @PathParam("nameurl") @Length(min=1) String nameurl) {
        try {
            String name = URLDecoder.decode(nameurl, "UTF-8");

            name = ParameterValidator.cutdown(name, 30);

            account.setPlainName(name);
            mAccounts.update(account,null,false);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.status(400).build());
        }
    }

    @Timed
    @POST
    @Path("/internal/account/invitation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse getInternalAccountInvitation(@Auth Account account, @Valid InternalAccountInvitationRequest params) {

        String number = account.getNumber();

        int total = 0;
        Optional<Account> a = mAccounts.get(account.getNumber());
        if (!a.isPresent()) {
            BaseResponse.err(200, BaseResponse.STATUS.INVALID_PARAMETER, "can not find the user", logger, null);
        }
        total = a.get().getInvitationPerDay();
        if (total == 0) total = 1000; // 默认一天1000个邀请码

        int consumed = 0;
        List<InternalAccountsInvitationRow> invs = mInternalAccountsInvitationTable.getByInviterToday(number);
        if (null != invs) {
            consumed = invs.size();
        }

        String invCode = "";
        int remaining = total - consumed;
        if (remaining > 0) {
            remaining -= 1;
            while (true) {
                invCode = mRandomString.nextString();
                if(StringUtils.isNotEmpty(params.getClient()) && params.getClient().equals("Chative")){
                    invCode = "CHATIVE" + invCode.substring(0, 25);
                }
                List<InternalAccountsInvitationRow> il = mInternalAccountsInvitationTable.get(invCode);
                if (0 != il.size()) {
                    continue;
                }

                mInternalAccountsInvitationTable.insert(invCode, account.getNumber(), System.currentTimeMillis(), 0, "", params.getName(), "", "","", "","");
                break;
            }
        }

        return BaseResponse.ok(new RespInvitation(invCode, total, remaining));
    }

    private byte[] decodeToken(String encoded) throws IOException {
        return Base64.decodeWithoutPadding(encoded.replace('-', '+').replace('_', '/'));
    }
}
