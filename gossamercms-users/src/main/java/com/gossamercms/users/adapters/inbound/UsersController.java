package com.gossamercms.users.adapters.inbound;

import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.users.api.UserDto;
import com.gossamercms.users.api.requests.UserRequest;
import com.gossamercms.users.api.responses.UserResponse;
import com.gossamercms.users.handlers.UsersHandler;
import com.gossamercms.users.domain.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController extends BaseController<User, UserDto, UserRequest, UserResponse, UserResponse> {

    public UsersController(UsersHandler handler) {
        super(handler, UserResponse.class);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from public endpoint";
    }

    @Override
    protected UserDto toDto(UserRequest request) {
        return UserDto.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .build();
    }

    @Override
    protected UserResponse toResponse(UserDto dto) {
        return UserResponse.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .status(dto.getStatus())
                .ipAddress(dto.getIpAddress())
                .createdAt(dto.getCreatedAt())
                .build();
    }

}