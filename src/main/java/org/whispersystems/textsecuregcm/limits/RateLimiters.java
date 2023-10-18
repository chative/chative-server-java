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
package org.whispersystems.textsecuregcm.limits;


import org.whispersystems.textsecuregcm.configuration.RateLimitsConfiguration;
import org.whispersystems.textsecuregcm.controllers.RateLimitExceededException;
import org.whispersystems.textsecuregcm.storage.MemCache;

public class RateLimiters {

  private final RateLimiter smsDestinationLimiter;
  private final RateLimiter voiceDestinationLimiter;
  private final RateLimiter voiceDestinationDailyLimiter;
  private final RateLimiter verifyLimiter;
  private final RateLimiter pinLimiter;

  private final RateLimiter attachmentLimiter;
  private final RateLimiter contactsLimiter;
  private final RateLimiter preKeysLimiter;
  private final RateLimiter messagesLimiter;
  private final RateLimiter messagesForGroupLimiter;
  private final RateLimiter messagesForDestinationsLimiter;

  private final RateLimiter allocateDeviceLimiter;
  private final RateLimiter verifyDeviceLimiter;

  private final RateLimiter turnLimiter;

  private final RateLimiter profileLimiter;

  private final RateLimiter internalAccountInfoLimiter;

  private final RateLimiter invitation;

  private final RateLimiter getVCode;

  private final RateLimiter authorizationLimiter;
  private final RateLimiter createAccountLimiter;
  private final MemCache memCache;

  public RateLimiters(RateLimitsConfiguration config, MemCache cacheClient) {
    this.memCache=cacheClient;
    this.smsDestinationLimiter = new RateLimiter(cacheClient, "smsDestination",
                                                 config.getSmsDestination().getBucketSize(),
                                                 config.getSmsDestination().getLeakRatePerMinute());

    this.voiceDestinationLimiter = new RateLimiter(cacheClient, "voxDestination",
                                                   config.getVoiceDestination().getBucketSize(),
                                                   config.getVoiceDestination().getLeakRatePerMinute());

    this.voiceDestinationDailyLimiter = new RateLimiter(cacheClient, "voxDestinationDaily",
                                                        config.getVoiceDestinationDaily().getBucketSize(),
                                                        config.getVoiceDestinationDaily().getLeakRatePerMinute());

    this.verifyLimiter = new RateLimiter(cacheClient, "verify",
                                         config.getVerifyNumber().getBucketSize(),
                                         config.getVerifyNumber().getLeakRatePerMinute());

    this.pinLimiter = new LockingRateLimiter(cacheClient, "pin",
                                             config.getVerifyPin().getBucketSize(),
                                             config.getVerifyPin().getLeakRatePerMinute());

    this.attachmentLimiter = new RateLimiter(cacheClient, "attachmentCreate",
                                             config.getAttachments().getBucketSize(),
                                             config.getAttachments().getLeakRatePerMinute());

    this.contactsLimiter = new RateLimiter(cacheClient, "contactsQuery",
                                           config.getContactQueries().getBucketSize(),
                                           config.getContactQueries().getLeakRatePerMinute());

    this.preKeysLimiter = new RateLimiter(cacheClient, "prekeys",
                                          config.getPreKeys().getBucketSize(),
                                          config.getPreKeys().getLeakRatePerMinute());

    this.messagesLimiter = new RateLimiter(cacheClient, "messages",
                                           config.getMessages().getBucketSize(),
                                           config.getMessages().getLeakRatePerMinute());
    this.messagesForGroupLimiter = new RateLimiter(cacheClient, "messagesForGroup",
            config.getMessagesForGroup().getBucketSize(),
            config.getMessagesForGroup().getLeakRatePerMinute());
    this.messagesForDestinationsLimiter = new RateLimiter(cacheClient, "messagesForDestinations",
            config.getMessagesForDestinations().getBucketSize(),
            config.getMessagesForDestinations().getLeakRatePerMinute());

    this.allocateDeviceLimiter = new RateLimiter(cacheClient, "allocateDevice",
                                                 config.getAllocateDevice().getBucketSize(),
                                                 config.getAllocateDevice().getLeakRatePerMinute());

    this.verifyDeviceLimiter = new RateLimiter(cacheClient, "verifyDevice",
                                               config.getVerifyDevice().getBucketSize(),
                                               config.getVerifyDevice().getLeakRatePerMinute());

    this.turnLimiter = new RateLimiter(cacheClient, "turnAllocate",
                                       config.getTurnAllocations().getBucketSize(),
                                       config.getTurnAllocations().getLeakRatePerMinute());

    this.profileLimiter = new RateLimiter(cacheClient, "profile",
                                          config.getProfile().getBucketSize(),
                                          config.getProfile().getLeakRatePerMinute());

    this.internalAccountInfoLimiter = new RateLimiter(cacheClient,
            "internalAccountInfo",
            config.getInternalAccountInfo().getBucketSize(),
            config.getInternalAccountInfo().getLeakRatePerMinute());

    this.invitation = new RateLimiter(cacheClient,
            "invitation",
            config.getInviation().getBucketSize(),
            config.getInviation().getLeakRatePerMinute());

    this.getVCode = new RateLimiter(cacheClient,
            "getVCode",
            config.getInviation().getBucketSize(),
            config.getInviation().getLeakRatePerMinute());

    this.authorizationLimiter = new RateLimiter(cacheClient,
            "authorization",
            config.getAuthorization().getBucketSize(),
            config.getAuthorization().getLeakRatePerMinute());
    this.createAccountLimiter = new RateLimiter(cacheClient,
            "createAccount",
            config.getCreateAccount().getBucketSize(),
            config.getCreateAccount().getLeakRatePerMinute());
  }

