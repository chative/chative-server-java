package org.whispersystems.textsecuregcm.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisClusterConnectionHandler;

import java.util.Collections;
import java.util.Set;

public class MyJedisCluster extends JedisCluster {
    public MyJedisCluster(HostAndPort node) {
        this((Set) Collections.singleton(node), 2000);
    }

    public MyJedisCluster(HostAndPort node, int timeout) {
        this((Set)Collections.singleton(node), timeout, 5);
    }

    public MyJedisCluster(HostAndPort node, int timeout, int maxAttempts) {
        this(Collections.singleton(node), timeout, maxAttempts, new GenericObjectPoolConfig());
    }

    public MyJedisCluster(HostAndPort node, GenericObjectPoolConfig poolConfig) {
        this((Set)Collections.singleton(node), 2000, 5, poolConfig);
    }

    public MyJedisCluster(HostAndPort node, int timeout, GenericObjectPoolConfig poolConfig) {
        this((Set)Collections.singleton(node), timeout, 5, poolConfig);
    }

    public MyJedisCluster(HostAndPort node, int timeout, int maxAttempts, GenericObjectPoolConfig poolConfig) {
        this(Collections.singleton(node), timeout, maxAttempts, poolConfig);
    }

    public MyJedisCluster(HostAndPort node, int connectionTimeout, int soTimeout, int maxAttempts, GenericObjectPoolConfig poolConfig) {
        super(Collections.singleton(node), connectionTimeout, soTimeout, maxAttempts, poolConfig);
    }

    public MyJedisCluster(HostAndPort node, int connectionTimeout, int soTimeout, int maxAttempts, String password, GenericObjectPoolConfig poolConfig) {
        super(Collections.singleton(node), connectionTimeout, soTimeout, maxAttempts, password, poolConfig);
    }

    public MyJedisCluster(Set<HostAndPort> nodes) {
        this((Set)nodes, 2000);
    }

    public MyJedisCluster(Set<HostAndPort> nodes, int timeout) {
        this((Set)nodes, timeout, 5);
    }

    public MyJedisCluster(Set<HostAndPort> nodes, int timeout, int maxAttempts) {
        this(nodes, timeout, maxAttempts, new GenericObjectPoolConfig());
    }

    public MyJedisCluster(Set<HostAndPort> nodes, GenericObjectPoolConfig poolConfig) {
        this((Set)nodes, 2000, 5, poolConfig);
    }

    public MyJedisCluster(Set<HostAndPort> nodes, int timeout, GenericObjectPoolConfig poolConfig) {
        this((Set)nodes, timeout, 5, poolConfig);
    }

    public MyJedisCluster(Set<HostAndPort> jedisClusterNode, int timeout, int maxAttempts, GenericObjectPoolConfig poolConfig) {
        super(jedisClusterNode, timeout, maxAttempts, poolConfig);
    }

    public MyJedisCluster(Set<HostAndPort> jedisClusterNode, int connectionTimeout, int soTimeout, int maxAttempts, GenericObjectPoolConfig poolConfig) {
        super(jedisClusterNode, connectionTimeout, soTimeout, maxAttempts, poolConfig);
    }

    public MyJedisCluster(Set<HostAndPort> jedisClusterNode, int connectionTimeout, int soTimeout, int maxAttempts, String password, GenericObjectPoolConfig poolConfig) {
        super(jedisClusterNode, connectionTimeout, soTimeout, maxAttempts, password, poolConfig);
    }
    public JedisClusterConnectionHandler getJedisClusterConnectionHandler(){
        return super.connectionHandler;
    }
}
