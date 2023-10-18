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

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.InternalAccount.InternalAccountManager;
import org.whispersystems.textsecuregcm.auth.AuthenticationCredentials;
import org.whispersystems.textsecuregcm.auth.AuthorizationHeader;
import org.whispersystems.textsecuregcm.auth.InvalidAuthorizationHeaderException;
import org.whispersystems.textsecuregcm.configuration.EmailConfiguration;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.push.ThirdPartyPush;
import org.whispersystems.textsecuregcm.sms.SmsSender;
import org.whispersystems.textsecuregcm.sms.TwilioSmsSender;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.Constants;
import org.whispersystems.textsecuregcm.util.Util;
import org.whispersystems.textsecuregcm.util.VerificationCode;
import org.whispersystems.websocket.session.WebSocketSession;
import org.whispersystems.websocket.session.WebSocketSessionContext;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codahale.metrics.MetricRegistry.name;

@Path("/v1/accounts")
public class   AccountController {

  private final Logger         logger         = LoggerFactory.getLogger(AccountController.class);
  private final MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME);
  private final Meter          newUserMeter   = metricRegistry.meter(name(AccountController.class, "brand_new_user"));

  private final PendingAccountsManager                pendingAccounts;
  private final AccountsManager                       accounts;
  private final RateLimiters                          rateLimiters;
  private final SmsSender                             smsSender;
  private final MessagesManager                       messagesManager;
