package com.gossamercms.notifications.data;

import com.gossamercms.mvc.data.*;
import com.gossamercms.notifications.dtos.MessageRecipientDto;
import com.gossamercms.notifications.dtos.UserNotificationPreferenceDto;
import com.gossamercms.notifications.models.UserNotificationPreference;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.rbac.dtos.RoleDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@ModuleDbService
public class UserNotificationPreferencesDbService extends BaseDbService<UserNotificationPreference, UserNotificationPreferenceDto> {

    public UserNotificationPreferencesDbService(DataSourceManager ds) {
        super(UserNotificationPreference.class, UserNotificationPreferenceDto.class, ds);
    }

    @Override
    protected UserNotificationPreference mapToEntity(UserNotificationPreferenceDto dto) {
        return dto.toEntity();
    }

    @Override
    protected UserNotificationPreferenceDto mapToDto(UserNotificationPreference entity) {
        return entity.toDto();
    }

    @Override
    protected UserNotificationPreferenceDto removeExcludedFields(UserNotificationPreferenceDto dto) {
        return dto;
    }

    @Override
    protected void throw404(String id) {

    }

    public ListResultset<MessageRecipientDto> getRecipients(QueryOptions options) {

        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());
        String sql = """
                select u.id, uc.id as "userContextId", u.firstname, email.email, phone."numberE164" as phone,
                       coalesce(nullif(uc.metadata ->> 'locale', ''),
                                nullif(uc.metadata ->> 'language', '')) as locale
                from users u
                         inner join user_contexts uc on uc."userId" = u.id
                         left join lateral (
                    select e.email from emails e
                    where e."userContextId" = uc.id and e.status = 'ACTIVE'
                    order by e."primary" desc, e."createdAt", e.id limit 1
                    ) email on true
                         left join lateral (
                    select ut."numberE164" from user_telephone ut
                    where ut."userId" = u.id and ut.verified and ut."smsOptIn"
                    order by ut.preferred desc, ut."createdAt", ut.id limit 1
                    ) phone on true
            
                /*WHERE_CLAUSE*/
                """;

        String countSql = """
                select count(*)
                from users u
                         inner join user_contexts uc on uc."userId" = u.id
                         left join lateral (
                    select e.email from emails e
                    where e."userContextId" = uc.id and e.status = 'ACTIVE'
                    order by e."primary" desc, e."createdAt", e.id limit 1
                    ) email on true
                         left join lateral (
                    select ut."numberE164" from user_telephone ut
                    where ut."userId" = u.id and ut.verified and ut."smsOptIn"
                    order by ut.preferred desc, ut."createdAt", ut.id limit 1
                    ) phone on true
                
                /*WHERE_CLAUSE*/
                """;

        return ds.findAllBySql(
                sql,
                countSql,
                options.filters(),
                Map.of(
                        "userContextId", "ur.userContextId"
                ),
                Collections.emptyMap(),
                options.page(),
                options.size(),
                (rs, rowNum) -> MessageRecipientDto.builder()
                        .userId(rs.getObject("id", UUID.class))
                        .userContextId(rs.getObject("userContextId", UUID.class))
                        .firstName(rs.getString("firstname"))
                        .email(rs.getString("email"))
                        .phone(rs.getString("phone"))
                        .locale(rs.getString("locale"))
                        .build()

        );
    }
}
