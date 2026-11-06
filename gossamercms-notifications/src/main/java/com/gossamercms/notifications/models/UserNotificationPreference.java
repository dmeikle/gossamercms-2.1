package com.gossamercms.notifications.models;

import com.gossamercms.notifications.dtos.UserNotificationPreferenceDto;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationPreference implements BaseModel{
    private UUID id;
    private UUID userContextId;
    private String channel;
    private Boolean enabled;
    private UUID createdBy;
    private Instant createdAt;
    private UUID updatedBy;
    private Instant updatedAt;

    public UserNotificationPreferenceDto toDto() {
        return UserNotificationPreferenceDto.builder()
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


public static final ModelMeta META = ModelMeta.builderWithId("user_notification_preferences")
    .column("id", UUID.class)
    .column("userContextId", UUID.class)
    .column("channel", String.class)
    .column("enabled", Boolean.class)
    .column("createdBy", UUID.class)
    .column("createdAt", Instant.class)
    .column("updatedBy", UUID.class)
    .column("updatedAt", Instant.class)
    .column("deletedBy", UUID.class)
    .column("deletedAt", Instant.class)
    .defaultSort("id desc")
    .build();



    @Override
    public ModelMeta meta() {
        return META;
    }

    @Override
    public ModelMeta metaOf() {
        return BaseModel.super.metaOf();
    }

}
