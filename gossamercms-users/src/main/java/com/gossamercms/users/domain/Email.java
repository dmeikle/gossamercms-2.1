package com.gossamercms.users.domain;

import com.gossamercms.mvc.annotations.ModuleModel;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import com.gossamercms.users.api.EmailDto;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@ModuleModel
@Data
@Builder
public class Email implements BaseModel {

    private UUID id;
    private UUID userContextId;
    private String email;
    private boolean primary;
    private String status;
    private Instant createdAt;

    public static final ModelMeta META = ModelMeta.builder()
            .table("emails")
            .datasource("postgres")
            .column("id", UUID.class)                // primary key
            .column("userContextId", UUID.class)
            .column("email", String.class)
            .column("primary", Boolean.class)
            .column("status", String.class)
            .column("createdAt", Instant.class)
            .defaultSort("email DESC")
            .build();

    @Override
    public ModelMeta meta() {
        return META;
    }

    public EmailDto toDto() {
        return EmailDto.builder()
                .id(id)
                .userContextId(userContextId)
                .email(email)
                .primary(primary)
                .status(status)
                .createdAt(createdAt)
                .build();
    }

    public static Email fromDto(EmailDto dto) {
        return Email.builder()
                .id(dto.getId())
                .userContextId(dto.getUserContextId())
                .email(dto.getEmail())
                .primary(dto.isPrimary())
                .status(dto.getStatus())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}