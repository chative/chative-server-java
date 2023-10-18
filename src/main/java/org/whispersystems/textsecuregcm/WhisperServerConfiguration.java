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
package org.whispersystems.textsecuregcm;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.difft.factory.EnforcerConfig;
import org.whispersystems.textsecuregcm.configuration.*;
import org.whispersystems.websocket.configuration.WebSocketConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;

public class WhisperServerConfiguration extends Configuration {

//  @NotNull
//  @Valid
  @JsonProperty
  private TwilioConfiguration twilio;

//  @NotNull
//  @Valid
  @JsonProperty
  private PushConfiguration push;

  @NotNull
  @Valid
  @JsonProperty
  private AttachmentsConfiguration attachments;

  @NotNull
  @Valid
  @JsonProperty
  private AttachmentsConfiguration avatars;

  @NotNull
  @Valid
  @JsonProperty
  private ProfilesConfiguration profiles;

  @NotNull
  @Valid
  @JsonProperty
  private RedisConfiguration cache;

  @NotNull
  @Valid
  @JsonProperty
  private RedisConfiguration directory;

  @NotNull
  @Valid
  @JsonProperty
  private RedisConfiguration pushScheduler;

  @NotNull
  @Valid
  // @JsonProperty
  private RedisSentinelConfiguration redisSentinel;

  @NotNull
  @Valid
  @JsonProperty
  private MessageCacheConfiguration messageCache;

  @Valid
  @NotNull
  @JsonProperty
  private DataSourceFactory messageStore;


  @Valid
  @NotNull
  @JsonProperty
  private DataSourceFactory messageShardDb0;

  @Valid
  @NotNull
  @JsonProperty
  private DataSourceFactory messageShardDb1;

  @Valid
  @NotNull
  @JsonProperty
  private DataSourceFactory messageShardDb2;

  @Valid
  @NotNull
  @JsonProperty
  private List<TestDeviceConfiguration> testDevices = new LinkedList<>();

  @Valid
  @NotNull
  @JsonProperty
  private List<MaxDeviceConfiguration> maxDevices = new LinkedList<>();

  @Valid
  @JsonProperty
  private FederationConfiguration federation = new FederationConfiguration();

  @Valid
  @NotNull
  @JsonProperty
  private DataSourceFactory database = new DataSourceFactory();

  @JsonProperty
  private DataSourceFactory read_database;

  @Valid
  @NotNull
  @JsonProperty
  private RateLimitsConfiguration limits = new RateLimitsConfiguration();

  @Valid
  @NotNull
  @JsonProperty
  private JerseyClientConfiguration httpClient = new JerseyClientConfiguration();

  @Valid
  @NotNull
  @JsonProperty
  private WebSocketConfiguration webSocket = new WebSocketConfiguration();

//  @Valid
//  @NotNull
  @JsonProperty
  private TurnConfiguration turn;

  @Valid
  @NotNull
  @JsonProperty
  private MeetingConfiguration meeting;

  public int getAccMaxMsgEnvVersion() {
    return accMaxMsgEnvVersion;
  }

  @JsonProperty
  private int accMaxMsgEnvVersion = 255;


  @Valid
  @NotNull
  @JsonProperty
  private AuthorizationConfiguration authorization;

  @Valid
  @NotNull
  @JsonProperty
  private GcmConfiguration gcm;


  @NotNull
  @JsonProperty
  TpnConfiguration tpn;

  @Valid
  @NotNull
  @JsonProperty
  private Map<String, ApnConfiguration>  apn;

  @Valid
  @NotNull
  @JsonProperty
  private Map<String, ApnConfiguration>  voip;

  public ESLogConfig getESLoggerConfiguration() {
    return esLogger;
  }

  @JsonProperty
  private ESLogConfig esLogger;

  @Valid
  @NotNull
  @JsonProperty
  EmailConfiguration email;

  @Valid
  @NotNull
  @JsonProperty
  DistributedLockConfig  distributedLock;

  @JsonProperty
  ForcedUpgradeConfiguration forcedUpgrade;

  @Valid
  @JsonProperty
  GroupConfiguration group;

  @Valid
  @NotNull
  @JsonProperty
  private DirectoryNotifyConfiguration directoryNotify;
  @Valid
  @JsonProperty
  InternalTimedTaskConfiguration internalTimedTaskConfiguration;

  @Valid
  @JsonProperty
  AccountManagerConfiguration accountManagerConfiguration;

  @Valid
  @NotNull
  @JsonProperty
  KafkaConfiguration kafkaConfiguration;

  @Valid
  @NotNull
  @JsonProperty
  RedisClusterConfiguration cacheCluster;


  @Valid
  @NotNull
  @JsonProperty
  private List<OktaConfiguration>  okta;

  private List<String> forbiddenMessage;

  @Valid
  @NotNull
  @JsonProperty
  private EnforcerConfig casbin;

  @Valid
  @NotNull
  @JsonProperty
  private ConversationConfiguration conversationConfiguration;


