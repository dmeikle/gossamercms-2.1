package com.gossamercms.optimization.caching;

/**
 * Response saved by the application cache and replayed on the next matching request.
 */
public record CachedHttpResponse(
        int status,
        String contentType,
        byte[] body,
        String etag,
        long lastModifiedEpochMilli,
        long timeToLiveNanos
) {
}
