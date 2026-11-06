package com.gossamercms.rbac.controllers;

import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.rbac.dtos.PermissionDto;
import com.gossamercms.rbac.dtos.RolePermissionDto;
import com.gossamercms.rbac.dtos.requests.RolePermissionRequest;
import com.gossamercms.rbac.dtos.responses.RolePermissionResponse;
import com.gossamercms.rbac.models.RolePermission;
import com.gossamercms.rbac.handlers.RolePermissionsHandler;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin/rbac/roles")
public class AdminRolePermissionsController extends BaseController<RolePermission, RolePermissionDto, RolePermissionRequest, RolePermissionResponse, RolePermissionResponse> {

    private final RolePermissionsHandler handler;

    public AdminRolePermissionsController(RolePermissionsHandler handler) {
        super(handler, RolePermissionResponse.class);
        this.handler = handler;
    }

    // ------------------------------------------------------------
    // GET: List permissions for a role
    // ------------------------------------------------------------
    @GetMapping("/{roleId}/permissions")
    public List<PermissionDto> listPermissionsForRole(@PathVariable UUID roleId) {
        return handler.listPermissionsForRole(roleId);
    }

    // ------------------------------------------------------------
    // POST: Assign permission to role
    // ------------------------------------------------------------
    @PostMapping("/{roleId}/permissions/{permissionId}")
    public RolePermissionDto assignPermission(
            @PathVariable UUID roleId,
            @PathVariable UUID permissionId,
            @RequestAttribute("authUserId") UUID adminId
    ) {
        return handler.assignPermissionToRole(adminId, roleId, permissionId);
    }

    // ------------------------------------------------------------
    // DELETE: Remove permission from role
    // ------------------------------------------------------------
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public void removePermission(
            @PathVariable UUID roleId,
            @PathVariable UUID permissionId,
            @RequestAttribute("authUserId") UUID adminId
    ) {
        handler.removePermissionFromRole(adminId, roleId, permissionId);
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