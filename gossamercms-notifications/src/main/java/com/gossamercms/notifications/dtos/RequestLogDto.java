package com.gossamercms.notifications.dtos;

import com.gossamercms.notifications.models.RequestLog;
import com.gossamercms.mvc.data.DtoWithId;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestLogDto implements DtoWithId{
    private UUID id;
    private Instant createdAt;
    private UUID serverKey;
    private String eventKey;
    private Map<String, Object> requestBody;
    private Long processingTime;
    private UUID correlationId;
    private String logLevel;

    public RequestLog toEntity() {
        return RequestLog.builder()
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
}
