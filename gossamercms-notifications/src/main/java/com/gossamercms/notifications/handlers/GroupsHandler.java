package com.gossamercms.notifications.handlers;

import com.gossamercms.notifications.data.GroupsDbService;
import com.gossamercms.notifications.dtos.GroupDto;
import com.gossamercms.notifications.models.Group;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class GroupsHandler extends BaseHandler<Group, GroupDto> {

    public GroupsHandler(GroupsDbService db) {
        super(db);
    }
}
