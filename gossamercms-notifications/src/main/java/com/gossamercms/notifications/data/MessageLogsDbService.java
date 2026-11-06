package com.gossamercms.notifications.data;

import com.gossamercms.notifications.dtos.MessageLogDto;
import com.gossamercms.notifications.models.MessageLog;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;


@ModuleDbService
public class MessageLogsDbService extends BaseDbService<MessageLog, MessageLogDto> {

    public MessageLogsDbService(DataSourceManager ds) {
        super(MessageLog.class, MessageLogDto.class, ds);
    }

    @Override
    protected MessageLog mapToEntity(MessageLogDto dto) {
        return dto.toEntity();
    }

    @Override
    protected MessageLogDto mapToDto(MessageLog entity) {
        return entity.toDto();
    }

    @Override
    protected MessageLogDto removeExcludedFields(MessageLogDto dto) {
        return dto;
    }

    @Override
    protected void throw404(String id) {

    }

}
