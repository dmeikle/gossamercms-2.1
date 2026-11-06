package com.gossamercms.notifications.handlers;

import com.gossamercms.notifications.data.TemplateMessagesDbService;
import com.gossamercms.notifications.dtos.TemplateMessageDto;
import com.gossamercms.notifications.models.TemplateMessage;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class TemplateMessagesHandler extends BaseHandler<TemplateMessage, TemplateMessageDto> {

    public TemplateMessagesHandler(TemplateMessagesDbService db) {
        super(db);
    }
}
