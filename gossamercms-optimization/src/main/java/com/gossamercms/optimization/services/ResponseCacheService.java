package com.gossamercms.optimization.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;
import java.time.Instant;

public interface ResponseCacheService {

    void applyCacheHeaders(
            HttpServletResponse response,
            Duration maxAge,
            boolean isPublic,
            boolean mustRevalidate,
            Duration staleWhileRevalidate
    );

    void applyNoCache(
            HttpServletResponse response
    );

    void applyEtag(
            HttpServletResponse response,
            String etag
    );

    void applyLastModified(
            HttpServletResponse response,
            Instant lastModified
    );

    boolean isNotModified(
            HttpServletRequest request,
            String etag
    );

    boolean isNotModified(
            HttpServletRequest request,
            Instant lastModified
    );
}
