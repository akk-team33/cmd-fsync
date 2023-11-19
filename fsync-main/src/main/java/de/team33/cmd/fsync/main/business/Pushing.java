package de.team33.cmd.fsync.main.business;

import de.team33.cmd.fsync.main.common.Context;
import de.team33.patterns.io.alpha.FileEntry;

import java.nio.file.Path;

class Pushing {

    static void bothMissing(final Context context, final Path leftRoot, final Path rightRoot, final Path relative) {
        context.printf("%s: both files are missing -> nothing to do%n", relative);
    }

    static void rightOnly(final Context context, final Path leftRoot, final Path rightRoot, final Path relative) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @FunctionalInterface
    interface Operation {

        void accept(Context context, Path leftRoot, Path rightRoot, Path relative);
    }
}
