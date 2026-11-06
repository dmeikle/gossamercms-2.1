package com.gossamercms.notifications.data;

import com.gossamercms.notifications.dtos.TemplateMessageDto;
import com.gossamercms.notifications.models.TemplateMessage;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;


@ModuleDbService
public class TemplateMessagesDbService extends BaseDbService<TemplateMessage, TemplateMessageDto> {

    public TemplateMessagesDbService(DataSourceManager ds) {
        super(TemplateMessage.class, TemplateMessageDto.class, ds);
    }

    @Override
    protected TemplateMessage mapToEntity(TemplateMessageDto dto) {
        return dto.toEntity();
    }

    @Override
    protected TemplateMessageDto mapToDto(TemplateMessage entity) {
        return entity.toDto();
    }

    @Override
    protected TemplateMessageDto removeExcludedFields(TemplateMessageDto dto) {
        return dto;
    }

}
