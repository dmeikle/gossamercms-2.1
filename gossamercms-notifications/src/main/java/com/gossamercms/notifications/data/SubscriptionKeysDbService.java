package com.gossamercms.notifications.data;

import com.gossamercms.notifications.dtos.SubscriptionKeyDto;
import com.gossamercms.notifications.models.SubscriptionKey;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;


@ModuleDbService
public class SubscriptionKeysDbService extends BaseDbService<SubscriptionKey, SubscriptionKeyDto> {

    public SubscriptionKeysDbService(DataSourceManager ds) {
        super(SubscriptionKey.class, SubscriptionKeyDto.class, ds);
    }

    @Override
    protected SubscriptionKey mapToEntity(SubscriptionKeyDto dto) {
        return dto.toEntity();
    }

    @Override
    protected SubscriptionKeyDto mapToDto(SubscriptionKey entity) {
        return entity.toDto();
    }

    @Override
    protected SubscriptionKeyDto removeExcludedFields(SubscriptionKeyDto dto) {
        return dto;
    }

    @Override
    protected void throw404(String id) {

    }

}
