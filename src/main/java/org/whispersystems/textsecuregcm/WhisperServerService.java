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
package org.whispersystems.textsecuregcm;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.SharedMetricRegistries;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.github.difftim.eslogger.ESLogger;
import com.github.difftim.security.signing.grpc.GrpcSignatureVerifierInterceptor;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.difft.factory.EnforcerFactory;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.jdbi.v3.core.Jdbi;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.dispatch.DispatchManager;
import org.whispersystems.dropwizard.simpleauth.AuthDynamicFeature;
import org.whispersystems.dropwizard.simpleauth.AuthValueFactoryProvider;
import org.whispersystems.dropwizard.simpleauth.BasicCredentialAuthFilter;
import org.whispersystems.textsecuregcm.InternalAccount.InternalAccountManager;
import org.whispersystems.textsecuregcm.InternalAccount.InvitationsManager;
import org.whispersystems.textsecuregcm.auth.AccountAuthenticator;
import org.whispersystems.textsecuregcm.auth.FederatedPeerAuthenticator;
import org.whispersystems.textsecuregcm.configuration.ESLogConfig;
import org.whispersystems.textsecuregcm.controllers.*;
//import org.whispersystems.textsecuregcm.db.ShardTest;
import org.whispersystems.textsecuregcm.directorynotify.DirectoryNotifyManager;
import org.whispersystems.textsecuregcm.directorynotify.DirectoryNotifyTimer;
import org.whispersystems.textsecuregcm.distributedlock.DistributedLock;
import org.whispersystems.textsecuregcm.entities.SendMessageLogHandler;
import org.whispersystems.textsecuregcm.eslogger.loggerFilter;
import org.whispersystems.textsecuregcm.federation.FederatedClientManager;
import org.whispersystems.textsecuregcm.federation.FederatedPeer;
import org.whispersystems.textsecuregcm.filter.ResponseHeaderFilter;
import org.whispersystems.textsecuregcm.filter.UserAgentFilter;
import org.whispersystems.textsecuregcm.internal.*;
import org.whispersystems.textsecuregcm.limits.IPAllowList;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.liquibase.NameableMigrationsBundle;
import org.whispersystems.textsecuregcm.mappers.*;
import org.whispersystems.textsecuregcm.metrics.*;
import org.whispersystems.textsecuregcm.providers.RedisClientFactory;
import org.whispersystems.textsecuregcm.providers.RedisHealthCheck;
import org.whispersystems.textsecuregcm.providers.RedisSentinelClientFactory;
import org.whispersystems.textsecuregcm.push.*;
import org.whispersystems.textsecuregcm.redis.ReplicatedJedisPool;
import org.whispersystems.textsecuregcm.rpcclient.FriendGRPC;
import org.whispersystems.textsecuregcm.s3.UrlSignerAli;
import org.whispersystems.textsecuregcm.sms.SmsSender;
import org.whispersystems.textsecuregcm.sms.TwilioSmsSender;
import org.whispersystems.textsecuregcm.storage.*;
import org.whispersystems.textsecuregcm.util.Constants;
import org.whispersystems.textsecuregcm.util.JwtHelper;
import org.whispersystems.textsecuregcm.util.ProducerKafka;
import org.whispersystems.textsecuregcm.util.TokenUtil;
import org.whispersystems.textsecuregcm.websocket.AuthenticatedConnectListener;
import org.whispersystems.textsecuregcm.websocket.DeadLetterHandler;
import org.whispersystems.textsecuregcm.websocket.ProvisioningConnectListener;
import org.whispersystems.textsecuregcm.websocket.WebSocketAccountAuthenticator;
import org.whispersystems.textsecuregcm.workers.*;
import org.whispersystems.websocket.WebSocketResourceProviderFactory;
import org.whispersystems.websocket.setup.WebSocketEnvironment;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletRegistration;
import java.io.IOException;
import java.security.Security;
import java.util.*;

import static com.codahale.metrics.MetricRegistry.name;

public class WhisperServerService extends Application<WhisperServerConfiguration> {

