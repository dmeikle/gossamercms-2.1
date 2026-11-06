package com.gossamercms.notifications.dtos;

import com.gossamercms.mvc.helpers.annotations.JsonColumn;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class LoadedTemplateDto {
    private UUID id;
    private String subject;
    private String pushTitle;
    private String pushBody;
    private String sms;
    private String htmlBody;
    private String textBody;
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
}
