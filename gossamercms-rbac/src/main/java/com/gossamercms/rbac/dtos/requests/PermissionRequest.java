package com.gossamercms.rbac.dtos.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionRequest {
    private String name;
    private String description;
}
