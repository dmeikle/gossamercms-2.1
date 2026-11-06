package com.gossamercms.media.providers;

import com.gossamercms.media.dtos.StorageObject;
import com.gossamercms.media.storage.StorageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class RemoteMediaStorageProvider implements MediaStorageProvider {
    @Override
    public StorageResult store(UUID mediaId, MultipartFile file) {
        return null;
    }

    @Override
    public StorageObject load(String storageKey) {
        return null;
    }

    @Override
    public void delete(String storageKey) {

    }

    @Override
    public boolean exists(String storageKey) {
        return false;
    }
}
