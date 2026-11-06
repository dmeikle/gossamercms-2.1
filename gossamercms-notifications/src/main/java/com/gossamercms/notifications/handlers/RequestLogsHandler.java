package com.gossamercms.notifications.handlers;

import com.gossamercms.notifications.data.RequestLogsDbService;
import com.gossamercms.notifications.dtos.RequestLogDto;
import com.gossamercms.notifications.models.RequestLog;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class RequestLogsHandler extends BaseHandler<RequestLog, RequestLogDto> {

    public RequestLogsHandler(RequestLogsDbService db) {
        super(db);
    }
}
