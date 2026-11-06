package com.gossamercms.emails.converters;

import com.gossamercms.emails.data.ReminderTypesDbService;
import com.gossamercms.emails.dtos.ReminderTypeDto;
import com.gossamercms.mvc.annotations.ModuleConverter;
import org.springframework.core.convert.converter.Converter;
import com.gossamercms.mvc.converters.BaseConverter;

import java.util.UUID;


@ModuleConverter
public class ReminderTypeConverter
        extends BaseConverter<ReminderTypeDto>
         implements Converter<String, ReminderTypeDto> {

    public ReminderTypeConverter(ReminderTypesDbService dbService) {
        super(dbService, ReminderTypeDto.class);
    }
}

