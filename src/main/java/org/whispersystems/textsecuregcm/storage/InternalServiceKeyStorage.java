package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.difftim.security.signing.SignatureVerifier;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.util.RandomString;
import org.whispersystems.textsecuregcm.util.StringUtil;
import org.whispersystems.textsecuregcm.util.SystemMapper;

import java.util.Random;


public class InternalServiceKeyStorage implements SignatureVerifier.KeyStorage {
    Logger logger= LoggerFactory.getLogger(InternalServiceKeyStorage.class);
    InternalServiceKeysTable internalServiceKeysTable;
    private MemCache memCache;

    public InternalServiceKeyStorage(MemCache memCache,InternalServiceKeysTable internalServiceKeysTable){
        this.memCache=memCache;
        this.internalServiceKeysTable=internalServiceKeysTable;
//        List<String> ipList=new ArrayList<>();
//        ipList.add("18.167.26.97");
//        InternalServiceKey internalServiceKey=new InternalServiceKey("task-server","HmacSHA256",
//                "7cf20049c1e942a2a05c4191c4d7923378a59d2fdb1640f0bf758a015c7fd39f".getBytes(),10000,ipList);
//        internalServiceKeysTable.insert(internalServiceKey);
    }
    @Override
    public SignatureVerifier.Key getKey(String appid) {
        String cacheKey = String.join("_", this.getClass().getSimpleName(), "key", appid);

        String key=memCache.get(cacheKey);
        if(!StringUtil.isEmpty(key)){
            try {
                return  SystemMapper.getMapper().readValue(key, InternalServiceKey.class);
            } catch (JsonProcessingException e) {
                logger.error("getKey error! readKey error! msg:{}",e.getMessage());
            }
        }
        InternalServiceKey internalServiceKey=internalServiceKeysTable.get(appid);
        if(internalServiceKey!=null) {
            memCache.set(cacheKey,internalServiceKey);
        }
        return internalServiceKey;
    }

    @Override
    public void rememberNonce(String nonce, long millisecondsToExpire) {
        if (null != memCache) {
            String cacheKey = String.join("_", this.getClass().getSimpleName(), "nonce", nonce);
            memCache.setex(cacheKey,new Long(millisecondsToExpire/1000).intValue(),"");
         }
    }

    @Override
    public boolean nonceExists(String nonce) {
        if (null != memCache) {
            String cacheKey = String.join("_", this.getClass().getSimpleName(), "nonce", nonce);
            return null != memCache.get(cacheKey);
         }
        return false;
    }

    public static void main(String[] args) throws JsonProcessingException {
        String key="{\"algorithm\":\"HmacSHA256\",\"key\":\"N2NmMjAwNDljMWU5NDJhMmEwNWM0MTkxYzRkNzkyMzM3OGE1OWQyZmRiMTY0MGYwYmY3NThhMDE1YzdmZDM5Zg==\",\"signatureExpireTime\":10000,\"allowedIPList\":[\"18.167.26.97\",\"162.14.129.186\"],\"appid\":\"task-server\"}";
        InternalServiceKey internalServiceKey=SystemMapper.getMapper().readValue(key, InternalServiceKey.class);
        System.out.println(new Gson().toJson(internalServiceKey));
        System.out.println("7cf20049c1e942a2a05c4191c4d7923378a59d2fdb1640f0bf758a015c7fd39f".getBytes());
        System.out.println("7cf20049c1e942a2a05c4191c4d7923378a59d2fdb1640f0bf758a015c7fd39f".length());
        byte [] ss="7cf20049c1e942a2a05c4191c4d7923378a59d2fdb1640f0bf758a015c7fd39f".getBytes();
        for(int i=0;i<ss.length;i++){
//            System.out.println(ss[i]);
        }
        String appid="meeting-server";
        String key1= new RandomString(64).nextString();
        String ip="[\"172.31.29.20\"]";
        String sql="insert into internal_service_keys values ('"+appid+"','HmacSHA256','"+key1+"',60000,'"+ip+"')";
        System.out.println(sql);
//一个是openapi 一个是supportbot
        appid="openapi";
        key1= new RandomString(64).nextString();
        ip="[\"172.31.30.144\",\"172.31.29.20\"]";
        sql="insert into internal_service_keys values ('"+appid+"','HmacSHA256','"+key1+"',60000,'"+ip+"')";
        System.out.println(sql);

        appid="supportbot";
        key1= new RandomString(64).nextString();
        ip="[\"172.31.30.144\",\"172.31.29.20\"]";
        sql="insert into internal_service_keys values ('"+appid+"','HmacSHA256','"+key1+"',60000,'"+ip+"')";
        System.out.println(sql);


    }
}
