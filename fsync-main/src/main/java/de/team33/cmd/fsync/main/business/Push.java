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
    private final Pushing.Operation pushing;

    public Push(final Context context, final Path leftRoot, final Path rightRoot, final Path relative) {
        this.context = context;
        this.leftRoot = leftRoot;
        this.rightRoot = rightRoot;
        this.relative = relative;
        this.leftEntry = FileEntry.of(leftRoot.resolve(relative));
        this.rightEntry = FileEntry.of(rightRoot.resolve(relative));
        this.pushing = SyncStatus.map(leftEntry, rightEntry).pushing;
    }

    @Override
    public final void run() {
        pushing.accept(context, leftRoot, rightRoot, relative);
    }
}
