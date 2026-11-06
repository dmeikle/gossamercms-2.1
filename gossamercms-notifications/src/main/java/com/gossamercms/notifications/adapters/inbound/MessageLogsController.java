package com.gossamercms.notifications.adapters.inbound;


import com.gossamercms.notifications.dtos.MessageLogDto;
import com.gossamercms.notifications.handlers.MessageLogsHandler;
import com.gossamercms.notifications.models.MessageLog;
import com.gossamercms.mvc.controllers.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/message-logs")
public class MessageLogsController extends BaseController<
        MessageLog, MessageLogDto, MessageLogDto, MessageLogDto, MessageLogDto> {

    public MessageLogsController(MessageLogsHandler handler) {
        super(handler, MessageLogDto.class);
    }

    @Override
    protected MessageLogDto toDto(MessageLogDto request) {
        return request;
    }

    @Override
    protected MessageLogDto toResponse(MessageLogDto dto) {
        return dto;
    }
}
