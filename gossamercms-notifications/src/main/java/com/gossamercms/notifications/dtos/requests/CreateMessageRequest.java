package com.gossamercms.notifications.dtos.requests;

import com.gossamercms.notifications.dtos.AudienceDto;
import com.gossamercms.notifications.dtos.MessageAudienceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMessageRequest {

    private String eventKey;

    private List<MessageAudienceDto> audiences;
    private AudienceDto audience;
    private Map<String, Object> context;

    private Map<String, Object> parameters;

    private String locale;

    private String correlationId;
}