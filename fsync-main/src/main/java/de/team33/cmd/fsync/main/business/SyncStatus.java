package de.team33.cmd.fsync.main.business;

import de.team33.patterns.io.alpha.FileEntry;

import java.util.function.Function;
import java.util.function.Predicate;

import static de.team33.patterns.misc.alpha.Choice.on;

enum SyncStatus {

    BOTH_MISSING(Pushing::bothMissing),
    RIGHT_ONLY(Pushing::rightOnly);

    final Pushing.Operation pushing;

    SyncStatus(final Pushing.Operation pushing) {
        this.pushing = pushing;
    }

    static SyncStatus map(final FileEntry left, final FileEntry right) {
        return Cascade.HEAD.apply(new Input(left, right));
    }

    private enum Cascade implements Function<Input, SyncStatus> {
        LEFT_MISSING_RIGHT_EXISTS(null),
        LEFT_MISSING_RIGHT_NOT_REGULAR(on(Condition.RIGHT_EXISTS).apply(LEFT_MISSING_RIGHT_EXISTS)
                                                                 .orReply(BOTH_MISSING)),
        LEFT_IS_MISSING(on(Condition.RIGHT_IS_REGULAR).reply(RIGHT_ONLY)
                                                      .orApply(LEFT_MISSING_RIGHT_NOT_REGULAR)),
        LEFT_EXISTS(null),
        LEFT_REGULAR_RIGHT_NOT(null),
        BOTH_ARE_REGULAR(null),
        LEFT_NOT_REGULAR(on(Condition.LEFT_EXISTS).apply(LEFT_EXISTS).orApply(LEFT_IS_MISSING)),
        LEFT_IS_REGULAR(on(Condition.RIGHT_IS_REGULAR).apply(BOTH_ARE_REGULAR).orApply(LEFT_REGULAR_RIGHT_NOT)),
        HEAD(on(Condition.LEFT_IS_REGULAR).apply(LEFT_IS_REGULAR).orApply(LEFT_NOT_REGULAR));

        private final Function<Input, SyncStatus> backing;

        Cascade(final Function<Input, SyncStatus> backing) {
            this.backing = backing;
        }

        @Override
        public SyncStatus apply(final Input input) {
            return backing.apply(input);
        }
    }

    @FunctionalInterface
    private interface Condition extends Predicate<Input> {

        Condition LEFT_EXISTS = input -> input.left.exists();
        Condition LEFT_IS_REGULAR = input -> input.left.isRegularFile();
        Condition RIGHT_EXISTS = input -> input.right.exists();
        Condition RIGHT_IS_REGULAR = input -> input.right.isRegularFile();
    }

    record Input(FileEntry left, FileEntry right) {
    }
}
