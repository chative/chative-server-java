package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.redis.ReplicatedJedisPool;
import org.whispersystems.textsecuregcm.util.StringUtil;
import org.whispersystems.textsecuregcm.util.SystemMapper;
import redis.clients.jedis.JedisCluster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MemCache {
  private final Logger logger = LoggerFactory.getLogger(PlatformManager.class);

  private final ReplicatedJedisPool cacheClient;
  private final ObjectMapper mapper;

  public JedisCluster getJedisCluster() {
    return cacheClient.getJedisCluster();
  }

  public MemCache(ReplicatedJedisPool cacheClient) {
    this.cacheClient = cacheClient;
    this.mapper = SystemMapper.getMapper();
  }

  public void set(String key, Object obj) {
    try{
      cacheClient.getJedisCluster().set(key, mapper.writeValueAsString(obj));
    } catch (Exception e) {
      logger.error("memcache setObject", "Exception", e);
    }
  }

  public void set(String key, String str) {
    try{
      cacheClient.getJedisCluster().set(key, str);
    } catch (Exception e) {
      logger.error("memcache setString", "Exception", e);
    }
  }

  public String set(String key, String value, String nxxx, String expx, int time) {
    try{
      return cacheClient.getJedisCluster().set(key,value,nxxx,expx,time);
    } catch (Exception e) {
      logger.error("memcache set for nxx expx", "Exception", e);
    }
    return null;
  }

  public Long expire(String key, final int seconds){
    try{
      return cacheClient.getJedisCluster().expire(key, seconds);
    } catch (Exception e) {
      logger.error("memcache expire", "Exception", e);
    }
    return null;
  }

  public Object get(String key, Class clazz) {
    try {
      String json = cacheClient.getJedisCluster().get(key);
      if (json != null) {
        return mapper.readValue(json, clazz);
      }
    } catch (Exception e) {
      logger.error("memcache getByJavaClass", "Exception", e);
    }
    return null;
  }

  public Object convert(Object fromObject, Class toType) {
    return mapper.convertValue(fromObject, toType);
  }

  public Long remove(String key) {
    try {
      return cacheClient.getJedisCluster().del(key);
    } catch (Exception e) {
      logger.error("memcache remove", "Exception", e);
    }
    return null;
  }

  public boolean exists(String key) {
    try {
      return cacheClient.getJedisCluster().exists(key);
    } catch (Exception e) {
      logger.error("memcache remove", "Exception", e);
    }
    return false;
  }

  public String get(String key) {
    try {
      return cacheClient.getJedisCluster().get(key);
    }catch (Exception e){
      logger.error("Memcache get error!",e);
      return null;
    }
  }

  public String setex(String key, int seconds, String value) {
    try {
      return cacheClient.getJedisCluster().setex(key, seconds, value);
    }catch (Exception e){
      logger.error("Memcache setex error!",e);
      return null;
    }
  }

  public Object get(String key, JavaType javaType) {
    try{
      String json = cacheClient.getJedisCluster().get(key);
      if (json != null) {
        return mapper.readValue(json, javaType);
      }
    } catch (Exception e) {
      logger.error("memcache getByJavaType", "Exception", e);
    }
    return null;
  }

  public Long hdel(String key,String... fields) {
    try {
      return cacheClient.getJedisCluster().hdel(key, fields);
    }catch (Exception e) {
      logger.error("memcache hdel", "Exception", e);
    }
    return null;
  }

  public Long hincrBy(String key, String field, long value) {
    try {
      return cacheClient.getJedisCluster().hincrBy(key, field, value);
    }catch (Exception e) {
      logger.error("memcache hincrBy", "Exception", e);
    }
    return -1L;
  }

  public Long incrBy(String key, long value) {
    try {
      return cacheClient.getJedisCluster().incrBy(key, value);
    } catch (Exception e) {
      logger.error("memcache incrBy", "Exception", e);
    }
    return -1L;
  }

  public String spop(String key) {
    try {
      return cacheClient.getJedisCluster().spop(key);
    } catch (Exception e) {
      logger.error("memcache spop", "Exception", e);
    }
    return null;
  }
  public Set<String> spop(String key, long count) {
    try {
      return cacheClient.getJedisCluster().spop(key, count);
    } catch (Exception e) {
      logger.error("memcache spop by count", "Exception", e);
    }
    return null;
  }
  public Long scard(String key) {
    try {
      return cacheClient.getJedisCluster().scard(key);
    }catch (Exception e) {
      logger.error("memcache scard", "Exception", e);
    }
    return null;
  }

  public Map<String, String> hgetAll(String key) {
    try {
      return cacheClient.getJedisCluster().hgetAll(key);
    }catch (Exception e) {
      logger.error("memcache hgetAll", "Exception", e);
    }
    return null;
  }

  public String hget(String key, String field) {
    try {
      return cacheClient.getJedisCluster().hget(key,field);
    }catch (Exception e) {
      logger.error("memcache hget", "Exception", e);
    }
    return null;
  }

  public Long hset(String key, String field, String value) {
    try {
      return cacheClient.getJedisCluster().hset(key,field,value);
    }catch (Exception e) {
      logger.error("memcache hset", "Exception", e);
    }
    return null;
  }

  public void hmset(String key, Map<String,Object> objectMap) {
    try {
      if (objectMap != null) {
        Map<String, String> mapString = new HashMap<String, String>();
        for (String hashKey : objectMap.keySet()) {
          String json = mapper.writeValueAsString(objectMap.get(hashKey));
          mapString.put(hashKey, json);
        }
        cacheClient.getJedisCluster().hmset(key,mapString);
      }
    }catch (Exception e) {
      logger.error("memcache hset", "Exception", e);
    }
  }

  public Map<String, Object> hgetAll(String key,Class clazz) {
    try{
      Map<String, String> map = cacheClient.getJedisCluster().hgetAll(key);
      if (map != null&&map.size()>0) {
        Map<String,Object> mapObj=new HashMap<String,Object>();
        for(String hashKey:map.keySet()){
          String json=map.get(hashKey);
          mapObj.put(hashKey,mapper.readValue(json, clazz));
        }
        return mapObj;
      }
    } catch (Exception e) {
      logger.error("memcache hgetAll", "Exception", e);
    }
    return null;
  }

  public Object hget(String key, String field,Class clazz) {
    try {
      String json=cacheClient.getJedisCluster().hget(key,field);
      if(!StringUtil.isEmpty(json)) {
        return mapper.readValue(json, clazz);
      }
    } catch (Exception e) {
      logger.error("memcache hget by clazz", "Exception", e);
    }
    return null;
  }

  public Long hset(String key, String field, Object object) {
    try {
      return cacheClient.getJedisCluster().hset(key,field,mapper.writeValueAsString(object));
    } catch (Exception e) {
      logger.error("memcache set by object", "Exception", e);
    }
    return null;
  }

  public Long srem(String key, String... members) {
    try {
      return cacheClient.getJedisCluster().srem(key,members);
    } catch (Exception e) {
      logger.error("memcache srem", "Exception", e);
    }
    return null;
  }
  public Long sadd(String key, String... members) {
    try {
    return cacheClient.getJedisCluster().sadd(key,members);
    } catch (Exception e) {
      logger.error("memcache sadd", "Exception", e);
    }
    return null;
  }

  public Set<String> smembers(String key, String... members) {
    try {
      return cacheClient.getJedisCluster().smembers(key);
    } catch (Exception e) {
      logger.error("memcache smembers", "Exception", e);
    }
    return null;
  }

  public void lPush(String key,int maxlength,String... strings) {
    try {
      cacheClient.getJedisCluster().lpush(key,strings);
      cacheClient.getJedisCluster().ltrim(key,0,maxlength-1);
    } catch (Exception e) {
      logger.error("memcache lPush", "Exception", e);
    }
  }

  public List<String> lRange(String key) {
    try {
      return cacheClient.getJedisCluster().lrange(key,0,-1);
    } catch (Exception e) {
      logger.error("memcache lRange", "Exception", e);
    }
    return null;
  }

  public Long lRem(String key,String value) {
    try {
      return cacheClient.getJedisCluster().lrem(key,0,value);
    } catch (Exception e) {
      logger.error("memcache lRem", "Exception", e);
    }
    return null;
  }

  public void zAdd(String key,int maxSize,double score, String value) {
    try {
      cacheClient.getJedisCluster().zadd(key,score,value);
      long size=cacheClient.getJedisCluster().zcard(key);
      if(size>maxSize){
        cacheClient.getJedisCluster().zremrangeByRank(key,0,size-maxSize);
      }
    } catch (Exception e) {
      logger.error("memcache zAddForMaxSize", "Exception", e);
    }
  }
  public void zAdd(String key,double score, String value) {
    try {
      cacheClient.getJedisCluster().zadd(key,score,value);
    } catch (Exception e) {
      logger.error("memcache zAdd", "Exception", e);
    }
  }

  public void zAdd(String key,Map<String,Double> scoreMembers,int maxSize) {
    try {
      cacheClient.getJedisCluster().zadd(key,scoreMembers);
      long size=cacheClient.getJedisCluster().zcard(key);
      if(size>maxSize){
        cacheClient.getJedisCluster().zremrangeByRank(key,0,size-maxSize);
      }
    } catch (Exception e) {
      logger.error("memcache zAddForMap", "Exception", e);
    }
  }

  public void zAdd(String key,Map<String,Double> scoreMembers) {
    try {
      cacheClient.getJedisCluster().zadd(key,scoreMembers);
    } catch (Exception e) {
      logger.error("memcache zAddForMap", "Exception", e);
    }
  }

  public Set<String> zRange(String key,int maxSize) {
    try {
      return cacheClient.getJedisCluster().zrange(key,0,maxSize-1);
    } catch (Exception e) {
      logger.error("memcache zRange", "Exception", e);
    }
    return null;
  }
  public Set<String> zRangeForLatest(String key,int latestSize) {
    try {
      Long count=cacheClient.getJedisCluster().zcard(key);
      Long start=count-latestSize>0?count-latestSize:0;
      return cacheClient.getJedisCluster().zrange(key,start,count-1);
    } catch (Exception e) {
      logger.error("memcache zRangeForLatest", "Exception", e);
    }
    return null;
  }

  public void zrem(String key,String value) {
    try {
      cacheClient.getJedisCluster().zrem(key,value);
    } catch (Exception e) {
      logger.error("memcache zrem", "Exception", e);
    }
  }
}
