package com.gossamercms.users.api.requests;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class UserContextRequest {
    private UUID userId;
    private UUID roleId;
    private String contextType;
    private Map<String, Object> metadata;
    private boolean isDefault;
}
