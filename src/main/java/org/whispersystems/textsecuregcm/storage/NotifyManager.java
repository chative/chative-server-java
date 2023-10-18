package org.whispersystems.textsecuregcm.storage;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.SharedMetricRegistries;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.Notification;
import org.whispersystems.textsecuregcm.entities.Notify;
import org.whispersystems.textsecuregcm.push.*;
import org.whispersystems.textsecuregcm.util.Constants;
import org.whispersystems.textsecuregcm.util.SystemMapper;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;

public class NotifyManager {
    private final Logger logger = LoggerFactory.getLogger(NotifyManager.class);
    private final WebsocketSender websocketSender;
    private final NotifySender websocketNotifySender;
    private final AccountsManager accountsManager;
    private final MessagesManager messagesManager;

    private final ObjectMapper mapper;
    ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 20, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new ThreadPoolExecutor.CallerRunsPolicy());

    public NotifyManager(WebsocketSender websocketSender, AccountsManager accountsManager, PushSender pushSender,KafkaSender kafkaSender,MessagesManager messagesManager) {
        this.accountsManager = accountsManager;
        this.websocketSender = websocketSender;
        this.messagesManager=messagesManager;
        this.websocketNotifySender = new NotifySender(websocketSender, pushSender,kafkaSender,accountsManager,messagesManager);
        this.mapper = SystemMapper.getMapper();
        SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME)
                .register(name(NotifyManager.class, "NotifyManager_executor_depth"),
                        (Gauge<Long>) ((ThreadPoolExecutor)executor)::getTaskCount);
    }

    public void sendNotify(Account account, Notify notify, Notification notification) {
        try {
            this.sendNotify(account,mapper.writeValueAsString(notify),notification);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void sendNotifySingle(Account account, Notify notify, Notification notification) {
        try {
            this.sendNotifySingle(account,mapper.writeValueAsString(notify),notification);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void sendNotifySingle(Account account, String content, Notification notification) {
        try {
            websocketNotifySender.send(account, content, notification);
            Thread.sleep(80);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendNotify(Account account, String content, Notification notification) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                websocketNotifySender.send(account, content, notification);
            }
        });
    }

    public void sendNotifyForGroup(Group group, String content, Notification notification) {
        websocketNotifySender.sendForGroup(group,content,notification);
    }

    public void sendNotifyForGroup(Group group, Notify notify, Notification notification) {
        try {
            websocketNotifySender.sendForGroup(group,mapper.writeValueAsString(notify),notification);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
