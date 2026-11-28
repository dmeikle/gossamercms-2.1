package com.gossamercms.optimization.caching;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "gossamercms.optimization.caching")
public class CacheProperties {

    private boolean enabled = true;

    private Http http = new Http();

    private Application application = new Application();

    /**
     * Endpoints to cache. The first matching pattern wins.
     * When this list is empty and no module files are configured, every safe GET outside
     * {@code http.no-store-paths} is cached with the default durations.
     */
    private List<Path> paths = new ArrayList<>();

    /**
     * Directories or files of per-module cache rules. A request loads the file whose
     * {@code match} pattern fits the endpoint. Globs are allowed.
     */
    private List<String> configLocations = new ArrayList<>();

    /**
     * Module files loaded ahead of {@code config-locations}. Earlier entries win when two
     * files have the same match specificity.
     */
    private List<ModuleFile> modules = new ArrayList<>();

    @Data
    public static class Http {

        private boolean enabled = true;

        private boolean etagEnabled = true;

        private boolean lastModifiedEnabled = true;

        private Duration defaultMaxAge = Duration.ofSeconds(60);

        private boolean publicCache = false;

        private boolean mustRevalidate = false;

        private Duration staleWhileRevalidate = Duration.ZERO;

        private List<String> noStorePaths = new ArrayList<>();
    }

    @Data
    public static class Application {

        private boolean enabled = true;

        private String type = "caffeine";

        private long maximumSize = 10000;

        private Duration expireAfterWrite = Duration.ofMinutes(10);

        private Duration expireAfterAccess = Duration.ofMinutes(5);

        private boolean statistics = true;
    }

    @Data
    public static class Path {

        private String pattern;

        /** Browser Cache-Control max-age. Falls back to http.default-max-age. */
        private Duration maxAge;

        /** How long the server reuses the response. Falls back to application.expire-after-write. */
        private Duration expireAfterWrite;
    }

    @Data
    public static class ModuleFile {

        private String location;
    }
}
