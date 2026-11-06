package com.gossamercms.notifications.data;

import com.gossamercms.notifications.dtos.GroupAccountDto;
import com.gossamercms.notifications.models.GroupAccount;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;


@ModuleDbService
public class GroupAccountsDbService extends BaseDbService<GroupAccount, GroupAccountDto> {

    public GroupAccountsDbService(DataSourceManager ds) {
        super(GroupAccount.class, GroupAccountDto.class, ds);
    }

    @Override
    protected GroupAccount mapToEntity(GroupAccountDto dto) {
        return dto.toEntity();
    }

    @Override
    protected GroupAccountDto mapToDto(GroupAccount entity) {
        return entity.toDto();
    }

    @Override
    protected GroupAccountDto removeExcludedFields(GroupAccountDto dto) {
        return dto;
    }

    @Override
    protected void throw404(String id) {

    }

}
