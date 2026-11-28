package com.gossamercms.optimization.caching;

import java.time.Duration;

public final class CacheControlBuilder {

    private CacheControlBuilder() {
    }

    public static String cacheControl(
            Duration maxAge,
            boolean isPublic,
            boolean mustRevalidate,
            Duration staleWhileRevalidate
    ) {
        StringBuilder cacheControl = new StringBuilder(isPublic ? "public" : "private");
        long maxAgeSeconds = maxAge == null ? 0 : Math.max(0, maxAge.getSeconds());
        cacheControl.append(", max-age=").append(maxAgeSeconds);

        if (mustRevalidate) {
            cacheControl.append(", must-revalidate");
        }

        if (staleWhileRevalidate != null
                && !staleWhileRevalidate.isNegative()
                && !staleWhileRevalidate.isZero()) {
            cacheControl.append(", stale-while-revalidate=")
                    .append(staleWhileRevalidate.getSeconds());
        }

        return cacheControl.toString();
    }

    public static String noStore() {
        return "no-store, no-cache, must-revalidate, max-age=0";
    }
}
