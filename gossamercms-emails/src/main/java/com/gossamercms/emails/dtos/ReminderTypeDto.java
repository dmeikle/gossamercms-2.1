package com.gossamercms.emails.dtos;

import com.gossamercms.emails.models.ReminderType;
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
public class ReminderTypeDto implements DtoWithId{
    private UUID id;
    private String name;

    public ReminderType toEntity() {
        return ReminderType.builder()
            .id(id)
            .name(name)
            .build();
    }
}
