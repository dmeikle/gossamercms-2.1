package com.gossamercms.rbac.dtos.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleRequest {
    private String name;
    private String description;
    private boolean isSystem;
}
