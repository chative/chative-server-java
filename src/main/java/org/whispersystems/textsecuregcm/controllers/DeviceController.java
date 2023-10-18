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
import com.google.common.annotations.VisibleForTesting;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.auth.AuthenticationCredentials;
import org.whispersystems.textsecuregcm.auth.AuthorizationHeader;
import org.whispersystems.textsecuregcm.auth.InvalidAuthorizationHeaderException;
import org.whispersystems.textsecuregcm.auth.StoredVerificationCode;
import org.whispersystems.textsecuregcm.entities.*;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.VerificationCode;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.util.*;


@Path("/v1/devices")
public class DeviceController {

  private final Logger logger = LoggerFactory.getLogger(DeviceController.class);

  private static final int MAX_DEVICES = 6;

  private final PendingDevicesManager pendingDevices;
  private final AccountsManager       accounts;
  private final MessagesManager       messages;
  private final RateLimiters          rateLimiters;
  private final Map<String, Integer>  maxDeviceConfiguration;

  public DeviceController(PendingDevicesManager pendingDevices,
                          AccountsManager accounts,
                          MessagesManager messages,
                          RateLimiters rateLimiters,
                          Map<String, Integer> maxDeviceConfiguration

  )
  {
    this.pendingDevices         = pendingDevices;
    this.accounts               = accounts;
    this.messages               = messages;
    this.rateLimiters           = rateLimiters;
    this.maxDeviceConfiguration = maxDeviceConfiguration;
  }

  @Timed
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public DeviceInfoList getDevices(@Auth Account account) {
    List<DeviceInfo> devices = new LinkedList<>();

    for (Device device : account.getDevices()) {
      devices.add(new DeviceInfo(device.getId(), device.getName(),
                                 device.getLastSeen(), device.getCreated()));
    }

    return new DeviceInfoList(devices);
  }

  @Timed
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  public BaseResponse removeDevice(@Auth Account account) {
    if (!account.getAuthenticatedDevice().isPresent()) {
      BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, "current device is not authenticated", logger);
    }

