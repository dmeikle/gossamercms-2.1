package com.gossamercms.notifications.services;

import com.gossamercms.mvc.annotations.ModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Placeholder ceilingbar delivery: logs the notification instead of delivering it. Replace with a real implementation.
 */
@ModuleService
public class LoggingCeilingbarDeliveryService implements CeilingbarDeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingCeilingbarDeliveryService.class);

    @Override
    public void send(UUID userId, String title, String body) {
        logger.warn("Ceilingbar delivery not implemented; notification not sent. userId={}, title={}", userId, title);
    }
}
