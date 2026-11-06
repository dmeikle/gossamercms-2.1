package com.gossamercms.emails.adapters.inbound;


import com.gossamercms.emails.dtos.UserMessageDto;
import com.gossamercms.emails.handlers.UserMessagesHandler;
import com.gossamercms.emails.models.UserMessage;
import com.gossamercms.mvc.controllers.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/user-messages")
public class UserMessagesController extends BaseController<
        UserMessage, UserMessageDto, UserMessageDto, UserMessageDto, UserMessageDto> {

    public UserMessagesController(UserMessagesHandler handler) {
        super(handler, UserMessageDto.class);
    }

    @Override
    protected UserMessageDto toDto(UserMessageDto request) {
        return request;
    }

    @Override
    protected UserMessageDto toResponse(UserMessageDto dto) {
        return dto;
    }
}
