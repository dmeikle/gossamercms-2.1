package com.gossamercms.notifications.services;

import com.gossamercms.notifications.data.TemplatesDbService;
import com.gossamercms.notifications.dtos.LoadedTemplateDto;
import com.gossamercms.notifications.dtos.MessageDto;
import com.gossamercms.notifications.dtos.MessageRecipientDto;
import com.gossamercms.notifications.dtos.RenderedMessageDto;
import com.gossamercms.notifications.enums.MessageChannel;
import com.gossamercms.notifications.dtos.requests.CreateMessageRequest;
import com.gossamercms.notifications.senders.MessageSender;
import com.gossamercms.mvc.annotations.ModuleService;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.mvc.jwt.CurrentJwtUser;

import com.gossamercms.security.jwt.JwtUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@ModuleService
public class MessagesService {

    private static final Logger logger =
            LoggerFactory.getLogger(MessagesService.class);

    private final TemplatesDbService templatesDbService;
    private final MessageRecipientResolver recipientResolver;
    private final MessagePreferencesService preferencesService;
    private final MessageTemplateRenderer templateRenderer;
    private final Map<MessageChannel, MessageSender> senders;
    private final MessagePersistenceService messagePersistenceService;

    public MessagesService(
            TemplatesDbService templatesDbService,
            MessageRecipientResolver recipientResolver,
            MessagePreferencesService preferencesService,
            MessageTemplateRenderer templateRenderer,
            List<MessageSender> messageSenders,
            MessagePersistenceService messagePersistenceService
    ) {
        this.templatesDbService = templatesDbService;
        this.recipientResolver = recipientResolver;
        this.preferencesService = preferencesService;
        this.templateRenderer = templateRenderer;
        this.messagePersistenceService = messagePersistenceService;

        this.senders = new EnumMap<>(MessageChannel.class);

        for (MessageSender sender : messageSenders) {
            MessageSender previous =
                    this.senders.put(sender.getChannel(), sender);

            if (previous != null) {
                throw new IllegalStateException(
                        "Multiple message senders registered for channel: "
                                + sender.getChannel()
                );
            }
        }
    }

    /**
     * Process a message request.
     */
    public void send(
            CurrentJwtUser jwtUser,
            CreateMessageRequest request
    ) {
        validateRequest(request);

        logger.info(
                "Processing message request. eventKey={}, correlationId={}",
                request.getEventKey(),
                request.getCorrelationId()
        );

        // Resolve recipients from the requested audiences.
        List<MessageRecipientDto> recipients =
                recipientResolver.resolve(
                        request.getEventKey(),
                        request.getAudiences()
                );

        if (recipients == null || recipients.isEmpty()) {
            logger.info(
                    "No recipients found for eventKey={}",
                    request.getEventKey()
            );
            return;
        }

        // Deduplicate recipients.
        Map<UUID, MessageRecipientDto> uniqueRecipients =
                new LinkedHashMap<>();

        for (MessageRecipientDto recipient : recipients) {
            if (recipient.getUserId() == null || recipient.getUserContextId() == null) {
                logger.warn("Ignoring recipient without a userId or userContextId");
                continue;
            }

            uniqueRecipients.putIfAbsent(
                    recipient.getUserContextId(),
                    recipient
            );
        }

        // Cache loaded templates by locale for this request.
        Map<String, LoadedTemplateDto> templateCache =
                new HashMap<>();

        // Process each recipient independently.
        for (MessageRecipientDto recipient :
                uniqueRecipients.values()) {

            processRecipient(
                    jwtUser,
                    request,
                    recipient,
                    templateCache
            );
        }
    }

