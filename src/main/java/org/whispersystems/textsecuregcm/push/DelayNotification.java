package org.whispersystems.textsecuregcm.push;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.protobuf.InvalidProtocolBufferException;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.distributedlock.DistributedLock;

public class DelayNotification {
    private static final Logger logger = LoggerFactory.getLogger(DelayNotification.class);
    private static RBlockingDeque<String> consumerQueueDesktopSent; // 检查是否发送到desktop
    private static RDelayedQueue<String> delayedQueueDesktopSent; // 延迟队列，producer;发送失败的话，就发notification

    private static RBlockingDeque<String> consumerQueueDesktopRead; // 检查desktop是否已读
    private static RDelayedQueue<String> delayedQueueDesktopRead; // 延迟队列，producer; 没有读对应的消息时，就发notification

    public static String checkSent = "checkSent";
    public static String checkRead = "checkRead";

    static DelayNotificationCallback callback;
    public static void init(DelayNotificationCallback callback) {
        consumerQueueDesktopSent = DistributedLock.getBlockingDeque("notification:check:desktop:sent");
        delayedQueueDesktopSent = DistributedLock.getDelayedQueue(consumerQueueDesktopSent);
        consumerQueueDesktopRead = DistributedLock.getBlockingDeque("notification:check:desktop:read");
        delayedQueueDesktopRead = DistributedLock.getDelayedQueue(consumerQueueDesktopRead);
        DelayNotification.callback = callback;
        new Thread(() -> {
            while (true) {
                try {
                    String obj = consumerQueueDesktopSent.take();
                    callback.notificationDelayCallback(checkSent, obj);
                } catch (InterruptedException e) {
                    logger.error("InterruptedException, delayedQueueDesktopSent take error", e);
                } catch (JsonProcessingException e) {
                    logger.error("JsonProcessingException,msg:{}, delayedQueueDesktopSent take error",
                            e.getMessage(), e);
                } catch (InvalidProtocolBufferException e) {
                    logger.error("InvalidProtocolBufferException,msg:{},  delayedQueueDesktopSent take error",
                            e.getMessage(), e);
                }
            }
        }).start();
        new Thread(() -> {
            while (true) {
                try {
                    String obj = consumerQueueDesktopRead.take();
                    callback.notificationDelayCallback(checkRead, obj);
                } catch (InterruptedException e) {
                    logger.error("InterruptedException, delayedQueueDesktopRead take error", e);
                } catch (JsonProcessingException e) {
                    logger.error("JsonProcessingException,msg:{}, delayedQueueDesktopRead take error",
                            e.getMessage(), e);
                } catch (InvalidProtocolBufferException e) {
                    logger.error("InvalidProtocolBufferException,msg:{},  delayedQueueDesktopRead take error",
                            e.getMessage(), e);
                }
            }
        }).start();
    }

    public static void addNotification(String type, String obj, long delay) {
        if (type.equals(checkSent)) {
            delayedQueueDesktopSent.offer(obj, delay, java.util.concurrent.TimeUnit.MILLISECONDS);
        } else if (type.equals(checkRead)) {
            delayedQueueDesktopRead.offer(obj, delay, java.util.concurrent.TimeUnit.MILLISECONDS);
        }
    }

    static public interface DelayNotificationCallback {
        public void notificationDelayCallback(String type, String obj) throws JsonProcessingException, InvalidProtocolBufferException;
    }
}


