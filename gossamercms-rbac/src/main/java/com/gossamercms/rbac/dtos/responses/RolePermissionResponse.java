package com.gossamercms.rbac.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionResponse {
    private UUID id;
    private UUID roleId;
    private UUID permissionId;
    private Instant createdAt;
}
