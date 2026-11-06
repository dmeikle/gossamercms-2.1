package com.gossamercms.notifications.adapters.inbound;


import com.gossamercms.notifications.dtos.GroupDto;
import com.gossamercms.notifications.handlers.GroupsHandler;
import com.gossamercms.notifications.models.Group;
import com.gossamercms.mvc.controllers.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/groups")
public class GroupsController extends BaseController<Group, GroupDto, GroupDto, GroupDto, GroupDto> {

    public GroupsController(GroupsHandler handler) {
        super(handler, GroupDto.class);
    }

    @Override
    protected GroupDto toDto(GroupDto request) {
        return request;
    }

    @Override
    protected GroupDto toResponse(GroupDto dto) {
        return dto;
    }
}
