package com.gossamercms.optimization.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "gossamercms.optimization")
public class OptimizationProperties {

    private boolean enabled = true;

    private Json json = new Json();

    private Compression compression = new Compression();

    private Caching caching = new Caching();

    private Pagination pagination = new Pagination();

    private Metrics metrics = new Metrics();

    @Data
    public static class Json {
        private boolean enabled = true;
        private boolean excludeNull = true;

    }

    @Data
    public static class Compression {
        private boolean enabled = true;
        private int minimumSize = 1024;
        private List<String> algorithms = new ArrayList<>(
                List.of("gzip")
        );

    }

    @Data
    public static class Caching {
        private boolean enabled = true;
        private Duration defaultMaxAge = Duration.ofSeconds(60);
        private boolean etagEnabled = true;
        private boolean lastModifiedEnabled = true;
        private boolean publicCache = false;
        private boolean mustRevalidate = false;
        private Duration staleWhileRevalidate = Duration.ZERO;

    }

    @Data
    public static class Pagination {
        private boolean enabled = true;
        private int defaultSize = 20;
        private int maximumSize = 100;

    }

    @Data
    public static class Metrics {
        private boolean enabled = true;

    }

}
