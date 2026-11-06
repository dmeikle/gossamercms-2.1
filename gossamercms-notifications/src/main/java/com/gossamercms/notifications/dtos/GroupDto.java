package com.gossamercms.notifications.dtos;

import com.gossamercms.notifications.models.Group;
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
public class GroupDto implements DtoWithId{
    private UUID id;
    private Instant createdAt;
    private UUID createdBy;
    private Instant deletedAt;
    private String name;
    private Boolean isPrivate;

    public Group toEntity() {
        return Group.builder()
            .id(id)
            .createdAt(createdAt)
            .createdBy(createdBy)
            .deletedAt(deletedAt)
            .name(name)
            .isPrivate(isPrivate)
            .build();
    }
}
