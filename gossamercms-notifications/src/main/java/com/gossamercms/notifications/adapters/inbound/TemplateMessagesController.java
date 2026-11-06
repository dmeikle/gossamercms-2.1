package com.gossamercms.notifications.adapters.inbound;


import com.gossamercms.notifications.dtos.TemplateMessageDto;
import com.gossamercms.notifications.handlers.TemplateMessagesHandler;
import com.gossamercms.notifications.models.TemplateMessage;
import com.gossamercms.mvc.controllers.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/template-messages")
public class TemplateMessagesController extends BaseController<
        TemplateMessage, TemplateMessageDto, TemplateMessageDto, TemplateMessageDto, TemplateMessageDto> {

    public TemplateMessagesController(TemplateMessagesHandler handler) {
        super(handler, TemplateMessageDto.class);
    }

    @Override
    protected TemplateMessageDto toDto(TemplateMessageDto request) {
        return request;
    }

    @Override
    protected TemplateMessageDto toResponse(TemplateMessageDto dto) {
        return dto;
    }
}
