package com.gossamercms.migration;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public class MigrationWriter {

    private static final Pattern VERSION = Pattern.compile("^V(\\d+)__.+\\.sql$");
    private static final Pattern CREATE_TABLE = Pattern.compile(
            "\\bCREATE\\s+TABLE\\s+(?:IF\\s+NOT\\s+EXISTS\\s+)?"
                    + "(?:\"?public\"?\\s*\\.\\s*)?\"?([a-zA-Z_][a-zA-Z_0-9]*)\"?\\s*\\(",
            Pattern.CASE_INSENSITIVE);

    public static List<Path> findMigrations(List<Path> roots) throws IOException {
        List<Path> migrations = new ArrayList<>();
        for (Path root : roots) {
            try (var modules = Files.list(root)) {
                for (Path module : modules.filter(Files::isDirectory).sorted().toList()) {
                    Path directory = module.resolve("src/main/resources/db/migration");
                    if (Files.isDirectory(directory)) {
                        try (var files = Files.walk(directory)) {
                            migrations.addAll(files.filter(Files::isRegularFile)
                                    .filter(p -> p.toString().endsWith(".sql")).sorted().toList());
                        }
                    }
                }
            }
        }
        return migrations;
    }

    public static Set<String> createdTables(List<Path> migrations) throws IOException {
        Set<String> tables = new HashSet<>();
        for (Path migration : migrations) {
            String sql = Files.readString(migration)
                    .replaceAll("(?s)/\\*.*?\\*/", "")
                    .replaceAll("(?m)--[^\\r\\n]*", "");
            var matcher = CREATE_TABLE.matcher(sql);
            while (matcher.find()) {
                tables.add(matcher.group(1).toLowerCase(Locale.ROOT));
            }
        }
        return tables;
    }

    public static BigInteger nextVersion(List<Path> migrations) {
        BigInteger next = new BigInteger(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        for (Path migration : migrations) {
            var matcher = VERSION.matcher(migration.getFileName().toString());
            if (matcher.matches()) {
                next = next.max(new BigInteger(matcher.group(1)).add(BigInteger.ONE));
            }
        }
        return next;
    }

    public static void writeMigration(Path resourcesDir, String table, String sql, BigInteger version)
            throws IOException {
        if (!table.matches("[a-zA-Z_][a-zA-Z_0-9]*")) {
            throw new IllegalArgumentException("Unsupported table name: " + table);
        }
        Path migrationDir = resourcesDir.resolve("db/migration");
        Files.createDirectories(migrationDir);
        Path file = migrationDir.resolve("V" + version + "__create_" + table + ".sql");
        Files.writeString(file, sql, StandardOpenOption.CREATE_NEW);
        System.out.println("Generated migration: " + file);
    }
}
