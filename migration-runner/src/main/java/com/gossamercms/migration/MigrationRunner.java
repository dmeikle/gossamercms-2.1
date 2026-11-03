package com.gossamercms.migration;

import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MigrationRunner {

    public static void main(String[] args) throws Exception {
        boolean dryRun = false;
        List<Path> roots = new ArrayList<>();
        for (String arg : args) {
            if ("--dry-run".equals(arg)) {
                dryRun = true;
            } else {
                Path root = Path.of(arg).toAbsolutePath().normalize();
                if (!Files.isRegularFile(root.resolve("pom.xml"))) {
                    throw new IllegalArgumentException("Invalid Maven project root: " + root);
                }
                roots.add(root);
            }
        }
        if (roots.isEmpty()) {
            throw new IllegalArgumentException(
                    "Usage: MigrationRunner [--dry-run] <project-root> [<project-root> ...]");
        }

        Map<Class<? extends BaseModel>, Path> models = new LinkedHashMap<>();
        for (Path root : roots) {
            for (Class<? extends BaseModel> model : ModelScanner.findAllModels(root)) {
                models.put(model, ModuleLocator.findResourcesFor(model, root));
            }
        }
        if (models.isEmpty()) {
            throw new IllegalStateException("No compiled models found; build the projects first.");
        }

        List<Path> migrations = MigrationWriter.findMigrations(roots);
        var version = MigrationWriter.nextVersion(migrations);
        var tables = MigrationWriter.createdTables(migrations);
        for (var entry : models.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getKey().getName())).toList()) {
            ModelMeta meta = BaseModel.metaOf(entry.getKey());
            if (meta.table() == null || meta.table().isBlank()) {
                System.out.println("Skipping model without a table mapping: " + entry.getKey().getName());
                continue;
            }
            if (tables.contains(meta.table().toLowerCase(java.util.Locale.ROOT))) {
                System.out.println("Skipping existing table: " + meta.table());
                continue;
            }
            String sql = MigrationGenerator.generateCreateTableSql(meta);
            if (dryRun) {
                System.out.println("Would generate V" + version + " for "
                        + meta.table() + " in " + entry.getValue());
                System.out.println(sql);
            } else {
                MigrationWriter.writeMigration(entry.getValue(), meta.table(), sql, version);
            }
            tables.add(meta.table().toLowerCase(java.util.Locale.ROOT));
            version = version.add(java.math.BigInteger.ONE);
        }
    }
}