  final private Logger logger = LoggerFactory.getLogger(WhisperServerService.class);

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  @Override
  public void initialize(Bootstrap<WhisperServerConfiguration> bootstrap) {
    bootstrap.addCommand(new DirectoryCommand());
    bootstrap.addCommand(new VacuumCommand());
    bootstrap.addCommand(new TrimMessagesCommand());
    bootstrap.addCommand(new PeriodicStatsCommand());
    bootstrap.addCommand(new DeleteUserCommand());
    bootstrap.addBundle(new NameableMigrationsBundle<WhisperServerConfiguration>("accountdb", "accountsdb.xml") {
      @Override
      public DataSourceFactory getDataSourceFactory(WhisperServerConfiguration configuration) {
        return configuration.getDataSourceFactory();
      }
    });

    bootstrap.addBundle(new NameableMigrationsBundle<WhisperServerConfiguration>("messagedb", "messagedb.xml") {
      @Override
      public DataSourceFactory getDataSourceFactory(WhisperServerConfiguration configuration) {
        return configuration.getMessageStoreConfiguration();
      }
    });
  }

  private void initESLogger(WhisperServerConfiguration config){
    ESLogConfig cfg = config.getESLoggerConfiguration();
    ESLogger.InitServerInfo(cfg.getMyServerIP(), cfg.getServiceName(),
            cfg.getUserName(), cfg.getPassword(), cfg.getEndPoint());
    if (cfg.getDefaultIndexName() != null && !cfg.getDefaultIndexName().equals(""))
      ESLogger.setDefaultIndexName(cfg.getDefaultIndexName());

  }

  @Override
  public String getName() {
    return "whisper-server";
  }

