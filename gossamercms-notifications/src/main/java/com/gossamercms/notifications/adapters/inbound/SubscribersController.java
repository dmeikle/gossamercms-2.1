package com.gossamercms.notifications.adapters.inbound;


import com.gossamercms.notifications.dtos.SubscriberDto;
import com.gossamercms.notifications.handlers.SubscribersHandler;
import com.gossamercms.notifications.models.Subscriber;
import com.gossamercms.mvc.controllers.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/subscribers")
public class SubscribersController extends BaseController<
        Subscriber, SubscriberDto, SubscriberDto, SubscriberDto, SubscriberDto> {

    public SubscribersController(SubscribersHandler handler) {
        super(handler, SubscriberDto.class);
    }

    @Override
    protected SubscriberDto toDto(SubscriberDto request) {
        return request;
    }

    @Override
    protected SubscriberDto toResponse(SubscriberDto dto) {
        return dto;
    }
}
