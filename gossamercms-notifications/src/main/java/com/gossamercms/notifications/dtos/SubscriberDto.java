package com.gossamercms.notifications.dtos;

import com.gossamercms.notifications.models.Subscriber;
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
public class SubscriberDto implements DtoWithId{
    private UUID id;
    private Instant createdAt;
    private UUID createdBy;
    private Instant updatedAt;
    private UUID updatedBy;
    private Instant deletedAt;
    private UUID deletedBy;
    private UUID accountMappingsId;
    private UUID subscriptionKeysId;

    public Subscriber toEntity() {
        return Subscriber.builder()
            .id(id)
            .createdAt(createdAt)
            .createdBy(createdBy)
            .updatedAt(updatedAt)
            .updatedBy(updatedBy)
            .deletedAt(deletedAt)
            .deletedBy(deletedBy)
            .accountMappingsId(accountMappingsId)
            .subscriptionKeysId(subscriptionKeysId)
            .build();
    }
}