  @Override
  public void run(WhisperServerConfiguration config, Environment environment)
      throws Exception
  {
    SharedMetricRegistries.add(Constants.METRICS_NAME, environment.metrics());
    environment.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    environment.getObjectMapper().setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
    environment.getObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    initESLogger(config);

    DBIFactory dbiFactory = new DBIFactory();
    DBI        database   = dbiFactory.build(environment, config.getDataSourceFactory(), "accountdb");
    DBI        messagedb  = dbiFactory.build(environment, config.getMessageStoreConfiguration(), "messagedb");
    Jdbi messagedbv3  = new JdbiFactory().build(environment,config.getMessageStoreConfiguration(),"messagedb1");

    DBIFactory dbiShardFactory = new DBIFactory();
    Map<String, PooledDataSourceFactory> dataSourceFactoryMap=new HashMap<>();
    dataSourceFactoryMap.put("messages_shard_ds0",config.getMessageShardDb0());
    dataSourceFactoryMap.put("messages_shard_ds1",config.getMessageShardDb1());
    dataSourceFactoryMap.put("messages_shard_ds2",config.getMessageShardDb2());
    dataSourceFactoryMap.put("messages_shard_default",config.getMessageShardDb0());
    dbiShardFactory.initDateSource(environment,dataSourceFactoryMap);
    DBI shardDb=dbiShardFactory.buildByShard(environment,config.getDataSourceFactory(),"shardDb");
    MessagesForSharding messagesForSharding=shardDb.onDemand(MessagesForSharding.class);
    PushConversationsTable pushConversationsTable=messagedb.onDemand(PushConversationsTable.class);
    PushMessagesTable pushMessagesTable=messagedb.onDemand(PushMessagesTable.class);
    ConversationMsgsTable conversationMsgsTable=messagedb.onDemand(ConversationMsgsTable.class);
//    MessagesForSharding messagesForSharding=null;
//    PushConversationsTable pushConversationsTable=messagedb.onDemand(PushConversationsTable.class);
//    PushMessagesTable pushMessagesTable=null;

//    ShardTest shardTest=new ShardTest(messagesTest);

    // 初始化分布式锁单例
    DistributedLock.init(config.getDistributedLock());

    // 客户端版本分布
    ClientServiceImpl.setClientVersion(database.onDemand(ClientVersion.class));

    Accounts        accounts        = database.onDemand(Accounts.class);
    PendingAccounts pendingAccounts = database.onDemand(PendingAccounts.class);
    InternalAccountsTable internalAccountsTable = database.onDemand(InternalAccountsTable.class);
    InternalTeamsDB internalTeamsDB = database.onDemand(InternalTeamsDB.class);
    InternalTeamsAccountsTable internalTeamsAccountsTable = database.onDemand(InternalTeamsAccountsTable.class);
    InternalAccountsInvitationTable internalAccountsInvitationTable = database.onDemand(InternalAccountsInvitationTable.class);
    GroupsTable groupsTable = database.onDemand(GroupsTable.class);
    GroupMembersTable groupMembersTable = database.onDemand(GroupMembersTable.class);
    GroupAnnouncementTable groupAnnouncementTable = database.onDemand(GroupAnnouncementTable.class);
    PendingDevices  pendingDevices  = database.onDemand(PendingDevices.class);
    Keys            keys            = database.onDemand(Keys.class);
    Messages        messages        = messagedb.onDemand(Messages.class);
    MessagesV3      messagesV3      = messagedbv3.onDemand(MessagesV3.class);
    InternalServiceKeysTable        internalServiceKeysTable        = database.onDemand(InternalServiceKeysTable.class);
    ConversationsTable        conversationsTable        = database.onDemand(ConversationsTable.class);
    InteractsTable        interactsTable        = database.onDemand(InteractsTable.class);

    ReadReceiptsTable        readReceiptsTable        = database.onDemand(ReadReceiptsTable.class);
    RecallMsgInfosTable      recallMsgInfosTable      = messagedb.onDemand(RecallMsgInfosTable.class);

    FriendGRPC friendGRPC = new FriendGRPC(config.getFriendServerConfiguration());
//    JohnyTestTable2 testTable2 = database.onDemand(JohnyTestTable2.class);
//    JohnyTestManager johnyTestManager = database.onDemand(JohnyTestManager.class);
    GroupManagerWithTransaction groupManagerWithTransaction = database.onDemand(GroupManagerWithTransaction.class);
    groupManagerWithTransaction.registerMetrics();
    PlatformManager platformManager = database.onDemand(PlatformManager.class);
    RedisClientFactory cacheClientFactory = null;
    RedisClientFactory directoryClientFactory = null;
    RedisClientFactory messagesClientFactory = null;
    RedisClientFactory pushSchedulerClientFactory = null;
    if (config.getRedisSentinel().getPassword().isEmpty()) {
      cacheClientFactory = new RedisClientFactory(config.getCacheConfiguration().getUrl(), config.getCacheConfiguration().getReplicaUrls());
      directoryClientFactory = new RedisClientFactory(config.getDirectoryConfiguration().getUrl(), config.getDirectoryConfiguration().getReplicaUrls());
      messagesClientFactory = new RedisClientFactory(config.getMessageCacheConfiguration().getRedisConfiguration().getUrl(), config.getMessageCacheConfiguration().getRedisConfiguration().getReplicaUrls());
      pushSchedulerClientFactory = new RedisClientFactory(config.getPushScheduler().getUrl(), config.getPushScheduler().getReplicaUrls());
    } else {
      cacheClientFactory = new RedisSentinelClientFactory(config.getRedisSentinel().getSentinels(), config.getRedisSentinel().getPassword());
      directoryClientFactory = new RedisSentinelClientFactory(config.getRedisSentinel().getSentinels(), config.getRedisSentinel().getPassword());
      messagesClientFactory = new RedisSentinelClientFactory(config.getRedisSentinel().getSentinels(), config.getRedisSentinel().getPassword());
      pushSchedulerClientFactory = new RedisSentinelClientFactory(config.getRedisSentinel().getSentinels(), config.getRedisSentinel().getPassword());
    }
    RedisClientFactory cacheClusterClientFactory = new RedisClientFactory(config.getCacheCluster().getNodes(),config.getCacheCluster().getPool());
    ReplicatedJedisPool cacheClusterClient  = cacheClusterClientFactory.getRedisClientPool();


    ReplicatedJedisPool cacheClient         = cacheClientFactory.getRedisClientPool();
    ReplicatedJedisPool directoryClient     = directoryClientFactory.getRedisClientPool();
    ReplicatedJedisPool messagesClient      = messagesClientFactory.getRedisClientPool();
    ReplicatedJedisPool pushSchedulerClient = pushSchedulerClientFactory.getRedisClientPool();
    WhisperServerConfigurationApollo apolloConfig=new WhisperServerConfigurationApollo();
    MemCache memCache = new MemCache(cacheClusterClient);
    platformManager.setMemCache(memCache);
    ReadReceiptsManager readReceiptsManager=new ReadReceiptsManager(readReceiptsTable,memCache);
    DirectoryManager           directory                  = new DirectoryManager(directoryClient);
    PendingAccountsManager     pendingAccountsManager     = new PendingAccountsManager(pendingAccounts, memCache);
    PendingDevicesManager      pendingDevicesManager      = new PendingDevicesManager (pendingDevices, memCache );
    AccountsManager            accountsManager            = database.onDemand(AccountsManager.class);
    accountsManager.setFields( directory, memCache,config.getInternalTimedTaskConfiguration().getDeivceExpireThreshold(),config.getAccountManagerConfiguration(),apolloConfig, config.getEmail());
    accountsManager.setAccMaxMeetingVersion(config.getMeeting().getAccMaxMeetingVersion());
    //AccountExtend.setMaxMsgEncVersion(config.getAccMaxMsgEnvVersion());
    EnforcerFactory enforcerFactory = new EnforcerFactory(config.getEnforcerConfig());
    enforcerFactory.afterPropertiesSet();
    TeamsManagerCasbin teamsManager = new TeamsManagerCasbin(internalTeamsDB, internalTeamsAccountsTable, accountsManager, memCache, enforcerFactory);
    teamsManager.setFriendGRPC(friendGRPC);
    accountsManager.setTeamsManager(teamsManager);
    InvitationsManager invitationsManager = new InvitationsManager(accountsManager, internalAccountsInvitationTable);
//    GroupManager               groupManager               = new GroupManager(groupsTable, groupMembersTable, groupAnnouncementTable, cacheClient);
    FederatedClientManager     federatedClientManager     = new FederatedClientManager(environment, config.getJerseyClientConfiguration(), config.getFederationConfiguration());
    MessagesCache              messagesCache              = new MessagesCache(messagesClient, messages, accountsManager, config.getMessageCacheConfiguration().getPersistDelayMinutes());
    MessagesManager            messagesManager            = new MessagesManager(messages, messagesCache, config.getMessageCacheConfiguration().getCacheRate(),memCache,new ArrayList<>(),messagesForSharding,pushConversationsTable,pushMessagesTable,readReceiptsManager,accountsManager,messagesV3,recallMsgInfosTable,conversationMsgsTable,groupManagerWithTransaction,config.getDataSourceFactory());
    DeadLetterHandler          deadLetterHandler          = new DeadLetterHandler(messagesManager);
    DispatchManager            dispatchManager            = new DispatchManager(cacheClusterClientFactory, Optional.of(deadLetterHandler));
    PubSubManager              pubSubManager              = new PubSubManager(cacheClusterClient, dispatchManager,memCache);
    APNSender                  apnSender                  = new APNSender(accountsManager, config.getApnConfigurations(),config.getVoip());
    TpnSender                 tpnSender                  = new TpnSender(config.getTpn());
    GCMSender                  gcmSender                  = new GCMSender(accountsManager, config.getGcmConfiguration().getApiKey());
    WebsocketSender            websocketSender            = new WebsocketSender(messagesManager, pubSubManager,memCache);
    AccountAuthenticator       deviceAuthenticator        = new AccountAuthenticator(accountsManager);
    FederatedPeerAuthenticator federatedPeerAuthenticator = new FederatedPeerAuthenticator(config.getFederationConfiguration());
    RateLimiters               rateLimiters               = new RateLimiters(config.getLimitsConfiguration(), memCache);
    IPAllowList                ipAllowList                = new IPAllowList();
    ProducerKafka              producerKafka              = new ProducerKafka(config.getKafkaConfiguration());
    KafkaSender                kafkaSender                = new KafkaSender(accountsManager,messagesManager,producerKafka,memCache);
    InternalServiceKeyStorage internalServiceKeyStorage=new InternalServiceKeyStorage(memCache,internalServiceKeysTable);
    TokenUtil tokenUtil=new TokenUtil(config.getAuthorization().getPubkey(), config.getAuthorization().getPrivkey(),config.getAuthorization().getEffectiveDuration());
    accountsManager.setPubSubManager(pubSubManager);
    readReceiptsManager.setMessagesManager(messagesManager);
    accountsManager.setMessagesManager(messagesManager);
    ConversationManager conversationManager=new ConversationManager(conversationsTable,memCache,messagesManager);
    InteractManager interactManager=new InteractManager(interactsTable,memCache,accountsManager);
    ApnFallbackManager       apnFallbackManager  = new ApnFallbackManager(pushSchedulerClient, apnSender, accountsManager);
    TwilioSmsSender          twilioSmsSender     = new TwilioSmsSender(config.getTwilioConfiguration());
    SmsSender                smsSender           = new SmsSender(twilioSmsSender);
    // UrlSignerAli             urlSigner           = new UrlSignerAli(config.getAttachmentsConfiguration(), config.getAttachmentsConfiguration().getBucket());
    UrlSignerAli urlSigner = new UrlSignerAli(config.getAttachmentsConfiguration());
//    TurnTokenGenerator       turnTokenGenerator  = new TurnTokenGenerator(config.getTurnConfiguration());
    // 使用分布式锁
    final JwtHelper jwtHelper = new JwtHelper(config.getAuthorization().getPubkey(), config.getAuthorization().getPrivkey());
    InternalAccountManager internalAccountManager = DistributedLock.proxyObj(new InternalAccountManager(
            internalAccountsTable, internalTeamsDB,
            internalTeamsAccountsTable,
            internalAccountsInvitationTable,
            pendingAccountsManager,
            accountsManager,teamsManager,
            config.getTestDevices(),
            rateLimiters, memCache,keys,messagesManager,
            apolloConfig));
    internalAccountManager.setFriendGRPC(friendGRPC);
    internalAccountManager.setJwtHelper(jwtHelper);

    PushSender               pushSender          = new PushSender(apnFallbackManager,tpnSender, /*gcmSender,*/ apnSender, websocketSender, config.getPushConfiguration(), accountsManager, memCache,groupManagerWithTransaction,kafkaSender,messagesManager,conversationManager);
    ReceiptSender            receiptSender       = new ReceiptSender(accountsManager, pushSender, federatedClientManager);
//    PrometheusReporter prometheusReporter=new PrometheusReporter(environment.metrics(),config.prometheusConfig);
    DelayNotification.init(pushSender);
    messagesCache.setPubSubManager(pubSubManager, pushSender);

    apnSender.setApnFallbackManager(apnFallbackManager);
    environment.lifecycle().manage(apnFallbackManager);
    environment.lifecycle().manage(pubSubManager);
    environment.lifecycle().manage(pushSender);
    environment.lifecycle().manage(messagesCache);

//    ClusterTest clusterTest=new ClusterTest(cacheClusterClient,cacheClusterClientFactory,pubSubManager);
//    environment.jersey().register(clusterTest);

    AttachmentController attachmentController = new AttachmentController(rateLimiters, federatedClientManager, urlSigner);
    KeysController       keysController       = new KeysController(rateLimiters, keys, accountsManager, federatedClientManager);
    MessageController    messageController    = new MessageController(rateLimiters, pushSender, receiptSender, accountsManager, messagesManager, federatedClientManager, apnFallbackManager,teamsManager,conversationManager,readReceiptsManager);
    MessageControllerV3 messageControllerV3 = new MessageControllerV3(rateLimiters, pushSender, receiptSender, accountsManager, messagesManager, federatedClientManager, apnFallbackManager,teamsManager,conversationManager,readReceiptsManager);

    NotifyManager notifyManager=new NotifyManager(websocketSender,accountsManager,pushSender,kafkaSender,messagesManager);
    conversationManager.setNotifyManager(notifyManager);
    DirectoryNotifyManager directoryNotifyManager=new DirectoryNotifyManager(memCache,teamsManager,notifyManager,accountsManager, config.getDirectoryNotify(),groupManagerWithTransaction,directoryClient);
    teamsManager.setDirectoryNotifyManager(directoryNotifyManager);
    accountsManager.setDirectoryNotifyManager(directoryNotifyManager);
    DirectoryNotifyTimer directoryNotifyTimer=new DirectoryNotifyTimer(directoryNotifyManager,config.getDirectoryNotify());
    directoryNotifyTimer.init();
    AuthenticationController authenticationController = new AuthenticationController(accountsManager, internalAccountManager,apolloConfig);

    AuthenticationControllerV2 authenticationControllerV2 = new AuthenticationControllerV2(accountsManager, internalAccountManager, config.getOkta());
//    GroupController groupController = new GroupController(accountsManager, groupManager,notifyManager,internalAccountManager);

//    johnyTestManager.setCacheClient(cacheClient);
//    JohnyTestManager2 johnyTestManager2=new JohnyTestManager2(testTable2) ;
//    JohnyTestController johnyTestController=new JohnyTestController(johnyTestManager,johnyTestManager2);

    groupManagerWithTransaction.setMemCache(memCache);
    groupManagerWithTransaction.setGroupConfiguration(config.getGroupConfiguration());
    groupManagerWithTransaction.setNotifyManager(notifyManager);
    groupManagerWithTransaction.setAccountsManager(accountsManager);
    groupManagerWithTransaction.setTokenUtil(tokenUtil);
    groupManagerWithTransaction.caffeineSub();
    groupManagerWithTransaction.setGroupMaxMeetingVersion(config.getMeeting().getGroupMaxMeetingVersion());
//    groupManagerWithTransaction.setTeamsManager(teamsManager);
    accountsManager.setGroupManager(groupManagerWithTransaction);
    GroupControllerWithTransaction groupControllerWithTransaction=new GroupControllerWithTransaction(accountsManager,groupManagerWithTransaction,tokenUtil);

    // edit by guolilei
//    MessageController    messageController    = new MessageController(rateLimiters, pushSender, receiptSender, accountsManager, messagesManager, federatedClientManager/*, apnFallbackManager*/);


    final UrlSignerAli urlSignerAli = new UrlSignerAli(config.getAvatarsConfiguration());
    ProfileController    profileController    = new ProfileController(memCache, rateLimiters, accountsManager, config.getProfilesConfiguration(), urlSignerAli, internalAccountsInvitationTable,teamsManager);
    MeetingController meetingController = new MeetingController(config.getMeeting().getAuth().getTokenSecret());
    // AuthorizationController authorizationController = new AuthorizationController(accountsManager, rateLimiters, config.getAuthorization().getPubkey(), config.getAuthorization().getPrivkey());
    AuthorizationController authorizationController = new AuthorizationController(accountsManager, rateLimiters, config.getAuthorization().getPubkey(), config.getAuthorization().getPrivkey(), config.getMeeting().getAuth().getTokenSecret());

    environment.jersey().register(new ClientLogger());
    environment.jersey().register(new BotPropertyController(database.onDemand(BotPropertyTable.class)));


    UserAgentFilter userAgentFilter=new UserAgentFilter(memCache,accountsManager,apnSender,deviceAuthenticator,config.getForcedUpgrade());
//    environment.jersey().register(userAgentFilter);
    //environment.jersey().register(new UserAgentFilterFeature(userAgentFilter));
    ResponseHeaderFilter responseHeaderFilter=new ResponseHeaderFilter();
    environment.jersey().register(responseHeaderFilter);

    environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<Account>()
                                                             .setAuthenticator(deviceAuthenticator)
                                                             .setPrincipal(Account.class)
                                                             .buildAuthFilter(),
                                                         new BasicCredentialAuthFilter.Builder<FederatedPeer>()
                                                             .setAuthenticator(federatedPeerAuthenticator)
                                                             .setPrincipal(FederatedPeer.class)
                                                             .buildAuthFilter()));
    environment.jersey().register(new AuthValueFactoryProvider.Binder());

