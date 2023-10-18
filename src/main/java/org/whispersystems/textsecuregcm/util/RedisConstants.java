package org.whispersystems.textsecuregcm.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import java.util.List;

public class RedisConstants {

    /**
     * Redis keys
     */
    private static final String CHAT_KEY = "chat";
    private static final String CHAT_SEQ_NO_KEY = "seq";
    private static final String CHAT_SEQ_NO_CACHE_KEY = "seqCache";
    private static final String INDIVIDUAL = "i";
    private static final String GROUP = "g";

    /**
     * 单聊并发锁key
     * @return
     */
    public static String buildIndividualChatKey(String source, String destination){
        return String.format("%s:%s:%s", CHAT_KEY, INDIVIDUAL, buildSortedIndividualStr(source, destination));
    }

    /**
     * 群聊并发锁key
     * @return
     */
    public static String buildGroupChatKey(String groupId){
        return String.format("%s:%s:%s", CHAT_KEY, GROUP, groupId);
    }

    /**
     * 单聊SeqNo key
     * @return
     */
    public static String buildIndividualChatSeqNoKey(String source, String destination,boolean notify){
        if(notify) {
            return String.format("%s:%s:%s:%s:%s", CHAT_KEY, INDIVIDUAL, CHAT_SEQ_NO_KEY, buildSortedIndividualStr(source, destination), "notify");
        }else {
            return String.format("%s:%s:%s:%s", CHAT_KEY, INDIVIDUAL, CHAT_SEQ_NO_KEY, buildSortedIndividualStr(source, destination));
        }
    }

    /**
     * 群聊SeqNo key
     * @return
     */
    public static String buildGroupChatSeqNoKey(String groupId,boolean notify){
        if(notify) {
            return String.format("%s:%s:%s:%s:%s", CHAT_KEY, GROUP, CHAT_SEQ_NO_KEY, groupId,"notify");
        }else{
            return String.format("%s:%s:%s:%s", CHAT_KEY, GROUP, CHAT_SEQ_NO_KEY, groupId);
        }
    }

    /**
     * 单聊同一消息缓存key
     * @return
     */
    public static String buildIndividualChatCacheKey(String source, String destination, String deviceId, long timestamp,boolean notify){
        if(notify) {
            return String.format("%s:%s:%s:%s:%s:%s:%s:%s", CHAT_KEY, INDIVIDUAL, CHAT_SEQ_NO_CACHE_KEY, source, destination, deviceId, timestamp,"notify");
        }else{
            return String.format("%s:%s:%s:%s:%s:%s:%s", CHAT_KEY, INDIVIDUAL, CHAT_SEQ_NO_CACHE_KEY, source, destination, deviceId, timestamp);
        }
    }

    /**
     * 群聊同一消息缓存key
     * @return
     */
    public static String buildGroupChatCacheKey(String groupId, String source, String deviceId, long timestamp,boolean notify) {
        if (notify) {
            return String.format("%s:%s:%s:%s:%s:%s:%s:%s", CHAT_KEY, GROUP, CHAT_SEQ_NO_CACHE_KEY, groupId, source, deviceId, timestamp,"notify");
        }else{
            return String.format("%s:%s:%s:%s:%s:%s:%s", CHAT_KEY, GROUP, CHAT_SEQ_NO_CACHE_KEY, groupId, source, deviceId, timestamp);
        }
    }

    private static String buildSortedIndividualStr(String source, String destination) {
        List<String> sortedList = Ordering.natural().sortedCopy(Lists.newArrayList(source, destination));
        return Joiner.on(":").join(sortedList);
    }

}
