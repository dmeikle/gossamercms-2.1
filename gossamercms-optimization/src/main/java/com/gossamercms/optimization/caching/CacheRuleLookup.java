package com.gossamercms.optimization.caching;

/**
 * Result of choosing a module file for a request path.
 * {@code handled} means a module file claimed the endpoint.
 */
public record CacheRuleLookup(boolean handled, CacheProperties.Path rule) {

    public static CacheRuleLookup none() {
        return new CacheRuleLookup(false, null);
    }

    public static CacheRuleLookup unlisted() {
        return new CacheRuleLookup(true, null);
    }

    public static CacheRuleLookup found(CacheProperties.Path rule) {
        return new CacheRuleLookup(true, rule);
    }
}