//    environment.jersey().register(new AccountController(pendingAccountsManager, accountsManager, rateLimiters, smsSender, messagesManager, turnTokenGenerator, config.getTestDevices()));
    environment.jersey().register(new AccountController(pendingAccountsManager, accountsManager, rateLimiters, smsSender, messagesManager,/* turnTokenGenerator,*/ config.getTestDevices(), internalAccountsTable, internalAccountsInvitationTable,
            config.getEmail(),internalAccountManager, memCache));
    environment.jersey().register(new HealthCheckController());

    environment.jersey().register(new DeviceController(pendingDevicesManager, accountsManager, messagesManager, rateLimiters, config.getMaxDevices()));
    environment.jersey().register(new DirectoryController(rateLimiters, directory, accountsManager, teamsManager, websocketSender, internalAccountsInvitationTable,directoryNotifyManager,interactManager, conversationManager));
    environment.jersey().register(new FederationControllerV1(accountsManager, attachmentController, messageController));
    environment.jersey().register(new FederationControllerV2(accountsManager, attachmentController, messageController, keysController));
    environment.jersey().register(new ProvisioningController(rateLimiters, pushSender));
    environment.jersey().register(new IndexController());
    environment.jersey().register(attachmentController);
    environment.jersey().register(keysController);
    environment.jersey().register(messageController);
    environment.jersey().register(messageControllerV3);
    environment.jersey().register(authenticationController);
    environment.jersey().register(authenticationControllerV2);
