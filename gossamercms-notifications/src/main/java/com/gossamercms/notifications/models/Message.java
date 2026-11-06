package com.gossamercms.notifications.models;

import com.gossamercms.notifications.dtos.MessageDto;
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
public class Message implements BaseModel{
    private UUID id;
    private String subject;
    private String message;
    private UUID userContextId;
    private Instant viewedAt;
    private UUID templateId;
    private String messageType;
    private UUID requestLogsId;
    private Instant decayDate;
    private Instant createdAt;
    private UUID createdBy;
    private Instant updatedAt;
    private UUID updatedBy;
    private Instant deletedAt;
    private UUID deletedBy;

    public MessageDto toDto() {
        return MessageDto.builder()
            .id(id)
            .createdAt(createdAt)
            .createdBy(createdBy)
            .updatedAt(updatedAt)
            .updatedBy(updatedBy)
            .subject(subject)
            .message(message)
            .userContextId(userContextId)
            .viewedAt(viewedAt)
            .templateId(templateId)
            .messageType(messageType)
            .requestLogsId(requestLogsId)
            .decayDate(decayDate)
            .build();
    }


public static final ModelMeta META = ModelMeta.builderWithId("messages")
    .column("id", UUID.class)
    .column("createdAt", Instant.class)
    .column("createdBy", UUID.class)
    .column("updatedAt", Instant.class)
    .column("updatedBy", UUID.class)
    .column("deletedAt", Instant.class)
    .column("deletedBy", UUID.class)
    .column("subject", String.class, 250)
    .column("message", String.class, 2000)
    .column("userContextId", UUID.class)
    .column("viewedAt", Instant.class)
    .column("templateId", UUID.class)
    .column("messageType", String.class, 7)
    .column("requestLogsId", UUID.class)
    .column("decayDate", Instant.class)
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
