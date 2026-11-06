package com.gossamercms.notifications.adapters.inbound;


import com.gossamercms.notifications.dtos.GroupAccountDto;
import com.gossamercms.notifications.handlers.GroupAccountsHandler;
import com.gossamercms.notifications.models.GroupAccount;
import com.gossamercms.mvc.controllers.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/groups-accounts")
public class GroupAccountsController extends BaseController<
        GroupAccount, GroupAccountDto, GroupAccountDto, GroupAccountDto, GroupAccountDto> {

    public GroupAccountsController(GroupAccountsHandler handler) {
        super(handler, GroupAccountDto.class);
    }

    @Override
    protected GroupAccountDto toDto(GroupAccountDto request) {
        return request;
    }

    @Override
    protected GroupAccountDto toResponse(GroupAccountDto dto) {
        return dto;
    }
}
