package com.gossamercms.firebase.services;


import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.gossamercms.mvc.annotations.ModuleService;

/**
 * this class was added later and may not be required.
 * The FirebasePushService is intended to push to a device.
 * this came up while investigating sms delivery.
 */
@ModuleService
public class FirebaseNotificationService {

    public String sendNotification(String targetToken, String title, String body) throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(targetToken) // The device's FCM registration token
                .setNotification(notification)
                .build();

        return FirebaseMessaging.getInstance().send(message);
    }
}