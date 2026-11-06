package com.gossamercms.notifications.dtos;

import com.gossamercms.notifications.enums.MessageAudienceType;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class MessageAudienceDto {

    private MessageAudienceType type;

    private UUID userContextId;

    private Map<String, Object> filters;
}
