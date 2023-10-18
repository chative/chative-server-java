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
package org.whispersystems.textsecuregcm.push;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.configuration.ApnConfiguration;
import org.whispersystems.textsecuregcm.push.RetryingApnsClient.ApnResult;
import org.whispersystems.textsecuregcm.redis.RedisOperation;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.Device;
import org.whispersystems.textsecuregcm.util.Constants;
import org.whispersystems.textsecuregcm.util.StringUtil;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

import static com.codahale.metrics.MetricRegistry.name;

public class APNSender implements Managed {

  private final Logger logger = LoggerFactory.getLogger(APNSender.class);

  private static final MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME);
  private static final Meter unregisteredEventStale  = metricRegistry.meter(name(APNSender.class, "unregistered_event_stale"));
  private static final Meter unregisteredEventFresh  = metricRegistry.meter(name(APNSender.class, "unregistered_event_fresh"));

  private ExecutorService    executor;
  private ApnFallbackManager fallbackManager;

  private final AccountsManager    accountsManager;
  private final Map<String, ApnConfiguration> configurations;
  private final Map<String, ApnConfiguration> voipConfigurations;

  private Map<String,RetryingApnsClient> apnsClients;
  private Map<String,RetryingApnsClient> voipsClients;


  public APNSender(AccountsManager accountsManager, Map<String, ApnConfiguration> configurations, Map<String, ApnConfiguration> voipConfigurations)
      throws IOException
  {
    this.accountsManager = accountsManager;
    this.configurations=configurations;
    apnsClients=new HashMap<>();
    if(configurations.size()>0){
      for(String type:configurations.keySet()){
        String pushCertificate=configurations.get(type).getPushCertificate();
        String pushKey=configurations.get(type).getPushKey();
        if(!StringUtil.isEmpty(pushCertificate)&&!StringUtil.isEmpty(pushKey)) {
          RetryingApnsClient retryingApnsClient = new RetryingApnsClient(pushCertificate,
                  pushKey,
                  3);
          apnsClients.put(type, retryingApnsClient);
        }
      }
    }
    this.voipConfigurations=voipConfigurations;
    voipsClients=new HashMap<>();
    if(configurations.size()>0){
      for(String type:voipConfigurations.keySet()){
        String pushCertificate=voipConfigurations.get(type).getPushCertificate();
        String pushKey=voipConfigurations.get(type).getPushKey();
        if(!StringUtil.isEmpty(pushCertificate)&&!StringUtil.isEmpty(pushKey)) {
          RetryingApnsClient retryingApnsClient = new RetryingApnsClient(pushCertificate,
                  pushKey,
                  3);
          voipsClients.put(type, retryingApnsClient);
        }
      }
    }
  }

  // modified by stone,2018-11-13 10:54 retryCount 10 -> 0

  @VisibleForTesting
  public APNSender(ExecutorService executor, AccountsManager accountsManager, RetryingApnsClient apnsClient, String bundleId, boolean sandbox, Map<String, ApnConfiguration> configurations,Map<String, ApnConfiguration> voipConfigurations) {
    this.executor        = executor;
    this.accountsManager = accountsManager;
    this.configurations = configurations;
    this.voipConfigurations=voipConfigurations;
  }

  public ListenableFuture<ApnResult> sendMessage(String userAgent,final ApnMessage message) {
    logger.info("in APNSender to {}: userAgent: {},isVoip:{} ", message.getNumber(), userAgent, message.isVoip());
    String type="";
    if(StringUtil.isEmpty(userAgent)){
      type="Difft";
    }else{
      type=userAgent.split("/")[0];
      if (type.contains("Chative")) type = "Chative";
    }
    String topic =null;
    RetryingApnsClient apnsClient=null;
    if(!message.isVoip()) {
      if (!configurations.containsKey(type)) {
        logger.warn("in APNSender to {}: userAgent: {},isVoip:{},no such type:{},do not sendApn ", message.getNumber(), userAgent, message.isVoip(),type);
        return null;
      }
      topic = configurations.get(type).getBundleId();
      if (StringUtil.isEmpty(topic)) {
        logger.warn("in APNSender to {}: userAgent: {},isVoip:{},type:{}, topic empty,do not sendApn ", message.getNumber(), userAgent, message.isVoip(),type);
        return null;
      }
      apnsClient= apnsClients.get(type);
      if (apnsClient == null) {
        logger.warn("in APNSender to {}: userAgent: {},isVoip:{},type:{}, apnsClient is null,do not sendApn ", message.getNumber(), userAgent, message.isVoip(),type);
        return null;
      }
    }else{
      if (!voipConfigurations.containsKey(type)) {
        return null;
      }
      topic = voipConfigurations.get(type).getBundleId()+".voip";
      if (StringUtil.isEmpty(topic)) {
        return null;
      }
      apnsClient= voipsClients.get(type);
      if (apnsClient == null) {
        return null;
      }
    }
//    if (message.isVoip()) {
//      topic = topic + ".voip";
//    }

    ListenableFuture<ApnResult> future = apnsClient.send(message.getNumber(), message.getApnId(), topic,
                                                         false,//message.isBackground(),
                                                         message.getMessage(),
//                                                          msg,
                                                         new Date(message.getExpirationTime()),message.getCollapseId());

    String finalTopic = topic;
    Futures.addCallback(future, new FutureCallback<ApnResult>() {
      @Override
      public void onSuccess(@Nullable ApnResult result) {
        if (result == null) {
          logger.warn("*** RECEIVED NULL APN RESULT ***");
        } else if (result.getStatus() == ApnResult.Status.NO_SUCH_USER) {
          handleUnregisteredUser(message.getApnId(), message.getNumber(), message.getDeviceId());
        } else if (result.getStatus() == ApnResult.Status.GENERIC_FAILURE) {
          logger.warn("*** Got APN generic failure: " + result.getReason() + ", " + message.getNumber());
        }else {
          logger.info("apnSend Success!topic:{},apnMsg:{}", finalTopic, new Gson().toJson(message));
        }
      }

      @Override
      public void onFailure(@Nullable Throwable t) {
        if (t != null) {
          logger.warn("Got fatal APNS exception,uid:{},reason:{}",message.getNumber(),t.getMessage(), t);
        }else {
          logger.warn("Got fatal APNS exception,uid:{},reason:{}",message.getNumber(),"null");
        }
      }
    }, executor);

    logger.warn("out APNSender to {}: userAgent: {},isVoip:{},type:{}, ", message.getNumber(), userAgent, message.isVoip(),type);

    return future;
  }

  @Override
  public void start() {
    this.executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());
    SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME)
            .register(name(APNSender.class, "APNSender_executor_depth"),
                    (Gauge<Long>) ((ThreadPoolExecutor)executor)::getTaskCount);
    for(String type:apnsClients.keySet()) {
      RetryingApnsClient apnsClient=apnsClients.get(type);
      apnsClient.connect(configurations.get(type).isSandboxEnabled());
    }
    for(String type:voipsClients.keySet()) {
      RetryingApnsClient apnsClient=voipsClients.get(type);
      apnsClient.connect(voipConfigurations.get(type).isSandboxEnabled());
    }
  }

  @Override
  public void stop() {
    this.executor.shutdown();
    for(String type:apnsClients.keySet()) {
      RetryingApnsClient apnsClient=apnsClients.get(type);
      apnsClient.disconnect();
    }
    for(String type:voipsClients.keySet()) {
      RetryingApnsClient apnsClient=voipsClients.get(type);
      apnsClient.disconnect();
    }
  }

  public void setApnFallbackManager(ApnFallbackManager fallbackManager) {
    this.fallbackManager = fallbackManager;
  }

  private void handleUnregisteredUser(String registrationId, String number, long deviceId) {
    logger.info("Got APN Unregistered: " + number + "," + deviceId);

    Optional<Account> account = accountsManager.get(number);

    if (!account.isPresent()) {
      logger.info("No account found: " + number);
      unregisteredEventStale.mark();
      return;
    }

    Optional<Device> device = account.get().getDevice(deviceId);

    if (!device.isPresent()) {
      logger.info("No device found: " + number);
      unregisteredEventStale.mark();
      return;
    }

    if (!registrationId.equals(device.get().getApnId()) &&
        !registrationId.equals(device.get().getVoipApnId()))
    {
      logger.info("Registration ID does not match: " + registrationId + ", " + device.get().getApnId() + ", " + device.get().getVoipApnId());
      unregisteredEventStale.mark();
      return;
    }

//    if (registrationId.equals(device.get().getApnId())) {
//      logger.info("APN Unregister APN ID matches! " + number + ", " + deviceId);
//    } else if (registrationId.equals(device.get().getVoipApnId())) {
//      logger.info("APN Unregister VoIP ID matches! " + number + ", " + deviceId);
//    }

    long tokenTimestamp = device.get().getPushTimestamp();

    if (tokenTimestamp != 0 && System.currentTimeMillis() < tokenTimestamp + TimeUnit.SECONDS.toMillis(10))
    {
      logger.info("APN Unregister push timestamp is more recent: " + tokenTimestamp + ", " + number);
      unregisteredEventStale.mark();
      return;
    }

//    logger.info("APN Unregister timestamp matches: " + device.get().getApnId() + ", " + device.get().getVoipApnId());
//    device.get().setApnId(null);
//    device.get().setVoipApnId(null);
//    device.get().setFetchesMessages(false);
//    accountsManager.update(account.get());
//
//    if (fallbackManager != null) {
//      fallbackManager.cancel(new WebsocketAddress(number, deviceId));
//    }

    if (fallbackManager != null) {
      RedisOperation.unchecked(() -> fallbackManager.cancel(account.get(), device.get()));
      unregisteredEventFresh.mark();
    }
  }
}
