package org.whispersystems.textsecuregcm.distributedlock;

import org.redisson.Redisson;
import org.redisson.RedissonMultiLock;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.redisson.config.ReplicatedServersConfig;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.configuration.DistributedLockConfig;
import org.whispersystems.textsecuregcm.configuration.RedisAddressConf;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class DistributedLock {
    static private final Logger logger = LoggerFactory.getLogger(DistributedLock.class);

    @SuppressWarnings("unchecked")
    static public <T> T proxyObj(T obj) {
        return (T) new ProxyFactory(obj).getProxyInstance();
    }

    // 根据锁名获取一个锁
    static public Lock getLocker(String[] names) {
        return getLocker(names, leaseTime, TimeUnit.SECONDS);
    }


    // 根据锁名获取一个锁
    static public Lock getLocker(String[] names, long time, TimeUnit unit) {
        RLock locker;
        int index = 0;
        if (names.length > 1) { // 判断是否需要联级锁
            RLock[] locks = new RLock[names.length];
            for (String name : names) {
                locks[index++] = client.getFairLock(name);
            }
            locker = new RedissonMultiLock(locks);
        } else {
            locker = client.getFairLock(names[0]); // 取第一个锁名
        }

        return new Lock() {
            @Override
            public void lock() {
                // 重写 lock 方法
                logger.info("being lock names: {}, time:{},TimeUnit:{} ", names, time, unit);
                locker.lock(time, unit);
                logger.info("have gotten lock names: {}, time:{},TimeUnit:{} ", names, time, unit);
            }

            @Override
            public void lockInterruptibly() throws InterruptedException {
                locker.lockInterruptibly();
            }

            @Override
            public boolean tryLock() {
                logger.info("being tryLock names: {},no wait,time:{},TimeUnit:{}  ", (Object) names, time, unit);
                boolean isLocked = false;
                try {
                    isLocked = locker.tryLock(-1,time,unit);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("tryLock return:{}, name: {},no wait, time:{},TimeUnit:{}  ", isLocked, names, time, unit);
                return isLocked;
            }

            @Override
            public boolean tryLock(long waittime,  TimeUnit unit) throws InterruptedException {
                logger.info("being tryLock names: {}, waittime{},time:{},TimeUnit:{} ", names,waittime, time, unit);
                boolean isLocked = locker.tryLock(waittime,time, unit);
                logger.info("tryLock return:{},lockNames: {}, waittime{},time:{},TimeUnit:{} ", isLocked, names, waittime,time, unit);
                return isLocked;
            }

            @Override
            public void unlock() {
                try {
                    locker.unlock();// 有超时自动是否释放的情况；超时后释放会抛出异常
                } catch (Exception e) {
                    logger.warn("unlock names:{}, Exception:{},", names, e);
                }
                logger.info("unlock names: {} ", (Object) names);
            }

            @Override
            public Condition newCondition() {
                return locker.newCondition();
            }
        };
    }


    static public void init(DistributedLockConfig distributedLockConfig) {
        Config config = new Config();
        List<String> replicaUrls = distributedLockConfig.getReplicaUrls();

        final RedisAddressConf redisCfg = new RedisAddressConf(distributedLockConfig.getUrl());
        String password = redisCfg.getPassword();
        if (password == null || password.equals("")) password = null;

        if (replicaUrls == null || replicaUrls.isEmpty()) { // 没有 replications
            SingleServerConfig sc = config.useSingleServer().setTcpNoDelay(true).
                    setAddress(redisCfg.getAddress()).setDatabase(redisCfg.getDatabase());
            sc.setPassword(password);
        } else {
            replicaUrls.add(0, distributedLockConfig.getUrl());
            ReplicatedServersConfig replicasCfg = config.useReplicatedServers().setTcpNoDelay(true).
                    setPassword(password).setDatabase(redisCfg.getDatabase());
            replicasCfg.setNodeAddresses(replicaUrls);
        }

        client = Redisson.create(config);
        DistributedLock.leaseTime = distributedLockConfig.getLeaseTime();
    }


    /**
     * Returns unbounded blocking deque instance by name.
     *
     * @param <V> type of value
     * @param name - name of object
     * @return BlockingDeque object
     */
    static public <V> RBlockingDeque<V> getBlockingDeque(String name){
        return client.getBlockingDeque(name);
    }

    /**
     * Returns unbounded delayed queue instance by name.
     * <p>
     * Could be attached to destination queue only.
     * All elements are inserted with transfer delay to destination queue.
     *
     * @param <V> type of value
     * @param destinationQueue - destination queue
     * @return Delayed queue object
     */
    static public <V> RDelayedQueue<V> getDelayedQueue(RQueue<V> destinationQueue){
        return client.getDelayedQueue(destinationQueue);
    }


    // 初始化client
    static public void init(String address, String password, int database, long leaseTime) {
        Config config = new Config();
// Tcp No Delay
        SingleServerConfig sc = config.useSingleServer().setTcpNoDelay(true).setAddress(address).setDatabase(database);
        if (password == null || password.equals("")) {
            sc.setPassword(null);
        } else {
            sc.setPassword(password);
        }
        client = Redisson.create(config);
        DistributedLock.leaseTime = leaseTime;
    }

    // 关闭
    static public void shutdown() {
        if (client != null) {
            client.shutdown();
            client = null;
        }
    }

    /**
     * 获取自增ID
     * @param atomicName
     * @return
     */
    public static RAtomicLong getRedissonAtomicLong(String atomicName){
        RAtomicLong atomicLong = client.getAtomicLong(atomicName);
        return atomicLong;
    }

    // 基于redis实现分布式锁
    static private RedissonClient client;

    static private long leaseTime;
}

