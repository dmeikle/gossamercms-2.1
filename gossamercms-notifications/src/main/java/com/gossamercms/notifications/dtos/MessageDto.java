package com.gossamercms.notifications.dtos;

import com.gossamercms.notifications.models.Message;
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
public class MessageDto implements DtoWithId{
    private UUID id;
    private String subject;
    private String message;
    private UUID userContextId;
    private Instant viewedAt;
    private UUID templateId;
    private String messageType;
    private UUID requestLogsId;
    private Instant decayDate;

    private Instant createdAt;
    private UUID createdBy;
    private Instant updatedAt;
    private UUID updatedBy;

    public Message toEntity() {
        return Message.builder()
            .id(id)
            .createdAt(createdAt)
            .createdBy(createdBy)
            .updatedAt(updatedAt)
            .updatedBy(updatedBy)
            .subject(subject)
            .message(message)
            .userContextId(userContextId)
            .viewedAt(viewedAt)
            .templateId(templateId)
            .messageType(messageType)
            .requestLogsId(requestLogsId)
            .decayDate(decayDate)
            .build();
    }
}
