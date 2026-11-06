package com.gossamercms.notifications.senders;


import com.gossamercms.notifications.dtos.MessageRecipientDto;
import com.gossamercms.notifications.dtos.RenderedMessageDto;
import com.gossamercms.notifications.enums.MessageChannel;
import com.gossamercms.notifications.services.EmailDeliveryService;
import com.gossamercms.mvc.annotations.ModuleService;

@ModuleService
public class EmailMessageSender implements MessageSender {

    private final EmailDeliveryService emailDeliveryService;

    public EmailMessageSender(
            EmailDeliveryService emailDeliveryService
    ) {
        this.emailDeliveryService = emailDeliveryService;
    }

    @Override
    public MessageChannel getChannel() {
        return MessageChannel.EMAIL;
    }

    @Override
    public void send(
            MessageRecipientDto recipient,
            RenderedMessageDto message
    ) {
        if (recipient.getEmail() == null
                || recipient.getEmail().isBlank()) {
            throw new IllegalArgumentException(
                    "Recipient email address is missing"
            );
        }

        emailDeliveryService.send(
                recipient.getEmail(),
                message.getEmailSubject(),
                message.getEmailTextBody(),
                message.getEmailHtmlBody()
        );
    }
}
