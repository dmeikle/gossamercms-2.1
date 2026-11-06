package com.gossamercms.notifications.dtos;

import com.gossamercms.notifications.models.TemplateMessage;
import com.gossamercms.mvc.data.DtoWithId;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateMessageDto implements DtoWithId{
    private UUID id;
    private Instant createdAt;
    private UUID createdBy;
    private Instant updatedAt;
    private UUID updatedBy;

    private UUID templateId;
    private String locale;
    private String subject;
    private String pushTitle;
    private String pushBody;
    private String sms;
    private String htmlBody;
    private String textBody;

    public TemplateMessage toEntity() {
        return TemplateMessage.builder()
            .id(id)
            .createdAt(createdAt)
            .createdBy(createdBy)
            .updatedAt(updatedAt)
            .updatedBy(updatedBy)
            .templateId(templateId)
            .locale(locale)
            .subject(subject)
            .pushTitle(pushTitle)
            .pushBody(pushBody)
            .sms(sms)
            .htmlBody(htmlBody)
            .textBody(textBody)
            .build();
    }
}
