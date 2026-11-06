package com.gossamercms.notifications.adapters.inbound;


import com.gossamercms.notifications.dtos.MessageAudienceDto;
import com.gossamercms.notifications.dtos.MessageDto;
import com.gossamercms.notifications.dtos.requests.CreateMessageRequest;
import com.gossamercms.notifications.enums.MessageAudienceType;
import com.gossamercms.notifications.handlers.MessagesHandler;
import com.gossamercms.notifications.models.Message;
import com.gossamercms.notifications.services.MessagesService;
import com.gossamercms.mvc.annotations.CurrentUser;
import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.mvc.http.ApiResponse;
import com.gossamercms.mvc.jwt.CurrentJwtUser;
import com.gossamercms.security.jwt.JwtUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/messages")
public class MessagesController extends BaseController<Message, MessageDto, CreateMessageRequest, MessageDto, MessageDto> {

    private final MessagesService messagesService;

    public MessagesController(MessagesHandler handler,
                              MessagesService messagesService) {
        super(handler, MessageDto.class);
        this.messagesService = messagesService;
    }

    @Override
    protected MessageDto toDto(CreateMessageRequest request) {
        return MessageDto.builder()
                .subject(request.getEventKey())
                .build();
    }

    @Override
    protected MessageDto toResponse(MessageDto dto) {
        return dto;
    }

    @Override
    @PostMapping
    public ApiResponse<MessageDto> create(
            @CurrentUser CurrentJwtUser jwtUser,
            @RequestBody CreateMessageRequest request
    ) {


        if(request.getAudience() != null) {
            MessageAudienceDto audience = MessageAudienceDto.builder()
                    .type(MessageAudienceType.USER)
                    .userContextId(jwtUser.getUserContextId())
                    .build();
            request.setAudiences(List.of(audience));
        }
        messagesService.send(jwtUser, request);


        //TODO: for now, just return the request as a response.
        // In the future, we may want to return a more detailed response with message status, etc.
        return ApiResponse.ok(
                MessageDto.builder()
                        .subject(request.getEventKey())
                        .build()
        );
    }

}
