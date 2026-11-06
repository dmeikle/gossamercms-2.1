package com.gossamercms.notifications.services;

import com.gossamercms.media.dtos.StorageObject;
import com.gossamercms.media.providers.LocalMediaStorageProvider;
import com.gossamercms.media.providers.MediaStorageProvider;
import com.gossamercms.notifications.config.MessageTemplateConfiguration;
import com.gossamercms.notifications.dtos.LoadedTemplateDto;
import com.gossamercms.notifications.dtos.RenderedMessageDto;
import com.gossamercms.notifications.exceptions.LoadTemplateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.thymeleaf.TemplateEngine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageTemplateRendererTest {
    @TempDir
    Path storage;

    @Test
    void selectsStringTemplateEngineWhenApplicationEngineIsAlsoPresent() {
        NotificationTemplateService templates = mock(NotificationTemplateService.class);
        when(templates.loadTemplate("subject.txt")).thenReturn("Subject [(${name})]");
        TemplateEngine applicationEngine = mock(TemplateEngine.class);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean("templateEngine", TemplateEngine.class, () -> applicationEngine,
                    definition -> definition.setPrimary(true));
            context.registerBean(NotificationTemplateService.class, () -> templates);
            context.register(MessageTemplateConfiguration.class, MessageTemplateRenderer.class);
            context.refresh();

            RenderedMessageDto result = context.getBean(MessageTemplateRenderer.class).render(
                    LoadedTemplateDto.builder().subject("subject.txt").build(),
                    Map.of("name", "Marie"), Locale.CANADA);

            assertEquals("Subject Marie", result.getEmailSubject());
            verifyNoInteractions(applicationEngine);
        }
    }

    @Test
    void loadsAndRendersEveryChannelFromLocalFilesAndWrapsHtmlInLayout() throws IOException {
        Files.createDirectories(storage.resolve("articles"));
        write("subject.txt", "Article [(${title})]");
        write("text.txt", "Read [(${title})]");
        write("body.html", "<p th:text=\"${title}\">placeholder</p>");
        write("layout.html", "<html><body><main th:utext=\"${htmlBody}\"></main></body></html>");
        write("sms.txt", "SMS [(${title})]");
        write("push-title.txt", "New [(${title})]");
        write("push-body.txt", "Push [(${title})]");
        LoadedTemplateDto template = LoadedTemplateDto.builder()
                .subject("articles/subject.txt").textBody("articles/text.txt")
                .htmlBody("articles/body.html").templateUrl("articles/layout.html")
                .sms("articles/sms.txt").pushTitle("articles/push-title.txt")
                .pushBody("articles/push-body.txt").build();
        RenderedMessageDto result = renderer(new LocalMediaStorageProvider(storage.toString()))
                .render(template, Map.of("title", "A & B"), Locale.CANADA);

        assertEquals("Article A & B", result.getEmailSubject());
        assertEquals("Read A & B", result.getEmailTextBody());
        assertEquals("<html><body><main><p>A &amp; B</p></main></body></html>", result.getEmailHtmlBody());
        assertEquals("SMS A & B", result.getSmsBody());
        assertEquals("New A & B", result.getCeilingbarTitle());
        assertEquals("Push A & B", result.getCeilingbarBody());
        assertEquals("articles/body.html", template.getHtmlBody());
    }

    @Test
    void usesConfiguredProviderAndClosesUtf8Stream() throws IOException {
        MediaStorageProvider provider = mock(MediaStorageProvider.class);
        byte[] content = "Bonjour [[${name}]] \u00e9".getBytes(StandardCharsets.UTF_8);
        InputStream input = spy(new ByteArrayInputStream(content));
        when(provider.load("articles/sms.txt"))
                .thenReturn(new StorageObject("articles/sms.txt", input, content.length));

        RenderedMessageDto result = renderer(provider).render(
                LoadedTemplateDto.builder().sms("articles/sms.txt").build(),
                Map.of("name", "Marie"), Locale.CANADA_FRENCH);

        assertEquals("Bonjour Marie \u00e9", result.getSmsBody());
        verify(input).close();
        verify(provider).load("articles/sms.txt");
        verifyNoMoreInteractions(provider);
    }

    @Test
    void rendersHtmlWithoutLayoutAndLayoutWithoutBody() throws IOException {
        Files.createDirectories(storage.resolve("articles"));
        write("body.html", "<b th:text=\"${name}\"></b>");
        write("layout.html", "<h1 th:text=\"${name}\"></h1>");
        MessageTemplateRenderer renderer = renderer(new LocalMediaStorageProvider(storage.toString()));

        assertEquals("<b>Marie</b>", renderer.render(
                LoadedTemplateDto.builder().htmlBody("articles/body.html").build(),
                Map.of("name", "Marie"), Locale.CANADA).getEmailHtmlBody());
        assertEquals("<h1>Marie</h1>", renderer.render(
                LoadedTemplateDto.builder().templateUrl("articles/layout.html").build(),
                Map.of("name", "Marie"), Locale.CANADA).getEmailHtmlBody());
    }

    @Test
    void skipsAbsentPaths() {
        MediaStorageProvider provider = mock(MediaStorageProvider.class);
        RenderedMessageDto result = renderer(provider).render(
                LoadedTemplateDto.builder().subject(" ").sms("").build(), Map.of(), Locale.CANADA);

        assertNull(result.getEmailSubject());
        assertNull(result.getEmailTextBody());
        assertNull(result.getEmailHtmlBody());
        assertNull(result.getSmsBody());
        assertNull(result.getCeilingbarTitle());
        assertNull(result.getCeilingbarBody());
        verifyNoInteractions(provider);
    }

    @Test
    void propagatesProviderFailureInsteadOfRenderingPath() {
        MediaStorageProvider provider = mock(MediaStorageProvider.class);
        IllegalStateException failure = new IllegalStateException("Storage unavailable");
        when(provider.load("articles/sms.txt")).thenThrow(failure);

        assertSame(failure, assertThrows(IllegalStateException.class, () -> renderer(provider).render(
                LoadedTemplateDto.builder().sms("articles/sms.txt").build(), Map.of(), Locale.CANADA)));
    }

    @Test
    void readFailurePreservesCauseAndClosesStream() throws IOException {
        MediaStorageProvider provider = mock(MediaStorageProvider.class);
        InputStream input = mock(InputStream.class);
        IOException failure = new IOException("Unable to read");
        when(input.readAllBytes()).thenThrow(failure);
        when(provider.load("articles/sms.txt")).thenReturn(new StorageObject("articles/sms.txt", input, 0));

        LoadTemplateException exception = assertThrows(LoadTemplateException.class,
                () -> new NotificationTemplateService(provider).loadTemplate("articles/sms.txt"));

        assertSame(failure, exception.getCause());
        verify(input).close();
    }

    @Test
    void rejectsUnimplementedProviderReturningNull() {
        MediaStorageProvider provider = mock(MediaStorageProvider.class);
        assertThrows(LoadTemplateException.class,
                () -> new NotificationTemplateService(provider).loadTemplate("articles/sms.txt"));
    }

    private MessageTemplateRenderer renderer(MediaStorageProvider provider) {
        return new MessageTemplateRenderer(new MessageTemplateConfiguration().messageTemplateEngine(),
                new NotificationTemplateService(provider));
    }

    private void write(String name, String content) throws IOException {
        Files.writeString(storage.resolve("articles").resolve(name), content, StandardCharsets.UTF_8);
    }
}
