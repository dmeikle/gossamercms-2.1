package com.gossamercms.optimization.autoconfigure;

import com.gossamercms.optimization.config.OptimizationProperties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties(OptimizationProperties.class)
@ConditionalOnProperty(
        prefix = "gossamercms.optimization",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@Import({
        JsonOptimizationConfiguration.class,
        CompressionConfiguration.class,
        CacheConfiguration.class,
        PaginationConfiguration.class,
        MetricsConfiguration.class
})
public class OptimizationAutoConfiguration {
}