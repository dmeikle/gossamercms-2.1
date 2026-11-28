package com.gossamercms.optimization.autoconfigure;

import com.gossamercms.optimization.config.OptimizationProperties;
import com.gossamercms.optimization.pagination.PaginationValidator;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "gossamercms.optimization.pagination",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class PaginationConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PaginationValidator paginationValidator(
            OptimizationProperties properties
    ) {
        return new PaginationValidator(
                properties.getPagination().getDefaultSize(),
                properties.getPagination().getMaximumSize()
        );
    }
}