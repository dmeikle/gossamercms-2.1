package com.gossamercms.optimization.autoconfigure;

import com.gossamercms.optimization.config.OptimizationProperties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "gossamercms.optimization.compression",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class CompressionConfiguration {

    @Bean
    public ServerProperties serverProperties(
            ServerProperties properties,
            OptimizationProperties optimization
    ) {
        properties.getCompression().setEnabled(true);

        properties.getCompression().setMinResponseSize(
                DataSize.ofBytes(optimization.getCompression().getMinimumSize())
        );

        properties.getCompression().setMimeTypes(
                new String[] {
                        "application/json",
                        "application/xml",
                        "text/html",
                        "text/plain",
                        "text/css",
                        "text/javascript"
                }
        );

        return properties;
    }
}