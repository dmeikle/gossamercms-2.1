package com.gossamercms.notifications.senders;

import com.gossamercms.notifications.dtos.MessageRecipientDto;
import com.gossamercms.notifications.dtos.RenderedMessageDto;
import com.gossamercms.notifications.enums.MessageChannel;
import com.gossamercms.notifications.services.CeilingbarDeliveryService;
import com.gossamercms.mvc.annotations.ModuleService;

@ModuleService
public class CeilingbarMessageSender implements MessageSender {

    private final CeilingbarDeliveryService ceilingbarDeliveryService;

    public CeilingbarMessageSender(
            CeilingbarDeliveryService ceilingbarDeliveryService
    ) {
        this.ceilingbarDeliveryService = ceilingbarDeliveryService;
    }

    @Override
    public MessageChannel getChannel() {
        return MessageChannel.IN_APP;
    }

    @Override
    public void send(
            MessageRecipientDto recipient,
            RenderedMessageDto message
    ) {
        ceilingbarDeliveryService.send(
                recipient.getUserId(),
                message.getCeilingbarTitle(),
                message.getCeilingbarBody()
        );
    }
}
