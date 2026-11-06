package com.gossamercms.emails.data;

import com.gossamercms.emails.dtos.ReminderDto;
import com.gossamercms.emails.models.Reminder;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@ModuleDbService
public class RemindersDbService extends BaseDbService<Reminder, ReminderDto> {

    public RemindersDbService(DataSourceManager ds) {
        super(Reminder.class, ReminderDto.class, ds);
    }

    @Override
    protected Reminder mapToEntity(ReminderDto dto) {
        return dto.toEntity();
    }

    @Override
    protected ReminderDto mapToDto(Reminder entity) {
        return entity.toDto();
    }

    @Override
    protected ReminderDto removeExcludedFields(ReminderDto dto) {
        return dto;
    }

    @Override
    protected void throw404(String id) {

    }

}
