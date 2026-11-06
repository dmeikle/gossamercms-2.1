package com.gossamercms.notifications.models;

import com.gossamercms.notifications.dtos.MessageLogDto;
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
public class MessageLog implements BaseModel{
    private UUID id;
    private Instant createdAt;
    private UUID createdBy;
    private Instant updatedAt;
    private UUID updatedBy;
    private Instant deletedAt;
    private UUID deletedBy;
    private UUID messagesId;
    private String result;
    private String message;
    private String notificationTarget;

    public MessageLogDto toDto() {
        return MessageLogDto.builder()
            .id(id)
            .createdAt(createdAt)
            .createdBy(createdBy)
            .updatedAt(updatedAt)
            .updatedBy(updatedBy)
            .deletedAt(deletedAt)
            .deletedBy(deletedBy)
            .messagesId(messagesId)
            .result(result)
            .message(message)
            .notificationTarget(notificationTarget)
            .build();
    }


public static final ModelMeta META = ModelMeta.builderWithId("message_logs")
    .column("id", UUID.class)
    .column("createdAt", Instant.class)
    .column("createdBy", UUID.class)
    .column("updatedAt", Instant.class)
    .column("updatedBy", UUID.class)
    .column("deletedAt", Instant.class)
    .column("deletedBy", UUID.class)
    .column("messagesId", UUID.class)
    .column("result", String.class, 10)
    .column("message", String.class, 200)
    .column("notificationTarget", String.class, 45)
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
