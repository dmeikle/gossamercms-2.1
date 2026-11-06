package com.gossamercms.notifications.handlers;

import com.gossamercms.notifications.data.MessageLogsDbService;
import com.gossamercms.notifications.dtos.MessageLogDto;
import com.gossamercms.notifications.models.MessageLog;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class MessageLogsHandler extends BaseHandler<MessageLog, MessageLogDto> {

    public MessageLogsHandler(MessageLogsDbService db) {
        super(db);
    }
}
