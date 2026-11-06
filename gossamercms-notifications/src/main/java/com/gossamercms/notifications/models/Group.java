package com.gossamercms.notifications.models;

import com.gossamercms.notifications.dtos.GroupDto;
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
public class Group implements BaseModel{
    private UUID id;
    private Instant createdAt;
    private UUID createdBy;
    private Instant deletedAt;
    private String name;
    private Boolean isPrivate;

    public GroupDto toDto() {
        return GroupDto.builder()
            .id(id)
            .createdAt(createdAt)
            .createdBy(createdBy)
            .deletedAt(deletedAt)
            .name(name)
            .isPrivate(isPrivate)
            .build();
    }


public static final ModelMeta META = ModelMeta.builderWithId("groups")
    .column("id", UUID.class)
    .column("createdAt", Instant.class)
    .column("createdBy", UUID.class)
    .column("deletedAt", Instant.class)
    .column("name", String.class, 45)
    .column("isPrivate", Boolean.class)
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