    return removeDevice3(account, String.valueOf(account.getAuthenticatedDevice().get().getId()));
  }

  @Timed
  @DELETE
  @Path("/{deviceId}")
  @Produces(MediaType.APPLICATION_JSON)
  public BaseResponse removeDevice2(@Auth Account account, @PathParam("deviceId") String deviceId) {
    return removeDevice3(account, deviceId);
  }

  private BaseResponse removeDevice3(Account account, String deviceId) {
    // target device
    Device currentDevice = account.getAuthenticatedDevice().get();
    Device device = null;
    if (null == deviceId || deviceId.isEmpty()) {
      device = currentDevice;
    } else {
      Set<Device> devices = account.getDevices();
      for (Device d : devices) {
        if (d.getId() == Long.valueOf(deviceId)) {
          device = d;
          break;
        }
      }

      if (null == device) {
        BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "no such device " + deviceId, logger);
      }
    }

    // check permission
    if (currentDevice.isMaster()) {
      // master device can not be removed
      if (device.getId() == currentDevice.getId()) {
        BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, "master device can not be removed", logger);
      }
    } else {
      // only master device can remove other devices
      if (device.getId() != currentDevice.getId()) {
        BaseResponse.err(BaseResponse.STATUS.NO_PERMISSION, "Minor device can only unregister itself: " + account.getNumber() + "." + currentDevice.getId(), logger);
      }
    }

    // remove
    account.removeDevice(Long.parseLong(deviceId));
    // 重新计算meeting版本\消息加密版本
    Optional<Device> masterDeviceOptional = account.getDevice(Device.MASTER_ID);
    masterDeviceOptional.ifPresent(masterDevice -> {
      account.setMeetingVersion(masterDevice.getMeetingVersion(), masterDevice, accounts.getAccMaxMeetingVersion());
      account.setMsgEncVersion(masterDevice.getMsgEncVersion(), masterDevice);
    });

    Device device1 = new Device();
    device1.setId(Long.parseLong(deviceId));
    accounts.update(account, device1, true);

    // kick off device
    accounts.kickOffDevice(account.getNumber(), Long.valueOf(deviceId));

    // clear messages
    messages.clear(account.getNumber(), Long.valueOf(deviceId));

    return BaseResponse.ok();
  }

  @Timed
  @GET
  @Path("/provisioning/code")
  @Produces(MediaType.APPLICATION_JSON)
  public VerificationCode createDeviceToken(@Auth Account account)
      throws RateLimitExceededException, DeviceLimitExceededException
  {
    rateLimiters.getAllocateDeviceLimiter().validate(account.getNumber());

    int maxDeviceLimit = MAX_DEVICES;

    if (maxDeviceConfiguration.containsKey(account.getNumber())) {
      maxDeviceLimit = maxDeviceConfiguration.get(account.getNumber());
    }

    if (accounts.getActiveDeviceCount(account) >= maxDeviceLimit) {
      throw new DeviceLimitExceededException(account.getDevices().size(), MAX_DEVICES);
    }

    if (account.getAuthenticatedDevice().get().getId() != Device.MASTER_ID) {
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }

    VerificationCode       verificationCode       = generateVerificationCode();
    StoredVerificationCode storedVerificationCode = new StoredVerificationCode(verificationCode.getVerificationCode(),
                                                                               System.currentTimeMillis());

    pendingDevices.store(account.getNumber(), storedVerificationCode);

    return verificationCode;
  }

  @Timed
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/{verification_code}")
  public DeviceResponse verifyDeviceToken(@PathParam("verification_code") String verificationCode,
                                          @HeaderParam("Authorization")   String authorizationHeader,
                                          @HeaderParam("User-Agent")   String userAgent,
                                          @Valid                          AccountAttributes accountAttributes)
      throws RateLimitExceededException, DeviceLimitExceededException
  {
    try {
      AuthorizationHeader header = AuthorizationHeader.fromFullHeader(authorizationHeader);
      String number              = header.getNumber();
      String password            = header.getPassword();

      rateLimiters.getVerifyDeviceLimiter().validate(number);

      Optional<StoredVerificationCode> storedVerificationCode = pendingDevices.getCodeForNumber(number);

      if (!storedVerificationCode.isPresent() || !storedVerificationCode.get().isValid(verificationCode)) {
        throw new WebApplicationException(Response.status(403).build());
      }

      Optional<Account> account = accounts.get(number);

      if (!account.isPresent()) {
        throw new WebApplicationException(Response.status(403).build());
      }

      int maxDeviceLimit = MAX_DEVICES;

      if (maxDeviceConfiguration.containsKey(account.get().getNumber())) {
        maxDeviceLimit = maxDeviceConfiguration.get(account.get().getNumber());
      }

      if (accounts.getActiveDeviceCount(account.get()) >= maxDeviceLimit) {
        throw new DeviceLimitExceededException(account.get().getDevices().size(), MAX_DEVICES);
      }

      // Only allow one secondary device
      // remove and kick off other secondary devices
      Iterator<Device> itr = account.get().getDevices().iterator();
      while (itr.hasNext()) {
        Device d = itr.next();
        if (d.getId() != Device.MASTER_ID) {
          account.get().removeDevice(d.getId());
        }
      }

      Device device = new Device();
      device.setName(accountAttributes.getName());
      device.setAuthenticationCredentials(new AuthenticationCredentials(password));
      device.setSignalingKey(accountAttributes.getSignalingKey());
      device.setFetchesMessages(accountAttributes.getFetchesMessages());
      device.setId(accounts.getNextDeviceId(account.get()));
      device.setRegistrationId(accountAttributes.getRegistrationId());
      device.setLastSeen(System.currentTimeMillis());
      device.setCreated(System.currentTimeMillis());
      device.setUserAgent(userAgent);
      //device.setMeetingVersion(accountAttributes.getMeetingVersion());
      account.get().addDevice(device);
      messages.clear(account.get().getNumber(), device.getId());
      account.get().setMeetingVersion(accountAttributes.getMeetingVersion(),device, accounts.getAccMaxMeetingVersion());
      account.get().setMsgEncVersion(accountAttributes.getMsgEncVersion(),device);
      accounts.update(account.get(),device,false);

      pendingDevices.remove(number);

      // kick off other secondary devices
      itr = account.get().getDevices().iterator();
      while (itr.hasNext()) {
        Device d = itr.next();
        if (d.getId() != Device.MASTER_ID
                && d.getId() != device.getId()) {
          accounts.kickOffDevice(account.get().getNumber(), d.getId());
        }
      }
      accounts.get(account.get().getNumber()).ifPresent(a -> {
        final Optional<Device> deviceOptional = a.getDevice(device.getId());
        logger.info("saved new device of uid:{},did:{}", a.getNumber(), deviceOptional.map(Device::getId).orElse(-1L));
      });

      return new DeviceResponse(device.getId());
    } catch (InvalidAuthorizationHeaderException e) {
      logger.info("Bad Authorization Header", e);
      throw new WebApplicationException(Response.status(401).build());
    }
  }

  @VisibleForTesting protected VerificationCode generateVerificationCode() {
    SecureRandom random = new SecureRandom();
    int randomInt       = 100000 + random.nextInt(900000);
    return new VerificationCode(randomInt);
  }
}
