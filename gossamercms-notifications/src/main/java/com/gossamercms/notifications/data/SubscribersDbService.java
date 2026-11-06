package com.gossamercms.notifications.data;

import com.gossamercms.notifications.dtos.SubscriberDto;
import com.gossamercms.notifications.models.Subscriber;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;


@ModuleDbService
public class SubscribersDbService extends BaseDbService<Subscriber, SubscriberDto> {

    public SubscribersDbService(DataSourceManager ds) {
        super(Subscriber.class, SubscriberDto.class, ds);
    }

    @Override
    protected Subscriber mapToEntity(SubscriberDto dto) {
        return dto.toEntity();
    }

    @Override
    protected SubscriberDto mapToDto(Subscriber entity) {
        return entity.toDto();
    }

    @Override
    protected SubscriberDto removeExcludedFields(SubscriberDto dto) {
        return dto;
    }

    @Override
    protected void throw404(String id) {

    }

}
