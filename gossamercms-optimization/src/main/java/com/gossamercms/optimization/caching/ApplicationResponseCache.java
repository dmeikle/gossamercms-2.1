package com.gossamercms.optimization.caching;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.Ticker;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

import java.time.Duration;
import java.util.Optional;

/**
 * In-process store of controller responses. A hit is returned before the controller runs.
 */
public final class ApplicationResponseCache {

    private final Cache<String, CachedHttpResponse> cache;

    public ApplicationResponseCache(Cache<String, CachedHttpResponse> cache) {
        this.cache = cache;
    }

    public static ApplicationResponseCache create(CacheProperties.Application application) {
        return create(application, null);
    }

    public static ApplicationResponseCache create(CacheProperties.Application application, Ticker ticker) {
        Caffeine<String, CachedHttpResponse> builder = Caffeine.newBuilder()
                .maximumSize(Math.max(1, application.getMaximumSize()))
                .expireAfter(new EntryExpiry(application.getExpireAfterAccess()));
        if (ticker != null) {
            builder.ticker(ticker);
        }
        if (application.isStatistics()) {
            builder.recordStats();
        }
        return new ApplicationResponseCache(builder.build());
    }

    public Optional<CachedHttpResponse> find(String key) {
        if (key == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cache.getIfPresent(key));
    }

    public void store(String key, CachedHttpResponse response) {
        if (key == null || response == null) {
            return;
        }
        cache.put(key, response);
    }

    public CacheStats stats() {
        return cache.stats();
    }

    private static final class EntryExpiry implements Expiry<String, CachedHttpResponse> {

        private final long accessNanos;

        private EntryExpiry(Duration expireAfterAccess) {
            this.accessNanos = expireAfterAccess == null
                    || expireAfterAccess.isZero()
                    || expireAfterAccess.isNegative()
                    ? 0
                    : expireAfterAccess.toNanos();
        }

        @Override
        public long expireAfterCreate(String key, CachedHttpResponse value, long currentTime) {
            return writeNanos(value);
        }

        @Override
        public long expireAfterUpdate(
                String key,
                CachedHttpResponse value,
                long currentTime,
                long currentDuration
        ) {
            return writeNanos(value);
        }

        @Override
        public long expireAfterRead(
                String key,
                CachedHttpResponse value,
                long currentTime,
                long currentDuration
        ) {
            if (accessNanos == 0) {
                return currentDuration;
            }
            return Math.min(currentDuration, accessNanos);
        }

        private static long writeNanos(CachedHttpResponse value) {
            long ttl = value.timeToLiveNanos();
            return ttl > 0 ? ttl : Long.MAX_VALUE;
        }
    }
}
