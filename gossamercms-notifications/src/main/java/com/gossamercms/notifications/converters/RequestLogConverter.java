package com.gossamercms.notifications.converters;

import com.gossamercms.notifications.data.RequestLogsDbService;
import com.gossamercms.notifications.dtos.RequestLogDto;
import com.gossamercms.mvc.annotations.ModuleConverter;
import org.springframework.core.convert.converter.Converter;
import com.gossamercms.mvc.converters.BaseConverter;


@ModuleConverter
public class RequestLogConverter
        extends BaseConverter<RequestLogDto>
         implements Converter<String, RequestLogDto> {

    public RequestLogConverter(RequestLogsDbService dbService) {
        super(dbService, RequestLogDto.class);
    }
}

