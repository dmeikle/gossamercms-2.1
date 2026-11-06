package com.gossamercms.emails.handlers;

import com.gossamercms.emails.data.RemindersDbService;
import com.gossamercms.emails.dtos.ReminderDto;
import com.gossamercms.emails.models.Reminder;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class RemindersHandler extends BaseHandler<Reminder, ReminderDto> {

    public RemindersHandler(RemindersDbService db) {
        super(db);
    }
}
