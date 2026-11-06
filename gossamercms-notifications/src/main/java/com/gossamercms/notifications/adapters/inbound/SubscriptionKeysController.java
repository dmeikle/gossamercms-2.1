package com.gossamercms.notifications.adapters.inbound;


import com.gossamercms.notifications.dtos.SubscriptionKeyDto;
import com.gossamercms.notifications.handlers.SubscriptionKeysHandler;
import com.gossamercms.notifications.models.SubscriptionKey;
import com.gossamercms.mvc.controllers.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/SubscriptionKeys")
public class SubscriptionKeysController extends BaseController<
        SubscriptionKey, SubscriptionKeyDto, SubscriptionKeyDto, SubscriptionKeyDto, SubscriptionKeyDto> {

    public SubscriptionKeysController(SubscriptionKeysHandler handler) {
        super(handler, SubscriptionKeyDto.class);
    }

    @Override
    protected SubscriptionKeyDto toDto(SubscriptionKeyDto request) {
        return request;
    }

    @Override
    protected SubscriptionKeyDto toResponse(SubscriptionKeyDto dto) {
        return dto;
    }
}
