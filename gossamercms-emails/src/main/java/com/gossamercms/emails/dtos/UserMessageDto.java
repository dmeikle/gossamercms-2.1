package com.gossamercms.emails.dtos;

import com.gossamercms.emails.models.UserMessage;
import com.gossamercms.mvc.data.DtoWithId;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMessageDto implements DtoWithId{
    private UUID id;
    private Instant createdAt;
    private UUID createdBy;
    private Instant updatedAt;
    private UUID updatedBy;
    private Instant deletedAt;
    private UUID deletedBy;
    private UUID accountMappingsId;
    private String comments;
    private String status;
    private Instant answeredAt;
    private UUID answeredBy;
    private String topic;
    private String username;

    public UserMessage toEntity() {
        return UserMessage.builder()
            .id(id)
            .createdAt(createdAt)
            .createdBy(createdBy)
            .updatedAt(updatedAt)
            .updatedBy(updatedBy)
            .deletedAt(deletedAt)
            .deletedBy(deletedBy)
            .accountMappingsId(accountMappingsId)
            .comments(comments)
            .status(status)
            .answeredAt(answeredAt)
            .answeredBy(answeredBy)
            .topic(topic)
            .username(username)
            .build();
    }
}
