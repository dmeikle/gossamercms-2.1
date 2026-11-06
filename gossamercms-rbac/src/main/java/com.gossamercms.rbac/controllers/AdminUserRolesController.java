package com.gossamercms.rbac.controllers;

import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.rbac.dtos.RoleDto;
import com.gossamercms.rbac.dtos.UserRoleDto;
import com.gossamercms.rbac.dtos.requests.UserRoleRequest;
import com.gossamercms.rbac.dtos.responses.UserRoleResponse;
import com.gossamercms.rbac.models.UserRole;
import com.gossamercms.rbac.handlers.UserRolesHandler;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin/rbac/users")
public class AdminUserRolesController extends BaseController<UserRole, UserRoleDto, UserRoleRequest, UserRoleResponse, UserRoleResponse> {

    private final UserRolesHandler handler;

    public AdminUserRolesController(UserRolesHandler handler) {
        super(handler, UserRoleResponse.class);
        this.handler = handler;
    }

    // ------------------------------------------------------------
    // GET: List roles for a user
    // ------------------------------------------------------------
    @GetMapping("/{userId}/roles")
    public List<RoleDto> listRolesForUser(@PathVariable UUID userId) {
        return handler.listRolesForUser(userId);
    }

    // ------------------------------------------------------------
    // POST: Assign role to user
    // ------------------------------------------------------------
    @PostMapping("/{userContextId}/roles/{roleId}")
    public UserRoleDto assignRole(
            @PathVariable UUID userContextId,
            @PathVariable UUID roleId,
            @RequestAttribute("authUserId") UUID adminId
    ) {

        return handler.assignRoleToUser(adminId, userContextId, roleId);
    }

    // ------------------------------------------------------------
    // DELETE: Remove role from user
    // ------------------------------------------------------------
    @DeleteMapping("/{userId}/roles/{roleId}")
    public void removeRole(
            @PathVariable UUID userId,
            @PathVariable UUID roleId,
            @RequestAttribute("authUserId") UUID adminId
    ) {
        handler.removeRoleFromUser(adminId, userId, roleId);
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