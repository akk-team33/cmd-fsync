package de.team33.cmd.fsync.main;

import de.team33.cmd.fsync.main.common.BadRequestException;
import de.team33.cmd.fsync.main.common.Context;
import de.team33.patterns.io.deimos.TextIO;

import java.util.List;

public final class Stub implements Context {

    private final Runnable job;

    private Stub(final List<String> args) {
        this.job = newJob(args);
    }

    public static void main(final String... args) {
        new Stub(List.of(args)).run();
    }

    private static String argsInLine(final List<String> args) {
        return args.isEmpty() ? "*called without arguments*" : String.join(" ", args);
    }

    private Runnable newJob(final List<String> args) {
        try {
            if (1 > args.size()) {
                throw new BadRequestException(TextIO.read(Stub.class, "main.txt"));
            } else {
                return Job.runnable(this, args.get(0), args.subList(1, args.size()));
            }
        } catch (final BadRequestException e) {
            return () -> printf("%s%nYour request is incomplete or incorrect:%n%n    %s%n%n%s%n%n",
                                TextIO.read(Stub.class, "head.txt"),
                                argsInLine(args),
                                e.getMessage());
        }
    }

    private void run() {
        job.run();
    }
}
