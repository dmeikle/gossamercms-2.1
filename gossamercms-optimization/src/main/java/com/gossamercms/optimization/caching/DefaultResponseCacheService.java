package com.gossamercms.optimization.caching;

import com.gossamercms.optimization.services.ResponseCacheService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;

import java.time.Duration;
import java.time.Instant;

public class DefaultResponseCacheService implements ResponseCacheService {

    @Override
    public void applyCacheHeaders(
            HttpServletResponse response,
            Duration maxAge,
            boolean isPublic,
            boolean mustRevalidate,
            Duration staleWhileRevalidate
    ) {
        response.setHeader(
                HttpHeaders.CACHE_CONTROL,
                CacheControlBuilder.cacheControl(
                        maxAge,
                        isPublic,
                        mustRevalidate,
                        staleWhileRevalidate
                )
        );
    }

    @Override
    public void applyNoCache(HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, CacheControlBuilder.noStore());
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0);
    }

    @Override
    public void applyEtag(HttpServletResponse response, String etag) {
        if (etag != null && !etag.isBlank()) {
            response.setHeader(HttpHeaders.ETAG, etag);
        }
    }

    @Override
    public void applyLastModified(HttpServletResponse response, Instant lastModified) {
        if (lastModified != null) {
            response.setDateHeader(HttpHeaders.LAST_MODIFIED, lastModified.toEpochMilli());
        }
    }

    @Override
    public boolean isNotModified(HttpServletRequest request, String etag) {
        if (etag == null || etag.isBlank()) {
            return false;
        }

        String header = request.getHeader(HttpHeaders.IF_NONE_MATCH);
        if (header == null || header.isBlank()) {
            return false;
        }

        String expected = stripWeak(etag);
        for (String candidate : header.split(",")) {
            String value = stripWeak(candidate);
            if ("*".equals(value) || expected.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isNotModified(HttpServletRequest request, Instant lastModified) {
        if (lastModified == null) {
            return false;
        }

        long ifModifiedSince = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
        if (ifModifiedSince < 0) {
            return false;
        }

        return lastModified.getEpochSecond() <= ifModifiedSince / 1000;
    }

    private static String stripWeak(String value) {
        String trimmed = value.trim();
        if (trimmed.regionMatches(true, 0, "W/", 0, 2)) {
            trimmed = trimmed.substring(2).trim();
        }
        return trimmed;
    }
}
