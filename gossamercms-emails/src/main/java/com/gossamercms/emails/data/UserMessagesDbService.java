package com.gossamercms.emails.data;

import com.gossamercms.emails.dtos.UserMessageDto;
import com.gossamercms.emails.models.UserMessage;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@ModuleDbService
public class UserMessagesDbService extends BaseDbService<UserMessage, UserMessageDto> {

    public UserMessagesDbService(DataSourceManager ds) {
        super(UserMessage.class, UserMessageDto.class, ds);
    }

    @Override
    protected UserMessage mapToEntity(UserMessageDto dto) {
        return dto.toEntity();
    }

    @Override
    protected UserMessageDto mapToDto(UserMessage entity) {
        return entity.toDto();
    }

    @Override
    protected UserMessageDto removeExcludedFields(UserMessageDto dto) {
        return dto;
    }

    @Override
    protected void throw404(String id) {

    }

}
