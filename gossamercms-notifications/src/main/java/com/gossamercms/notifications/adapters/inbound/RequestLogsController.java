package com.gossamercms.notifications.adapters.inbound;


import com.gossamercms.notifications.dtos.RequestLogDto;
import com.gossamercms.notifications.handlers.RequestLogsHandler;
import com.gossamercms.notifications.models.RequestLog;
import com.gossamercms.mvc.controllers.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/request-logs")
public class RequestLogsController extends BaseController<
        RequestLog, RequestLogDto, RequestLogDto, RequestLogDto, RequestLogDto> {

    public RequestLogsController(RequestLogsHandler handler) {
        super(handler, RequestLogDto.class);
    }

    @Override
    protected RequestLogDto toDto(RequestLogDto request) {
        return request;
    }

    @Override
    protected RequestLogDto toResponse(RequestLogDto dto) {
        return dto;
    }
}
