package com.gossamercms.media.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MediaResponse {
    private UUID id;
    private String filename;
    private String originalFilename;
    private String mimeType;
    private String storageProvider;
    private String storageKey;
    private Long sizeBytes;
    private Integer width;
    private Integer height;
    private Instant createdAt;
    private UUID createdBy;
}
