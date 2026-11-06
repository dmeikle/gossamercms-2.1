package com.gossamercms.notifications.adapters.inbound;


import com.gossamercms.notifications.dtos.UserNotificationPreferenceDto;
import com.gossamercms.notifications.handlers.UserNotificationPreferencesHandler;
import com.gossamercms.notifications.models.UserNotificationPreference;
import com.gossamercms.mvc.controllers.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/UserNotificationPreferences")
public class UserNotificationPreferencesController extends BaseController<UserNotificationPreference, UserNotificationPreferenceDto, UserNotificationPreferenceDto, UserNotificationPreferenceDto, UserNotificationPreferenceDto> {

    public UserNotificationPreferencesController(UserNotificationPreferencesHandler handler) {
        super(handler, UserNotificationPreferenceDto.class);
    }

    @Override
    protected UserNotificationPreferenceDto toDto(UserNotificationPreferenceDto request) {
        return null;
    }

    @Override
    protected UserNotificationPreferenceDto toResponse(UserNotificationPreferenceDto dto) {
        return null;
    }
}
