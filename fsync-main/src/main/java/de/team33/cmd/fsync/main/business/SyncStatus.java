package de.team33.cmd.fsync.main.business;

import de.team33.patterns.decision.telesto.Choice;
import de.team33.patterns.io.phobos.FileEntry;

import java.util.function.Function;

import static de.team33.patterns.decision.telesto.Choice.on;

enum SyncStatus {

    SAME_SIZE_DATE;

    static SyncStatus map(final FileEntry left, final FileEntry right) {
        return Case.HEAD.apply(new Input(left, right));
    }

    private enum Case implements Function<Input, SyncStatus> {

        SAME_DATE(on(Input::isSameSize).reply(SAME_SIZE_DATE).orApply(fail("SAME_DATE"))),
        BOTH_REGULAR(on(Input::isSameDate).apply(SAME_DATE).orApply(fail("BOTH_REGULAR"))),
        LEFT_REGULAR(on(Input::isRightRegular).apply(BOTH_REGULAR).orApply(fail("LEFT_REGULAR"))),
        HEAD(on(Input::isLeftRegular).apply(LEFT_REGULAR).orApply(fail("HEAD")));

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

        final boolean isLeftRegular() {
            return left.isRegularFile();
        }

        final boolean isRightRegular() {
            return right.isRegularFile();
        }

        final boolean isSameDate() {
            return left.lastModified().equals(right.lastModified());
        }

        final boolean isSameSize() {
            return left.size() == right.size();
        }
    }
}