//    environment.jersey().register(groupController);
    environment.jersey().register(profileController);
//    environment.jersey().register(johnyTestController);
    environment.jersey().register(groupControllerWithTransaction);
    environment.jersey().register(meetingController);
    environment.jersey().register(authorizationController);
    environment.jersey().register(new InternalAPIController(ipAllowList, accountsManager));

    environment.jersey().register(new VersionController(memCache));
    environment.jersey().register(new MessageRemindController(accountsManager));
    environment.jersey().register(new ReadReceiptController(rateLimiters,readReceiptsManager,messagesManager));
    environment.jersey().register(new ConversationController(conversationManager,config.getConversationConfiguration(), friendGRPC));
    environment.jersey().register(new InteractController(interactManager,accountsManager));


    WebSocketEnvironment webSocketEnvironment = new WebSocketEnvironment(environment, config.getWebSocketConfiguration(), 90000);
    webSocketEnvironment.setAuthenticator(new WebSocketAccountAuthenticator(deviceAuthenticator,memCache));
    webSocketEnvironment.setConnectListener(new AuthenticatedConnectListener(pushSender, receiptSender, messagesManager, pubSubManager, apnFallbackManager));

    webSocketEnvironment.jersey().register(new KeepAliveController(pubSubManager,accountsManager));
    webSocketEnvironment.jersey().register(messageController);
    webSocketEnvironment.jersey().register(messageControllerV3);
    webSocketEnvironment.jersey().register(profileController);
    webSocketEnvironment.jersey().register(meetingController);
    webSocketEnvironment.jersey().register(authorizationController);

    WebSocketEnvironment provisioningEnvironment = new WebSocketEnvironment(environment, webSocketEnvironment.getRequestLog(), 60000);
    provisioningEnvironment.setConnectListener(new ProvisioningConnectListener(pubSubManager));
    provisioningEnvironment.jersey().register(new KeepAliveController(pubSubManager,accountsManager));

    WebSocketResourceProviderFactory webSocketServlet    = new WebSocketResourceProviderFactory(webSocketEnvironment   );
    WebSocketResourceProviderFactory provisioningServlet = new WebSocketResourceProviderFactory(provisioningEnvironment);

    ServletRegistration.Dynamic websocket    = environment.servlets().addServlet("WebSocket", webSocketServlet      );
    ServletRegistration.Dynamic provisioning = environment.servlets().addServlet("Provisioning", provisioningServlet);

    websocket.addMapping("/v1/websocket/");
    websocket.setAsyncSupported(true);

    provisioning.addMapping("/v1/websocket/provisioning/");
    provisioning.setAsyncSupported(true);

    webSocketServlet.start();
    provisioningServlet.start();

    FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
    filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    filter.setInitParameter("allowedOrigins", "*");
    filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,X-Signal-Agent");
    filter.setInitParameter("allowedMethods", "GET,PUT,POST,DELETE,OPTIONS");
    filter.setInitParameter("preflightMaxAge", "5184000");
    filter.setInitParameter("allowCredentials", "true");

    loggerFilter.initVersionTable(database.onDemand(ClientVersionTable.class),memCache);
    FilterRegistration.Dynamic filterLog =  environment.servlets().addFilter("logger", loggerFilter.class);

    filterLog.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

    FilterRegistration.Dynamic uaFilter =  environment.servlets().addFilter("uaFilter", userAgentFilter);

    uaFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

    environment.healthChecks().register("directory", new RedisHealthCheck(memCache));
    environment.healthChecks().register("cache", new RedisHealthCheck(memCache));

    environment.jersey().register(new IOExceptionMapper());
    environment.jersey().register(new RegisterExceptionMapper());
    environment.jersey().register(new RateLimitExceededExceptionMapper());
    environment.jersey().register(new InvalidWebsocketAddressExceptionMapper());
    environment.jersey().register(new DeviceLimitExceededExceptionMapper());
    environment.jersey().register(new NotFoundMapper());

