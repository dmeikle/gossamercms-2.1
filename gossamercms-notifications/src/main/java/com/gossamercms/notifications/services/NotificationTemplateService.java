package com.gossamercms.notifications.services;

import com.gossamercms.media.dtos.StorageObject;
import com.gossamercms.media.providers.MediaStorageProvider;
import com.gossamercms.notifications.exceptions.LoadTemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class NotificationTemplateService {

    private final MediaStorageProvider storageProvider;

    public String loadTemplate(String storageKey) {
        if (storageKey == null || storageKey.isBlank()) {
            throw new IllegalArgumentException("storageKey is required");
        }

        try (StorageObject storageObject =
                     storageProvider.load(storageKey)) {
            if (storageObject == null || storageObject.getInputStream() == null) {
                throw new LoadTemplateException(storageKey);
            }

            return new String(
                    storageObject.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new LoadTemplateException(storageKey, e);
        }
    }
}
