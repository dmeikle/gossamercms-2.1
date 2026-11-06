package com.gossamercms.notifications.dtos;

import com.gossamercms.notifications.models.UserNotificationPreference;
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
public class UserNotificationPreferenceDto implements DtoWithId{
    private UUID id;
    private UUID userContextId;
    private String channel;
    private Boolean enabled;
    private UUID createdBy;
    private Instant createdAt;
    private UUID updatedBy;
    private Instant updatedAt;

    public UserNotificationPreference toEntity() {
        return UserNotificationPreference.builder()
            .id(id)
            .userContextId(userContextId)
            .channel(channel)
            .enabled(enabled)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .updatedBy(updatedBy)
            .updatedAt(updatedAt)
            .build();
    }
}
