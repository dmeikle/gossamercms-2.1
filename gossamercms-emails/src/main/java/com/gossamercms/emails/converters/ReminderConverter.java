package com.gossamercms.emails.converters;

import com.gossamercms.emails.data.RemindersDbService;
import com.gossamercms.emails.dtos.ReminderDto;
import com.gossamercms.mvc.annotations.ModuleConverter;
import org.springframework.core.convert.converter.Converter;
import com.gossamercms.mvc.converters.BaseConverter;

import java.util.UUID;


@ModuleConverter
public class ReminderConverter
        extends BaseConverter<ReminderDto>
         implements Converter<String, ReminderDto> {

    public ReminderConverter(RemindersDbService dbService) {
        super(dbService, ReminderDto.class);
    }
}

