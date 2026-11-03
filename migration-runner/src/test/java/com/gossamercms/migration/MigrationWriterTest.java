package com.gossamercms.migration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MigrationWriterTest {
    @TempDir
    Path root;

    @Test
    void discoversNestedMigrationsAcrossProjectsAndSkipsComments() throws Exception {
        Path first = root.resolve("shared");
        Path second = root.resolve("engagement");
        Path a = first.resolve("users/src/main/resources/db/migration/sql/V1__users.sql");
        Path b = second.resolve("events/src/main/resources/db/migration/V2__events.sql");
        Files.createDirectories(a.getParent());
        Files.createDirectories(b.getParent());
        Files.writeString(a, "-- CREATE TABLE ignored (id UUID);\n"
                + "CREATE TABLE IF NOT EXISTS public.\"users\" (id UUID);");
        Files.writeString(b, "/* CREATE TABLE ignored2 (id UUID); */\n"
                + "CREATE TABLE events (id UUID);\nCREATE TABLE tickets (id UUID);");
        var migrations = MigrationWriter.findMigrations(List.of(first, second));
        assertEquals(2, migrations.size());
        assertEquals(Set.of("users", "events", "tickets"), MigrationWriter.createdTables(migrations));
    }

    @Test
    void versionsExceedAllModulesWithoutIntegerOverflow() {
        BigInteger maximum = new BigInteger("99999999999999999999999");
        assertEquals(maximum.add(BigInteger.ONE), MigrationWriter.nextVersion(
                List.of(Path.of("V" + maximum + "__future.sql"), Path.of("V1__old.sql"))));
    }

    @Test
    void writesToFlywayDirectoryWithoutOverwriting() throws Exception {
        MigrationWriter.writeMigration(root, "users", "CREATE TABLE users (id UUID);", BigInteger.ONE);
        Path file = root.resolve("db/migration/V1__create_users.sql");
        assertTrue(Files.isRegularFile(file));
        assertThrows(java.nio.file.FileAlreadyExistsException.class,
                () -> MigrationWriter.writeMigration(root, "users", "changed", BigInteger.ONE));
        assertThrows(IllegalArgumentException.class,
                () -> MigrationWriter.writeMigration(root, "../escape", "", BigInteger.TWO));
    }

    @Test
    void rejectsMissingProjectRoots() {
        assertThrows(IllegalArgumentException.class, () -> MigrationRunner.main(new String[0]));
        assertThrows(IllegalArgumentException.class,
                () -> MigrationRunner.main(new String[]{root.toString()}));
    }
}