  public RateLimiter getAllocateDeviceLimiter() {
    return allocateDeviceLimiter;
  }

  public RateLimiter getVerifyDeviceLimiter() {
    return verifyDeviceLimiter;
  }

  public RateLimiter getMessagesLimiter() {
    return messagesLimiter;
  }

  public RateLimiter getPreKeysLimiter() {
    return preKeysLimiter;
  }

  public RateLimiter getContactsLimiter() {
    return contactsLimiter;
  }

  public RateLimiter getAttachmentLimiter() {
    return this.attachmentLimiter;
  }

  public RateLimiter getSmsDestinationLimiter() {
    return smsDestinationLimiter;
  }

  public RateLimiter getVoiceDestinationLimiter() {
    return voiceDestinationLimiter;
  }

  public RateLimiter getVoiceDestinationDailyLimiter() {
    return voiceDestinationDailyLimiter;
  }

  public RateLimiter getVerifyLimiter() {
    return verifyLimiter;
  }

  public RateLimiter getPinLimiter() {
    return pinLimiter;
  }

  public RateLimiter getTurnLimiter() {
    return turnLimiter;
  }

  public RateLimiter getProfileLimiter() {
    return profileLimiter;
  }

  public RateLimiter getInternalAccountInfoLimiter() {
    return internalAccountInfoLimiter;
  }

  public RateLimiter getInvitation() {
    return invitation;
  }

  public RateLimiter getGetVCode() {
    return getVCode;
  }

  public RateLimiter getAuthorizationLimiter() {
    return authorizationLimiter;
  }

  public RateLimiter getCreateAccountLimiter() {
    return createAccountLimiter;
  }

  public RateLimiter getMessagesForGroupLimiter() {
    return messagesForGroupLimiter;
  }

  public RateLimiter getMessagesForDestinationsLimiter() {
    return messagesForDestinationsLimiter;
  }

  public RateLimiter getCustomizeLimiter(String name,int bucketSize, double leakRatePerMinute){
    return new RateLimiter(this.memCache, name, bucketSize, leakRatePerMinute);
  }
}
