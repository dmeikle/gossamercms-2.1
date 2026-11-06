package com.gossamercms.notifications.converters;

import com.gossamercms.notifications.data.MessageLogsDbService;
import com.gossamercms.notifications.dtos.MessageLogDto;
import com.gossamercms.mvc.annotations.ModuleConverter;
import org.springframework.core.convert.converter.Converter;
import com.gossamercms.mvc.converters.BaseConverter;


@ModuleConverter
public class MessageLogConverter
        extends BaseConverter<MessageLogDto>
         implements Converter<String, MessageLogDto> {

    public MessageLogConverter(MessageLogsDbService dbService) {
        super(dbService, MessageLogDto.class);
    }
}

