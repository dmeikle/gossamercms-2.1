package com.gossamercms.notifications.services;

import com.gossamercms.mvc.annotations.ModuleService;
import com.gossamercms.notifications.dtos.MessageDto;
import com.gossamercms.notifications.handlers.MessagesHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ModuleService
@RequiredArgsConstructor
public class MessagePersistenceService {
    private final MessagesHandler messagesHandler;

    @Async("messagePersistenceExecutor")
    public CompletableFuture<Void> save(UUID createdBy, MessageDto message) {
        messagesHandler.create(createdBy, message);
        return CompletableFuture.completedFuture(null);
    }
}