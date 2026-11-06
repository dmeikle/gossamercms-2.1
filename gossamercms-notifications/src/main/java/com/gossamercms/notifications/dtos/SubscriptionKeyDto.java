package com.gossamercms.notifications.dtos;

import com.gossamercms.notifications.models.SubscriptionKey;
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
public class SubscriptionKeyDto implements DtoWithId{
    private UUID id;
    private String subscriptionKey;
    private String name;
    private String description;
    private String section;
    private UUID createdBy;
    private Instant createdAt;
    private UUID updatedBy;
    private Instant updatedAt;
    private UUID deletedBy;
    private Instant deletedAt;

    public SubscriptionKey toEntity() {
        return SubscriptionKey.builder()
            .id(id)
            .subscriptionKey(subscriptionKey)
            .name(name)
            .description(description)
            .section(section)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .updatedBy(updatedBy)
            .updatedAt(updatedAt)
            .deletedBy(deletedBy)
            .deletedAt(deletedAt)
            .build();
    }
}