  @Valid
  @NotNull
  @JsonProperty
  private UserStatusServerConfiguration userStatusServerConfiguration;

  @Valid
  @NotNull
  @JsonProperty
  private FriendServerConfiguration friendServerConfiguration;

  public WebSocketConfiguration getWebSocketConfiguration() {
    return webSocket;
  }

  public TwilioConfiguration getTwilioConfiguration() {
    return twilio;
  }

  public PushConfiguration getPushConfiguration() {
    return push;
  }

  public JerseyClientConfiguration getJerseyClientConfiguration() {
    return httpClient;
  }

  public AttachmentsConfiguration getAttachmentsConfiguration() {
    return attachments;
  }

  public AttachmentsConfiguration getAvatarsConfiguration() {
    return avatars;
  }

  public RedisConfiguration getCacheConfiguration() {
    return cache;
  }

  public RedisConfiguration getDirectoryConfiguration() {
    return directory;
  }

  public MessageCacheConfiguration getMessageCacheConfiguration() {
    return messageCache;
  }

  public RedisConfiguration getPushScheduler() {
    return pushScheduler;
  }

  public RedisSentinelConfiguration getRedisSentinel() {
    return redisSentinel;
  }

  public DataSourceFactory getMessageStoreConfiguration() {
    return messageStore;
  }

  public DataSourceFactory getDataSourceFactory() {
    return database;
  }

  public DataSourceFactory getReadDataSourceFactory() {
    return read_database;
  }

  public RateLimitsConfiguration getLimitsConfiguration() {
    return limits;
  }

  public FederationConfiguration getFederationConfiguration() {
    return federation;
  }

  public TurnConfiguration getTurnConfiguration() {
    return turn;
  }

  public MeetingConfiguration getMeeting() {
    return meeting;
  }

  public AuthorizationConfiguration getAuthorization() {
    return authorization;
  }

  public GcmConfiguration getGcmConfiguration() {
    return gcm;
  }

  public TpnConfiguration getTpn() {
    return tpn;
  }

  public Map<String, ApnConfiguration> getApnConfigurations() {
    return apn;
  }

  public ProfilesConfiguration getProfilesConfiguration() {
    return profiles;
  }

  public EmailConfiguration getEmail() {
    return email;
  }

  public DistributedLockConfig getDistributedLock() {
    return distributedLock;
  }


  public ForcedUpgradeConfiguration getForcedUpgrade() {
    return forcedUpgrade;
  }

  public GroupConfiguration getGroupConfiguration() {
    return group;
  }

  public DirectoryNotifyConfiguration getDirectoryNotify() {
    return directoryNotify;
  }

  public InternalTimedTaskConfiguration getInternalTimedTaskConfiguration() {
    return internalTimedTaskConfiguration;
  }

  public Map<String, Integer> getTestDevices() {
    Map<String, Integer> results = new HashMap<>();

    for (TestDeviceConfiguration testDeviceConfiguration : testDevices) {
      results.put(testDeviceConfiguration.getNumber(),
                  testDeviceConfiguration.getCode());
    }

    return results;
  }

  public Map<String, Integer> getMaxDevices() {
    Map<String, Integer> results = new HashMap<>();

    for (MaxDeviceConfiguration maxDeviceConfiguration : maxDevices) {
      results.put(maxDeviceConfiguration.getNumber(),
                  maxDeviceConfiguration.getCount());
    }

    return results;
  }

  public List<OktaConfiguration> getOkta() {
    return okta;
  }

  public AccountManagerConfiguration getAccountManagerConfiguration() {
    return accountManagerConfiguration;
  }

  public KafkaConfiguration getKafkaConfiguration() {
    return kafkaConfiguration;
  }

  public RedisClusterConfiguration getCacheCluster() {
    return cacheCluster;
  }

  public DataSourceFactory getMessageShardDb0() {
    return messageShardDb0;
  }

  public DataSourceFactory getMessageShardDb1() {
    return messageShardDb1;
  }

  public DataSourceFactory getMessageShardDb2() {
    return messageShardDb2;
  }

  public EnforcerConfig getEnforcerConfig() {
    return casbin;
  }

  public ConversationConfiguration getConversationConfiguration() {
    return conversationConfiguration;
  }

  public void setConversationConfiguration(ConversationConfiguration conversationConfiguration) {
    this.conversationConfiguration = conversationConfiguration;
  }

  public Map<String, ApnConfiguration> getVoip() {
    return voip;
  }

  public void setVoip(Map<String, ApnConfiguration> voip) {
    this.voip = voip;
  }



  public UserStatusServerConfiguration getUserStatusServerConfiguration() {
    return userStatusServerConfiguration;
  }

  public void setUserStatusServerConfiguration(UserStatusServerConfiguration userStatusServerConfiguration) {
    this.userStatusServerConfiguration = userStatusServerConfiguration;
  }

  public FriendServerConfiguration getFriendServerConfiguration() {
    return friendServerConfiguration;
  }

}
