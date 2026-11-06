package com.gossamercms.notifications.data;

import com.gossamercms.notifications.dtos.MessageDto;
import com.gossamercms.notifications.models.Message;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;


@ModuleDbService
public class MessagesDbService extends BaseDbService<Message, MessageDto> {

    public MessagesDbService(DataSourceManager ds) {
        super(Message.class, MessageDto.class, ds);
    }

    @Override
    protected Message mapToEntity(MessageDto dto) {
        return dto.toEntity();
    }

    @Override
    protected MessageDto mapToDto(Message entity) {
        return entity.toDto();
    }

    @Override
    protected MessageDto removeExcludedFields(MessageDto dto) {
        return dto;
    }

    @Override
    protected void throw404(String id) {

    }

}
