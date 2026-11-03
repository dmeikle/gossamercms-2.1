package com.gossamercms.notifications.adapters.inbound;


import com.gossamercms.notifications.dtos.NotificationJobDto;
import com.gossamercms.notifications.dtos.requests.NotificationRequest;
import com.gossamercms.notifications.dtos.responses.NotificationResponse;
import com.gossamercms.notifications.handlers.NotificationJobsHandler;
import com.gossamercms.notifications.models.NotificationJob;
import com.gossamercms.mvc.controllers.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/notification_jobs")
public class NotificationJobsController extends BaseController<NotificationJob, NotificationJobDto, NotificationRequest, NotificationResponse> {

    public NotificationJobsController(NotificationJobsHandler handler) {
        super(handler);
    }

    @Override
    protected NotificationJobDto toDto(NotificationRequest request) {
        return NotificationJobDto.builder()
                .recipientId(request.getRecipientId())
                .recipientType(request.getRecipientType())
                .recipientContactId(request.getRecipientContactId())
                .notificationType(request.getNotificationType())
                .notificationChannel(request.getNotificationChannel())
                .title(request.getTitle())
                .body(request.getBody())
                .priority(request.getPriority())
                .metadata(request.getMetadata())
                .build();
    }

    @Override
    protected NotificationResponse toResponse(NotificationJobDto dto) {
        return NotificationResponse.builder()
                .id(dto.getId())
                .recipientId(dto.getRecipientId())
                .recipientType(dto.getRecipientType())
                .recipientContactId(dto.getRecipientContactId())
                .notificationType(dto.getNotificationType())
                .notificationChannel(dto.getNotificationChannel())
                .title(dto.getTitle())
                .body(dto.getBody())
                .priority(dto.getPriority())
                .metadata(dto.getMetadata())
                .scheduledAt(dto.getScheduledAt())
                .status(dto.getStatus())
                .attemptCount(dto.getAttemptCount())
                .nextAttemptAt(dto.getNextAttemptAt())
                .claimedAt(dto.getClaimedAt())
                .sentAt(dto.getSentAt())
                .lastError(dto.getLastError())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}
