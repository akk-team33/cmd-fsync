package de.team33.cmd.fsync.trial;

import de.team33.cmd.fsync.main.business.SyncStatus;
import de.team33.patterns.io.phobos.FileEntry;

import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;

class Collector {

    private final Path leftRoot;
    private final Path rightRoot;
    private final Map<Path, Entry> map = new TreeMap<>(Comparator.comparing(Path::toString));

    Collector(final Path leftRoot, final Path rightRoot) {
        this.leftRoot = leftRoot;
        this.rightRoot = rightRoot;
    }

    final Stream<StatusEntry> stream() {
        return map.values()
                  .stream()
                  .map(Entry::asStatusEntry);
    }

    final void add(final FileEntry fileEntry) {
        if (fileEntry.path().startsWith(leftRoot)) {
            addLeft(fileEntry);
        } else if (fileEntry.path().startsWith(rightRoot)) {
            addRight(fileEntry);
        } else {
            throw new IllegalArgumentException("neither left nor right -> cannot add <" + fileEntry + ">");
        }
    }

    private void addLeft(final FileEntry fileEntry) {
        final Path path = leftRoot.relativize(fileEntry.path());
        map.computeIfAbsent(path, Entry::new)
           .setLeft(fileEntry);
    }

    private void addRight(final FileEntry fileEntry) {
        final Path path = rightRoot.relativize(fileEntry.path());
        map.computeIfAbsent(path, Entry::new)
           .setRight(fileEntry);
    }

    final void addAll(final Collector other) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    private class Entry implements StatusEntry {

        private final Path relativePath;
        private FileEntry _left;
        private FileEntry _right;

        private Entry(final Path relativePath) {
            this.relativePath = relativePath;
        }

        final FileEntry getLeft() {
            return Optional.ofNullable(_left).orElseGet(() -> FileEntry.of(leftRoot.resolve(relativePath),
                                                                           LinkOption.NOFOLLOW_LINKS));
        }

        final void setLeft(final FileEntry fileEntry) {
            this._left = fileEntry;
        }

        final FileEntry getRight() {
            return Optional.ofNullable(_right).orElseGet(() -> FileEntry.of(rightRoot.resolve(relativePath),
                                                                            LinkOption.NOFOLLOW_LINKS));
        }

        final void setRight(final FileEntry fileEntry) {
            this._right = fileEntry;
        }

        private StatusEntry asStatusEntry() {
            return this;
        }

        @Override
        public final Path getRelativePath() {
            return relativePath;
        }

        public final SyncStatus getSyncStatus() {
            return SyncStatus.map(getLeft(), getRight());
        }
    }
}
