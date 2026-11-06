package com.gossamercms.emails.converters;

import com.gossamercms.emails.data.UserMessagesDbService;
import com.gossamercms.emails.dtos.UserMessageDto;
import com.gossamercms.mvc.annotations.ModuleConverter;
import org.springframework.core.convert.converter.Converter;
import com.gossamercms.mvc.converters.BaseConverter;

import java.util.UUID;


@ModuleConverter
public class UserMessageConverter
        extends BaseConverter<UserMessageDto>
         implements Converter<String, UserMessageDto> {

    public UserMessageConverter(UserMessagesDbService dbService) {
        super(dbService, UserMessageDto.class);
    }
}

