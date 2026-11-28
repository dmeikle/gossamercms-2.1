package com.gossamercms.optimization.filters;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import com.gossamercms.optimization.caching.ApplicationResponseCache;
import com.gossamercms.optimization.caching.CacheModuleCatalog;
import com.gossamercms.optimization.caching.CacheProperties;
import com.gossamercms.optimization.caching.DefaultResponseCacheService;
import com.gossamercms.optimization.caching.EtagGenerator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResponseCachingFilterTest {

    private ApplicationResponseCache cache;
    private ResponseCachingFilter filter;
    private RecordingChain chain;

    @BeforeEach
    void setUp() {
        cache = new ApplicationResponseCache(Caffeine.newBuilder().maximumSize(100).build());
        filter = new ResponseCachingFilter(
                properties(),
                new DefaultResponseCacheService(),
                new EtagGenerator(),
                cache
        );
        chain = new RecordingChain();
    }

    @Test
    void returnsCachedBodyWithoutCallingTheControllerAgain() throws Exception {
        MockHttpServletResponse first = invoke("GET", "/api/pages");
        MockHttpServletResponse second = invoke("GET", "/api/pages");

        assertEquals(1, chain.calls);
        assertEquals("{\"ok\":true}", first.getContentAsString());
        assertEquals("{\"ok\":true}", second.getContentAsString());
        assertEquals(200, second.getStatus());
        assertTrue(second.getHeader(HttpHeaders.CACHE_CONTROL).contains("private"));
        assertTrue(second.getHeader(HttpHeaders.CACHE_CONTROL).contains("max-age=60"));
        assertTrue(second.getHeader(HttpHeaders.CACHE_CONTROL).contains("stale-while-revalidate=30"));
        assertNotNull(second.getHeader(HttpHeaders.ETAG));
    }

    @Test
    void differentQueryStringsAreCachedSeparately() throws Exception {
        invoke("GET", "/api/pages", "q=1");
        invoke("GET", "/api/pages", "q=2");

        assertEquals(2, chain.calls);
    }

    @Test
    void matchingEtagReturnsNotModifiedWithoutABody() throws Exception {
        MockHttpServletResponse first = invoke("GET", "/api/pages");
        String etag = first.getHeader(HttpHeaders.ETAG);

        MockHttpServletRequest request = request("GET", "/api/pages", null);
        request.addHeader(HttpHeaders.IF_NONE_MATCH, etag);
        MockHttpServletResponse second = new MockHttpServletResponse();
        filter.doFilter(request, second, chain);

        assertEquals(1, chain.calls);
        assertEquals(304, second.getStatus());
        assertEquals(0, second.getContentAsByteArray().length);
        assertEquals(etag, second.getHeader(HttpHeaders.ETAG));
    }

    @Test
    void headReusesTheGetEntryAndDoesNotWriteABody() throws Exception {
        invoke("GET", "/api/pages");
        MockHttpServletResponse head = invoke("HEAD", "/api/pages");

        assertEquals(1, chain.calls);
        assertEquals(200, head.getStatus());
        assertEquals(0, head.getContentAsByteArray().length);
        assertTrue(head.getContentLength() > 0);
    }

    @Test
    void noStorePathsAlwaysReachTheController() throws Exception {
        invoke("GET", "/admin/pages");
        invoke("GET", "/api/admin/users");
        invoke("GET", "/auth/login");

        assertEquals(3, chain.calls);
        MockHttpServletResponse response = invoke("GET", "/admin/pages");
        assertTrue(response.getHeader(HttpHeaders.CACHE_CONTROL).contains("no-store"));
        assertEquals(4, chain.calls);
    }

    @Test
    void authorizedAndSessionRequestsAreNotCached() throws Exception {
        MockHttpServletRequest authorized = request("GET", "/api/pages", null);
        authorized.addHeader(HttpHeaders.AUTHORIZATION, "Bearer secret");
        filter.doFilter(authorized, new MockHttpServletResponse(), chain);
        filter.doFilter(authorized, new MockHttpServletResponse(), chain);

        MockHttpServletRequest session = request("GET", "/api/pages", null);
        session.getSession(true);
        filter.doFilter(session, new MockHttpServletResponse(), chain);
        filter.doFilter(session, new MockHttpServletResponse(), chain);

        assertEquals(4, chain.calls);
    }

    @Test
    void postRequestsAreNotCached() throws Exception {
        invoke("POST", "/api/pages");
        invoke("POST", "/api/pages");

        assertEquals(2, chain.calls);
    }

    @Test
    void controllerNoStoreResponseIsNotCached() throws Exception {
        chain.cacheControl = "no-store";
        invoke("GET", "/api/pages");
        invoke("GET", "/api/pages");

        assertEquals(2, chain.calls);
    }

    @Test
    void configuredPathsUseTheirOwnDurationsAndLeaveOtherEndpointsUncached() throws Exception {
        CacheProperties properties = properties();
        properties.setPaths(List.of(
                path("/api/pages/preview/**", Duration.ofSeconds(15), Duration.ofSeconds(15)),
                path("/api/pages/**", Duration.ofMinutes(5), Duration.ofMinutes(10)),
                path("/api/events/**", Duration.ofSeconds(30), Duration.ofMinutes(1))
        ));
        filter = new ResponseCachingFilter(
                properties,
                new DefaultResponseCacheService(),
                new EtagGenerator(),
                cache
        );
        chain = new RecordingChain();

        MockHttpServletResponse preview = invoke("GET", "/api/pages/preview/1");
        invoke("GET", "/api/pages/preview/1");
        MockHttpServletResponse pages = invoke("GET", "/api/pages/home");
        invoke("GET", "/api/pages/home");
        MockHttpServletResponse events = invoke("GET", "/api/events/1");
        invoke("GET", "/api/events/1");
        MockHttpServletResponse orders = invoke("GET", "/api/orders");
        invoke("GET", "/api/orders");

        assertEquals(5, chain.calls);
        assertTrue(preview.getHeader(HttpHeaders.CACHE_CONTROL).contains("max-age=15"));
        assertTrue(pages.getHeader(HttpHeaders.CACHE_CONTROL).contains("max-age=300"));
        assertTrue(events.getHeader(HttpHeaders.CACHE_CONTROL).contains("max-age=30"));
        assertNull(orders.getHeader(HttpHeaders.CACHE_CONTROL));
    }

    @Test
    void moduleFilesAreSelectedByEndpoint() throws Exception {
        CacheProperties properties = properties();
        properties.setConfigLocations(List.of("classpath:caching/*.yaml"));
        filter = new ResponseCachingFilter(
                properties,
                new DefaultResponseCacheService(),
                new EtagGenerator(),
                cache,
                CacheModuleCatalog.load(properties)
        );
        chain = new RecordingChain();

        MockHttpServletResponse preview = invoke("GET", "/api/pages/preview/1");
        invoke("GET", "/api/pages/preview/1");
        MockHttpServletResponse pages = invoke("GET", "/api/pages/home");
        invoke("GET", "/api/pages/home");
        MockHttpServletResponse tickets = invoke("GET", "/api/events/4/tickets");
        invoke("GET", "/api/events/4/tickets");
        MockHttpServletResponse events = invoke("GET", "/api/events/9");
        invoke("GET", "/api/events/9");
        MockHttpServletResponse orders = invoke("GET", "/api/orders");
        invoke("GET", "/api/orders");

        assertEquals(6, chain.calls);
        assertTrue(preview.getHeader(HttpHeaders.CACHE_CONTROL).contains("max-age=15"));
        assertTrue(pages.getHeader(HttpHeaders.CACHE_CONTROL).contains("max-age=300"));
        assertTrue(tickets.getHeader(HttpHeaders.CACHE_CONTROL).contains("max-age=10"));
        assertTrue(events.getHeader(HttpHeaders.CACHE_CONTROL).contains("max-age=5"));
        assertNull(orders.getHeader(HttpHeaders.CACHE_CONTROL));
    }

    @Test
    void versionedApiPrefixAndModuleSubfoldersAreIgnoredWhenMatching() throws Exception {
        CacheProperties properties = properties();
        properties.setConfigLocations(List.of("classpath:caching/*.yaml"));
        filter = new ResponseCachingFilter(
                properties,
                new DefaultResponseCacheService(),
                new EtagGenerator(),
                cache,
                CacheModuleCatalog.load(properties)
        );
        chain = new RecordingChain();

        MockHttpServletResponse pages = invoke("GET", "/api1.0/pages/home");
        MockHttpServletResponse laterVersion = invoke("GET", "/api2.0/pages/home");
        MockHttpServletResponse categories = invoke("GET", "/api1.0/categories/3");
        invoke("GET", "/api1.0/categories/3");
        MockHttpServletResponse admin = invoke("GET", "/api1.0/admin/users");

        assertEquals(4, chain.calls);
        assertTrue(pages.getHeader(HttpHeaders.CACHE_CONTROL).contains("max-age=300"));
        assertTrue(laterVersion.getHeader(HttpHeaders.CACHE_CONTROL).contains("max-age=300"));
        assertTrue(categories.getHeader(HttpHeaders.CACHE_CONTROL).contains("max-age=120"));
        assertTrue(admin.getHeader(HttpHeaders.CACHE_CONTROL).contains("no-store"));
    }

    @Test
    void moduleFileWithoutMatchFailsToLoad() {
        CacheProperties properties = properties();
        properties.setConfigLocations(List.of("classpath:caching-invalid/*.yaml"));

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> CacheModuleCatalog.load(properties)
        );
        assertTrue(error.getMessage().contains("match"));
    }

    @Test
    void pathExpireAfterWriteDropsTheServerEntry() throws Exception {
        ManualTicker ticker = new ManualTicker();
        CacheProperties properties = properties();
        properties.getApplication().setExpireAfterAccess(Duration.ZERO);
        properties.setPaths(List.of(
                path("/api/pages/**", Duration.ofSeconds(60), Duration.ofSeconds(10))
        ));
        filter = new ResponseCachingFilter(
                properties,
                new DefaultResponseCacheService(),
                new EtagGenerator(),
                ApplicationResponseCache.create(properties.getApplication(), ticker)
        );
        chain = new RecordingChain();

        invoke("GET", "/api/pages/home");
        invoke("GET", "/api/pages/home");
        assertEquals(1, chain.calls);

        ticker.advance(Duration.ofSeconds(11));
        invoke("GET", "/api/pages/home");
        assertEquals(2, chain.calls);
    }

    @Test
    void disabledApplicationCacheStillSetsHttpValidators() throws Exception {
        filter = new ResponseCachingFilter(
                properties(),
                new DefaultResponseCacheService(),
                new EtagGenerator(),
                null
        );

        MockHttpServletResponse first = invoke("GET", "/api/pages");
        MockHttpServletResponse second = invoke("GET", "/api/pages");

        assertEquals(2, chain.calls);
        assertNotNull(first.getHeader(HttpHeaders.ETAG));
        assertEquals(first.getHeader(HttpHeaders.ETAG), second.getHeader(HttpHeaders.ETAG));
    }

    private MockHttpServletResponse invoke(String method, String path) throws ServletException, IOException {
        return invoke(method, path, null);
    }

    private MockHttpServletResponse invoke(String method, String path, String query)
            throws ServletException, IOException {
        MockHttpServletRequest request = request(method, path, query);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, chain);
        return response;
    }

    private MockHttpServletRequest request(String method, String path, String query) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        request.setRequestURI(path);
        request.setQueryString(query);
        return request;
    }

    private CacheProperties.Path path(String pattern, Duration maxAge, Duration expireAfterWrite) {
        CacheProperties.Path rule = new CacheProperties.Path();
        rule.setPattern(pattern);
        rule.setMaxAge(maxAge);
        rule.setExpireAfterWrite(expireAfterWrite);
        return rule;
    }

    private CacheProperties properties() {
        CacheProperties properties = new CacheProperties();
        properties.setEnabled(true);

        CacheProperties.Http http = properties.getHttp();
        http.setEnabled(true);
        http.setDefaultMaxAge(Duration.ofSeconds(60));
        http.setEtagEnabled(true);
        http.setLastModifiedEnabled(true);
        http.setPublicCache(false);
        http.setMustRevalidate(false);
        http.setStaleWhileRevalidate(Duration.ofSeconds(30));
        http.setNoStorePaths(List.of("/admin/**", "/api/admin/**", "/auth/**"));

        CacheProperties.Application application = properties.getApplication();
        application.setEnabled(true);
        application.setType("caffeine");
        return properties;
    }

    private static final class ManualTicker implements Ticker {
        private long nanos;

        void advance(Duration duration) {
            nanos += duration.toNanos();
        }

        @Override
        public long read() {
            return nanos;
        }
    }

    private static final class RecordingChain implements FilterChain {
        private int calls;
        private String cacheControl;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException {
            calls++;
            HttpServletResponse http = (HttpServletResponse) response;
            http.setStatus(200);
            http.setContentType("application/json");
            if (cacheControl != null) {
                http.setHeader(HttpHeaders.CACHE_CONTROL, cacheControl);
            }
            http.getWriter().write("{\"ok\":true}");
        }
    }
}
