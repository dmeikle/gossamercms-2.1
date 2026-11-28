package com.gossamercms.optimization.filters;

import com.gossamercms.optimization.caching.ApplicationResponseCache;
import com.gossamercms.optimization.caching.CacheModuleCatalog;
import com.gossamercms.optimization.caching.CachePaths;
import com.gossamercms.optimization.caching.CacheProperties;
import com.gossamercms.optimization.caching.CacheRuleLookup;
import com.gossamercms.optimization.caching.CachedHttpResponse;
import com.gossamercms.optimization.caching.EtagGenerator;
import com.gossamercms.optimization.services.ResponseCacheService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.http.HttpHeaders;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Checks the application cache before the controller runs. A hit is written
 * straight back to the client. A miss lets the controller run, then stores
 * the response for the next matching request.
 */
public class ResponseCachingFilter extends OncePerRequestFilter {

    static final int MAX_CACHED_BODY_BYTES = 1024 * 1024;

    private static final String VARY = "Accept, Accept-Language, Authorization";

    private final CacheProperties properties;
    private final ResponseCacheService responseCacheService;
    private final EtagGenerator etagGenerator;
    private final ApplicationResponseCache applicationCache;
    private final CacheModuleCatalog moduleCatalog;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public ResponseCachingFilter(
            CacheProperties properties,
            ResponseCacheService responseCacheService,
            EtagGenerator etagGenerator,
            ApplicationResponseCache applicationCache
    ) {
        this(properties, responseCacheService, etagGenerator, applicationCache, null);
    }

