package com.gossamercms.notifications.converters;

import com.gossamercms.notifications.data.SubscribersDbService;
import com.gossamercms.notifications.dtos.SubscriberDto;
import com.gossamercms.mvc.annotations.ModuleConverter;
import org.springframework.core.convert.converter.Converter;
import com.gossamercms.mvc.converters.BaseConverter;


@ModuleConverter
public class SubscriberConverter
        extends BaseConverter<SubscriberDto>
         implements Converter<String, SubscriberDto> {

    public SubscriberConverter(SubscribersDbService dbService) {
        super(dbService, SubscriberDto.class);
    }
}

