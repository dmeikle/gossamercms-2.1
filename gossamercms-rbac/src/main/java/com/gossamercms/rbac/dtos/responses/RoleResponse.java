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
public class RoleResponse {
    private UUID id;
    private String name;
    private String description;
    private boolean isSystem;
    private Instant createdAt;
    private Instant updatedAt;
}
