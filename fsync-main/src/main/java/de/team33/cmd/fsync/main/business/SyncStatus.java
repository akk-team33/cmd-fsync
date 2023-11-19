package de.team33.cmd.fsync.main.business;

import de.team33.patterns.io.alpha.FileEntry;

import java.util.NoSuchElementException;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

enum SyncStatus {

    BOTH_ARE_MISSING((left, right) -> !left.exists() && !right.exists());

    private final BiPredicate<FileEntry, FileEntry> filter;

    SyncStatus(final BiPredicate<FileEntry, FileEntry> filter) {
        this.filter = filter;
    }

    static SyncStatus of(final FileEntry left, final FileEntry right) {
        return Stream.of(values())
                     .filter(item -> item.filter.test(left, right))
                     .findAny()
                     .orElseThrow(() -> newNoSuchElementException(left, right));
    }

    private static NoSuchElementException newNoSuchElementException(final FileEntry left, final FileEntry right) {
        final String message = String.format("No Status found for ...%n" +
                                                     "    left:  %s%n" +
                                                     "    right: %s%n", left.path(), right.path());
        return new NoSuchElementException(message);
    }
}
