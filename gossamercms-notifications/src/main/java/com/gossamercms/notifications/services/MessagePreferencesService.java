package com.gossamercms.notifications.services;


import com.gossamercms.mvc.annotations.ModuleService;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.mvc.data.QueryFilter;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.notifications.data.UserNotificationPreferencesDbService;
import com.gossamercms.notifications.dtos.UserNotificationPreferenceDto;
import com.gossamercms.notifications.enums.MessageChannel;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@ModuleService
@RequiredArgsConstructor
public class MessagePreferencesService {

    private final UserNotificationPreferencesDbService userNotificationPreferencesDbService;

    public Set<MessageChannel> getEnabledChannels(
            UUID userContextId,
            String subscriptionKey
    ) {
        if (userContextId == null) {
            throw new IllegalArgumentException("userContextId is required");
        }

        Map<String, Object> filters = Map.of(
                "userContextId", userContextId,
                "enabled", true);

        Set<MessageChannel> channels = EnumSet.noneOf(MessageChannel.class);
        int page = 1;
        ListResultset<UserNotificationPreferenceDto> preferences;
        do {
            preferences = userNotificationPreferencesDbService.getAll(
                    new QueryOptions(page, 50, filters, Map.of("id", "ASC"),
                            List.of(new QueryFilter("deletedAt", QueryFilter.Operator.IS_NULL, null))));
            for (UserNotificationPreferenceDto preference : preferences.list()) {
                channels.add(MessageChannel.valueOf(preference.getChannel()));
            }
            page++;
        } while (page <= preferences.totalPages());
        return channels;
    }
}
