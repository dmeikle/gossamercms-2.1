package com.gossamercms.rbac.controllers;

import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.rbac.dtos.UserRoleDto;
import com.gossamercms.rbac.dtos.requests.UserRoleRequest;
import com.gossamercms.rbac.dtos.responses.UserRoleResponse;
import com.gossamercms.rbac.models.UserRole;
import com.gossamercms.rbac.handlers.UserRolesHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rbac/user-roles")
public class UserRolesController extends BaseController<UserRole, UserRoleDto, UserRoleRequest, UserRoleResponse> {

    public UserRolesController(UserRolesHandler handler) {
        super(handler);
    }

    @Override
    protected UserRoleDto toDto(UserRoleRequest request) {
        return UserRoleDto.builder()
                .userContextId(request.getUserContextId())
                .roleId(request.getRoleId())
                .assignedBy(request.getAssignedBy())
                .assignedAt(request.getAssignedAt())
                .expiresAt(request.getExpiresAt())
                .build();
    }

    @Override
    protected UserRoleResponse toResponse(UserRoleDto dto) {
        return UserRoleResponse.builder()
                .accountRoleId(dto.getAccountRoleId())
                .id(dto.getId())
                .userContextId(dto.getUserContextId())
                .roleId(dto.getRoleId())
                .assignedBy(dto.getAssignedBy())
                .assignedAt(dto.getAssignedAt())
                .expiresAt(dto.getExpiresAt())
                .build();
    }
}