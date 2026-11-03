package com.gossamercms.rbac.controllers;

import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.rbac.dtos.PermissionDto;
import com.gossamercms.rbac.dtos.requests.PermissionRequest;
import com.gossamercms.rbac.dtos.responses.PermissionResponse;
import com.gossamercms.rbac.models.Permission;
import com.gossamercms.rbac.handlers.PermissionsHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rbac/permissions")
public class PermissionsController extends BaseController<Permission, PermissionDto, PermissionRequest, PermissionResponse> {

    public PermissionsController(PermissionsHandler handler) {
        super(handler);
    }

    @Override
    protected PermissionDto toDto(PermissionRequest request) {
        return PermissionDto.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    @Override
    protected PermissionResponse toResponse(PermissionDto dto) {
        return PermissionResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}