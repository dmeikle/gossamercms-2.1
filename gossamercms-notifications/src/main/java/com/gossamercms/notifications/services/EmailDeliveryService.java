package com.gossamercms.notifications.services;

public interface EmailDeliveryService {

    void send(
            String to,
            String subject,
            String textBody,
            String htmlBody
    );
}