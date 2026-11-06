package com.gossamercms.emails.dtos;

import com.gossamercms.emails.models.Reminder;
import com.gossamercms.mvc.data.DtoWithId;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderDto implements DtoWithId{
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

    public Reminder toEntity() {
        return Reminder.builder()
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
}
