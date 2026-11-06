package com.gossamercms.emails.models;

import com.gossamercms.emails.dtos.ReminderTypeDto;
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
public class ReminderType implements BaseModel{
    private UUID id;
    private String name;

    public ReminderTypeDto toDto() {
        return ReminderTypeDto.builder()
            .id(id)
            .name(name)
            .build();
    }


public static final ModelMeta META = ModelMeta.builderWithId("reminder_types")
    .column("id", Integer.class)
    .column("name", String.class, 45)
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
