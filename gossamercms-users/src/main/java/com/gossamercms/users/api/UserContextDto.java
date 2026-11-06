package com.gossamercms.users.api;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.gossamercms.mvc.data.DtoWithId;
import lombok.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserContextDto implements DtoWithId {

    private UUID id;
    private UUID userId;
    private UUID roleId;
    private String contextType;
    private Map<String, Object> metadata;
    private Instant createdAt;
    @JsonSetter(nulls = Nulls.SKIP)
    private boolean isDefault;
}