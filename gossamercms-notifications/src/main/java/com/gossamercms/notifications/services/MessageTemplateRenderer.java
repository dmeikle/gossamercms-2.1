package com.gossamercms.notifications.services;

import com.gossamercms.notifications.dtos.LoadedTemplateDto;
import com.gossamercms.notifications.dtos.RenderedMessageDto;
import com.gossamercms.mvc.annotations.ModuleService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.TemplateSpec;

import java.util.Locale;
import java.util.Map;

@ModuleService
public class MessageTemplateRenderer {

    private final TemplateEngine templateEngine;
    private final NotificationTemplateService templateService;

    public MessageTemplateRenderer(@Qualifier("messageTemplateEngine") TemplateEngine templateEngine,
                                   NotificationTemplateService templateService) {
        this.templateEngine = templateEngine;
        this.templateService = templateService;
    }

    public RenderedMessageDto render(
            LoadedTemplateDto template,
            Map<String, Object> parameters,
            Locale locale
    ) {
        Context context = new Context(locale);

        if (parameters != null) {
            context.setVariables(parameters);
        }

        RenderedMessageDto result = new RenderedMessageDto();

        result.setEmailSubject(renderText(
                template.getSubject(),
                context
        ));

        result.setEmailTextBody(renderText(
                template.getTextBody(),
                context
        ));

        String htmlBody = renderHtml(
                template.getHtmlBody(),
                context
        );
        if (template.getTemplateUrl() != null && !template.getTemplateUrl().isBlank()) {
            context.setVariable("htmlBody", htmlBody);
            result.setEmailHtmlBody(renderHtml(template.getTemplateUrl(), context));
        } else {
            result.setEmailHtmlBody(htmlBody);
        }

        result.setSmsBody(renderText(
                template.getSms(),
                context
        ));

        result.setCeilingbarTitle(renderText(
                template.getPushTitle(),
                context
        ));

        result.setCeilingbarBody(renderText(
                template.getPushBody(),
                context
        ));

        return result;
    }

    private String renderHtml(String template, Context context) {
        return render(template, context, TemplateMode.HTML);
    }

    private String renderText(String template, Context context) {
        return render(template, context, TemplateMode.TEXT);
    }

    private String render(
            String storageKey,
            Context context,
            TemplateMode mode
    ) {
        if (storageKey == null || storageKey.isBlank()) {
            return null;
        }
        String content = templateService.loadTemplate(storageKey);
        System.out.println(content);
        TemplateSpec templateSpec = new TemplateSpec(content, mode);

        return templateEngine.process(templateSpec, context);
    }
}