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
package org.whispersystems.textsecuregcm.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.dispatch.io.RedisPubSubConnectionFactory;
import org.whispersystems.dispatch.redis.PubSubConnection;
import org.whispersystems.textsecuregcm.redis.ReplicatedJedisPool;
import org.whispersystems.textsecuregcm.redis.ReplicatedJedisSentinelPool;
import org.whispersystems.textsecuregcm.util.Util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.RedisInputStream;

public class RedisSentinelClientFactory extends RedisClientFactory {

  private final Logger logger = LoggerFactory.getLogger(RedisSentinelClientFactory.class);

  private final JedisSentinelPool sentinelPool;
  private final ReplicatedJedisPool jedisPool;
  private final String password;

  public RedisSentinelClientFactory(List<String> sentinels, String password) throws URISyntaxException {
    super("redis://127.0.0.1:6379/0", new ArrayList<String>(){{
      add("redis://127.0.0.1:6379/0");
    }}); // fake
    this.password = password;

    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setTestOnBorrow(true);

    sentinelPool = new JedisSentinelPool("mymaster", new HashSet<>(sentinels), password);
    jedisPool = (ReplicatedJedisPool)new ReplicatedJedisSentinelPool(sentinelPool);
  }

  public ReplicatedJedisPool getRedisClientPool() {
    return jedisPool;
  }

  @Override
  public PubSubConnection connect() {
    while (true) {
      try {
        String host = sentinelPool.getCurrentHostMaster().getHost();
        int port = sentinelPool.getCurrentHostMaster().getPort();

        Socket socket = new Socket(host, port);

        // auth
        if (null != this.password) {
          OutputStream outputStream = socket.getOutputStream();
          RedisInputStream inputStream = new RedisInputStream(new BufferedInputStream(socket.getInputStream()));

          byte[] command = org.whispersystems.dispatch.util.Util.combine("AUTH ".getBytes(), this.password.getBytes(), "\r\n".getBytes());
          outputStream.write(command);

          String resp = inputStream.readLine();
          if (!resp.equals("+OK")) {
            throw new IOException("auth failed: " + resp);
          }
        }

        return new PubSubConnection(socket);
      } catch (IOException e) {
        logger.warn("Error connecting", e);
        Util.sleep(2000);
      } catch (Exception e) {
        e.printStackTrace();
        Util.sleep(2000);
      }
    }
  }
}
