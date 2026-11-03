package com.gossamercms.media.dtos.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MediaRequest {
    private String filename;
    private String originalFilename;
    private String mimeType;
    private String storageProvider;
    private String storageKey;
    private Long sizeBytes;
    private Integer width;
    private Integer height;
}
