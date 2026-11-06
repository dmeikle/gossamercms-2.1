package com.gossamercms.notifications.services;


public interface SmsDeliveryService {

    void send(String phoneNumber, String body);
}
