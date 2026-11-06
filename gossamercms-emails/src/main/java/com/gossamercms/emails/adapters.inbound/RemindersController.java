package com.gossamercms.emails.adapters.inbound;


import com.gossamercms.emails.dtos.ReminderDto;
import com.gossamercms.emails.handlers.RemindersHandler;
import com.gossamercms.emails.models.Reminder;
import com.gossamercms.mvc.controllers.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/reminders")
public class RemindersController extends BaseController<Reminder, ReminderDto, ReminderDto, ReminderDto, ReminderDto> {

    public RemindersController(RemindersHandler handler) {
        super(handler, ReminderDto.class);
    }

    @Override
    protected ReminderDto toDto(ReminderDto request) {
        return request;
    }

    @Override
    protected ReminderDto toResponse(ReminderDto dto) {
        return dto;
    }
}
