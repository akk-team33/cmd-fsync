package de.team33.cmd.fsync.trial;

import de.team33.cmd.fsync.main.business.SyncStatus;
import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.io.phobos.FileIndex;
import de.team33.patterns.testing.titan.io.ZipIO;

import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatusTrial {

    private static final Path BASE_PATH = Path.of("target", "testing",
                                                  StatusTrial.class.getSimpleName(),
                                                  UUID.randomUUID().toString())
                                              .toAbsolutePath()
                                              .normalize();
    private static final Path LEFT_PATH = BASE_PATH.resolve("left");
    private static final Path RIGHT_PATH = BASE_PATH.resolve("right");

    private static void init() {
        ZipIO.unzip(StatusTrial.class, "/template.zip", BASE_PATH);
    }

    public static void main(final String[] args) {
        init();
        run();
    }

    private static void run() {
        final Map<SyncStatus, List<StatusEntry>> map = //
                Stream.concat(FileIndex.of(LEFT_PATH, LinkOption.NOFOLLOW_LINKS)
                                       .stream(),
                              FileIndex.of(RIGHT_PATH, LinkOption.NOFOLLOW_LINKS)
                                       .stream())
                      .filter(FileEntry::isRegularFile)
                      .collect(() -> new Collector(LEFT_PATH, RIGHT_PATH), Collector::add, Collector::addAll)
                      .stream()
                      .peek(entry -> System.out.printf("%s ... %s%n", entry.getRelativePath(), entry.getSyncStatus()))
                      .collect(Collectors.groupingBy(StatusEntry::getSyncStatus));

        System.out.printf("%nStatus of ...%n    left:  %s%n    right: %s%n%n", LEFT_PATH, RIGHT_PATH);
        map.forEach((status, entries) -> {
            System.out.printf("%s : %d entries%n", status, entries.size());
        });

        if (map.containsKey(SyncStatus.LEFT_MORE_RECENT)) {
            if (map.containsKey(SyncStatus.RIGHT_MORE_RECENT)) {
                System.out.printf("%nLeft and right seem out of sync.%n" +
                                          "There are entries on both sites that are more recent than the other one!%n");
                System.out.printf("%nRecommendation: do both ...%n%n" +
                                          "    fsync push %1$s %2$s%n" +
                                          "    fsync pull %1$s %2$s%n%n" +
                                          "... to synchronize existing files and than care for missing files",
                                  LEFT_PATH, RIGHT_PATH);
            } else if (map.containsKey(SyncStatus.RIGHT_ONLY)) {
                System.out.printf("%nLeft seem to be more recent.%n");
                System.out.printf("%nRecommendation: do ...%n%n" +
                                          "    fsync push %1$s %2$s%n%n" +
                                          "... to synchronize existing files and than care for missing files or ...%n%n" +
                                          "    fsync push -f %1$s %2$s%n%n" +
                                          "... to synchronize existing and 'left only' files and remove 'right only' files",
                                  LEFT_PATH, RIGHT_PATH);
            } else {

            }
        }
    }

}
