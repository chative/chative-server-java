package org.whispersystems.textsecuregcm.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.JedisClusterCRC16;

public class ReplicatedJedisPool {

  private final Logger        logger       = LoggerFactory.getLogger(ReplicatedJedisPool.class);
  private final AtomicInteger replicaIndex = new AtomicInteger(0);

  private JedisPool   master;
  private JedisPool[] replicas;
  private MyJedisCluster myJedisCluster;

  public ReplicatedJedisPool(JedisPool master, List<JedisPool> replicas) {
    if (replicas.size() < 1) throw new IllegalArgumentException("There must be at least one replica");

    this.master   = master;
    this.replicas = new JedisPool[replicas.size()];

    for (int i=0;i<this.replicas.length;i++) {
      this.replicas[i] = replicas.get(i);
    }
  }

  public ReplicatedJedisPool(Set<HostAndPort> nodes, GenericObjectPoolConfig poolConfig) {
    myJedisCluster=new MyJedisCluster(nodes,100,100,3,poolConfig);
  }

  public Jedis getWriteResource() {
    return master.getResource();
  }

  public boolean isCluster() {
    return myJedisCluster!=null;
  }

//  public Jedis getWriteResource(String key) {
//    int slot = JedisClusterCRC16.getSlot(key);
//    Jedis jedis=((JedisSlotBasedConnectionHandler)myJedisCluster.getJedisClusterConnectionHandler()).getConnectionFromSlot(slot);
////    logger.info("ReplicatedJedisPool.getWriteResource key:{},slot:{},jedis host:{},jedis port:{}",key,slot,jedis.getClient().getHost(),jedis.getClient().getPort());
//    return jedis;
//  }

  public JedisCluster getJedisCluster() {
    return myJedisCluster;
  }

//  public Jedis getReadResource(String key) {
//    int slot = JedisClusterCRC16.getSlot(key);
//    Jedis jedis=((JedisSlotBasedConnectionHandler)myJedisCluster.getJedisClusterConnectionHandler()).getConnectionFromSlot(slot);
//    return jedis;
//  }

//  public Jedis getReadResource(String key) {
//    int slot = JedisClusterCRC16.getSlot(key);
//    Jedis jedis=((JedisSlotBasedConnectionHandler)myJedisCluster.getJedisClusterConnectionHandler()).getConnectionFromSlot(slot);
//    return jedis;
//  }

  public Map<String, JedisPool> getClusterNodes() {
    if(myJedisCluster!=null) {
      return myJedisCluster.getClusterNodes();
    }else{
      return null;
    }
  }

  public JedisPool getClusterNodes(String nodeKey) {
    if(myJedisCluster!=null) {
      return myJedisCluster.getClusterNodes().get(nodeKey);
    }else{
      return null;
    }
  }


  public void returnWriteResource(Jedis jedis) {
    master.returnResource(jedis);
  }

  public Jedis getReadResource() {
    int failureCount = 0;

    while (failureCount < replicas.length) {
      try {
        return replicas[replicaIndex.getAndIncrement() % replicas.length].getResource();
      } catch (JedisException e) {
        logger.error("Failure obtaining read replica pool", e);
      }

      failureCount++;
    }

    throw new JedisException("All read replica pools failed!");
  }

}
