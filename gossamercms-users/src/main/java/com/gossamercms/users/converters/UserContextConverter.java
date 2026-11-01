package com.gossamercms.users.converters;

import com.gossamercms.mvc.annotations.ModuleConverter;
import com.gossamercms.users.api.UserContextDto;
import com.gossamercms.users.data.UserContextsDbService;
import org.springframework.core.convert.converter.Converter;

import java.util.UUID;

@ModuleConverter
public class UserContextConverter implements Converter<String, UserContextDto> {

    private final UserContextsDbService userContextsDbService;

    public UserContextConverter(UserContextsDbService userContextsDbService) {
        this.userContextsDbService = userContextsDbService;
    }

    @Override
    public UserContextDto convert(String source) {
        UUID id = UUID.fromString(source);

        return userContextsDbService.getById(id);
    }
}
