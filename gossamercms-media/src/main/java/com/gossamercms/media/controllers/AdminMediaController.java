package com.gossamercms.media.controllers;

import com.gossamercms.media.dtos.MediaDto;
import com.gossamercms.media.dtos.requests.MediaRequest;
import com.gossamercms.media.dtos.responses.MediaResponse;
import com.gossamercms.media.handlers.MediaHandler;
import com.gossamercms.media.services.MediaService;
import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.media.models.Media;
import com.gossamercms.mvc.annotations.CurrentUser;
import com.gossamercms.security.jwt.JwtUser;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/admin/media")
public class AdminMediaController extends BaseController<Media, MediaDto, MediaRequest, MediaResponse, MediaResponse> {
    protected final MediaService mediaService;

    public AdminMediaController(MediaHandler handler,
                                MediaService mediaService) {
        super(handler, MediaResponse.class);
        this.mediaService = mediaService;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public MediaResponse upload(
            @RequestParam("file") MultipartFile file,
            @CurrentUser JwtUser jwtUser
    ) throws IOException {
//        BufferedImage image =
//                ImageIO.read(file.getInputStream());
//
//        Integer width = image != null ? image.getWidth() : null;
//        Integer height = image != null ? image.getHeight() : null;
        return toResponse(mediaService.upload(jwtUser.getUserId(), file));
    }

    @Override
    protected MediaDto toDto(MediaRequest request) {
        return MediaDto.builder()
                .filename(request.getFilename())
                .originalFilename(request.getOriginalFilename())
                .mimeType(request.getMimeType())
                .storageProvider(request.getStorageProvider())
                .storageKey(request.getStorageKey())
                .sizeBytes(request.getSizeBytes())
                .width(request.getWidth())
                .height(request.getHeight())
                .build();
    }

    @Override
    protected MediaResponse toResponse(MediaDto dto) {
        return MediaResponse.builder()
                .id(dto.getId())
                .filename(dto.getFilename())
                .originalFilename(dto.getOriginalFilename())
                .mimeType(dto.getMimeType())
                .storageProvider(dto.getStorageProvider())
                .storageKey(dto.getStorageKey())
                .sizeBytes(dto.getSizeBytes())
                .width(dto.getWidth())
                .height(dto.getHeight())
                .createdAt(dto.getCreatedAt())
                .createdBy(dto.getCreatedBy())
                .build();
    }
}
