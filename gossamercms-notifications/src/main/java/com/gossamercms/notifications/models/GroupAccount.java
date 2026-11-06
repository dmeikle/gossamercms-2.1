package com.gossamercms.notifications.models;

import com.gossamercms.notifications.dtos.GroupAccountDto;
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
public class GroupAccount implements BaseModel{
    private UUID id;
    private Instant createdAt;
    private Instant deletedAt;
    private UUID groupsId;
    private UUID accountsId;

    public GroupAccountDto toDto() {
        return GroupAccountDto.builder()
            .id(id)
            .createdAt(createdAt)
            .deletedAt(deletedAt)
            .groupsId(groupsId)
            .accountsId(accountsId)
            .build();
    }


public static final ModelMeta META = ModelMeta.builderWithId("groups_accounts")
    .column("id", UUID.class)
    .column("createdAt", Instant.class)
    .column("deletedAt", Instant.class)
    .column("groupsId", UUID.class)
    .column("accountsId", UUID.class)
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
