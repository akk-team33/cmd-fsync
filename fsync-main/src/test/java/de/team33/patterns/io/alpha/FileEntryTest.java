package de.team33.patterns.io.alpha;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileEntryTest {

    private static final Path TEST_PATH = Path.of("target", "testing", FileEntryTest.class.getSimpleName());
    private static final Path MISSING_PATH = TEST_PATH.resolve("missing");
    private static final Path DIRECTORY_PATH = TEST_PATH.resolve("directory");
    private static final Path REGULAR_PATH = DIRECTORY_PATH.resolve("regular.file");
    private static final Path DIR_LINK_PATH = TEST_PATH.resolve("symbolic.link.to.directory");
    private static final Path REGULAR_LINK_PATH = DIRECTORY_PATH.resolve("symbolic.link.to.regular.file");
    private static final Path DEV_PATH = Path.of("/dev/null");
    private static final Instant NOW = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final FileTime FT_NOW = FileTime.from(NOW);

    @BeforeAll
    static void init() throws IOException {
        for (final Path path : Arrays.asList(REGULAR_LINK_PATH, DIR_LINK_PATH, REGULAR_PATH, DIRECTORY_PATH))
            if (Files.exists(path))
                Files.delete(path);

        Files.createDirectories(DIRECTORY_PATH);
        Files.writeString(REGULAR_PATH, REGULAR_PATH.toString(), StandardCharsets.UTF_8);
        Files.createSymbolicLink(DIR_LINK_PATH, DIRECTORY_PATH.getFileName());
        Files.createSymbolicLink(REGULAR_LINK_PATH, REGULAR_PATH.getFileName());
    }

    static Stream<String> path() {
        return Stream.of(MISSING_PATH, REGULAR_PATH, DIRECTORY_PATH, DIR_LINK_PATH, REGULAR_LINK_PATH)
                     .map(Path::toString);
    }

    @ParameterizedTest
    @MethodSource
    final void path(final String path) {
        final Path origin = Path.of(path);
        assertFalse(origin.isAbsolute());

        final Path result = FileEntry.of(origin).path();

        assertTrue(result.isAbsolute());
    }

    @Test
    final void isDirectory() {
        assertFalse(FileEntry.of(MISSING_PATH).isDirectory());
        assertFalse(FileEntry.of(DEV_PATH).isDirectory());
        assertTrue(FileEntry.of(DIRECTORY_PATH).isDirectory());
        assertTrue(FileEntry.of(DIR_LINK_PATH).isDirectory());
        assertFalse(FileEntry.of(REGULAR_PATH).isDirectory());
        assertFalse(FileEntry.of(REGULAR_LINK_PATH).isDirectory());

        assertFalse(FileEntry.of(MISSING_PATH, LinkOption.NOFOLLOW_LINKS).isDirectory());
        assertFalse(FileEntry.of(DEV_PATH, LinkOption.NOFOLLOW_LINKS).isDirectory());
        assertTrue(FileEntry.of(DIRECTORY_PATH, LinkOption.NOFOLLOW_LINKS).isDirectory());
        assertFalse(FileEntry.of(DIR_LINK_PATH, LinkOption.NOFOLLOW_LINKS).isDirectory());
        assertFalse(FileEntry.of(REGULAR_PATH, LinkOption.NOFOLLOW_LINKS).isDirectory());
        assertFalse(FileEntry.of(REGULAR_LINK_PATH, LinkOption.NOFOLLOW_LINKS).isDirectory());
    }

    @Test
    final void isRegularFile() {
        assertFalse(FileEntry.of(MISSING_PATH).isRegularFile());
        assertFalse(FileEntry.of(DEV_PATH).isRegularFile());
        assertFalse(FileEntry.of(DIRECTORY_PATH).isRegularFile());
        assertFalse(FileEntry.of(DIR_LINK_PATH).isRegularFile());
        assertTrue(FileEntry.of(REGULAR_PATH).isRegularFile());
        assertTrue(FileEntry.of(REGULAR_LINK_PATH).isRegularFile());

        assertFalse(FileEntry.of(MISSING_PATH, LinkOption.NOFOLLOW_LINKS).isRegularFile());
        assertFalse(FileEntry.of(DEV_PATH, LinkOption.NOFOLLOW_LINKS).isRegularFile());
        assertFalse(FileEntry.of(DIRECTORY_PATH, LinkOption.NOFOLLOW_LINKS).isRegularFile());
        assertFalse(FileEntry.of(DIR_LINK_PATH, LinkOption.NOFOLLOW_LINKS).isRegularFile());
        assertTrue(FileEntry.of(REGULAR_PATH, LinkOption.NOFOLLOW_LINKS).isRegularFile());
        assertFalse(FileEntry.of(REGULAR_LINK_PATH, LinkOption.NOFOLLOW_LINKS).isRegularFile());
    }

    @Test
    final void isSymbolicLink() {
        assertFalse(FileEntry.of(MISSING_PATH).isSymbolicLink());
        assertFalse(FileEntry.of(DEV_PATH).isSymbolicLink());
        assertFalse(FileEntry.of(DIRECTORY_PATH).isSymbolicLink());
        assertFalse(FileEntry.of(DIR_LINK_PATH).isSymbolicLink());
        assertFalse(FileEntry.of(REGULAR_PATH).isSymbolicLink());
        assertFalse(FileEntry.of(REGULAR_LINK_PATH).isSymbolicLink());

        assertFalse(FileEntry.of(MISSING_PATH, LinkOption.NOFOLLOW_LINKS).isSymbolicLink());
        assertFalse(FileEntry.of(DEV_PATH, LinkOption.NOFOLLOW_LINKS).isSymbolicLink());
        assertFalse(FileEntry.of(DIRECTORY_PATH, LinkOption.NOFOLLOW_LINKS).isSymbolicLink());
        assertTrue(FileEntry.of(DIR_LINK_PATH, LinkOption.NOFOLLOW_LINKS).isSymbolicLink());
        assertFalse(FileEntry.of(REGULAR_PATH, LinkOption.NOFOLLOW_LINKS).isSymbolicLink());
        assertTrue(FileEntry.of(REGULAR_LINK_PATH, LinkOption.NOFOLLOW_LINKS).isSymbolicLink());
    }

    @Test
    final void isOther() {
        assertFalse(FileEntry.of(MISSING_PATH).isOther());
        assertTrue(FileEntry.of(DEV_PATH).isOther());
        assertFalse(FileEntry.of(DIRECTORY_PATH).isOther());
        assertFalse(FileEntry.of(DIR_LINK_PATH).isOther());
        assertFalse(FileEntry.of(REGULAR_PATH).isOther());
        assertFalse(FileEntry.of(REGULAR_LINK_PATH).isOther());

        assertFalse(FileEntry.of(MISSING_PATH, LinkOption.NOFOLLOW_LINKS).isOther());
        assertTrue(FileEntry.of(DEV_PATH, LinkOption.NOFOLLOW_LINKS).isOther());
        assertFalse(FileEntry.of(DIRECTORY_PATH, LinkOption.NOFOLLOW_LINKS).isOther());
        assertFalse(FileEntry.of(DIR_LINK_PATH, LinkOption.NOFOLLOW_LINKS).isOther());
        assertFalse(FileEntry.of(REGULAR_PATH, LinkOption.NOFOLLOW_LINKS).isOther());
        assertFalse(FileEntry.of(REGULAR_LINK_PATH, LinkOption.NOFOLLOW_LINKS).isOther());
    }

    @Test
    final void exists() {
        assertFalse(FileEntry.of(MISSING_PATH).exists());
        assertTrue(FileEntry.of(DEV_PATH).exists());
        assertTrue(FileEntry.of(DIRECTORY_PATH).exists());
        assertTrue(FileEntry.of(DIR_LINK_PATH).exists());
        assertTrue(FileEntry.of(REGULAR_PATH).exists());
        assertTrue(FileEntry.of(REGULAR_LINK_PATH).exists());

        assertFalse(FileEntry.of(MISSING_PATH, LinkOption.NOFOLLOW_LINKS).exists());
        assertTrue(FileEntry.of(DEV_PATH, LinkOption.NOFOLLOW_LINKS).exists());
        assertTrue(FileEntry.of(DIRECTORY_PATH, LinkOption.NOFOLLOW_LINKS).exists());
        assertTrue(FileEntry.of(DIR_LINK_PATH, LinkOption.NOFOLLOW_LINKS).exists());
        assertTrue(FileEntry.of(REGULAR_PATH, LinkOption.NOFOLLOW_LINKS).exists());
        assertTrue(FileEntry.of(REGULAR_LINK_PATH, LinkOption.NOFOLLOW_LINKS).exists());
    }

    @Test
    final void lastModified() throws IOException {
        assertEquals(Files.getLastModifiedTime(DIRECTORY_PATH).toInstant(),
                     FileEntry.of(DIRECTORY_PATH).lastModified());
        assertEquals(Files.getLastModifiedTime(REGULAR_PATH).toInstant(),
                     FileEntry.of(REGULAR_PATH).lastModified());
        assertEquals(Files.getLastModifiedTime(REGULAR_LINK_PATH).toInstant(),
                     FileEntry.of(REGULAR_LINK_PATH).lastModified());
        assertEquals(Files.getLastModifiedTime(DIR_LINK_PATH).toInstant(),
                     FileEntry.of(DIR_LINK_PATH).lastModified());

        final FileEntry missing = FileEntry.of(MISSING_PATH);
        assertThrows(UnsupportedOperationException.class, missing::lastModified);
    }

    @Test
    final void lastAccess() throws IOException {
        assertEquals(Files.getAttribute(DIRECTORY_PATH, "lastAccessTime"),
                     FileTime.from(FileEntry.of(DIRECTORY_PATH).lastAccess()));
        assertEquals(Files.getAttribute(REGULAR_PATH, "lastAccessTime"),
                     FileTime.from(FileEntry.of(REGULAR_PATH).lastAccess()));
        assertEquals(Files.getAttribute(REGULAR_LINK_PATH, "lastAccessTime"),
                     FileTime.from(FileEntry.of(REGULAR_LINK_PATH).lastAccess()));
        assertEquals(Files.getAttribute(DIR_LINK_PATH, "lastAccessTime"),
                     FileTime.from(FileEntry.of(DIR_LINK_PATH).lastAccess()));

        final FileEntry missing = FileEntry.of(MISSING_PATH);
        assertThrows(UnsupportedOperationException.class, missing::lastAccess);
    }

    @Test
    final void creation() throws IOException {
        assertEquals(Files.getAttribute(DIRECTORY_PATH, "creationTime"),
                     FileTime.from(FileEntry.of(DIRECTORY_PATH).creation()));
        assertEquals(Files.getAttribute(REGULAR_PATH, "creationTime"),
                     FileTime.from(FileEntry.of(REGULAR_PATH).creation()));
        assertEquals(Files.getAttribute(REGULAR_LINK_PATH, "creationTime"),
                     FileTime.from(FileEntry.of(REGULAR_LINK_PATH).creation()));
        assertEquals(Files.getAttribute(DIR_LINK_PATH, "creationTime"),
                     FileTime.from(FileEntry.of(DIR_LINK_PATH).creation()));

        final FileEntry missing = FileEntry.of(MISSING_PATH);
        assertThrows(UnsupportedOperationException.class, missing::creation);
    }

    @Test
    final void size() throws IOException {
        assertEquals(Files.size(DIRECTORY_PATH),
                     FileEntry.of(DIRECTORY_PATH).size());
        assertEquals(Files.size(REGULAR_PATH),
                     FileEntry.of(REGULAR_PATH).size());
        assertEquals(Files.size(REGULAR_LINK_PATH),
                     FileEntry.of(REGULAR_LINK_PATH).size());
        assertEquals(Files.size(DIR_LINK_PATH),
                     FileEntry.of(DIR_LINK_PATH).size());

        final FileEntry missing = FileEntry.of(MISSING_PATH);
        assertThrows(UnsupportedOperationException.class, missing::size);
    }

    @Test
    final void content() {
        final List<Path> expected = Stream.of(REGULAR_PATH, REGULAR_LINK_PATH)
                                           .map(Path::toAbsolutePath)
                                           .map(Path::normalize)
                                           .toList();
        final FileEntry directory = FileEntry.of(DIRECTORY_PATH);
        assertEquals(expected, directory.entries().map(FileEntry::path).toList());

        final FileEntry regular = FileEntry.of(REGULAR_PATH);
        assertThrows(UnsupportedOperationException.class, regular::entries);

        final FileEntry dirLink = FileEntry.of(DIR_LINK_PATH, LinkOption.NOFOLLOW_LINKS);
        assertThrows(UnsupportedOperationException.class, dirLink::entries);

        final FileEntry regularLinked = FileEntry.of(REGULAR_LINK_PATH);
        assertThrows(UnsupportedOperationException.class, regularLinked::entries);

        final FileEntry regularLink = FileEntry.of(REGULAR_LINK_PATH, LinkOption.NOFOLLOW_LINKS);
        assertThrows(UnsupportedOperationException.class, regularLink::entries);

        final FileEntry missing = FileEntry.of(MISSING_PATH);
        assertThrows(UnsupportedOperationException.class, missing::entries);
    }
}