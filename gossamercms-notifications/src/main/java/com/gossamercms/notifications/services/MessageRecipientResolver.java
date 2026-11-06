package com.gossamercms.notifications.services;

import com.gossamercms.notifications.dtos.MessageAudienceDto;
import com.gossamercms.notifications.dtos.MessageRecipientDto;

import java.util.List;

public interface MessageRecipientResolver {

    List<MessageRecipientDto> resolve(
            String eventKey,
            List<MessageAudienceDto> audiences
    );
}
