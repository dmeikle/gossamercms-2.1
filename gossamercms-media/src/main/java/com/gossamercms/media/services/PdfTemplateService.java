package com.gossamercms.media.services;


import com.gossamercms.media.dtos.StorageObject;
import com.gossamercms.media.providers.MediaStorageProvider;
import com.gossamercms.mvc.annotations.ModuleService;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

@ModuleService
public class PdfTemplateService {

    private final SpringTemplateEngine templateEngine;
    private final PdfGeneratorService pdfGeneratorService;
    private final MediaStorageProvider mediaStorageProvider;

    public PdfTemplateService(
            PdfGeneratorService pdfGeneratorService,
            MediaStorageProvider mediaStorageProvider
    ) {
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);
        this.templateEngine = new SpringTemplateEngine();
        this.templateEngine.setTemplateResolver(resolver);
        this.pdfGeneratorService = pdfGeneratorService;
        this.mediaStorageProvider = mediaStorageProvider;
    }

    /**
     * Generates a PDF using a Thymeleaf template.
     *
     * @param templateName storage key for the HTML template
     * @param variables    Template variables
     * @return PDF document bytes
     */
    public byte[] generatePdf(
            String templateName,
            Map<String, Object> variables) {

        return pdfGeneratorService.generate(generateHtml(templateName, variables));
    }

    /**
     * Generates HTML without converting it to PDF.
     * Useful for previewing templates.
     */
    public String generateHtml(
            String templateName,
            Map<String, Object> variables) {

        Context context = new Context(Locale.getDefault());

        if (variables != null) {
            variables.forEach(context::setVariable);
        }

        return templateEngine.process(loadTemplate(templateName), context);
    }


    private String loadTemplate(String storageKey) {
        if (storageKey == null || storageKey.isBlank()) {
            throw new IllegalArgumentException("storageKey is required");
        }

        StorageObject storageObject = mediaStorageProvider.load(storageKey);
        if (storageObject == null || storageObject.getInputStream() == null) {
            throw new IllegalStateException("No template returned for storage key: " + storageKey);
        }

        try (storageObject) {
            return new String(
                    storageObject.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read template: " + storageKey, e);
        }
    }
}