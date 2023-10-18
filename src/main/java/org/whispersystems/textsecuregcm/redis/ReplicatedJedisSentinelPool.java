package org.whispersystems.textsecuregcm.redis;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

public class ReplicatedJedisSentinelPool extends ReplicatedJedisPool {

  private final JedisSentinelPool sentinels;

  public ReplicatedJedisSentinelPool(JedisSentinelPool sentinels) {
    super(new JedisPool(), new ArrayList<JedisPool>(){{
      add(new JedisPool());
    }}); // fake
    this.sentinels = sentinels;
  }

  public Jedis getWriteResource() {
    return this.sentinels.getResource();
  }

  public void returnWriteResource(Jedis jedis) {
    jedis.close();
  }

  public Jedis getReadResource() {
    return this.sentinels.getResource();
  }

}
