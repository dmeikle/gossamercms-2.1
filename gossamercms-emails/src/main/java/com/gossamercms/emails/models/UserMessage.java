package com.gossamercms.emails.models;

import com.gossamercms.emails.dtos.UserMessageDto;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMessage implements BaseModel{
    private UUID id;
    private Instant createdAt;
    private UUID createdBy;
    private Instant updatedAt;
    private UUID updatedBy;
    private Instant deletedAt;
    private UUID deletedBy;
    private UUID accountMappingsId;
    private String comments;
    private String status;
    private Instant answeredAt;
    private UUID answeredBy;
    private String topic;
    private String username;

    public UserMessageDto toDto() {
        return UserMessageDto.builder()
            .id(id)
            .createdAt(createdAt)
            .createdBy(createdBy)
            .updatedAt(updatedAt)
            .updatedBy(updatedBy)
            .deletedAt(deletedAt)
            .deletedBy(deletedBy)
            .accountMappingsId(accountMappingsId)
            .comments(comments)
            .status(status)
            .answeredAt(answeredAt)
            .answeredBy(answeredBy)
            .topic(topic)
            .username(username)
            .build();
    }


public static final ModelMeta META = ModelMeta.builderWithId("user_messages")
    .column("id", UUID.class)
    .column("createdAt", Instant.class)
    .column("createdBy", UUID.class)
    .column("updatedAt", Instant.class)
    .column("updatedBy", UUID.class)
    .column("deletedAt", Instant.class)
    .column("deletedBy", UUID.class)
    .column("accountMappingsId", UUID.class)
    .column("comments", String.class, 1000)
    .column("status", String.class, 9)
    .column("answeredAt", Instant.class)
    .column("answeredBy", UUID.class)
    .column("topic", String.class, 255)
    .column("username", String.class, 45)
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
