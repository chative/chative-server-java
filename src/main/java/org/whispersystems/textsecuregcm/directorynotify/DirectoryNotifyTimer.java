package org.whispersystems.textsecuregcm.directorynotify;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.configuration.DirectoryNotifyConfiguration;
import org.whispersystems.textsecuregcm.configuration.RedisAddressConf;
import org.whispersystems.textsecuregcm.distributedlock.DistributedLock;
import org.whispersystems.textsecuregcm.entities.DirectoryNotify;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class DirectoryNotifyTimer {
    final private Logger logger = LoggerFactory.getLogger(DirectoryNotifyTimer.class);

    private DirectoryNotifyManager directoryNotifyManager;

    private DirectoryNotifyConfiguration directoryNotifyConfiguration;
    public DirectoryNotifyTimer(DirectoryNotifyManager directoryNotifyManager,DirectoryNotifyConfiguration directoryNotifyConfiguration) {
       this.directoryNotifyManager=directoryNotifyManager;
       this.directoryNotifyConfiguration=directoryNotifyConfiguration;
    }

    public void init(){
        Timer accountBasicInfoChangeTimer = new Timer();
        accountBasicInfoChangeTimer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                Lock lock=DistributedLock.getLocker(new String[]{DirectoryNotify.DIRECTORY_NOTIFY_LOCK_KEY},directoryNotifyConfiguration.getAccountBasicInfoChangeTimerLockLeaseTime(), TimeUnit.MILLISECONDS);
                if(lock.tryLock()){
                    directoryNotifyManager.handleAccountBasicInfoChangeSend(null);
                }else{
                    logger.info("accountBasicInfoChangeTimer not get lock!");
                }
            }
        },directoryNotifyConfiguration.getAccountBasicInfoChangeTimerDelay(),directoryNotifyConfiguration.getAccountBasicInfoChangeTimerPeriod());
    }


    public static void main(String[] args) throws URISyntaxException {
        Config config = new Config();

        final RedisAddressConf redisCfg = new RedisAddressConf("redis://127.0.0.1:6379");
        String password = redisCfg.getPassword();
        if (password == null || password.equals("")) password = null;
        SingleServerConfig sc = config.useSingleServer().setTcpNoDelay(true).
                    setAddress(redisCfg.getAddress()).setDatabase(redisCfg.getDatabase());
        sc.setPassword(password);
        RedissonClient client = Redisson.create(config);
        RBucket<String> bucket =client.getBucket("aa");
        bucket.set("bbb");
        System.out.println(bucket.get());
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Lock lock=client.getFairLock("lock1");
                if(lock.tryLock()) {
                    System.out.println("aaaaa");
                    lock.unlock();
                }else {
                    System.out.println("aaaaa not get lock!");
                }
            }
        },1000,5000);

        Timer timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Lock lock=client.getFairLock("lock1");
                if(lock.tryLock()) {
                    System.out.println("bbbbb");
                    lock.unlock();
                }else {
                    System.out.println("bbbbb not get lock!");
                }
            }
        },1000,5000);
    }
}
