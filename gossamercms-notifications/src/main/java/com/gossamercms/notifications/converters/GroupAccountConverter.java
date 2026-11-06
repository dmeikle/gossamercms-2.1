package com.gossamercms.notifications.converters;

import com.gossamercms.notifications.data.GroupAccountsDbService;
import com.gossamercms.notifications.dtos.GroupAccountDto;
import com.gossamercms.mvc.annotations.ModuleConverter;
import org.springframework.core.convert.converter.Converter;
import com.gossamercms.mvc.converters.BaseConverter;


@ModuleConverter
public class GroupAccountConverter
        extends BaseConverter<GroupAccountDto>
         implements Converter<String, GroupAccountDto> {

    public GroupAccountConverter(GroupAccountsDbService dbService) {
        super(dbService, GroupAccountDto.class);
    }
}

