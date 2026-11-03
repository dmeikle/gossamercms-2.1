package com.gossamercms.rbac.dtos.requests;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class RolePermissionRequest {
    private UUID roleId;
    private UUID permissionId;
    private Instant createdAt;
}
