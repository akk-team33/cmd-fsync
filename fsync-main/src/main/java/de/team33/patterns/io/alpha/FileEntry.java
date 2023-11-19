package de.team33.patterns.io.alpha;

import de.team33.patterns.lazy.narvi.Lazy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents an entry from a virtual file index.
 * Includes some meta information about a file, particularly the file system path, file type, size,
 * and some timestamps.
 */
public abstract class FileEntry {

    private final Path path;

    private FileEntry(final Path path) {
        this.path = path;
    }

    /**
     * Returns a FileEntry of a given {@link Path}.
     */
    public static FileEntry of(final Path path, final LinkOption... options) {
        return ofNormal(path.toAbsolutePath().normalize(), options);
    }

    private static FileEntry ofNormal(final Path path, final LinkOption[] options) {
        try {
            final BasicFileAttributes attributes = Files.readAttributes(path,
                                                                        BasicFileAttributes.class,
                                                                        options);
            return attributes.isDirectory()
                    ? new Directory(path, attributes, options)
                    : new NoDirectory(path, attributes);
        } catch (final IOException e) {
            return new Missing(path, e);
        }
    }

    /**
     * Returns the file system path of the represented file.
     * This implementation returns an {@link Path#toAbsolutePath() absolute} {@link Path#normalize() normalized}
     * {@link Path}.
     */
    public final Path path() {
        return path;
    }

    /**
     * Determines if the represented file is a directory.
     */
    public abstract boolean isDirectory();

    /**
     * Determines if the represented file is a regular file.
     */
    public abstract boolean isRegularFile();

    /**
     * Determines if the represented file is a symbolic link.
     */
    public abstract boolean isSymbolicLink();

    /**
     * Determines if the represented file is something else than a directory, a regular file or a symbolic link.
     * E.g. a special file.
     */
    public abstract boolean isOther();

    /**
     * Determines if the represented file exists.
     */
    public abstract boolean exists();

    /**
     * Returns the timestamp of the last modification of the represented file.
     *
     * @throws UnsupportedOperationException if the file does not exist.
     */
    public abstract Instant lastModified();

    /**
     * Returns the timestamp of the last access to the represented file.
     *
     * @throws UnsupportedOperationException if the file does not exist.
     */
    public abstract Instant lastAccess();

    /**
     * Returns the timestamp of the creation of the represented file.
     *
     * @throws UnsupportedOperationException if the file does not exist.
     */
    public abstract Instant creation();

    /**
     * Returns the size of the represented file.
     *
     * @throws UnsupportedOperationException if the file does not exist.
     */
    public abstract long size();

    /**
     * Returns the entries of the content of the represented file if it {@link #isDirectory() is a directory}.
     *
     * @throws UnsupportedOperationException if the represented file is not a directory.
     */
    public abstract Stream<FileEntry> entries();

    private static final class Directory extends Existing {

        private static final Comparator<Path> ORDER = Comparator.comparing(path -> path.getFileName().toString());

        private final LinkOption[] options;
        private final Lazy<List<FileEntry>> lazyContent;

        Directory(final Path path, final BasicFileAttributes attributes, final LinkOption[] options) {
            super(path, attributes);
            this.options = options;
            this.lazyContent = Lazy.init(this::newContent);
        }

        private List<FileEntry> newContent() {
            try (final Stream<Path> stream = Files.list(path())) {
                return stream.sorted(ORDER)
                             .map(path -> of(path, options))
                             .toList();
            } catch (final IOException ignored) {
                return List.of();
            }
        }

        @Override
        public final Stream<FileEntry> entries() {
            return lazyContent.get().stream();
        }
    }

    private static class NoDirectory extends Existing {

        NoDirectory(final Path path, final BasicFileAttributes attributes) {
            super(path, attributes);
        }

        @Override
        public final Stream<FileEntry> entries() {
            throw new UnsupportedOperationException("this is not a directory - only directories can have entries");
        }
    }

    private static abstract class Existing extends FileEntry {

        private final BasicFileAttributes attributes;

        Existing(final Path path, final BasicFileAttributes attributes) {
            super(path);
            this.attributes = attributes;
        }

        @Override
        public final boolean isDirectory() {
            return attributes.isDirectory();
        }

        @Override
        public final boolean isRegularFile() {
            return attributes.isRegularFile();
        }

        @Override
        public final boolean isSymbolicLink() {
            return attributes.isSymbolicLink();
        }

        @Override
        public final boolean isOther() {
            return attributes.isOther();
        }

        @Override
        public final boolean exists() {
            return true;
        }

        @Override
        public final Instant lastModified() {
            return attributes.lastModifiedTime().toInstant();
        }

        @Override
        public final Instant lastAccess() {
            return attributes.lastAccessTime().toInstant();
        }

        @Override
        public final Instant creation() {
            return attributes.creationTime().toInstant();
        }

        @Override
        public final long size() {
            return attributes.size();
        }
    }

    private static class Missing extends FileEntry {

        private final IOException cause;

        Missing(final Path path, final IOException cause) {
            super(path);
            this.cause = cause;
        }

        @Override
        public final boolean isDirectory() {
            return false;
        }

        @Override
        public final boolean isRegularFile() {
            return false;
        }

        @Override
        public final boolean isSymbolicLink() {
            return false;
        }

        @Override
        public final boolean isOther() {
            return false;
        }

        @Override
        public final boolean exists() {
            return false;
        }

        @Override
        public final Instant lastModified() {
            throw new UnsupportedOperationException("a missing file cannot have a timestamp", cause);
        }

        @Override
        public final Instant lastAccess() {
            throw new UnsupportedOperationException("a missing file cannot have a timestamp", cause);
        }

        @Override
        public final Instant creation() {
            throw new UnsupportedOperationException("a missing file cannot have a timestamp", cause);
        }

        @Override
        public final long size() {
            throw new UnsupportedOperationException("a missing file cannot have a size", cause);
        }

        @Override
        public final Stream<FileEntry> entries() {
            throw new UnsupportedOperationException("a missing file cannot have a entries", cause);
        }
    }
}
