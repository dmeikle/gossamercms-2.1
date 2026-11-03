package com.gossamercms.notifications.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private UUID id;
    private UUID recipientId;
    private String recipientType;
    private UUID recipientContactId;
    private String notificationType;
    private String notificationChannel;
    private String title;
    private String body;
    private String priority;
    private String metadata;
    private Instant scheduledAt;
    private String status;
    private Integer attemptCount;
    private Instant nextAttemptAt;
    private Instant claimedAt;
    private Instant sentAt;
    private String lastError;
    private Instant createdAt;
    private Instant updatedAt;
}
