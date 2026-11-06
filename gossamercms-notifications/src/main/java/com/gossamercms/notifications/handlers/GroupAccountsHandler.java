package com.gossamercms.notifications.handlers;

import com.gossamercms.notifications.data.GroupAccountsDbService;
import com.gossamercms.notifications.dtos.GroupAccountDto;
import com.gossamercms.notifications.models.GroupAccount;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class GroupAccountsHandler extends BaseHandler<GroupAccount, GroupAccountDto> {

    public GroupAccountsHandler(GroupAccountsDbService db) {
        super(db);
    }
}
