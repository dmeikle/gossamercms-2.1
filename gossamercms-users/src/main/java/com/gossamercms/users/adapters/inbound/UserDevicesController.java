package com.gossamercms.users.adapters.inbound;


import com.gossamercms.mvc.annotations.CurrentUser;
import com.gossamercms.mvc.exceptions.NotFoundException;
import com.gossamercms.security.jwt.JwtUser;
import com.gossamercms.users.api.UserDeviceDto;
import com.gossamercms.users.api.requests.RegisterUserDeviceRequest;
import com.gossamercms.users.api.requests.UserDeviceRequest;
import com.gossamercms.users.api.responses.UserDeviceResponse;
import com.gossamercms.users.handlers.UserDevicesHandler;
import com.gossamercms.users.domain.UserDevice;
import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.users.services.RegisterUserDeviceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/admin/user-devices")
public class UserDevicesController extends BaseController<UserDevice, UserDeviceDto, UserDeviceRequest, UserDeviceResponse, UserDeviceResponse> {

    private final RegisterUserDeviceService registerUserDeviceService;

    public UserDevicesController(UserDevicesHandler handler,
                                 RegisterUserDeviceService registerUserDeviceService
                                 ) {
        super(handler, UserDeviceResponse.class);
        this.registerUserDeviceService = registerUserDeviceService;
    }

    @PostMapping("/register")
    public void register(@CurrentUser JwtUser jwtUser, @RequestBody RegisterUserDeviceRequest request) {
        UserDeviceDto device = null;
        try {
            handler.get(Map.of("firebaseToken", request.getFirebaseToken()));
        }catch (NotFoundException e) {
            device = new UserDeviceDto();
        }
        registerUserDeviceService.register(jwtUser.getUserId(), device, request);
    }

    @Override
    protected UserDeviceDto toDto(UserDeviceRequest request) {
        return UserDeviceDto.builder()
                .userId(request.getUserId())
                .firebaseToken(request.getFirebaseToken())
                .platform(request.getPlatform())
                .deviceId(request.getDeviceId())
                .appVersion(request.getAppVersion())
                .active(request.getActive())
                .build();
    }

    @Override
    protected UserDeviceResponse toResponse(UserDeviceDto dto) {
        return UserDeviceResponse.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .firebaseToken(dto.getFirebaseToken())
                .platform(dto.getPlatform())
                .deviceId(dto.getDeviceId())
                .appVersion(dto.getAppVersion())
                .active(dto.getActive())
                .lastSeenAt(dto.getLastSeenAt())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}
