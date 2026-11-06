package com.gossamercms.notifications.models;

import com.gossamercms.notifications.dtos.RequestLogDto;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;
import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestLog implements BaseModel{
    private UUID id;
    private Instant createdAt;
    private UUID serverKey;
    private String eventKey;
    private Map<String, Object> requestBody;
    private Long processingTime;
    private UUID correlationId;
    private String logLevel;

    public RequestLogDto toDto() {
        return RequestLogDto.builder()
            .id(id)
            .createdAt(createdAt)
            .serverKey(serverKey)
            .eventKey(eventKey)
            .requestBody(requestBody)
            .processingTime(processingTime)
            .correlationId(correlationId)
            .logLevel(logLevel)
            .build();
    }


public static final ModelMeta META = ModelMeta.builderWithId("request_logs")
    .column("id", UUID.class)
    .column("createdAt", Instant.class)
    .column("serverKey", UUID.class)
    .column("eventKey", String.class, 45)
    .column("requestBody", Map.class)
    .column("processingTime", Long.class)
    .column("correlationId", UUID.class)
    .column("logLevel", String.class, 7)
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
