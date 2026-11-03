package com.gossamercms.rbac.dtos.requests;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class UserRoleRequest {
    private UUID userContextId;
    private UUID roleId;
    private UUID assignedBy;
    private Instant assignedAt;
    private Instant expiresAt;
}
