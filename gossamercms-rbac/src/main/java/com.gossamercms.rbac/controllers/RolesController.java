package com.gossamercms.rbac.controllers;

import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.rbac.dtos.RoleDto;
import com.gossamercms.rbac.dtos.requests.RoleRequest;
import com.gossamercms.rbac.dtos.responses.RoleResponse;
import com.gossamercms.rbac.models.Role;
import com.gossamercms.rbac.handlers.RolesHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rbac/roles")
public class RolesController extends BaseController<Role, RoleDto, RoleRequest, RoleResponse, RoleResponse> {

    public RolesController(RolesHandler handler) {
        super(handler, RoleResponse.class);
    }

    @Override
    protected RoleDto toDto(RoleRequest request) {
        return RoleDto.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isSystem(request.isSystem())
                .build();
    }

    @Override
    protected RoleResponse toResponse(RoleDto dto) {
        return RoleResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .isSystem(dto.isSystem())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}