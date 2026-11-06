package com.gossamercms.notifications.dtos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EmailMessageDto {
    private String fromAddress;
    private String fromName;
    private String toAddress;
    private String subject;
    private String body;

}
