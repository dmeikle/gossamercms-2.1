package com.gossamercms.optimization.caching;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Loads one cache file per module and picks the file from the request path.
 */
@Slf4j
public final class CacheModuleCatalog {

    private final List<LoadedModule> modules;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private CacheModuleCatalog(List<LoadedModule> modules) {
        this.modules = List.copyOf(modules);
    }

    public static CacheModuleCatalog load(CacheProperties properties) {
        return load(properties, new PathMatchingResourcePatternResolver());
    }

    public static CacheModuleCatalog load(CacheProperties properties, ResourcePatternResolver resolver) {
        List<Resource> resources = new ArrayList<>();
        if (properties.getModules() != null) {
            for (CacheProperties.ModuleFile module : properties.getModules()) {
                if (module == null || module.getLocation() == null || module.getLocation().isBlank()) {
                    continue;
                }
                resources.addAll(resolve(module.getLocation(), resolver, true));
            }
        }
        if (properties.getConfigLocations() != null) {
            for (String location : properties.getConfigLocations()) {
                if (location == null || location.isBlank()) {
                    continue;
                }
                resources.addAll(resolve(location, resolver, false));
            }
        }

        List<LoadedModule> modules = new ArrayList<>();
        List<String> seen = new ArrayList<>();
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        for (Resource resource : resources) {
            String identity = identity(resource);
            if (seen.contains(identity)) {
                continue;
            }
            seen.add(identity);
            modules.add(read(loader, resource, modules.size()));
        }

        if (!modules.isEmpty()) {
            log.info("Loaded {} cache module file(s)", modules.size());
        }
        return new CacheModuleCatalog(modules);
    }

    public boolean isEmpty() {
        return modules.isEmpty();
    }

    public CacheRuleLookup lookup(String requestPath) {
        String path = CachePaths.withoutApiVersion(requestPath);
        List<LoadedModule> matches = new ArrayList<>();
        for (LoadedModule module : modules) {
            if (CachePaths.matches(pathMatcher, module.match(), requestPath)) {
                matches.add(module);
            }
        }
        if (matches.isEmpty()) {
            return CacheRuleLookup.none();
        }

        matches.sort(Comparator
                .comparing(
                        (LoadedModule module) -> CachePaths.withoutApiVersion(module.match()),
                        pathMatcher.getPatternComparator(path)
                )
                .thenComparingInt(LoadedModule::order));
        LoadedModule selected = matches.get(0);
        for (CacheProperties.Path rule : selected.paths()) {
            if (rule != null
                    && rule.getPattern() != null
                    && !rule.getPattern().isBlank()
                    && CachePaths.matches(pathMatcher, rule.getPattern(), requestPath)) {
                return CacheRuleLookup.found(rule);
            }
        }
        return CacheRuleLookup.unlisted();
    }

    private static List<Resource> resolve(String location, ResourcePatternResolver resolver, boolean required) {
        String pattern = includeSubfolders(location);
        try {
            List<Resource> found = Arrays.stream(resolver.getResources(pattern))
                    .filter(Resource::exists)
                    .sorted(Comparator.comparing(CacheModuleCatalog::filename))
                    .toList();
            if (found.isEmpty() && required) {
                throw new IllegalStateException("Cache configuration file not found: " + location);
            }
            return found;
        } catch (IOException ex) {
            if (required) {
                throw new IllegalStateException("Unable to read cache configuration location " + location, ex);
            }
            return List.of();
        }
    }

    private static String includeSubfolders(String location) {
        if (location.contains("**")) {
            return location;
        }
        if (location.endsWith("/*.yaml")) {
            return location.substring(0, location.length() - "/*.yaml".length()) + "/**/*.yaml";
        }
        if (location.endsWith("/*.yml")) {
            return location.substring(0, location.length() - "/*.yml".length()) + "/**/*.yml";
        }
        return location;
    }

    private static LoadedModule read(YamlPropertySourceLoader loader, Resource resource, int order) {
        List<PropertySource<?>> documents;
        try {
            documents = loader.load(filename(resource), resource);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read cache configuration file " + resource.getDescription(), ex);
        }
        if (documents == null || documents.isEmpty()) {
            throw new IllegalStateException("Cache configuration file is empty: " + resource.getDescription());
        }
        if (documents.size() > 1) {
            throw new IllegalStateException(
                    "Cache configuration file must contain one document: " + resource.getDescription()
            );
        }

        MutablePropertySources sources = new MutablePropertySources();
        sources.addFirst(documents.get(0));
        CacheModuleFile file = new Binder(ConfigurationPropertySources.from(sources))
                .bind("", Bindable.of(CacheModuleFile.class))
                .orElseThrow(() -> new IllegalStateException(
                        "Unable to bind cache configuration file " + resource.getDescription()
                ));

        if (file.getMatch() == null || file.getMatch().isBlank()) {
            throw new IllegalStateException(
                    "Cache configuration file is missing match: " + resource.getDescription()
            );
        }

        List<CacheProperties.Path> paths = file.getPaths() == null ? List.of() : List.copyOf(file.getPaths());
        log.debug("Cache module '{}' matches {} ({} endpoint rule(s))", file.getName(), file.getMatch(), paths.size());
        return new LoadedModule(order, file.getName(), file.getMatch(), paths);
    }

    private static String filename(Resource resource) {
        String filename = resource.getFilename();
        return filename == null ? resource.getDescription() : filename;
    }

    private static String identity(Resource resource) {
        try {
            return resource.getURL().toExternalForm();
        } catch (IOException ex) {
            return resource.getDescription();
        }
    }

    private record LoadedModule(int order, String name, String match, List<CacheProperties.Path> paths) {
    }
}
