package com.gossamercms.optimization.caching;

import org.springframework.util.AntPathMatcher;

import java.util.regex.Pattern;

/**
 * Cache rules omit the API version segment. {@code /api}, {@code /api1.0}, and {@code /api2.0}
 * are the same prefix when matching a pattern.
 */
public final class CachePaths {

    private static final Pattern API_VERSION = Pattern.compile("(?i)api(?:\\d+(?:\\.\\d+)*)?");

    private CachePaths() {
    }

    public static boolean matches(AntPathMatcher matcher, String pattern, String path) {
        if (pattern == null || path == null) {
            return false;
        }
        return matcher.match(withoutApiVersion(pattern), withoutApiVersion(path));
    }

    public static String withoutApiVersion(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }
        String normalized = path.startsWith("/") ? path : "/" + path;
        String[] segments = normalized.split("/", -1);
        if (segments.length < 2 || !API_VERSION.matcher(segments[1]).matches()) {
            return normalized;
        }

        StringBuilder rest = new StringBuilder();
        for (int i = 2; i < segments.length; i++) {
            if (segments[i].isEmpty() && i == segments.length - 1) {
                continue;
            }
            rest.append('/').append(segments[i]);
        }
        return rest.isEmpty() ? "/" : rest.toString();
    }
}
