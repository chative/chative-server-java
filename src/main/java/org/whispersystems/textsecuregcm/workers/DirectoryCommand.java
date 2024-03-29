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
package org.whispersystems.textsecuregcm.workers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.ImmutableListContainerFactory;
import io.dropwizard.jdbi.ImmutableSetContainerFactory;
import io.dropwizard.jdbi.OptionalContainerFactory;
import io.dropwizard.jdbi.args.OptionalArgumentFactory;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.WhisperServerConfiguration;
import org.whispersystems.textsecuregcm.providers.RedisClientFactory;
import org.whispersystems.textsecuregcm.providers.RedisSentinelClientFactory;
import org.whispersystems.textsecuregcm.redis.ReplicatedJedisPool;
import org.whispersystems.textsecuregcm.storage.*;

public class DirectoryCommand extends EnvironmentCommand<WhisperServerConfiguration> {

  private final Logger logger = LoggerFactory.getLogger(DirectoryCommand.class);

  public DirectoryCommand() {
    super(new Application<WhisperServerConfiguration>() {
      @Override
      public void run(WhisperServerConfiguration configuration, Environment environment)
          throws Exception
      {

      }
    }, "directory", "Update directory from DB and peers.");
  }

  @Override
  protected void run(Environment environment, Namespace namespace,
                     WhisperServerConfiguration configuration)
      throws Exception
  {
    try {
      environment.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      DataSourceFactory dbConfig = configuration.getReadDataSourceFactory();
      DBI               dbi      = new DBI(dbConfig.getUrl(), dbConfig.getUser(), dbConfig.getPassword());

      dbi.registerArgumentFactory(new OptionalArgumentFactory(dbConfig.getDriverClass()));
      dbi.registerContainerFactory(new ImmutableListContainerFactory());
      dbi.registerContainerFactory(new ImmutableSetContainerFactory());
      dbi.registerContainerFactory(new OptionalContainerFactory());

      Accounts            accounts        = dbi.onDemand(Accounts.class);
      InternalAccountsTable internalAccountsTable = dbi.onDemand(InternalAccountsTable.class);

      ReplicatedJedisPool cacheClient = null;
      ReplicatedJedisPool redisClient = null;
      if (configuration.getRedisSentinel().getPassword().isEmpty()) {
        cacheClient = new RedisClientFactory(configuration.getCacheConfiguration().getUrl(), configuration.getCacheConfiguration().getReplicaUrls()).getRedisClientPool();
        redisClient = new RedisClientFactory(configuration.getDirectoryConfiguration().getUrl(), configuration.getDirectoryConfiguration().getReplicaUrls()).getRedisClientPool();
      } else {
        cacheClient = new RedisSentinelClientFactory(configuration.getRedisSentinel().getSentinels(), configuration.getRedisSentinel().getPassword()).getRedisClientPool();
        redisClient = new RedisSentinelClientFactory(configuration.getRedisSentinel().getSentinels(), configuration.getRedisSentinel().getPassword()).getRedisClientPool();
      }

      MemCache memCache = new MemCache(cacheClient);

      DirectoryManager    directory       = new DirectoryManager(redisClient);

      AccountsManager     accountsManager = dbi.onDemand(AccountsManager.class); accountsManager.setFields( directory, memCache,configuration.getInternalTimedTaskConfiguration().getDeivceExpireThreshold(),configuration.getAccountManagerConfiguration(),null, configuration.getEmail());

//      FederatedClientManager federatedClientManager = new FederatedClientManager(environment,
//                                                                                 configuration.getJerseyClientConfiguration(),
//                                                                                 configuration.getFederationConfiguration());

      DirectoryUpdater update = new DirectoryUpdater(accountsManager, directory);

      update.updateFromLocalDatabase();
//      update.updateFromPeers();
    } catch (Exception ex) {
      logger.warn("Directory Exception", ex);
      throw new RuntimeException(ex);
    } finally {
//      Thread.sleep(3000);
//      System.exit(0);
    }
  }
}
