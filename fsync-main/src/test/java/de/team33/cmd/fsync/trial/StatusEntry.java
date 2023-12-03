package de.team33.cmd.fsync.trial;

import de.team33.cmd.fsync.main.business.SyncStatus;

import java.nio.file.Path;

public interface StatusEntry {

    Path getRelativePath();

    SyncStatus getSyncStatus();
}
