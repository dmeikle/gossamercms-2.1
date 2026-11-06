package com.gossamercms.notifications.converters;

import com.gossamercms.notifications.data.UserNotificationPreferencesDbService;
import com.gossamercms.notifications.dtos.UserNotificationPreferenceDto;
import com.gossamercms.mvc.annotations.ModuleConverter;
import org.springframework.core.convert.converter.Converter;
import com.gossamercms.mvc.converters.BaseConverter;

import java.util.UUID;


@ModuleConverter
public class UserNotificationPreferenceConverter
        extends BaseConverter<UserNotificationPreferenceDto>
         implements Converter<String, UserNotificationPreferenceDto> {

    public UserNotificationPreferenceConverter(UserNotificationPreferencesDbService dbService) {
        super(dbService, UserNotificationPreferenceDto.class);
    }
}