//  private final TurnTokenGenerator                    turnTokenGenerator;
  private final Map<String, Integer>                  testDevices;
  private final InternalAccountsTable mInternalAccountsTable;
  private final InternalAccountsInvitationTable mInternalAccountsInvitationTable;
  private final EmailConfiguration emailConfiguration;
  private final InternalAccountManager internalAccountManager;
  private final MemCache memCache;

  private ThirdPartyPush mThirdPartyPush = null;

  public AccountController(PendingAccountsManager pendingAccounts,
                           AccountsManager accounts,
                           RateLimiters rateLimiters,
                           SmsSender smsSenderFactory,
                           MessagesManager messagesManager,
                           /*TurnTokenGenerator turnTokenGenerator,*/
                           Map<String, Integer> testDevices,
                           InternalAccountsTable internalAccountsTable,
                           InternalAccountsInvitationTable internalAccountsInvitationTable,
                           EmailConfiguration emailConfiguration,
                           InternalAccountManager internalAccountManager,
                           MemCache memCache
  )
  {
    this.pendingAccounts    = pendingAccounts;
    this.accounts           = accounts;
    this.rateLimiters       = rateLimiters;
    this.smsSender          = smsSenderFactory;
    this.messagesManager    = messagesManager;
    this.testDevices        = testDevices;
//    this.turnTokenGenerator = turnTokenGenerator;
    this.mInternalAccountsTable = internalAccountsTable;
    this.mInternalAccountsInvitationTable = internalAccountsInvitationTable;
    this.emailConfiguration = emailConfiguration;
    mThirdPartyPush = ThirdPartyPush.getInstance(accounts, memCache);
    this.internalAccountManager=internalAccountManager;
    this.memCache = memCache;
  }

  @Timed
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{transport}/code/{number}")
  public sendCodeResponse createAccount(@PathParam("transport") String transport,
                                @PathParam("number")    String number,
                                @QueryParam("client")   Optional<String> client)
      throws IOException, RateLimitExceededException
  {
    if (!Util.isValidNumber(number)) {
      logger.debug("Invalid number: " + number);
      throw new WebApplicationException(Response.status(400).build());
    }

    switch (transport) {
      case "sms":
        rateLimiters.getSmsDestinationLimiter().validate(number);
        break;
      case "voice":
        rateLimiters.getVoiceDestinationLimiter().validate(number);
        rateLimiters.getVoiceDestinationDailyLimiter().validate(number);
        break;
      default:
        throw new WebApplicationException(Response.status(422).build());
    }

    boolean requirePin = internalAccountManager.sendVerificationCode(number);

    return new sendCodeResponse(requirePin,0);
  }

  @Timed
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/code/{verification_code}")
  public void verifyAccount(@PathParam("verification_code") String verificationCode,
                            @HeaderParam("Authorization")   String authorizationHeader,
                            @HeaderParam("User-Agent")  String userAgent,
                            @Valid AccountAttributes accountAttributes)
      throws RateLimitExceededException
  {
    try {
      AuthorizationHeader header = AuthorizationHeader.fromFullHeader(authorizationHeader);
      String number = header.getNumber();
      String password = header.getPassword();

      logger.info("in /v1/accounts/code/ before getVerifyLimiter validate,uid:{},ua:{}", number, userAgent);
      rateLimiters.getVerifyLimiter().validate(number);

      internalAccountManager.register(number, verificationCode, accountAttributes, password, userAgent,
              messagesManager, newUserMeter);
    } catch (InvalidAuthorizationHeaderException e) {
      logger.warn("Bad Authorization Header", e);
      throw new WebApplicationException(Response.status(401).build());
    }

  }

  private  void  disconnectDevices(Account account){
    for(Device device:account.getDevices()) { // 遍历所有设备
      accounts.kickOffDevice(account.getNumber(), device.getId());
    }
  }

  private void validateInvitationCode(String text) {
    Pattern pattern = Pattern.compile("[0-9a-zA-Z]{8,32}");
    Matcher matcher = pattern.matcher(text);
    if (!matcher.matches()) {
      logger.debug("invalid invitation code: " + text);
      throwBadInvitationCode("Invalid Invitation Code");
    }
  }

  private void throwBadInvitationCode(String reason){
    HashMap<String, Object> mapData = new HashMap<>();
    mapData.put("code",400);
//    mapData.put("message","Bad Invitation Code");
    mapData.put("reason",reason);
    BaseResponse baseResponse=new BaseResponse(1,10002, "Invalid invite code.",mapData);
    throw new WebApplicationException(Response.status(200).entity(baseResponse).build());
  }

  @Timed
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/invitation/{invitation_code}")
  public BaseResponse verifyInvitation(@PathParam("invitation_code") String invitationCode)
          throws RateLimitExceededException
  {
    rateLimiters.getInvitation().validate("invitation");

    validateInvitationCode(invitationCode);

    if (invitationCode.length() == 8 || !invitationCode.startsWith("CHATIVE")){ //长期邀请码
      String invitationCodeShort = invitationCode;
      if (invitationCode.length() > 8)invitationCodeShort = invitationCode.substring(0,8);
      final InvitationAccount invitationAccount = internalAccountManager.verifyInvitation(invitationCodeShort);
      if (null != invitationAccount)
        return BaseResponse.ok(invitationAccount);
    }

    List<InternalAccountsInvitationRow> rows =  mInternalAccountsInvitationTable.get(invitationCode);
    if (0 == rows.size()) {
      logger.warn("* trying to register with an invitation that is not exists: " + invitationCode);
      throwBadInvitationCode("Invitation Code Not Found");
    } else if (rows.size() > 1) {
      logger.warn("* the invitation code is not unique in the database: " + invitationCode);
      BaseResponse.err(200, BaseResponse.STATUS.SERVER_INTERNAL_ERROR,"System Error: the invitation code is not unique",logger,null);
    }
    InternalAccountsInvitationRow inv = rows.get(0);
    // already used?
    if (0 != inv.getRegister_time()) {
      logger.warn("somebody trying to register an invitation which already been used: " + inv.getCode());
        throwBadInvitationCode("Invitation Code Is Already Used");
    }

    // expired?
    if (System.currentTimeMillis() - inv.getTimestamp() > 1000 * 86400 * 7) {
      logger.warn("somebody trying to register an invitation which is already expired: " + inv.getCode());
      throwBadInvitationCode("Expired Invitation Code");
    }

    if(!internalAccountManager.canInviteReg(inv.getInviter())){
      //不能用于注册的邀请码
      logger.warn("Invitation code that cannot be used for registration,inviter:{}",inv.getInviter());
      BaseResponse baseResponse=new BaseResponse(1,10005, "Invitation code that cannot be used for registration",null);
      throw new WebApplicationException(Response.status(200).entity(baseResponse).build());
    }

    InvitationAccount res = internalAccountManager.verifyInvitation(inv);
    res.setInviter(inv.getInviter());
    return BaseResponse.ok(res);
  }

