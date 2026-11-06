package com.gossamercms.notifications.data;

import com.gossamercms.notifications.dtos.RequestLogDto;
import com.gossamercms.notifications.models.RequestLog;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;


@ModuleDbService
public class RequestLogsDbService extends BaseDbService<RequestLog, RequestLogDto> {

    public RequestLogsDbService(DataSourceManager ds) {
        super(RequestLog.class, RequestLogDto.class, ds);
    }

    @Override
    protected RequestLog mapToEntity(RequestLogDto dto) {
        return dto.toEntity();
    }

    @Override
    protected RequestLogDto mapToDto(RequestLog entity) {
        return entity.toDto();
    }

    @Override
    protected RequestLogDto removeExcludedFields(RequestLogDto dto) {
        return dto;
    }

    @Override
    protected void throw404(String id) {

    }

}
