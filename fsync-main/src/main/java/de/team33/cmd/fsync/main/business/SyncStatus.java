package de.team33.cmd.fsync.main.business;

import de.team33.patterns.io.phobos.FileEntry;

import java.util.function.Function;

import static de.team33.patterns.decision.telesto.Choice.on;
import static java.lang.String.format;

enum SyncStatus {

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
     * The date and time the files were last modified and their size are the same.
     * So the files are probably in sync.
     */
    PROBABLY_SYNC,

    /**
     * Both files exist, but their types do not allow comparison or synchronization.
     */
    INCOMPARABLE;

    static SyncStatus map(final FileEntry left, final FileEntry right) {
        if (left.isRegularFile()) {
            return Case.HEAD.apply(new Input(left, right));
        } else {
            throw new IllegalArgumentException(format("<%s> is expected to be a regular file - but was %s",
                                                      left.path(), left.type().name()));
        }
    }

    private static SyncStatus mapType(final FileEntry left, final FileEntry right) {
        return switch (right.type()) {
            case MISSING -> LEFT_ONLY;
            case REGULAR -> mapDate(left, right);
            default -> {
                final String message = format("<%s> is expected to be missing or a regular file - but was %s",
                                              right.path(), right.type().name());
                throw new IllegalArgumentException(message);
            }
        };
    }

    private static SyncStatus mapDate(FileEntry left, FileEntry right) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    private enum Case implements Function<Input, SyncStatus> {

        SAME_DATE(on(Input::isSameSize).reply(PROBABLY_SYNC).orApply(fail("SAME_DATE"))),
        BOTH_REGULAR(on(Input::isSameDate).apply(SAME_DATE).orApply(fail("BOTH_REGULAR"))),
        RIGHT_NOT_REGULAR(on(Input::isRightExisting).apply(fail("RIGHT_NOT_REGULAR")).orReply(LEFT_ONLY)),
        HEAD(on(Input::isRightRegular).apply(BOTH_REGULAR).orApply(RIGHT_NOT_REGULAR));

        private static Function<Input, SyncStatus> fail(final String hint) {
            return input -> {
                throw new UnsupportedOperationException("not yet implemented: " + hint);
            };
        }

        private final Function<Input, SyncStatus> backing;

        Case(final Function<Input, SyncStatus> backing) {
            this.backing = backing;
        }

        @Override
        public final SyncStatus apply(final Input input) {
            return backing.apply(input);
        }
    }

    private record Input(FileEntry left, FileEntry right) {

        final boolean isRightRegular() {
            return right.isRegularFile();
        }

        final boolean isRightExisting() {
            return right.exists();
        }

        final boolean isSameDate() {
            return left.lastModified().equals(right.lastModified());
        }

        final boolean isSameSize() {
            return left.size() == right.size();
        }
    }
}
