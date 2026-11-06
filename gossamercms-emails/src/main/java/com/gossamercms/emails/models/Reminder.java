package com.gossamercms.emails.models;

import com.gossamercms.emails.dtos.ReminderDto;
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
public class Reminder implements BaseModel{
    private UUID id;
    private Instant createdAt;
    private UUID createdBy;
    private Instant updatedAt;
    private UUID updatedBy;
    private Instant deletedAt;
    private UUID deletedBy;
    private Integer reminderTypesId;
    private UUID accountMappingsId;
    private Instant notifyAt;
    private String notifyAtLabel;
    private Boolean notificationSent;
    private UUID proxyId;
    private String scheduleName;
    private Integer sms;
    private Integer email;

    public ReminderDto toDto() {
        return ReminderDto.builder()
            .id(id)
            .createdAt(createdAt)
            .createdBy(createdBy)
            .updatedAt(updatedAt)
            .updatedBy(updatedBy)
            .deletedAt(deletedAt)
            .deletedBy(deletedBy)
            .reminderTypesId(reminderTypesId)
            .accountMappingsId(accountMappingsId)
            .notifyAt(notifyAt)
            .notifyAtLabel(notifyAtLabel)
            .notificationSent(notificationSent)
            .proxyId(proxyId)
            .scheduleName(scheduleName)
            .sms(sms)
            .email(email)
            .build();
    }


public static final ModelMeta META = ModelMeta.builderWithId("reminders")
    .column("id", UUID.class)
    .column("createdAt", Instant.class)
    .column("createdBy", UUID.class)
    .column("updatedAt", Instant.class)
    .column("updatedBy", UUID.class)
    .column("deletedAt", Instant.class)
    .column("deletedBy", UUID.class)
    .column("reminderTypesId", Integer.class)
    .column("accountMappingsId", UUID.class)
    .column("notifyAt", Instant.class)
    .column("notifyAtLabel", String.class, 100)
    .column("notificationSent", Boolean.class)
    .column("proxyId", UUID.class)
    .column("scheduleName", String.class, 100)
    .column("sms", Integer.class)
    .column("email", Integer.class)
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
