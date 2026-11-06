package com.gossamercms.notifications.handlers;

import com.gossamercms.notifications.data.SubscribersDbService;
import com.gossamercms.notifications.dtos.SubscriberDto;
import com.gossamercms.notifications.models.Subscriber;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class SubscribersHandler extends BaseHandler<Subscriber, SubscriberDto> {

    public SubscribersHandler(SubscribersDbService db) {
        super(db);
    }
}
