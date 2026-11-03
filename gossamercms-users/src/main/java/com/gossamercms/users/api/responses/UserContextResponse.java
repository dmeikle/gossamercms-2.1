package com.gossamercms.users.api.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserContextResponse {
    private UUID id;
    private UUID userId;
    private UUID roleId;
    private String contextType;
    private Map<String, Object> metadata;
    private Instant createdAt;
    private boolean isDefault;
}