    public ResponseCachingFilter(
            CacheProperties properties,
            ResponseCacheService responseCacheService,
            EtagGenerator etagGenerator,
            ApplicationResponseCache applicationCache,
            CacheModuleCatalog moduleCatalog
    ) {
        this.properties = properties;
        this.responseCacheService = responseCacheService;
        this.etagGenerator = etagGenerator;
        this.applicationCache = applicationCache;
        this.moduleCatalog = moduleCatalog;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (!properties.isEnabled() || !isSafeMethod(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = pathWithinApplication(request);
        if (isNoStore(request, path)) {
            if (httpEnabled()) {
                responseCacheService.applyNoCache(response);
            }
            filterChain.doFilter(request, response);
            return;
        }

        RuleDecision decision = decide(path);
        if (!decision.cache()) {
            filterChain.doFilter(request, response);
            return;
        }
        CacheProperties.Path rule = decision.rule();

        if (httpEnabled() && !response.containsHeader(HttpHeaders.CACHE_CONTROL)) {
            CacheProperties.Http http = properties.getHttp();
            responseCacheService.applyCacheHeaders(
                    response,
                    maxAge(http, rule),
                    http.isPublicCache(),
                    http.isMustRevalidate(),
                    http.getStaleWhileRevalidate()
            );
            response.setHeader(HttpHeaders.VARY, VARY);
        }

        String key = cacheKey(request);
        if (applicationEnabled()) {
            var hit = applicationCache.find(key);
            if (hit.isPresent()) {
                writeCached(request, response, hit.get());
                return;
            }
        }

        if (!applicationEnabled() && !etagEnabled() && !lastModifiedEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(request, wrapper);
        completeMiss(request, wrapper, key, timeToLiveNanos(rule));
    }

    private void completeMiss(
            HttpServletRequest request,
            ContentCachingResponseWrapper wrapper,
            String key,
            long timeToLiveNanos
    ) throws IOException {
        byte[] body = wrapper.getContentAsByteArray();
        int status = wrapper.getStatus();

        String etag = null;
        if (etagEnabled() && status == HttpServletResponse.SC_OK && body.length > 0) {
            etag = etagGenerator.generate(body);
            responseCacheService.applyEtag(wrapper, etag);
        }

        long lastModified = resolveLastModified(wrapper);
        if (lastModified >= 0) {
            responseCacheService.applyLastModified(wrapper, Instant.ofEpochMilli(lastModified));
        }

        if (shouldStore(wrapper, body)) {
            applicationCache.store(key, new CachedHttpResponse(
                    status,
                    wrapper.getContentType(),
                    Arrays.copyOf(body, body.length),
                    etag,
                    lastModified,
                    timeToLiveNanos
            ));
        }

        if (status == HttpServletResponse.SC_OK && notModified(request, etag, lastModified)) {
            sendNotModified(wrapper);
            return;
        }

        if ("HEAD".equalsIgnoreCase(request.getMethod())) {
            HttpServletResponse target = unwrap(wrapper);
            target.setStatus(status);
            target.setContentLength(body.length);
            return;
        }

        wrapper.copyBodyToResponse();
    }

    private void writeCached(
            HttpServletRequest request,
            HttpServletResponse response,
            CachedHttpResponse cached
    ) throws IOException {
        if (httpEnabled()) {
            if (cached.etag() != null) {
                responseCacheService.applyEtag(response, cached.etag());
            }
            if (cached.lastModifiedEpochMilli() >= 0) {
                responseCacheService.applyLastModified(
                        response,
                        Instant.ofEpochMilli(cached.lastModifiedEpochMilli())
                );
            }
            if (notModified(request, cached.etag(), cached.lastModifiedEpochMilli())) {
                sendNotModified(response);
                return;
            }
        }

        response.setStatus(cached.status());
        if (cached.contentType() != null) {
            response.setContentType(cached.contentType());
        }

        byte[] body = cached.body() == null ? new byte[0] : cached.body();
        response.setContentLength(body.length);
        if (!"HEAD".equalsIgnoreCase(request.getMethod()) && body.length > 0) {
            response.getOutputStream().write(body);
        }
    }

    private boolean notModified(HttpServletRequest request, String etag, long lastModified) {
        if (etag != null && !etag.isBlank()) {
            return responseCacheService.isNotModified(request, etag);
        }
        if (lastModified >= 0) {
            return responseCacheService.isNotModified(request, Instant.ofEpochMilli(lastModified));
        }
        return false;
    }

    private void sendNotModified(HttpServletResponse response) {
        HttpServletResponse target = unwrap(response);
        target.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        target.setContentLength(0);
    }

    private boolean shouldStore(ContentCachingResponseWrapper wrapper, byte[] body) {
        if (!applicationEnabled() || wrapper.getStatus() != HttpServletResponse.SC_OK) {
            return false;
        }
        if (body.length == 0 || body.length > MAX_CACHED_BODY_BYTES) {
            return false;
        }
        if (wrapper.containsHeader(HttpHeaders.SET_COOKIE)) {
            return false;
        }

        String cacheControl = wrapper.getHeader(HttpHeaders.CACHE_CONTROL);
        if (cacheControl != null && cacheControl.toLowerCase(Locale.ROOT).contains("no-store")) {
            return false;
        }

        String disposition = wrapper.getHeader(HttpHeaders.CONTENT_DISPOSITION);
        if (disposition != null && disposition.toLowerCase(Locale.ROOT).contains("attachment")) {
            return false;
        }

        return isCacheableContentType(wrapper.getContentType());
    }

    private boolean isCacheableContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return false;
        }

        String type = contentType.toLowerCase(Locale.ROOT);
        int semicolon = type.indexOf(';');
        if (semicolon >= 0) {
            type = type.substring(0, semicolon).trim();
        }

        return type.startsWith("text/")
                || "application/json".equals(type)
                || type.endsWith("+json")
                || "application/xml".equals(type)
                || type.endsWith("+xml")
                || "application/javascript".equals(type)
                || "application/xhtml+xml".equals(type);
    }

    private long resolveLastModified(HttpServletResponse response) {
        String existing = response.getHeader(HttpHeaders.LAST_MODIFIED);
        if (existing != null && !existing.isBlank()) {
            try {
                return ZonedDateTime.parse(existing, DateTimeFormatter.RFC_1123_DATE_TIME)
                        .toInstant()
                        .toEpochMilli();
            } catch (DateTimeParseException ex) {
                return -1;
            }
        }
        if (lastModifiedEnabled()) {
            return System.currentTimeMillis();
        }
        return -1;
    }

    private RuleDecision decide(String path) {
        if (moduleCatalog != null && !moduleCatalog.isEmpty()) {
            CacheRuleLookup lookup = moduleCatalog.lookup(path);
            if (lookup.handled()) {
                return lookup.rule() == null ? RuleDecision.skip() : RuleDecision.use(lookup.rule());
            }
            if (!restrictsPaths()) {
                return RuleDecision.skip();
            }
        }

        CacheProperties.Path inline = matchingPath(path);
        if (inline == null && restrictsPaths()) {
            return RuleDecision.skip();
        }
        return RuleDecision.use(inline);
    }

    private CacheProperties.Path matchingPath(String path) {
        if (properties.getPaths() == null) {
            return null;
        }
        for (CacheProperties.Path rule : properties.getPaths()) {
            if (rule != null
                    && rule.getPattern() != null
                    && !rule.getPattern().isBlank()
                    && CachePaths.matches(pathMatcher, rule.getPattern(), path)) {
                return rule;
            }
        }
        return null;
    }

    private boolean restrictsPaths() {
        if (properties.getPaths() == null) {
            return false;
        }
        for (CacheProperties.Path rule : properties.getPaths()) {
            if (rule != null && rule.getPattern() != null && !rule.getPattern().isBlank()) {
                return true;
            }
        }
        return false;
    }

    private Duration maxAge(CacheProperties.Http http, CacheProperties.Path rule) {
        if (rule != null && rule.getMaxAge() != null) {
            return rule.getMaxAge();
        }
        return http.getDefaultMaxAge();
    }

    private long timeToLiveNanos(CacheProperties.Path rule) {
        Duration ttl = rule == null ? null : rule.getExpireAfterWrite();
        if (ttl == null && properties.getApplication() != null) {
            ttl = properties.getApplication().getExpireAfterWrite();
        }
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            return 0;
        }
        return ttl.toNanos();
    }

