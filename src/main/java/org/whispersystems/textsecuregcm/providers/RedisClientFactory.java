/**
 * Copyright (C) 2013 Open WhisperSystems
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.whispersystems.textsecuregcm.providers;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.dispatch.io.RedisInputStream;
import org.whispersystems.dispatch.io.RedisPubSubConnectionFactory;
import org.whispersystems.dispatch.redis.PubSubConnection;
import org.whispersystems.textsecuregcm.distributedlock.DistributedLock;
import org.whispersystems.textsecuregcm.entities.DirectoryNotify;
import org.whispersystems.textsecuregcm.redis.ReplicatedJedisPool;
import org.whispersystems.textsecuregcm.util.StringUtil;
import org.whispersystems.textsecuregcm.util.Util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.locks.Lock;

import redis.clients.jedis.*;

import javax.net.ssl.SSLSocketFactory;

public class RedisClientFactory implements RedisPubSubConnectionFactory {

    private final Logger logger = LoggerFactory.getLogger(RedisClientFactory.class);

    private  String host;
    private  int port;
    private  boolean ssl;
    private String password = null;
    private  ReplicatedJedisPool jedisPool;

    public RedisClientFactory(String url, List<String> replicaUrls) throws URISyntaxException {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setTestOnBorrow(true);

        URI redisURI = new URI(url);

        this.host = redisURI.getHost();
        int port = redisURI.getPort();
        this.port = -1 == port ? 6379 : port;
        this.password = null;
        String auth = redisURI.getUserInfo();
        if (null != auth && auth.contains(":")) {
            this.password = auth.split(":")[1];
        }
        // 确定是否是tls
        ssl = redisURI.getScheme() != null && redisURI.getScheme().equals("rediss");

        JedisPool masterPool = new JedisPool(poolConfig, redisURI, Protocol.DEFAULT_TIMEOUT,
                null, null, null);

        List<JedisPool> replicaPools = new LinkedList<>();

        for (String replicaUrl : replicaUrls) {
            URI replicaURI = new URI(replicaUrl);
            replicaPools.add(new JedisPool(poolConfig, replicaURI, Protocol.DEFAULT_TIMEOUT,
                    null, null, null));
        }

        this.jedisPool = new ReplicatedJedisPool(masterPool, replicaPools);
    }

    public RedisClientFactory(String nodes, GenericObjectPoolConfig poolConfig) throws URISyntaxException {
        if(StringUtil.isEmpty(nodes)){
            logger.error("nodes is null!");
        }
        String[] args = nodes.split(",");
        if(args.length==0){
            logger.error("nodes is error!");
        }
        Set<HostAndPort> hostAndPorts=new HashSet<>();
        for(String arg:args) {
            String[] node = arg.split(":");
            if(node==null){
                logger.error("node is error!");
            }
            if(node.length!=2){
                logger.error("node is error!");
            }
            hostAndPorts.add(new HostAndPort(node[0],Integer.valueOf(node[1])));
        }
        this.jedisPool = new ReplicatedJedisPool(hostAndPorts, poolConfig);
    }

    public ReplicatedJedisPool getRedisClientPool() {
        return jedisPool;
    }

    @Override
    public PubSubConnection connect() {
        while (true) {
            try {
                if (jedisPool!=null&&jedisPool.getClusterNodes()!=null) {
//                    Lock locker = DistributedLock.getLocker(new String[]{DirectoryNotify.DIRECTORY_CHANGE_LOCK_KEY});
                    try{
//                        locker.lock();
                        Map<String,JedisPool> clusterNodes=jedisPool.getClusterNodes();
                        if(clusterNodes.size()==0){
                            logger.error("PubSubConnection connect error! clusterNodes.size is 0!");
                            Util.sleep(1000);
                            continue;
                        }
                        long min=0L;
                        List<String> keyList=new ArrayList(clusterNodes.keySet());
                        int randomIndex=new Random().nextInt(keyList.size());
                        String selectKey=keyList.get(randomIndex);
                        try {
                            for (String key : clusterNodes.keySet()) {
                                long minTemp = jedisPool.getJedisCluster().hincrBy("PubSubConnectionSelect", key, 0).longValue();
                                if (minTemp == 0) {
                                    selectKey = key;
                                    break;
                                } else if (min == 0 || minTemp < min) {
                                    min = minTemp;
                                    selectKey = key;
                                }
                            }
                            jedisPool.getJedisCluster().hincrBy("PubSubConnectionSelect", selectKey, 1);
                        }catch (Exception e){
                            logger.error("PubSubConnection connect selectKey error! msg:{}",e.getMessage());
                        }
                        logger.info("RedisClientFactory.connect selectKey:{}",selectKey);
                        try(Jedis jedis1=clusterNodes.get(selectKey).getResource()) {
                            String host =jedis1.getClient().getHost();
                            int port =jedis1.getClient().getPort();
                            Socket socket = new Socket();
                            socket.connect(new InetSocketAddress(host, port), 1000);
//                            socket.setSoTimeout(100);
                            return new PubSubConnection(socket,jedisPool,selectKey);
                        }
                    } catch (Exception e){
                        Util.sleep(1000);
                        logger.error("PubSubConnection connect error! msg:{}",e.getMessage());
                    }finally{
//                        locker.unlock();
                    }
                }else {
                    Socket socket = new Socket(host, port);
                    if (ssl) {
                        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                        socket = sslSocketFactory.createSocket(socket, host, port, true);
                    }
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
                }
            } catch (IOException e) {
                logger.warn("Error connecting", e);
                Util.sleep(5000);
            }
        }
    }
}
