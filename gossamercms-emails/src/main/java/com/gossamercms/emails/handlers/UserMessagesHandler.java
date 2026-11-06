package com.gossamercms.emails.handlers;

import com.gossamercms.emails.data.UserMessagesDbService;
import com.gossamercms.emails.dtos.UserMessageDto;
import com.gossamercms.emails.models.UserMessage;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class UserMessagesHandler extends BaseHandler<UserMessage, UserMessageDto> {

    public UserMessagesHandler(UserMessagesDbService db) {
        super(db);
    }
}
