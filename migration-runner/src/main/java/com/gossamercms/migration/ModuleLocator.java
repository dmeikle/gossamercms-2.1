package com.gossamercms.migration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModuleLocator {

    public static Path findResourcesFor(Class<?> modelClass, Path root) throws IOException {
        String classFile = modelClass.getName().replace('.', '/') + ".class";
        try (var modules = Files.list(root)) {
            var matches = modules.filter(Files::isDirectory)
                    .filter(module -> Files.isRegularFile(module.resolve("target/classes").resolve(classFile)))
                    .toList();
            if (matches.size() != 1) {
                throw new IllegalStateException("Expected one module for " + modelClass.getName()
                        + " under " + root + ", found " + matches.size());
            }
            return matches.getFirst().resolve("src/main/resources");
        }
    }
}
