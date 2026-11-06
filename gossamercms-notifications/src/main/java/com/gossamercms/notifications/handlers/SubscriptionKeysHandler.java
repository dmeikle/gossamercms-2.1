package com.gossamercms.notifications.handlers;

import com.gossamercms.notifications.data.SubscriptionKeysDbService;
import com.gossamercms.notifications.dtos.SubscriptionKeyDto;
import com.gossamercms.notifications.models.SubscriptionKey;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class SubscriptionKeysHandler extends BaseHandler<SubscriptionKey, SubscriptionKeyDto> {

    public SubscriptionKeysHandler(SubscriptionKeysDbService db) {
        super(db);
    }
}
