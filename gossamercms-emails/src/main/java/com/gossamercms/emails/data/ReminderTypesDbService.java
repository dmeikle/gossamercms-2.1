package com.gossamercms.emails.data;

import com.gossamercms.emails.dtos.ReminderTypeDto;
import com.gossamercms.emails.models.ReminderType;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@ModuleDbService
public class ReminderTypesDbService extends BaseDbService<ReminderType, ReminderTypeDto> {

    public ReminderTypesDbService(DataSourceManager ds) {
        super(ReminderType.class, ReminderTypeDto.class, ds);
    }

    @Override
    protected ReminderType mapToEntity(ReminderTypeDto dto) {
        return dto.toEntity();
    }

    @Override
    protected ReminderTypeDto mapToDto(ReminderType entity) {
        return entity.toDto();
    }

    @Override
    protected ReminderTypeDto removeExcludedFields(ReminderTypeDto dto) {
        return dto;
    }

    @Override
    protected void throw404(String id) {

    }

}
