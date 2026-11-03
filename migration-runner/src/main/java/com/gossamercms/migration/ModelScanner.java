package com.gossamercms.migration;

import com.gossamercms.mvc.models.BaseModel;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ModelScanner {

    public static List<Class<? extends BaseModel>> findAllModels(Path root)
            throws IOException, ClassNotFoundException {
        List<Class<? extends BaseModel>> models = new ArrayList<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (var modules = Files.list(root)) {
            for (Path module : modules.filter(Files::isDirectory).sorted().toList()) {
                Path classes = module.resolve("target/classes");
                if (!Files.isDirectory(classes)) {
                    continue;
                }
                try (var files = Files.walk(classes)) {
                    for (Path file : files.filter(p -> p.toString().endsWith(".class")).sorted().toList()) {
                        String name = classes.relativize(file).toString()
                                .replace(java.io.File.separatorChar, '.');
                        name = name.substring(0, name.length() - ".class".length());
                        if (!name.startsWith("com.gossamercms.") || name.contains("$")) {
                            continue;
                        }
                        Class<?> type = Class.forName(name, false, loader);
                        if (BaseModel.class.isAssignableFrom(type)
                                && !type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
                            models.add(type.asSubclass(BaseModel.class));
                        }
                    }
                }
            }
        }
        return models;
    }
}
