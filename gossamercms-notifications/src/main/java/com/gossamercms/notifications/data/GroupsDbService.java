package com.gossamercms.notifications.data;

import com.gossamercms.notifications.dtos.GroupDto;
import com.gossamercms.notifications.models.Group;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;


@ModuleDbService
public class GroupsDbService extends BaseDbService<Group, GroupDto> {

    public GroupsDbService(DataSourceManager ds) {
        super(Group.class, GroupDto.class, ds);
    }

    @Override
    protected Group mapToEntity(GroupDto dto) {
        return dto.toEntity();
    }

    @Override
    protected GroupDto mapToDto(Group entity) {
        return entity.toDto();
    }

    @Override
    protected GroupDto removeExcludedFields(GroupDto dto) {
        return dto;
    }

    @Override
    protected void throw404(String id) {

    }

}
