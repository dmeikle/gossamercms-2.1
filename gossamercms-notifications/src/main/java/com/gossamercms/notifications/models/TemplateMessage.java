package com.gossamercms.notifications.models;

import com.gossamercms.notifications.dtos.TemplateMessageDto;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateMessage implements BaseModel{
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

    public TemplateMessageDto toDto() {
        return TemplateMessageDto.builder()
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


public static final ModelMeta META = ModelMeta.builderWithId("template_messages")
    .column("id", UUID.class)
    .column("createdAt", Instant.class)
    .column("createdBy", UUID.class)
    .column("updatedAt", Instant.class)
    .column("updatedBy", UUID.class)
    .column("deletedAt", Instant.class)
    .column("deletedBy", UUID.class)
    .column("templateId", UUID.class)
    .column("locale", String.class, 5)
    .column("subject", String.class, 100)
    .column("pushTitle", String.class, 255)
    .column("pushBody", String.class, 600)
    .column("sms", String.class, 200)
    .column("htmlBody", String.class)
    .column("textBody", String.class)
    .defaultSort("id desc")
    .build();



    @Override
    public ModelMeta meta() {
        return META;
    }

    @Override
    public ModelMeta metaOf() {
        return BaseModel.super.metaOf();
    }

}
