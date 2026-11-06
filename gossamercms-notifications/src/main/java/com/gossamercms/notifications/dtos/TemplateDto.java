package com.gossamercms.notifications.dtos;

import com.gossamercms.notifications.models.Template;
import com.gossamercms.mvc.data.DtoWithId;
import com.gossamercms.mvc.helpers.annotations.JsonColumn;
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
public class TemplateDto implements DtoWithId {
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

    public Template toEntity() {
        return Template.builder()
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
}
