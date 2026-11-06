package com.gossamercms.notifications.converters;

import com.gossamercms.notifications.data.MessagesDbService;
import com.gossamercms.notifications.dtos.MessageDto;
import com.gossamercms.mvc.annotations.ModuleConverter;
import org.springframework.core.convert.converter.Converter;
import com.gossamercms.mvc.converters.BaseConverter;


@ModuleConverter
public class MessageConverter
        extends BaseConverter<MessageDto>
         implements Converter<String, MessageDto> {

    public MessageConverter(MessagesDbService dbService) {
        super(dbService, MessageDto.class);
    }
}

