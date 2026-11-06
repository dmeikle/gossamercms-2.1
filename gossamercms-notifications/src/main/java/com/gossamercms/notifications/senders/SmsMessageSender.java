package com.gossamercms.notifications.senders;


import com.gossamercms.notifications.dtos.MessageRecipientDto;
import com.gossamercms.notifications.dtos.RenderedMessageDto;
import com.gossamercms.notifications.enums.MessageChannel;
import com.gossamercms.notifications.services.SmsDeliveryService;
import com.gossamercms.mvc.annotations.ModuleService;

@ModuleService
public class SmsMessageSender implements MessageSender {

    private final SmsDeliveryService smsDeliveryService;

    public SmsMessageSender(SmsDeliveryService smsDeliveryService) {
        this.smsDeliveryService = smsDeliveryService;
    }

    @Override
    public MessageChannel getChannel() {
        return MessageChannel.SMS;
    }

    @Override
    public void send(
            MessageRecipientDto recipient,
            RenderedMessageDto message
    ) {
        if (recipient.getPhone() == null
                || recipient.getPhone().isBlank()) {
            throw new IllegalArgumentException(
                    "Recipient phone number is missing"
            );
        }

        smsDeliveryService.send(
                recipient.getPhone(),
                message.getSmsBody()
        );
    }
}