    /**
     * Process an individual recipient.
     */
    private void processRecipient(
            CurrentJwtUser jwtUser,
            CreateMessageRequest request,
            MessageRecipientDto recipient,
            Map<String, LoadedTemplateDto> templateCache
    ) {
        UUID userContextId = recipient.getUserContextId();

        try {
            // Resolve the recipient's preferred locale.
            String locale = resolveLocale(request, recipient);

            // Load the localized template.
            LoadedTemplateDto template = templateCache.computeIfAbsent(
                    locale,
                    key -> loadTemplate(request.getEventKey(), key)
            );

            if (template == null) {
                throw new IllegalStateException(
                        "No template found for eventKey="
                                + request.getEventKey()
                                + ", locale=" + locale
                );
            }

            Set<MessageChannel> channels =
                    preferencesService.getEnabledChannels(
                            recipient.getUserContextId(),
                            template.getSubscriptionKey()
                    );

            if (channels.isEmpty()) {
                logger.debug(
                        "No enabled notification channels for userContextId={}",
                        userContextId
                );
                return;
            }

            // Combine event parameters with recipient-specific variables.
            Map<String, Object> parameters =
                    buildParameters(request, recipient);

            // Render the template.
            RenderedMessageDto rendered =
                    templateRenderer.render(
                            template,
                            parameters,
                            Locale.forLanguageTag(locale)
                    );

            messagePersistenceService.save(jwtUser.getUserContextId(), MessageDto.builder()
                            .message(rendered.getCeilingbarBody())
                            .templateId(template.getId())
                            .subject(rendered.getCeilingbarTitle())
                            .userContextId(userContextId)
                            .build())
                    .whenComplete((ignored, failure) -> {
                        if (failure != null) {
                            logger.error("Failed to persist message", failure);
                        }
                    });


            // Dispatch through the enabled channels.
            dispatch(
                    recipient,
                    rendered,
                    channels,
                    request.getCorrelationId()
            );

        } catch (Exception e) {
            logger.error(
                    "Failed to process notification for userContextId={}, eventKey={}",
                    userContextId,
                    request.getEventKey(),
                    e
            );
        }
    }

    /**
     * Dispatch the rendered message through enabled channels.
     */
    private void dispatch(
            MessageRecipientDto recipient,
            RenderedMessageDto rendered,
            Set<MessageChannel> channels,
            String correlationId
    ) {
        for (MessageChannel channel : channels) {

            MessageSender sender = senders.get(channel);

            if (sender == null) {
                logger.warn(
                        "No sender registered for channel={}",
                        channel
                );
                continue;
            }

            try {
                sender.send(recipient, rendered);

                logger.info(
                        "Message dispatched. userId={}, channel={}, correlationId={}",
                        recipient.getUserId(),
                        channel,
                        correlationId
                );

            } catch (Exception e) {
                logger.error(
                        "Message delivery failed. userContextId={}, channel={}, correlationId={}",
                        recipient.getUserContextId(),
                        channel,
                        correlationId,
                        e
                );
            }
        }
    }

    /**
     * Resolve the locale to use for this recipient.
     */
    private String resolveLocale(
            CreateMessageRequest request,
            MessageRecipientDto recipient
    ) {
        if (recipient.getLocale() != null
                && !recipient.getLocale().isBlank()) {
            return recipient.getLocale();
        }

        if (request.getLocale() != null
                && !request.getLocale().isBlank()) {
            return request.getLocale();
        }

        return "en-CA";
    }

    /**
     * Build template variables.
     */
    private Map<String, Object> buildParameters(
            CreateMessageRequest request,
            MessageRecipientDto recipient
    ) {
        Map<String, Object> parameters = new HashMap<>();

        if (request.getParameters() != null) {
            parameters.putAll(request.getParameters());
        }

        parameters.put("recipientId", recipient.getUserContextId());
        parameters.put("recipientName", recipient.getFirstName());
        parameters.put("recipientEmail", recipient.getEmail());

        // Context is available as a nested object for templates
        // that need access to business-event information.
        if (request.getContext() != null) {
            parameters.put("context", request.getContext());
        }

        return parameters;
    }

    /**
     * Load a localized notification template.
     */
    protected LoadedTemplateDto loadTemplate(
            String eventKey,
            String locale
    ) {
        if (eventKey == null || eventKey.isBlank()) {
            throw new IllegalArgumentException(
                    "eventKey is required"
            );
        }

        if (locale == null || locale.isBlank()) {
            throw new IllegalArgumentException(
                    "locale is required"
            );
        }

        return templatesDbService.getLoadedTemplate(
                new QueryOptions(
                        1,
                        1,
                        Map.of(
                                "eventKey", eventKey,
                                "locale", locale
                        ),
                        Map.of()
                )
        );
    }

    private void validateRequest(CreateMessageRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Message request is required"
            );
        }

        if (request.getEventKey() == null
                || request.getEventKey().isBlank()) {
            throw new IllegalArgumentException(
                    "eventKey is required"
            );
        }

        if (request.getAudiences() == null
                || request.getAudiences().isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one audience is required"
            );
        }
    }
}