    private boolean isSafeMethod(HttpServletRequest request) {
        String method = request.getMethod();
        return "GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method);
    }

    private boolean isNoStore(HttpServletRequest request, String path) {
        if (request.getHeader(HttpHeaders.AUTHORIZATION) != null) {
            return true;
        }
        if (request.getSession(false) != null) {
            return true;
        }

        List<String> patterns = properties.getHttp() == null
                ? List.of()
                : properties.getHttp().getNoStorePaths();
        if (patterns == null) {
            return false;
        }
        for (String pattern : patterns) {
            if (pattern != null && CachePaths.matches(pathMatcher, pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private boolean httpEnabled() {
        return properties.getHttp() != null && properties.getHttp().isEnabled();
    }

    private boolean etagEnabled() {
        return httpEnabled() && properties.getHttp().isEtagEnabled();
    }

    private boolean lastModifiedEnabled() {
        return httpEnabled() && properties.getHttp().isLastModifiedEnabled();
    }

    private boolean applicationEnabled() {
        return applicationCache != null
                && properties.getApplication() != null
                && properties.getApplication().isEnabled();
    }

    private String cacheKey(HttpServletRequest request) {
        String method = "HEAD".equalsIgnoreCase(request.getMethod()) ? "GET" : request.getMethod();
        StringBuilder key = new StringBuilder(method)
                .append(' ')
                .append(pathWithinApplication(request));

        String query = request.getQueryString();
        if (query != null && !query.isEmpty()) {
            key.append('?').append(query);
        }

        appendHeader(key, request, HttpHeaders.ACCEPT);
        appendHeader(key, request, HttpHeaders.ACCEPT_LANGUAGE);
        return key.toString();
    }

    private void appendHeader(StringBuilder key, HttpServletRequest request, String header) {
        String value = request.getHeader(header);
        if (value != null && !value.isBlank()) {
            key.append('\n').append(header).append('=').append(value);
        }
    }

    private String pathWithinApplication(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri == null || uri.isEmpty()) {
            return "/";
        }
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        }
        return uri.isEmpty() ? "/" : uri;
    }

    private record RuleDecision(boolean cache, CacheProperties.Path rule) {

        private static RuleDecision skip() {
            return new RuleDecision(false, null);
        }

        private static RuleDecision use(CacheProperties.Path rule) {
            return new RuleDecision(true, rule);
        }
    }

    private HttpServletResponse unwrap(HttpServletResponse response) {
        HttpServletResponse current = response;
        while (current instanceof HttpServletResponseWrapper wrapper) {
            ServletResponse nested = wrapper.getResponse();
            if (!(nested instanceof HttpServletResponse http)) {
                break;
            }
            current = http;
        }
        return current;
    }
}
