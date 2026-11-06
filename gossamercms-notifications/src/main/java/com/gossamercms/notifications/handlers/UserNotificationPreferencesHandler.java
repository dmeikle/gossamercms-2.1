package com.gossamercms.notifications.handlers;

import com.gossamercms.notifications.data.UserNotificationPreferencesDbService;
import com.gossamercms.notifications.dtos.UserNotificationPreferenceDto;
import com.gossamercms.notifications.models.UserNotificationPreference;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class UserNotificationPreferencesHandler extends BaseHandler<UserNotificationPreference, UserNotificationPreferenceDto> {

    public UserNotificationPreferencesHandler(UserNotificationPreferencesDbService db) {
        super(db);
    }
}
