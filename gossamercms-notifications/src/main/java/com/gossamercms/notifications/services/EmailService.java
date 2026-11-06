package com.gossamercms.notifications.services;


import com.gossamercms.notifications.dtos.EmailMessageDto;
import com.gossamercms.notifications.dtos.EmailableInterface;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;


    public void sendAdminNotification(
            EmailMessageDto submission,
            List<String> recipients) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(submission.getFromName() + " <" + submission.getFromAddress() + ">");
        message.setTo(recipients.toArray(new String[0]));
        message.setSubject(
              submission.getSubject()
        );

        message.setText(
               submission.getBody()
        );

        mailSender.send(message);
    }
}