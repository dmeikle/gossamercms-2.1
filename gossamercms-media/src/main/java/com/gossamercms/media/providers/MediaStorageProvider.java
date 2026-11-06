package com.gossamercms.media.providers;

import com.gossamercms.media.dtos.StorageObject;
import com.gossamercms.media.storage.StorageResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

//
//public interface MediaStorageProvider {
//
//    StorageResult store(
//            UUID mediaId,
//            MultipartFile file
//    );
//
//    InputStream load(String storageKey);
//
//    void delete(String storageKey);
//}


public interface MediaStorageProvider {

    StorageResult store(UUID mediaId, MultipartFile file);

    StorageObject load(String storageKey);

    void delete(String storageKey);

    boolean exists(String storageKey);
}