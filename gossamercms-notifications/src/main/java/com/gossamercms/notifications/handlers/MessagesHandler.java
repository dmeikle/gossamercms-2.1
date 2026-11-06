package com.gossamercms.notifications.handlers;

import com.gossamercms.notifications.data.MessagesDbService;
import com.gossamercms.notifications.dtos.MessageDto;
import com.gossamercms.notifications.models.Message;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class MessagesHandler extends BaseHandler<Message, MessageDto> {

    public MessagesHandler(MessagesDbService db) {
        super(db);
    }
}
