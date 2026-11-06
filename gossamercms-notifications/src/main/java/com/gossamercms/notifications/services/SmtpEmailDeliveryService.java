package com.gossamercms.notifications.services;

import com.gossamercms.mvc.annotations.ModuleService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Delivers rendered emails through the configured Spring {@link JavaMailSender} (SMTP, e.g. Brevo).
 * Sender details come from {@code app.mail.from}, {@code app.mail.from-name} and {@code app.mail.reply-to}.
 */
@ModuleService
public class SmtpEmailDeliveryService implements EmailDeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(SmtpEmailDeliveryService.class);

    private final JavaMailSender mailSender;
    private final Environment environment;

    public SmtpEmailDeliveryService(JavaMailSender mailSender, Environment environment) {
        this.mailSender = mailSender;
        this.environment = environment;
    }

    @Override
    public void send(String to, String subject, String textBody, String htmlBody) {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Recipient email address is missing");
        }
        boolean hasText = textBody != null && !textBody.isBlank();
        boolean hasHtml = htmlBody != null && !htmlBody.isBlank();
        if (!hasText && !hasHtml) {
            throw new IllegalArgumentException("Email must have a text or HTML body");
        }

        String from = environment.getProperty("app.mail.from");
        if (from == null || from.isBlank()) {
            throw new IllegalStateException("app.mail.from (MAIL_FROM) is not configured");
        }
        String fromName = environment.getProperty("app.mail.from-name");
        String replyTo = environment.getProperty("app.mail.reply-to");

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, hasText && hasHtml, StandardCharsets.UTF_8.name());

            if (fromName != null && !fromName.isBlank()) {
                helper.setFrom(from, fromName);
            } else {
                helper.setFrom(from);
            }
            if (replyTo != null && !replyTo.isBlank()) {
                helper.setReplyTo(replyTo);
            }
            helper.setTo(to);
            helper.setSubject(subject == null ? "" : subject);

            if (hasText && hasHtml) {
                helper.setText(textBody, htmlBody);
            } else if (hasHtml) {
                helper.setText(htmlBody, true);
            } else {
                helper.setText(textBody, false);
            }

            mailSender.send(message);
            logger.info("Email accepted by SMTP server. to={}, subject={}", to, subject);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailSendException("Failed to build email for " + to, e);
        }
    }
}
