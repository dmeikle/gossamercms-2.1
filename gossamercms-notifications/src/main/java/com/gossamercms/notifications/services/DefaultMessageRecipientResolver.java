package com.gossamercms.notifications.services;

import com.gossamercms.mvc.data.QueryFilter;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.notifications.data.UserNotificationPreferencesDbService;
import com.gossamercms.notifications.dtos.MessageAudienceDto;
import com.gossamercms.notifications.dtos.MessageRecipientDto;
import com.gossamercms.mvc.annotations.ModuleService;
import com.gossamercms.notifications.enums.MessageAudienceType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ModuleService
@RequiredArgsConstructor
public class DefaultMessageRecipientResolver implements MessageRecipientResolver {

    private final UserNotificationPreferencesDbService userNotificationPreferencesDbService;
//    private static final String RECIPIENT_SQL = """
//            select u.id, uc.id as "userContextId", u.firstname, email.email, phone."numberE164" as phone,
//                   coalesce(nullif(uc.metadata ->> 'locale', ''),
//                            nullif(uc.metadata ->> 'language', '')) as locale
//            from users u
//            left join lateral (
//                select e.email from emails e
//                where e."userId" = u.id and e.status = 'ACTIVE'
//                order by e."primary" desc, e."createdAt", e.id limit 1
//            ) email on true
//            left join lateral (
//                select p."numberE164" from user_telephone p
//                where p."userId" = u.id and p.verified and p."smsOptIn"
//                order by p.preferred desc, p."createdAt", p.id limit 1
//            ) phone on true
//            """;

    private static final RowMapper<MessageRecipientDto> MAPPER = (rs, rowNum) -> {
        MessageRecipientDto recipient = new MessageRecipientDto();
        recipient.setUserId(rs.getObject("id", UUID.class));
        recipient.setUserContextId(rs.getObject("userContextId", UUID.class));
        recipient.setFirstName(rs.getString("firstname"));
        recipient.setEmail(rs.getString("email"));
        recipient.setPhone(rs.getString("phone"));
        recipient.setLocale(rs.getString("locale"));
        return recipient;
    };
//
//    private final DataSourceManager dataSources;
//
//    public DefaultMessageRecipientResolver(DataSourceManager dataSources) {
//        this.dataSources = dataSources;
//    }

    @Override
    public List<MessageRecipientDto> resolve(String eventKey, List<MessageAudienceDto> audiences) {
        if (eventKey == null || eventKey.isBlank()) {
            throw new IllegalArgumentException("eventKey is required");
        }
        if (audiences == null || audiences.isEmpty()) {
            throw new IllegalArgumentException("At least one audience is required");
        }
        // Validate every audience before executing any queries.
        for (MessageAudienceDto audience : audiences) {
            validateAudience(audience);
        }
//        var source = dataSources.get("postgres");
//        if (source == null) {
//            throw new IllegalStateException("The postgres datasource is not registered");
//        }

//        Map<UUID, MessageRecipientDto> recipients = new LinkedHashMap<>();
//        for (MessageAudienceDto audience : audiences) {

//                case EVENT_SUBSCRIBERS -> sql.append("""
//                        inner join user_contexts uc on uc."userId" = u.id
//                        inner join account_mappings am on am."userContextId" = uc.id
//                        inner join subscribers s on s."accountMappingsId" = am.id
//                        inner join "SubscriptionKeys" sk on sk.id = s."subscriptionKeysId"
//                        where s."deletedAt" is null and sk."deletedAt" is null
//                          and (am."expiresAt" is null or am."expiresAt" > current_timestamp)
//                          and exists (
//                              select 1 from templates t
//                              where t."subscriptionKey" = sk."subscriptionKey"
//                                and t."eventKey" = :eventKey and t."deletedAt" is null
//                          )
//                        """);
//            }
//            if (audience.getFilters() != null) {
//                int index = 0;
//                for (var filter : audience.getFilters().entrySet()) {
//                    sql.append(" and uc.metadata ->> :filterKey").append(index)
//                            .append(" = :filterValue").append(index);
//                    params.addValue("filterKey" + index, filter.getKey());
//                    params.addValue("filterValue" + index, filter.getValue().toString());
//                    index++;
//                }
//            }
//            sql.append(" order by u.id, locale nulls last, uc.id");
//            var matches = source.getNamedParameterJdbcTemplate().query(sql.toString(), params, MAPPER);
//            for (MessageRecipientDto recipient : matches) {
//                recipients.putIfAbsent(recipient.getUserContextId(), recipient);
//            }
//        }
        //get all userContextIds from audiences
        List<UUID> userContextIds = audiences.stream().filter(audience -> audience.getType() == MessageAudienceType.USER)
                .map(MessageAudienceDto::getUserContextId)
                .collect(Collectors.toList());

        Map<UUID, MessageRecipientDto> recipients = userNotificationPreferencesDbService.getRecipients(QueryOptions.builder()
                        .queryFilters(List.of(
                                new QueryFilter("userContextId", QueryFilter.Operator.IN, userContextIds)
                        ))
                        .build()
                    )
        .list().stream().collect(Collectors.toMap(MessageRecipientDto::getUserContextId, r -> r));

        return new ArrayList<>(recipients.values());
    }

    private static void validateAudience(MessageAudienceDto audience) {
        if (audience == null || audience.getType() == null) {
            throw new IllegalArgumentException("Each audience must have a type");
        }
        switch (audience.getType()) {
            case USER -> {
                if (audience.getUserContextId() == null) {
                    throw new IllegalArgumentException("USER audiences require userContextId");
                }
            }
            case EVENT_SUBSCRIBERS -> {
                if (audience.getUserContextId() != null) {
                    throw new IllegalArgumentException("EVENT_SUBSCRIBERS does not accept userContextId");
                }
            }
        }
        if (audience.getFilters() != null) {
            for (var filter : audience.getFilters().entrySet()) {
                Object value = filter.getValue();
                if (filter.getKey() == null || filter.getKey().isBlank()
                        || !(value instanceof String || value instanceof Number || value instanceof Boolean)) {
                    throw new IllegalArgumentException(
                            "Audience filters require nonblank metadata keys and scalar values");
                }
            }
        }
    }
}