//    environment.metrics().register(name(BufferPoolMetricSet.class, "bufferPoolMetricSet"), new BufferPoolMetricSet());
//    environment.metrics().register(name(CachedThreadStatesGaugeSet.class, "memoryUsageGaugeSet"), new CachedThreadStatesGaugeSet());
//    environment.metrics().register(name(ClassLoadingGaugeSet.class, "classLoadingGaugeSet"), new ClassLoadingGaugeSet());
//    environment.metrics().register(name(GarbageCollectorMetricSet.class, "garbageCollectorMetricSet"), new GarbageCollectorMetricSet());
//    environment.metrics().register(name(JmxAttributeGauge.class, "JmxAttributeGauge"), new JmxAttributeGauge());
//    environment.metrics().register(name(JvmAttributeGaugeSet.class, "jvmAttributeGaugeSet"), new JvmAttributeGaugeSet());
//    environment.metrics().register(name(MemoryUsageGaugeSet.class, "memoryUsageGaugeSet"), new MemoryUsageGaugeSet());
//    environment.metrics().register(name(ThreadStatesGaugeSet.class, "threadStatesGaugeSet"), new ThreadStatesGaugeSet());

    environment.metrics().register(name(CpuUsageGauge.class, "cpu"), new CpuUsageGauge());
    environment.metrics().register(name(FreeMemoryGauge.class, "free_memory"), new FreeMemoryGauge());
    environment.metrics().register(name(NetworkSentGauge.class, "bytes_sent"), new NetworkSentGauge());
    environment.metrics().register(name(NetworkReceivedGauge.class, "bytes_received"), new NetworkReceivedGauge());
    environment.metrics().register(name(FileDescriptorGauge.class, "fd_count"), new FileDescriptorGauge());
    environment.metrics().register(name(LinuxDesk.class, "deskUsed"), (Gauge<Double>) LinuxDesk.getDeskUsage()::getUsed);
    environment.metrics().register(name(LinuxDesk.class, "deskUsedRate"), (Gauge<Double>) LinuxDesk.getDeskUsage()::getUseRate);
    environment.metrics().register(name(SendMessageLogHandler.class, "SendMessageLogHandler_executor_depth"), (Gauge<Long>) SendMessageLogHandler::getTaskCount);

    CollectorRegistry collectorRegistry = new CollectorRegistry();
    collectorRegistry.register(new DropwizardExports(environment.metrics()));
    MetricsServlet metricsServlet    = new MetricsServlet(collectorRegistry);

    ServletRegistration.Dynamic metrics  =environment.servlets().addServlet("metrics", metricsServlet);
    metrics.addMapping("/metrics");

    // init Error pages
    // final ErrorPageErrorHandler epeh = new ErrorPageErrorHandler();
    // // 400 - Bad Request, leave alone
    // epeh.addErrorPage(401, "/error/general-error");
    // epeh.addErrorPage(402, "/error/general-error");
    // epeh.addErrorPage(403, "/error/403");
    // epeh.addErrorPage(404, "/error/404");
    // epeh.addErrorPage(405, 499, "/error/general-error");
    // epeh.addErrorPage(500, 599, "/error/general-error");
    // environment.getApplicationContext().setErrorHandler(epeh);
    // environment.getAdminContext().setErrorHandler(epeh);
    //
    // ErrorController errorResource = new ErrorController();
    // environment.jersey().register(errorResource);

    // GRPC service
    // TODO: get configs from file
    int port = 55565;
    Server server = ServerBuilder.forPort(port).intercept(new ResponseHeaderInsterceptor())
            .addService(new InternalAccountsServiceImpl(conversationManager, urlSignerAli,accountsManager, internalAccountManager,
                    new InternalServicePermissionService(platformManager,teamsManager)))
            .addService(ServerInterceptors.intercept(new ClientServiceImpl(),new GrpcSignatureVerifierInterceptor(internalServiceKeyStorage)))
            .addService(ServerInterceptors.intercept(new InternalNotifyServiceImpl(accountsManager,rateLimiters,notifyManager,groupManagerWithTransaction),new GrpcSignatureVerifierInterceptor(internalServiceKeyStorage)))
