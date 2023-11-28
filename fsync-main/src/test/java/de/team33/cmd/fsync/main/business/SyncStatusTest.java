package de.team33.cmd.fsync.main.business;

import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.io.phobos.FileIndex;
import de.team33.patterns.testing.titan.io.ZipIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SyncStatusTest {

    private static final Path TEST_PATH = Path.of("target", "testing", SyncStatusTest.class.getSimpleName())
                                              .toAbsolutePath()
                                              .normalize();
    private static final String SAME_SIZE_DATE = "same.size.date";
    private static final String LEFT_ONLY = "left.only";

    private Path testPath;

    @BeforeEach
    final void beforeEach() {
        testPath = TEST_PATH.resolve(UUID.randomUUID().toString());
        ZipIO.unzip(getClass(), "/template.zip", testPath);
    }

    @Test
    final void map_PROBABLY_SYNC() {
        final Path left = testPath.resolve("left").resolve(SAME_SIZE_DATE);
        final Path right = testPath.resolve("right").resolve(SAME_SIZE_DATE);
        final FileIndex index = FileIndex.of(left);

        index.stream().filter(FileEntry::isRegularFile).forEach(entry -> {
            // System.out.println(entry);
            final Path relative = left.relativize(entry.path());
            final SyncStatus result = SyncStatus.map(entry, FileEntry.of(right.resolve(relative)));
            assertEquals(SyncStatus.PROBABLY_SYNC, result);
        });
    }

    @Test
    final void map_LEFT_ONLY() {
        final Path left = testPath.resolve("left").resolve(LEFT_ONLY);
        final Path right = testPath.resolve("right").resolve(LEFT_ONLY);
        final FileIndex index = FileIndex.of(left);

        index.stream().filter(FileEntry::isRegularFile).forEach(entry -> {
            System.out.println(entry);
            final Path relative = left.relativize(entry.path());
            final SyncStatus result = SyncStatus.map(entry, FileEntry.of(right.resolve(relative)));
            assertEquals(SyncStatus.LEFT_ONLY, result);
        });
    }
}
