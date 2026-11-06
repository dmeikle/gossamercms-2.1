package com.gossamercms.notifications.dtos;

import com.gossamercms.notifications.models.MessageLog;
import com.gossamercms.mvc.data.DtoWithId;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageLogDto implements DtoWithId{
    private UUID id;
    private Instant createdAt;
    private UUID createdBy;
    private Instant updatedAt;
    private UUID updatedBy;
    private Instant deletedAt;
    private UUID deletedBy;
    private UUID messagesId;
    private String result;
    private String message;
    private String notificationTarget;

    public MessageLog toEntity() {
        return MessageLog.builder()
            .id(id)
            .createdAt(createdAt)
            .createdBy(createdBy)
            .updatedAt(updatedAt)
            .updatedBy(updatedBy)
            .deletedAt(deletedAt)
            .deletedBy(deletedBy)
            .messagesId(messagesId)
            .result(result)
            .message(message)
            .notificationTarget(notificationTarget)
            .build();
    }
}
