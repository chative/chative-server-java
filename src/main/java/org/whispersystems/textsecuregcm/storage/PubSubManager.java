package org.whispersystems.textsecuregcm.storage;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.dispatch.DispatchChannel;
import org.whispersystems.dispatch.DispatchManager;
import org.whispersystems.dispatch.redis.PubSubConnection;
import org.whispersystems.textsecuregcm.redis.ReplicatedJedisPool;

import io.dropwizard.lifecycle.Managed;

import static com.codahale.metrics.MetricRegistry.name;
import static org.whispersystems.textsecuregcm.storage.PubSubProtos.PubSubMessage;

import org.whispersystems.textsecuregcm.util.Constants;
import org.whispersystems.textsecuregcm.util.StringUtil;
import redis.clients.jedis.Jedis;

public class PubSubManager implements Managed {

  private static final String KEEPALIVE_CHANNEL = "KEEPALIVE";

  private final Logger logger = LoggerFactory.getLogger(PubSubManager.class);

  private final DispatchManager     dispatchManager;
  private final ReplicatedJedisPool jedisPool;
  private final MemCache memCache;

  private boolean subscribed = false;
  private static final MetricRegistry metricRegistry    = SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME);

  private static final Timer publishMeter    = metricRegistry.timer(name(MessagesManager.class, "publish"   ));

  public PubSubManager(ReplicatedJedisPool jedisPool, DispatchManager dispatchManager,MemCache memCache) {
    this.dispatchManager = dispatchManager;
    this.jedisPool       = jedisPool;
    this.memCache=memCache;
  }

  @Override
  public void start() throws Exception {
    this.dispatchManager.start();

    KeepaliveDispatchChannel keepaliveDispatchChannel = new KeepaliveDispatchChannel();
    this.dispatchManager.subscribe(KEEPALIVE_CHANNEL, keepaliveDispatchChannel);

    synchronized (this) {
      while (!subscribed) wait(0);
    }

    new KeepaliveSender().start();
  }

  @Override
  public void stop() throws Exception {
    dispatchManager.shutdown();
  }

  public void subscribe(PubSubAddress address, DispatchChannel channel) {
    dispatchManager.subscribe(address.serialize(), channel);
  }

  public void unsubscribe(PubSubAddress address, DispatchChannel dispatchChannel) {
    dispatchManager.unsubscribe(address.serialize(), dispatchChannel);
  }

  public void unsubscribe(PubSubAddress address, DispatchChannel dispatchChannel,int statusCode) {
    dispatchManager.unsubscribe(address.serialize(), dispatchChannel,statusCode);
  }
  public boolean hasLocalSubscription(PubSubAddress address) {
    return dispatchManager.hasSubscription(address.serialize());
  }

  public boolean publish(PubSubAddress address, PubSubMessage message) {
    logger.info("PubSubManager.publish begin to {} , message type:{} ", address.serialize(), message.getType());
    Timer.Context context=publishMeter.time();
    try {
      final boolean over = publish(address.serialize().getBytes(), message);
      logger.info("PubSubManager.publish over:{}, to {} , message type:{} ", over, address.serialize(), message.getType());
      return over;
    }catch (Exception e){
      logger.error("PubSubManager.publish error! to {}  msg:{}", address.serialize(),e.getMessage());
      return false;
    } finally {
      context.stop();
    }
  }

  private boolean publish(byte[] channel, PubSubMessage message) {
    if(jedisPool.isCluster()) {
      String subKey = PubSubConnection.getSubscribeKey(new String((channel)));
      String nodeKey = memCache.get(subKey);
      if (!StringUtil.isEmpty(nodeKey) && jedisPool.getClusterNodes(nodeKey) != null) {
        try (Jedis jedisForPub = jedisPool.getClusterNodes(nodeKey).getResource()) {
          long result = jedisForPub.publish(channel, message.toByteArray());
          if (result < 0) {
            logger.warn("**** Jedis publish result < 0,redisHost:{}", jedisForPub.getClient().getHost());
          }
          return result > 0;
        }
      } else {
        if(!StringUtil.isEmpty(nodeKey)) {
          logger.error("publish error! channel:{},nodeKey:{},jedisPool is null!", subKey, nodeKey);
        }
      }
      return false;
    }else{
      try (Jedis jedis = jedisPool.getWriteResource()) {
        long result = jedis.publish(channel, message.toByteArray());
        if (result < 0) {
          logger.warn("**** Jedis publish result < 0");
        }
        return result > 0;
      }
    }
  }

  private class KeepaliveDispatchChannel implements DispatchChannel {

    @Override
    public void onDispatchMessage(String channel, byte[] message) {
      // Good
    }

    @Override
    public void onDispatchSubscribed(String channel) {
      if (KEEPALIVE_CHANNEL.equals(channel)) {
        synchronized (PubSubManager.this) {
          subscribed = true;
          PubSubManager.this.notifyAll();
        }
      }
    }

    @Override
    public void onDispatchUnsubscribed(String channel) {
      logger.warn("***** KEEPALIVE CHANNEL UNSUBSCRIBED *****");
    }
  }

  private class KeepaliveSender extends Thread {
    @Override
    public void run() {
      while (true) {
        try {
          Thread.sleep(20000);
          publish(KEEPALIVE_CHANNEL.getBytes(), PubSubMessage.newBuilder()
                                                             .setType(PubSubMessage.Type.KEEPALIVE)
                                                             .build());
        } catch (Throwable e) {
          logger.warn("***** KEEPALIVE EXCEPTION ******", e);
        }
      }
    }
  }
}
