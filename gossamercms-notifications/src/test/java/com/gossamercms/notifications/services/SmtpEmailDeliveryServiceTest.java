package com.gossamercms.notifications.services;

import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.env.MockEnvironment;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SmtpEmailDeliveryServiceTest {

    private JavaMailSender mailSender;
    private MockEnvironment environment;
    private SmtpEmailDeliveryService service;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        when(mailSender.createMimeMessage())
                .thenAnswer(i -> new MimeMessage(Session.getInstance(new Properties())));
        environment = new MockEnvironment()
                .withProperty("app.mail.from", "noreply@example.com")
                .withProperty("app.mail.from-name", "Quantum")
                .withProperty("app.mail.reply-to", "support@example.com");
        service = new SmtpEmailDeliveryService(mailSender, environment);
    }

    @Test
    void sendsMultipartMessageWithConfiguredSender() throws Exception {
        service.send("user@example.com", "Hello", "plain body", "<p>html body</p>");

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        MimeMessage sent = captor.getValue();

        InternetAddress from = (InternetAddress) sent.getFrom()[0];
        assertEquals("noreply@example.com", from.getAddress());
        assertEquals("Quantum", from.getPersonal());
        assertEquals("support@example.com", ((InternetAddress) sent.getReplyTo()[0]).getAddress());
        assertEquals("user@example.com", ((InternetAddress) sent.getAllRecipients()[0]).getAddress());
        assertEquals("Hello", sent.getSubject());
        sent.saveChanges();
        assertTrue(sent.getContentType().startsWith("multipart/"));
    }

    @Test
    void rejectsMissingRecipient() {
        assertThrows(IllegalArgumentException.class, () -> service.send(" ", "s", "b", null));
        verify(mailSender, never()).send(org.mockito.ArgumentMatchers.any(MimeMessage.class));
    }

    @Test
    void rejectsMissingFromAddress() {
        service = new SmtpEmailDeliveryService(mailSender, new MockEnvironment());
        assertThrows(IllegalStateException.class, () -> service.send("user@example.com", "s", "b", null));
    }
}