//  @Timed
//  @GET
//  @Path("/vcode/{email}")
//  public void getVCode(@PathParam("email")    String email)
//          throws RateLimitExceededException, GeneralSecurityException, IOException {
//    rateLimiters.getGetVCode().validate(email);
//
//    InternalAccountsRow a = mInternalAccountsDB.getByEmail(email);
//    if (null == a) {
//      throw new WebApplicationException(Response.status(404).build());
//    }
//
//    SecureRandom random = new SecureRandom();
//    int newVCode = 100000 + random.nextInt(999999 - 100000);
//    mInternalAccountsDB.setVcode(a.getNumber(), newVCode);
//    mInternalAccountsDB.setDisabled(a.getNumber(), false);
//
//    String displayNumber = a.getNumber();
//    displayNumber = displayNumber.substring(0, 2) + "-" + displayNumber.substring(2, displayNumber.length());
//
//    try {
//      SMTPClient.send(
//          emailConfiguration.getServer(),
//          emailConfiguration.getPort(),
//          emailConfiguration.getUsername(),
//          emailConfiguration.getPassword(),
//          Arrays.asList(email),
//          "Difft Verification Code",
//          "Hi, here is your Difft verification code which you can login with on the App.\n\nNumber: " + displayNumber + "\nvcode: " + newVCode
//      );
//    } catch (MessagingException e) {
//      logger.error(e.toString());
//      throw new WebApplicationException(Response.status(500).build());
//    }
//  }

