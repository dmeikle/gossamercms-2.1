package com.gossamercms.notifications.converters;

import com.gossamercms.notifications.data.GroupsDbService;
import com.gossamercms.notifications.dtos.GroupDto;
import com.gossamercms.mvc.annotations.ModuleConverter;
import org.springframework.core.convert.converter.Converter;
import com.gossamercms.mvc.converters.BaseConverter;


@ModuleConverter
public class GroupConverter
        extends BaseConverter<GroupDto>
         implements Converter<String, GroupDto> {

    public GroupConverter(GroupsDbService dbService) {
        super(dbService, GroupDto.class);
    }
}

