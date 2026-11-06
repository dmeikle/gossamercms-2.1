package com.gossamercms.notifications.models;

import com.gossamercms.notifications.dtos.SubscriptionKeyDto;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionKey implements BaseModel{
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

    public SubscriptionKeyDto toDto() {
        return SubscriptionKeyDto.builder()
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


public static final ModelMeta META = ModelMeta.builderWithId("SubscriptionKeys")
    .column("id", UUID.class)
    .column("subscriptionKey", String.class, 45)
    .column("name", String.class, 45)
    .column("description", String.class, 200)
    .column("section", String.class)
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