//  @Timed
//  @GET
//  @Path("/turn/")
//  @Produces(MediaType.APPLICATION_JSON)
//  public TurnToken getTurnToken(@Auth Account account) throws RateLimitExceededException {
//    rateLimiters.getTurnLimiter().validate(account.getNumber());
//    return turnTokenGenerator.generate();
//  }
  @Timed
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/renew")
  public BaseResponse renew(@Valid RenewAccountRequest renewAccountRequest) {
    try {
      final AuthenticationOktaResponse authenticationOktaResponse = internalAccountManager.renew(renewAccountRequest.getToken());
      return BaseResponse.ok(authenticationOktaResponse);
    } catch (InterrupterProcessException e) {
      logger.warn("renew InterrupterProcessException,reason:{},status:{}", e.getReason(), e.getStatus());
      return new BaseResponse(1, e.getStatus(), e.getReason(), null);
    }
  }

  @Timed
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/resetpassword")
  public BaseResponse resetPassword(@Valid ResetPasswordRequest passwordRequest) {
    try {
      // 验证token
      final Account account = internalAccountManager.verifyResetPasswordToken(passwordRequest.getToken());
      // 更新密码
      final Device device = account.getDevice(Device.MASTER_ID).orElse(null);
      if (device == null){
        return new BaseResponse(1, -1, "impossible in resetPassword", null);
      }
      device.setAuthenticationCredentials(new AuthenticationCredentials(passwordRequest.getPassword()));
      accounts.update(account,device,false);
      // 断开ws链接
      accounts.kickOffDevice(account.getNumber(),device.getId());
      return BaseResponse.ok();
    } catch (InterrupterProcessException e) {
      logger.warn("renew InterrupterProcessException,reason:{},status:{}", e.getReason(), e.getStatus());
      return new BaseResponse(1, e.getStatus(), e.getReason(), null);
    }
  }

  @Timed
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/transfer/verifyToken")
  public BaseResponse verifyTransferToken(@Auth Account account,@Valid TDTokenRequest request){
    try {
      final boolean b = internalAccountManager.verifyTransferToken(request.getToken(), account);
      if (b){
        return BaseResponse.ok();
      }else {
        logger.error("verifyTransferToken error,");
        return new BaseResponse(1, -1,"", null);
      }
    } catch (InterrupterProcessException e) {
      logger.warn("verifyTransferToken InterrupterProcessException,reason:{},status:{}", e.getReason(), e.getStatus());
      return new BaseResponse(1, e.getStatus(), e.getReason(), null);
    }
  }

  @Timed
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/androidnotify")
  public BaseResponse setAndroidNotify(@Auth Account account,@Valid AndroidNotifyRegistrationId request) {
    Device device = account.getAuthenticatedDevice().get();



    device.setApnId(null);
    device.setVoipApnId(null);
    device.setAndroidNotify(new Device.AndroidNotify(request.getTpnID(), request.getFcmID()));
    device.setFetchesMessages(true);

    accounts.update(account,device,false);
    return new BaseResponse(1, 0, "OK", null);
  }

  @Timed
  @PUT
  @Path("/gcm/")
  @Consumes(MediaType.APPLICATION_JSON)
  public void setGcmRegistrationId(@Auth Account account, @Valid GcmRegistrationId registrationId) {
    Device device = account.getAuthenticatedDevice().get();

    if (device.getGcmId() != null &&
        device.getGcmId().equals(registrationId.getGcmRegistrationId()))
    {
      return;
    }

    device.setApnId(null);
    device.setVoipApnId(null);
    device.setGcmId(registrationId.getGcmRegistrationId());

    if (registrationId.isWebSocketChannel()) device.setFetchesMessages(true);
    else                                     device.setFetchesMessages(false);

    accounts.update(account,device,false);
  }

  @Timed
  @DELETE
  @Path("/gcm/")
  public void deleteGcmRegistrationId(@Auth Account account) {
    Device device = account.getAuthenticatedDevice().get();
    device.setGcmId(null);
    device.setFetchesMessages(false);
    accounts.update(account,device,false);
  }

  @Timed
  @PUT
  @Path("/apn/")
  @Consumes(MediaType.APPLICATION_JSON)
  public void setApnRegistrationId(@Auth Account account, @Valid ApnRegistrationId registrationId) {
    Device device = account.getAuthenticatedDevice().get();
    device.setApnId(registrationId.getApnRegistrationId());
    device.setVoipApnId(registrationId.getVoipRegistrationId());
    device.setGcmId(null);
    device.setFetchesMessages(true);
    accounts.update(account,device,false);
  }

  @Timed
  @DELETE
  @Path("/apn/")
  public void deleteApnRegistrationId(@Auth Account account) {
    Device device = account.getAuthenticatedDevice().get();
    device.setApnId(null);
    device.setFetchesMessages(false);
    accounts.update(account,device,false);
  }

  @Timed
  @POST
  @Path("/push/gettoken")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public GetPushToken getPushToken(@Auth Account account, @Valid GetPushToken input) {
    String addr = account.getNumber();
    String type = input.getType();
    String token = mThirdPartyPush.newToken(addr, type);
    if (null == token) {
      token = "";
    }
    memCache.remove(internalAccountManager.getInternalAccountKey(account.getNumber()));
    return new GetPushToken(type, token);
  }

  @Timed
  @POST
  @Path("/push/settoken")
  @Consumes(MediaType.APPLICATION_JSON)
  public void setPushToken(@Auth Account account, @Valid SetPushToken input) {
    String addr = account.getNumber();
    String type = input.getType();
    String token = input.getToken();
    mThirdPartyPush.setToken(addr, type, token);
    memCache.remove(internalAccountManager.getInternalAccountKey(account.getNumber()));
  }

  @Timed
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/pin/")
  public void setPin(@Auth Account account, @Valid RegistrationLock accountLock) {
    account.setPin(accountLock.getPin());
    accounts.update(account,null,false);
  }

  @Timed
  @DELETE
  @Path("/pin/")
  public void removePin(@Auth Account account) {
    account.setPin(null);
    accounts.update(account,null,false);
  }

  @Timed
  @PUT
  @Path("/attributes/")
  @Consumes(MediaType.APPLICATION_JSON)
  public void setAccountAttributes(@Auth Account account,
                                   @HeaderParam("X-Signal-Agent") String signalUserAgent,
                                   @HeaderParam("User-Agent") String userAgent,
                                   @Valid AccountAttributes attributes)
  {
    Device device = account.getAuthenticatedDevice().get();

    device.setFetchesMessages(attributes.getFetchesMessages());
    device.setName(attributes.getName());
    device.setLastSeen(System.currentTimeMillis());
    device.setVoiceSupported(attributes.getVoice());
    device.setVideoSupported(attributes.getVideo());
    device.setRegistrationId(attributes.getRegistrationId());
    device.setSignalingKey(attributes.getSignalingKey());
    if (userAgent == null || userAgent.isEmpty()) {
      userAgent = signalUserAgent;
    }
    device.setUserAgent(userAgent);


    account.setMeetingVersion(attributes.getMeetingVersion(), device, accounts.getAccMaxMeetingVersion());
    account.setMsgEncVersion(attributes.getMsgEncVersion(), device);

    account.setPin(attributes.getPin());

    accounts.update(account,device,false);
  }

  @Timed
  @POST
  @Path("/voice/twiml/{code}")
  @Produces(MediaType.APPLICATION_XML)
  public Response getTwiml(@PathParam("code") String encodedVerificationText) {
    return Response.ok().entity(String.format(TwilioSmsSender.SAY_TWIML,
        encodedVerificationText)).build();
  }

  @Timed
  @PUT
  @Path("/logout")
  @Produces(MediaType.APPLICATION_JSON)
  public BaseResponse logout(@Auth Account account) {
    InternalAccountsRow a = mInternalAccountsTable.get(account.getNumber());
    if (null == a) {
      logger.error("no such user, uid:{}", account.getNumber());
      return new BaseResponse(1, 1, "No such user", null);
    }
    internalAccountManager.disableSearch(account.getNumber());
    accounts.logout(account);
    logger.info(" uid:{} has logged ", account.getNumber());
    return BaseResponse.ok();
  }

  //@Deprecated
  //@Timed
  //@PUT
  //@Path("/unregister")
  //public Response unregister(@Auth Account account,
  //                       @WebSocketSession WebSocketSessionContext context
  //) {
  //  InternalAccountsRow a = mInternalAccountsTable.get(account.getNumber());
  //  if (null == a) {
  //    return Response.status(404).build();
  //  }
  //  accounts.delete(account);
  //  return Response.status(200).build();
  //}

  @Timed
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  public BaseResponse delete(@Auth Account account) {
    // only master device can do this
    if (account.getAuthenticatedDevice().isPresent()
            && !account.getAuthenticatedDevice().get().isMaster()) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, account.getNumber() + "." + account.getAuthenticatedDevice().get().getId() + " is not master device.", logger);
    }

    accounts.delete(account);

    return BaseResponse.ok();
  }

  private void createAccount(String number, String password, String userAgent, AccountAttributes accountAttributes) {
    Device device = new Device();
    device.setId(Device.MASTER_ID);
    device.setAuthenticationCredentials(new AuthenticationCredentials(password));
    device.setSignalingKey(accountAttributes.getSignalingKey());
    device.setFetchesMessages(accountAttributes.getFetchesMessages());
    device.setRegistrationId(accountAttributes.getRegistrationId());
    device.setName(accountAttributes.getName());
    device.setVoiceSupported(accountAttributes.getVoice());
    device.setVideoSupported(accountAttributes.getVideo());
    device.setCreated(System.currentTimeMillis());
    device.setLastSeen(System.currentTimeMillis());
    device.setUserAgent(userAgent);

    Account account = new Account();
    account.setNumber(number);
    account.addDevice(device);
    account.setPin(accountAttributes.getPin());

    if (accounts.create(account)) {
      newUserMeter.mark();
    }

    messagesManager.clear(number);
    pendingAccounts.remove(number);
  }

  @VisibleForTesting protected VerificationCode generateVerificationCode(Account account,String number) {
    if (testDevices.containsKey(number)) {
      return new VerificationCode(testDevices.get(number));
    }

    return new VerificationCode(account.getVcode());
  }

  class sendCodeResponse{
    @JsonProperty
    private Boolean requirePin;
    @JsonProperty
    private int status;

    public sendCodeResponse(Boolean requirePin, int status) {
      this.requirePin = requirePin;
      this.status = status;
    }
  }
}
