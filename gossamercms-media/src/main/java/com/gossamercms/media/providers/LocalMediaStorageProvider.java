package com.gossamercms.media.providers;


import com.gossamercms.media.dtos.StorageObject;
import com.gossamercms.media.exceptions.LoadStoredFileException;
import com.gossamercms.media.exceptions.StoredFileNotFoundException;
import com.gossamercms.media.storage.StorageResult;
import com.gossamercms.mvc.annotations.ModuleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@ModuleProvider
public class LocalMediaStorageProvider implements MediaStorageProvider {

    private final Path rootDirectory;

    public LocalMediaStorageProvider(
            @Value("${media.storage.path}") String storagePath
    ) {
        System.out.println("Initialize LocalMediaStorageProvider *******************************");
        this.rootDirectory = Paths.get(storagePath)
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public StorageResult store(UUID mediaId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        try {
            Files.createDirectories(rootDirectory);

            String extension = getExtension(file.getOriginalFilename());

            String storageKey = extension.isBlank()
                    ? mediaId.toString()
                    : mediaId + "." + extension;

            Path destination = rootDirectory
                    .resolve(storageKey)
                    .normalize();

            if (!destination.startsWith(rootDirectory)) {
                throw new IllegalArgumentException("Invalid storage key");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(
                        inputStream,
                        destination,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            return StorageResult.builder()
                    .storageProvider("local")
                    .storageKey(storageKey)
                    .url("/media/" + storageKey)
                    .sizeBytes(Files.size(destination))
                    .mimeType(file.getContentType())
                    .build();

        } catch (IOException e) {
            throw new LoadStoredFileException(mediaId.toString());
        }
    }

//    @Override
//    public InputStream load(String storageKey) {
//
//        try {
//            return Files.newInputStream(
//                    rootDirectory.resolve(storageKey)
//            );
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to load media", e);
//        }
//    }
@Override
    public StorageObject load(String storageKey) {
        try {
            Path file = rootDirectory.resolve(storageKey).normalize();
System.out.println("Loading file: " + file.toString());
            if (!file.startsWith(rootDirectory)) {
                throw new IllegalArgumentException(
                        "Invalid storage key: " + storageKey
                );
            }

            if (!Files.isRegularFile(file)) {
                throw new StoredFileNotFoundException(storageKey);
            }

            InputStream inputStream = Files.newInputStream(file);

            return new StorageObject(
                    storageKey,
                    inputStream,
                    Files.size(file)
            );

        } catch (IOException e) {
            throw new LoadStoredFileException(storageKey);
        }
    }

    @Override
    public void delete(String storageKey) {

        try {
            Path file = rootDirectory.resolve(storageKey).normalize();

            if (!file.startsWith(rootDirectory)) {
                throw new IllegalArgumentException(
                        "Invalid storage key: " + storageKey
                );
            }

            Files.deleteIfExists(file);

        } catch (IOException e) {
            throw new LoadStoredFileException(storageKey);
        }
    }

    @Override
    public boolean exists(String storageKey) {
        return false;
    }

    private String getExtension(String filename) {

        if (filename == null) {
            return "";
        }

        int dotIndex = filename.lastIndexOf('.');

        if (dotIndex < 0) {
            return "";
        }

        return filename.substring(dotIndex + 1);
    }
}
