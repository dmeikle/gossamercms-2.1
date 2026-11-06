package com.gossamercms.notifications.data;

import com.gossamercms.notifications.dtos.LoadedTemplateDto;
import com.gossamercms.notifications.dtos.TemplateDto;
import com.gossamercms.notifications.models.Template;
import com.gossamercms.notifications.services.MessagesService;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.*;

import java.util.Map;
import com.gossamercms.mvc.util.JsonUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.postgresql.core.Oid.UUID;

@ModuleDbService
public class TemplatesDbService extends BaseDbService<Template, TemplateDto> {

    public TemplatesDbService(DataSourceManager ds) {
        super(Template.class, TemplateDto.class, ds);
    }

    @Override
    protected Template mapToEntity(TemplateDto dto) {
        return dto.toEntity();
    }

    @Override
    protected TemplateDto mapToDto(Template entity) {
        return entity.toDto();
    }

    @Override
    protected TemplateDto removeExcludedFields(TemplateDto dto) {
        return dto;
    }

    public LoadedTemplateDto getLoadedTemplate(QueryOptions options) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());
        String sql = """
            select 
                t.id,
                name,
                "templateUrl",
                "eventKey",
                variables,
                "subscriptionKey",
                "isSubscriber",
                priority,
                ttl,
                "buildCommand",
                subject,
                "pushTitle",
                "pushBody",
                sms,
                "htmlBody",
                "textBody"
           from templates t
           inner join template_messages tm on t.id = tm."templateId"
           where t."eventKey" = :eventKey and locale = :locale and t."deletedAt" is null limit 1
            """;

        return ds.findOneBySql(
                        sql,
                        options.filters(),
                        (rs, rowNum) -> LoadedTemplateDto.builder()
                                .id(rs.getObject("id", UUID.class))
                                .name(rs.getString("name"))
                                .templateUrl(rs.getString("templateUrl"))
                                .eventKey(rs.getString("eventKey"))
                                .variables(readVariables(rs))
                                .subscriptionKey(rs.getString("subscriptionKey"))
                                .isSubscriber(rs.getBoolean("isSubscriber"))
                                .priority(rs.getString("priority"))
                                .ttl(rs.getInt("ttl"))
                                .buildCommand(rs.getString("buildCommand"))
                                .subject(rs.getString("subject"))
                                .pushTitle(rs.getString("pushTitle"))
                                .pushBody(rs.getString("pushBody"))
                                .sms(rs.getString("sms"))
                                .htmlBody(rs.getString("htmlBody"))
                                .textBody(rs.getString("textBody"))
                                .build()
                );

    }

    private static Map<String, Object> readVariables(ResultSet rs)
            throws SQLException {
        String json = rs.getString("variables");
        if (json == null) {
            return null;
        }

        try {
            return JsonUtil.OBJECT_MAPPER.readValue(
                    json,
                    new TypeReference<Map<String, Object>>() {}
            );
        } catch (JacksonException e) {
            throw new SQLException(
                    "Unable to deserialize template variables", e);
        }
    }
}
