package com.gossamercms.media.services;

import com.gossamercms.mvc.annotations.ModuleService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.ByteArrayOutputStream;

@ModuleService
public class PdfGeneratorService {

    public byte[] generate(String html) {
        try (ByteArrayOutputStream output =
                     new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();

            builder.withHtmlContent(html, null);
            builder.toStream(output);
            builder.run();

            return output.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to generate PDF", e
            );
        }
    }
}
