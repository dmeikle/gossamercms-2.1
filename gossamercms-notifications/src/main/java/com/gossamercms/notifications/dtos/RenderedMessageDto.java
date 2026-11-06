package com.gossamercms.notifications.dtos;

import lombok.Data;

@Data
public class RenderedMessageDto {

    private String emailSubject;
    private String emailTextBody;
    private String emailHtmlBody;

    private String smsBody;

    private String ceilingbarTitle;
    private String ceilingbarBody;

}
