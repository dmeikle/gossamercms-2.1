package com.gossamercms.optimization.autoconfigure;

import com.gossamercms.optimization.caching.ApplicationResponseCache;
import com.gossamercms.optimization.caching.CacheModuleCatalog;
import com.gossamercms.optimization.caching.CacheProperties;
import com.gossamercms.optimization.caching.DefaultResponseCacheService;
import com.gossamercms.optimization.caching.EtagGenerator;
import com.gossamercms.optimization.filters.ResponseCachingFilter;
import com.gossamercms.optimization.services.ResponseCacheService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

@AutoConfiguration
@EnableConfigurationProperties(CacheProperties.class)
@ConditionalOnProperty(
        prefix = "gossamercms.optimization.caching",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class CacheConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EtagGenerator etagGenerator() {
        return new EtagGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public ResponseCacheService responseCacheService() {
        return new DefaultResponseCacheService();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "gossamercms.optimization.caching.application",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public ApplicationResponseCache applicationResponseCache(CacheProperties properties) {
        CacheProperties.Application application = properties.getApplication();
        String type = application.getType();
        if (type != null && !"caffeine".equalsIgnoreCase(type)) {
            throw new IllegalStateException(
                    "Unsupported gossamercms.optimization.caching.application.type '"
                            + type
                            + "'. The supported type is caffeine."
            );
        }
        return ApplicationResponseCache.create(application);
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheModuleCatalog cacheModuleCatalog(CacheProperties properties) {
        return CacheModuleCatalog.load(properties);
    }

    @Bean
    public FilterRegistrationBean<ResponseCachingFilter> responseCachingFilter(
            CacheProperties properties,
            ResponseCacheService responseCacheService,
            EtagGenerator etagGenerator,
            ObjectProvider<ApplicationResponseCache> applicationResponseCache,
            CacheModuleCatalog cacheModuleCatalog
    ) {
        ResponseCachingFilter filter = new ResponseCachingFilter(
                properties,
                responseCacheService,
                etagGenerator,
                applicationResponseCache.getIfAvailable(),
                cacheModuleCatalog
        );

        FilterRegistrationBean<ResponseCachingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("responseCachingFilter");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
        registration.setAsyncSupported(true);
        return registration;
    }
}
