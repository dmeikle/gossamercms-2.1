package com.gossamercms.notifications.dtos;

import com.gossamercms.notifications.models.GroupAccount;
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
public class GroupAccountDto implements DtoWithId{
    private UUID id;
    private Instant createdAt;
    private Instant deletedAt;
    private UUID groupsId;
    private UUID accountsId;

    public GroupAccount toEntity() {
        return GroupAccount.builder()
            .id(id)
            .createdAt(createdAt)
            .deletedAt(deletedAt)
            .groupsId(groupsId)
            .accountsId(accountsId)
            .build();
    }
}
