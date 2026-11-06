package com.gossamercms.notifications.senders;


import com.gossamercms.notifications.dtos.MessageRecipientDto;
import com.gossamercms.notifications.dtos.RenderedMessageDto;
import com.gossamercms.notifications.enums.MessageChannel;

public interface MessageSender {

    MessageChannel getChannel();

    void send(
            MessageRecipientDto recipient,
            RenderedMessageDto message
    );
}
