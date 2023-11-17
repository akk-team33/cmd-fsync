package de.team33.cmd.fstool.main;

import de.team33.cmd.fstool.main.common.BadRequestException;
import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.alpha.TextIO;

import java.util.List;

public class Main implements Context {

    private final Runnable job;

    private Main(final List<String> args) {
        this.job = newJob(args);
    }

    public static void main(final String... args) {
        new Main(List.of(args)).run();
    }

    private static String argsInLine(final List<String> args) {
        return args.isEmpty() ? "*called without arguments*" : String.join(" ", args);
    }

    private Runnable newJob(final List<String> args) {
        try {
            if (args.size() < 1) {
                throw new BadRequestException(TextIO.read(Main.class, "main.txt"));
            } else {
                return Job.runnable(this, args.get(0), args.subList(1, args.size()));
            }
        } catch (final BadRequestException e) {
            return () -> printf("%s%nYour request is incomplete or incorrect:%n%n    %s%n%n%s%n%n",
                                TextIO.read(Main.class, "head.txt"),
                                argsInLine(args),
                                e.getMessage());
        }
    }

    private void run() {
        job.run();
    }
}
