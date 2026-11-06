package com.gossamercms.notifications.services;

import java.util.UUID;

public interface CeilingbarDeliveryService {

    void send(
            UUID userId,
            String title,
            String body
    );
}