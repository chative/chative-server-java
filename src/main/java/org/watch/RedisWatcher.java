package org.watch;

import com.google.gson.Gson;
import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.Watcher;
import org.casbin.jcasbin.persist.WatcherEx;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.function.Consumer;

public class RedisWatcher implements WatcherEx {
    private final JedisPool pubJedisPool;
    private final JedisPool subJedisPool;
    private Consumer<String> consumer;
    private final String localId;
    private final String redisChannelName;
    private SubThread subThread;

    public RedisWatcher(String redisIp, int redisPort, String redisChannelName, int timeout, String password, int database, boolean ssl) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestOnReturn(true);
        jedisPoolConfig.setMaxIdle(50);
        jedisPoolConfig.setMinIdle(10);
        jedisPoolConfig.setMaxTotal(100);
        this.pubJedisPool = new JedisPool(jedisPoolConfig, redisIp, redisPort, timeout, password, database, ssl);
        this.subJedisPool = new JedisPool(jedisPoolConfig, redisIp, redisPort, timeout, password, database, ssl);
        this.localId = UUID.randomUUID().toString();
        this.redisChannelName = redisChannelName;
        startSub(false, this.localId);
    }

    public RedisWatcher(JedisPoolConfig jedisPoolConfig, String redisIp, int redisPort, String redisChannelName, int timeout, String password, int database, boolean ssl){
        this.pubJedisPool = new JedisPool(jedisPoolConfig, redisIp, redisPort, timeout, password, database, ssl);
        this.subJedisPool = new JedisPool(jedisPoolConfig, redisIp, redisPort, timeout, password, database, ssl);
        this.localId = UUID.randomUUID().toString();
        this.redisChannelName = redisChannelName;
        startSub(false, this.localId);
    }

    @Override
    public void setUpdateCallback(Consumer<String> consumer) {
        this.consumer=consumer;
        subThread.setUpdateCallback(consumer);
    }


    @Override
    public void setUpdateCallback(Runnable runnable) {
        subThread.setUpdateCallback(runnable);
    }

    @Override
    public void update() {
        try (Jedis jedis = pubJedisPool.getResource()) {
            jedis.publish(redisChannelName, "Casbin policy has a new version from redis watcher: "+localId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSub(Boolean ignoreSelf,String localid){
        subThread = new SubThread(subJedisPool,redisChannelName,consumer,ignoreSelf,localid);
        subThread.start();
    }

    // 增量添加
    @Override
    public void updateForAddPolicy(String s, String... strings) {
        try (Jedis jedis = pubJedisPool.getResource()) {
            Map<String, String> content = new HashMap<>();
            content.put("type", "add");
            content.put("ptype", s);
            for(int i=0; i<strings.length; i++){
                content.put("v"+i, strings[i]);
            }
            Gson gson = new Gson();
            jedis.publish(redisChannelName, gson.toJson(content));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 增量删除
    @Override
    public void updateForRemovePolicy(String s, String... strings) {
        try (Jedis jedis = pubJedisPool.getResource()) {
            Map<String, String> content = new HashMap<>();
            content.put("type", "remove");
            content.put("ptype", s);
            for(int i=0; i<strings.length; i++){
                content.put("v"+i, strings[i]);
            }
            Gson gson = new Gson();
            jedis.publish(redisChannelName, gson.toJson(content));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void updateForRemoveFilteredPolicy(int i, String... strings) {

    }

    @Override
    public void updateForSavePolicy(Model model) {

    }

    public void updateForUpdatePolicy(List<String> var1, List<String> var2) {
        try (Jedis jedis = pubJedisPool.getResource()) {
            Map<String, Object> content = new HashMap<>();
            content.put("type", "update");
            content.put("ptype", "ptype");
            content.put("oldrule", var1);
            content.put("newrule", var2);
            Gson gson = new Gson();
            jedis.publish(redisChannelName, gson.toJson(content));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
