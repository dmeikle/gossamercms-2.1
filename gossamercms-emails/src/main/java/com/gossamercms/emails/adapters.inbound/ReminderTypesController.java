package com.gossamercms.emails.adapters.inbound;


import com.gossamercms.emails.dtos.ReminderTypeDto;
import com.gossamercms.emails.handlers.ReminderTypesHandler;
import com.gossamercms.emails.models.ReminderType;
import com.gossamercms.mvc.controllers.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/reminder-types")
public class ReminderTypesController extends BaseController<
        ReminderType, ReminderTypeDto, ReminderTypeDto, ReminderTypeDto, ReminderTypeDto> {

    public ReminderTypesController(ReminderTypesHandler handler) {
        super(handler, ReminderTypeDto.class);
    }

    @Override
    protected ReminderTypeDto toDto(ReminderTypeDto request) {
        return request;
    }

    @Override
    protected ReminderTypeDto toResponse(ReminderTypeDto dto) {
        return dto;
    }
}
