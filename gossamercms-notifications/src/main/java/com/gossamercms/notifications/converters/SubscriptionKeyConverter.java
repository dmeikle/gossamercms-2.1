package com.gossamercms.notifications.converters;

import com.gossamercms.notifications.data.SubscriptionKeysDbService;
import com.gossamercms.notifications.dtos.SubscriptionKeyDto;
import com.gossamercms.mvc.annotations.ModuleConverter;
import org.springframework.core.convert.converter.Converter;
import com.gossamercms.mvc.converters.BaseConverter;


@ModuleConverter
public class SubscriptionKeyConverter
        extends BaseConverter<SubscriptionKeyDto>
         implements Converter<String, SubscriptionKeyDto> {

    public SubscriptionKeyConverter(SubscriptionKeysDbService dbService) {
        super(dbService, SubscriptionKeyDto.class);
    }
}

