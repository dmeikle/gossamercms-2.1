package com.gossamercms.notifications.models;

import com.gossamercms.notifications.dtos.TemplateDto;
import com.gossamercms.mvc.helpers.annotations.JsonColumn;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Template implements BaseModel {
    private UUID id;
    private Instant createdAt;
    private UUID createdBy;
    private Instant updatedAt;
    private UUID updatedBy;
    private String name;
    private String templateUrl;
    private String eventKey;
    @JsonColumn
    private Map<String, Object> variables;
    private String subscriptionKey;
    private Boolean isSubscriber;
    private String priority;
    private Integer ttl;
    private String buildCommand;

    public TemplateDto toDto() {
        return TemplateDto.builder()
                .id(id)
                .createdAt(createdAt)
                .createdBy(createdBy)
                .updatedAt(updatedAt)
                .updatedBy(updatedBy)
                .name(name)
                .templateUrl(templateUrl)
                .eventKey(eventKey)
                .variables(variables)
                .subscriptionKey(subscriptionKey)
                .isSubscriber(isSubscriber)
                .priority(priority)
                .ttl(ttl)
                .buildCommand(buildCommand)
                .build();
    }

    public static final ModelMeta META = ModelMeta.builderWithId("templates")
            .column("createdAt", Instant.class)
            .column("createdBy", UUID.class)
            .column("updatedAt", Instant.class)
            .column("updatedBy", UUID.class)
            .column("deletedAt", Instant.class)
            .column("deletedBy", UUID.class)
            .column("name", String.class, 255)
            .column("templateUrl", String.class, 200)
            .column("xliffUrl", String.class, 200)
            .column("eventKey", String.class, 45)
            .column("variables", Map.class)
            .column("subscriptionKey", String.class, 20)
            .column("isSubscriber", Boolean.class)
            .column("priority", String.class, 9)
            .column("ttl", Integer.class)
            .column("buildCommand", String.class, 45)
            .defaultSort("id desc")
            .build();

    @Override
    public ModelMeta meta() {
        return META;
    }
}
