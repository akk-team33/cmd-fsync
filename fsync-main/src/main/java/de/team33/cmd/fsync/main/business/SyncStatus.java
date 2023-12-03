package de.team33.cmd.fsync.main.business;

import de.team33.patterns.io.phobos.FileEntry;

import java.util.function.Function;

import static de.team33.patterns.decision.telesto.Choice.on;
import static java.lang.String.format;

public enum SyncStatus {

    /**
     * Both files are missing.
     */
    BOTH_MISSING,

    /**
     * Only the "left" file really exists (and is a regular file)
     */
    LEFT_ONLY,

    /**
     * Only the "right" file really exists (and is a regular file)
     */
    RIGHT_ONLY,

    /**
     * Both files are regular files, with the "left" file being last modified later.
     * So you can easily update from "left" to "right".
     */
    LEFT_MORE_RECENT,

    /**
     * Both files are regular files, with the "right" file being last modified later.
     * So you can easily update from "right" to "left".
     */
    RIGHT_MORE_RECENT,

    /**
     * The date and time the files were last modified are the same but size is different.
     */
    DIFFERENT_SIZE,

    /**
     * The date and time the files were last modified and their size are the same.
     * So the files are probably in sync.
     */
    PROBABLY_SYNC,

    /**
     * Both files exist, but their types do not allow comparison or synchronization.
     */
    INCOMPARABLE;

    public static SyncStatus map(final FileEntry left, final FileEntry right) {
        return switch (left.type()) {
            case REGULAR -> mapLeftIsRegular(left, right);
            case MISSING -> mapLeftIsMissing(left, right);
            default -> INCOMPARABLE;
        };
    }

    private static SyncStatus mapLeftIsRegular(final FileEntry left, final FileEntry right) {
        return switch (right.type()) {
            case REGULAR -> mapDate(left, right);
            case MISSING -> LEFT_ONLY;
            default -> INCOMPARABLE;
        };
    }

    private static SyncStatus mapLeftIsMissing(final FileEntry left, final FileEntry right) {
        return switch (right.type()) {
            case REGULAR -> RIGHT_ONLY;
            case MISSING -> BOTH_MISSING;
            default -> INCOMPARABLE;
        };
    }

    private static SyncStatus mapDate(final FileEntry left, final FileEntry right) {
        final int compared = left.lastModified().compareTo(right.lastModified());
        if (0 < compared) {
            return LEFT_MORE_RECENT;
        } else if (0 > compared) {
            return RIGHT_MORE_RECENT;
        } else {
            return mapSize(left, right);
        }
    }

    private static SyncStatus mapSize(final FileEntry left, final FileEntry right) {
        return (left.size() == right.size()) ? PROBABLY_SYNC : DIFFERENT_SIZE;
    }
}