//            .addService(new InternalNotifyServiceImpl(accountsManager,rateLimiters,notifyManager,groupManagerWithTransaction))
            .addService(new InternalTeamsServiceImpl(accountsManager, teamsManager, new InternalServicePermissionService(platformManager,teamsManager)))
            .addService((new InternalAccountsInvitationServiceImpl(invitationsManager)))
            .addService((new InternalDirectoryServiceImpl(accountsManager,notifyManager,teamsManager,directoryNotifyManager)))
            .addService(new GroupServiceImpl(urlSigner, accountsManager, groupManagerWithTransaction))
            .addService(new InternalTimedTaskServiceImpl(accountsManager, messagesManager,groupManagerWithTransaction,config.getInternalTimedTaskConfiguration(),config.getEmail()))
            .addService(ServerInterceptors.intercept(new AuthorizationServiceImpl(accountsManager, rateLimiters, config.getAuthorization().getPubkey(), config.getAuthorization().getPrivkey()), new UserAuthServerInsterceptor<>(deviceAuthenticator)))
            .addService(ProtoReflectionService.newInstance())
            .addService(ServerInterceptors.intercept(new InternalMessageServiceImpl(messagesManager),new GrpcSignatureVerifierInterceptor(internalServiceKeyStorage)))
            .build();
    try {
      server.start();
      logger.info("GRPC service started on port: " + port);
    } catch (IOException e) {
      logger.error("failed to start GRPC service: ", e);
    }
  }

  public static void main(String[] args) throws Exception {
    new WhisperServerService().run(args);
  }
}
