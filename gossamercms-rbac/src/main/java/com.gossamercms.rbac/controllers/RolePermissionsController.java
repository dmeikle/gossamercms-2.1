package com.gossamercms.rbac.controllers;

import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.rbac.dtos.RolePermissionDto;
import com.gossamercms.rbac.dtos.requests.RolePermissionRequest;
import com.gossamercms.rbac.dtos.responses.RolePermissionResponse;
import com.gossamercms.rbac.models.RolePermission;
import com.gossamercms.rbac.handlers.RolePermissionsHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rbac/role-permissions")
public class RolePermissionsController extends BaseController<RolePermission, RolePermissionDto, RolePermissionRequest, RolePermissionResponse, RolePermissionResponse> {

    public RolePermissionsController(RolePermissionsHandler handler) {
        super(handler, RolePermissionResponse.class);
    }

    @Override
    protected RolePermissionDto toDto(RolePermissionRequest request) {
        return RolePermissionDto.builder()
                .roleId(request.getRoleId())
                .permissionId(request.getPermissionId())
                .createdAt(request.getCreatedAt())
                .build();
    }

    @Override
    protected RolePermissionResponse toResponse(RolePermissionDto dto) {
        return RolePermissionResponse.builder()
                .id(dto.getId())
                .roleId(dto.getRoleId())
                .permissionId(dto.getPermissionId())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}