package com.turo.pushy.apns;

import com.turo.pushy.apns.util.SimpleApnsPushNotification;

import java.util.Date;

public class ApnsPushNotificationCanBackground extends SimpleApnsPushNotification {
    public boolean isBackground() {
        return background;
    }

    public void setBackground(boolean background) {
        this.background = background;
    }

    boolean background = false;
    public ApnsPushNotificationCanBackground(String token, String topic, String payload) {
        super(token, topic, payload);
    }

    public ApnsPushNotificationCanBackground(String token, String topic, String payload, Date invalidationTime) {
        super(token, topic, payload, invalidationTime);
    }

    public ApnsPushNotificationCanBackground(String token, String topic, String payload, Date invalidationTime, DeliveryPriority priority) {
        super(token, topic, payload, invalidationTime, priority);
    }

    public ApnsPushNotificationCanBackground(String token, String topic, String payload, Date invalidationTime, DeliveryPriority priority, String collapseId) {
        super(token, topic, payload, invalidationTime, priority, collapseId);
    }
}
