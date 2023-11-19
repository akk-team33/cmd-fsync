package de.team33.cmd.fsync.main.business;

import de.team33.cmd.fsync.main.common.Context;
import de.team33.patterns.io.alpha.FileEntry;

import java.nio.file.Files;
import java.nio.file.Path;

public class Push implements Runnable {

    private final Context context;
    private final Path leftRoot;
    private final Path rightRoot;
    private final Path relative;
    private final FileEntry leftEntry;
    private final FileEntry rightEntry;
    private final SyncStatus status;

    public Push(final Context context, final Path leftRoot, final Path rightRoot, final Path relative) {
        this.context = context;
        this.leftRoot = leftRoot;
        this.rightRoot = rightRoot;
        this.relative = relative;
        this.leftEntry = FileEntry.of(leftRoot.resolve(relative));
        this.rightEntry = FileEntry.of(rightRoot.resolve(relative));
        this.status = SyncStatus.of(leftEntry, rightEntry);
    }

    @Override
    public final void run() {
        if (leftEntry.exists()) {
            runLeftExists();
        } else {
            runLeftMissing();
        }
    }

    private void runLeftMissing() {
        if (rightEntry.exists()) {
            runRightOnly();
        } else {
            context.printf("%s: both files are missing -> nothing to do%n", relative);
        }
    }

    private void runRightOnly() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    private void runLeftExists() {
        if (rightEntry.exists()) {
            runBothExist();
        } else {
            context.printf("%s: both files are missing -> nothing to do%n", relative);
        }
    }

    private void runBothExist() {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
