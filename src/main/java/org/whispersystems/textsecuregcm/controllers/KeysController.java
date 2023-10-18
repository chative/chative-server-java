/**
 * Copyright (C) 2014 Open Whisper Systems
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
import org.skife.jdbi.v2.exceptions.TransactionException;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.federation.FederatedClientManager;
import org.whispersystems.textsecuregcm.federation.NoSuchPeerException;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.StringUtil;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Path("/v2/keys")
public class KeysController {

  private static final Logger logger = LoggerFactory.getLogger(KeysController.class);

  private final RateLimiters           rateLimiters;
  private final Keys                   keys;
  private final AccountsManager        accounts;
  private final FederatedClientManager federatedClientManager;


  public KeysController(RateLimiters rateLimiters,
                        Keys keys,
                        AccountsManager accounts,
                        FederatedClientManager federatedClientManager
  ) {

    this.rateLimiters = rateLimiters;
    this.keys = keys;
    this.accounts = accounts;
    this.federatedClientManager = federatedClientManager;
  }
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public PreKeyCount getStatus(@Auth Account account) {
    int count = keys.getCount(account.getNumber(), account.getAuthenticatedDevice().get().getId());

    if (count > 0) {
      count = count - 1;
    }

    return new PreKeyCount(count);
  }

  @Timed
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public void setKeys(@Auth Account account, @Valid PreKeyState preKeys)  {
    Device  device        = account.getAuthenticatedDevice().get();
    boolean updateAccount = false;

    if (!preKeys.getSignedPreKey().equals(device.getSignedPreKey())) {
      device.setSignedPreKey(preKeys.getSignedPreKey());
      updateAccount = true;
      try {
        if (preKeys != null && preKeys.getSignedPreKey() != null) {
          logger.warn("KeysController.setKeys number:{},deviceId:{},keyId:{}", account.getNumber(), device.getId(), preKeys.getSignedPreKey().getKeyId());
        }
      }catch (Exception e){
        logger.error("logger.warn error!",e);
      }
    }

    if (!preKeys.getIdentityKey().equals(account.getIdentityKey())) {
      account.setIdentityKey(preKeys.getIdentityKey());
      updateAccount = true;
    }

    if (updateAccount) {
      accounts.update(account,device,false);
    }
    keys.store(account.getNumber(), device.getId(), preKeys.getPreKeys());
  }


  @Timed
  @POST
  @Path("/identity")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public BaseResponse setIdentityKey(@Auth Account account, @Valid IdentityKeyState identityKeyState)  {
    if(identityKeyState==null|| StringUtil.isEmpty(identityKeyState.getIdentityKey())){
      BaseResponse.err(200, BaseResponse.STATUS.INVALID_PARAMETER,"Invalid param",logger,null);
    }
    if (!identityKeyState.getIdentityKey().equals(account.getIdentityKey())) {
      account.setIdentityKey(identityKeyState.getIdentityKey());
      accounts.update(account,null,false);
    }
    return BaseResponse.ok();
  }


  @Timed
  @GET
  @Path("/{number}/{device_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Optional<PreKeyResponse> getDeviceKeys(@Auth                   Account account,
                                                @PathParam("number")    String number,
                                                @PathParam("device_id") String deviceId,
                                                @QueryParam("relay")    Optional<String> relay)
      throws RateLimitExceededException
  {
    try {
      if (relay.isPresent()) {
        return federatedClientManager.getClient(relay.get()).getKeysV2(number, deviceId);
      }

      if (account.isRateLimited()) {
        rateLimiters.getPreKeysLimiter().validate(account.getNumber() +  "__" + number + "." + deviceId);
      }

      Optional<Account> accountOptional = accounts.get(number);
      if (accountOptional.isPresent()){
        Account target = accountOptional.get();
        if (target.isDeleted()){
          throw new WebApplicationException(Response.status(404).
                  entity(new BaseResponse(1, 10110,
                          "Operation denied. This account is already unregistered.", null)).build());
        }
        if (!target.isRegistered()){
          throw new WebApplicationException(Response.status(404).
                  entity(new BaseResponse(1, 10105,
                          "This account has logged out and messages can not be reached.", null)).build());
        }
        // 查看用户状态
        if (target.isDisabled()){
          throw new WebApplicationException(Response.status(404).entity(new BaseResponse(1, 14,
                  "USER_IS_DISABLED", null)).build());
        }
      }

      Account target = getAccount(number, deviceId);

      Optional<List<KeyRecord>> targetKeys = getLocalKeys(target, deviceId);
      List<PreKeyResponseItem>  devices    = new LinkedList<>();

      for (Device device : target.getDevices()) {
        if (accounts.isActiveDevice(device,target) && (deviceId.equals("*") || device.getId() == Long.parseLong(deviceId))) {
          SignedPreKey signedPreKey = device.getSignedPreKey();
          PreKey preKey       = null;

          if (targetKeys.isPresent()) {
            for (KeyRecord keyRecord : targetKeys.get()) {
              if (!keyRecord.isLastResort() && keyRecord.getDeviceId() == device.getId()) {
                preKey = new PreKey(keyRecord.getKeyId(), keyRecord.getPublicKey());
              }
            }
          }

          if (signedPreKey != null || preKey != null) {
            devices.add(new PreKeyResponseItem(device.getId(), device.getRegistrationId(), signedPreKey, preKey));
            try {
              logger.warn("KeysController.getDeviceKeys source:{},sourceDeviceId:{},number:{},deviceId:{},keyId:{}", account.getNumber(), account.getAuthenticatedDevice().get().getId(), number, deviceId, signedPreKey.getKeyId());
            }catch (Exception e){
              logger.error("logger.warn error!",e);
            }
          }
        }
      }

      if (devices.isEmpty()) return Optional.empty();
      else                   return Optional.of(new PreKeyResponse(target.getIdentityKey(), devices));
    } catch (NoSuchPeerException | NoSuchUserException e) {
      throw new WebApplicationException(Response.status(404).build());
    }
  }

  @Timed
  @PUT
  @Path("/signed")
  @Consumes(MediaType.APPLICATION_JSON)
  public void setSignedKey(@Auth Account account, @Valid SignedPreKey signedPreKey) {
    Device device = account.getAuthenticatedDevice().get();
    device.setSignedPreKey(signedPreKey);
    try {
      if (signedPreKey != null) {
        logger.warn("KeysController.setSignedKey number:{},deviceId:{},keyId:{}", account.getNumber(), device.getId(), signedPreKey.getKeyId());
      }
    }catch (Exception e){
      logger.error("logger.warn error!",e);
    }
    accounts.update(account,device,false);
  }

  @Timed
  @GET
  @Path("/signed")
  @Produces(MediaType.APPLICATION_JSON)
  public Optional<SignedPreKey> getSignedKey(@Auth Account account) {
    Device       device       = account.getAuthenticatedDevice().get();
    SignedPreKey signedPreKey = device.getSignedPreKey();
    try {
      if (signedPreKey != null) {
        logger.warn("KeysController.getSignedKey number:{},deviceId:{},keyId:{}", account.getNumber(), device.getId(), signedPreKey.getKeyId());
      }
    }catch (Exception e){
      logger.error("logger.warn error!",e);
    }
    if (signedPreKey != null) return Optional.of(signedPreKey);
    else                      return Optional.empty();
  }

  private Optional<List<KeyRecord>> getLocalKeys(Account destination, String deviceIdSelector)
      throws NoSuchUserException
  {
    try {
      if (deviceIdSelector.equals("*")) {
        return keys.get(destination.getNumber());
      }

      long deviceId = Long.parseLong(deviceIdSelector);

      for (int i=0;i<20;i++) {
        try {
          return keys.get(destination.getNumber(), deviceId);
        } catch (UnableToExecuteStatementException e) {
          logger.info(e.getMessage());
        } catch (TransactionException e) {
          logger.info(e.getMessage());
        } catch (Exception e) {
          logger.info(e.getMessage());
        }
      }

      throw new WebApplicationException(Response.status(500).build());
    } catch (NumberFormatException e) {
      throw new WebApplicationException(Response.status(422).build());
    }
  }

  private Account getAccount(String number, String deviceSelector)
      throws NoSuchUserException
  {
    try {
      Optional<Account> account = accounts.get(number);

      if (!account.isPresent() || !accounts.isActive(account.get())) {
        throw new NoSuchUserException("No active account");
      }

      if (!deviceSelector.equals("*")) {
        long deviceId = Long.parseLong(deviceSelector);

        Optional<Device> targetDevice = account.get().getDevice(deviceId);

        if (!targetDevice.isPresent() || !accounts.isActiveDevice(targetDevice.get(),account.get())) {
          throw new NoSuchUserException("No active device");
        }
      }

      return account.get();
    } catch (NumberFormatException e) {
      throw new WebApplicationException(Response.status(422).build());
    }
  }
}
