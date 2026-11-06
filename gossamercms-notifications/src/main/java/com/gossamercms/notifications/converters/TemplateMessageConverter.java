package com.gossamercms.notifications.converters;

import com.gossamercms.notifications.data.TemplateMessagesDbService;
import com.gossamercms.notifications.dtos.TemplateMessageDto;
import com.gossamercms.mvc.annotations.ModuleConverter;
import org.springframework.core.convert.converter.Converter;
import com.gossamercms.mvc.converters.BaseConverter;


@ModuleConverter
public class TemplateMessageConverter
        extends BaseConverter<TemplateMessageDto>
         implements Converter<String, TemplateMessageDto> {

    public TemplateMessageConverter(TemplateMessagesDbService dbService) {
        super(dbService, TemplateMessageDto.class);
    }
}

