package com.gossamercms.media.services;

import com.gossamercms.media.dtos.StorageObject;
import com.gossamercms.media.providers.MediaStorageProvider;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PdfTemplateServiceTest {
    private final MediaStorageProvider storage = mock(MediaStorageProvider.class);
    private final PdfGeneratorService pdfGenerator = mock(PdfGeneratorService.class);
    private final PdfTemplateService service = new PdfTemplateService(pdfGenerator, storage);

    @Test
    void rendersStorageContentForPdfAndHtml() {
        String template = "<p th:text=\"${name}\">placeholder</p>";
        when(storage.load("ticket.html")).thenAnswer(invocation -> object(template));
        byte[] pdf = {1, 2, 3};
        when(pdfGenerator.generate("<p>&lt;Ada&gt;</p>")).thenReturn(pdf);

        assertEquals("<p>&lt;Ada&gt;</p>", service.generateHtml("ticket.html", Map.of("name", "<Ada>")));
        assertSame(pdf, service.generatePdf("ticket.html", Map.of("name", "<Ada>")));
        verify(storage, times(2)).load("ticket.html");
        verify(pdfGenerator).generate("<p>&lt;Ada&gt;</p>");
    }

    @Test
    void reloadsChangedStorageContent() {
        when(storage.load("ticket.html")).thenReturn(object("<p>first</p>"), object("<p>second</p>"));
        assertEquals("<p>first</p>", service.generateHtml("ticket.html", null));
        assertEquals("<p>second</p>", service.generateHtml("ticket.html", null));
    }

    @Test
    void closesStorageStream() throws IOException {
        InputStream input = spy(new ByteArrayInputStream("<p>test</p>".getBytes(StandardCharsets.UTF_8)));
        when(storage.load("ticket.html")).thenReturn(new StorageObject("ticket.html", input, 11));
        service.generateHtml("ticket.html", null);
        verify(input).close();
    }

    @Test
    void reportsMissingAndUnreadableContent() {
        assertThrows(IllegalArgumentException.class, () -> service.generateHtml(" ", null));
        assertThrows(IllegalStateException.class, () -> service.generateHtml("missing", null));
        when(storage.load("empty")).thenReturn(new StorageObject("empty", null, 0));
        assertThrows(IllegalStateException.class, () -> service.generateHtml("empty", null));
        InputStream broken = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("read failed");
            }
        };
        when(storage.load("broken")).thenReturn(new StorageObject("broken", broken, 0));
        var exception = assertThrows(UncheckedIOException.class, () -> service.generateHtml("broken", null));
        assertEquals("read failed", exception.getCause().getMessage());
    }

    private static StorageObject object(String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        return new StorageObject("ticket.html", new ByteArrayInputStream(bytes), bytes.length);
    }
}
