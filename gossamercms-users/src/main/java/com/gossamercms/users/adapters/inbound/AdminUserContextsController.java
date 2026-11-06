package com.gossamercms.users.adapters.inbound;

import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.mvc.http.RequestContext;
import com.gossamercms.users.api.UserContextDto;
import com.gossamercms.users.api.UserDto;
import com.gossamercms.users.api.requests.UserContextRequest;
import com.gossamercms.users.api.responses.UserContextResponse;
import com.gossamercms.users.domain.UserContext;
import com.gossamercms.users.handlers.UserContextsHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/auth/contexts")
public class AdminUserContextsController extends BaseController<UserContext, UserContextDto, UserContextRequest, UserContextResponse, UserContextResponse> {

    public AdminUserContextsController(UserContextsHandler handler) {
        super(handler, UserContextResponse.class);
    }


    @GetMapping("/user/{user}")
    public Object listUserContextsByUserId(@PathVariable("user") UserDto user) {
        System.out.println("UserContextsAdminController.listByUserId: " + user.getId());
        RequestContext ctx = new RequestContext(user.getId(), Map.of());
        return  ((UserContextsHandler) handler).listAvailableContexts(ctx);
    }

    @Override
    protected UserContextDto toDto(UserContextRequest request) {
        return UserContextDto.builder()
                .userId(request.getUserId())
                .roleId(request.getRoleId())
                .contextType(request.getContextType())
                .metadata(request.getMetadata())
                .isDefault(request.isDefault())
                .build();
    }

    @Override
    protected UserContextResponse toResponse(UserContextDto dto) {
        return UserContextResponse.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .roleId(dto.getRoleId())
                .contextType(dto.getContextType())
                .metadata(dto.getMetadata())
                .createdAt(dto.getCreatedAt())
                .isDefault(dto.isDefault())
                .build();
    }
//    @GetMapping("/users1/userId")
//    public Object listByUserId(UUID userId) {
//        return "this is the id ";// + userId.toString();
//    }
//    @GetMapping("/users1/userId/{userId}")
//    public Object listByUserId2(@PathVariable UUID userId) {
//        return "this is the id " + userId.toString();
//    }
////    @GetMapping("/users1/userId/user/{id}")
////    public Object listByUserId2(@PathVariable User user) {
////        return user;
////    }
//    @GetMapping("/users1/userId/user/{user}")
//    public Object listByUserId2(@PathVariable("user") UserDto user) {
//        return user;
//    }
}
