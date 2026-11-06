package com.gossamercms.emails.handlers;

import com.gossamercms.emails.data.ReminderTypesDbService;
import com.gossamercms.emails.dtos.ReminderTypeDto;
import com.gossamercms.emails.models.ReminderType;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class ReminderTypesHandler extends BaseHandler<ReminderType, ReminderTypeDto> {

    public ReminderTypesHandler(ReminderTypesDbService db) {
        super(db);
    }
}
