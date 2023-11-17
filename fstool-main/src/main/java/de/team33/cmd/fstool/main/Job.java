package de.team33.cmd.fstool.main;

import de.team33.cmd.fstool.main.business.Setup;
import de.team33.cmd.fstool.main.common.BadRequestException;
import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.alpha.TextIO;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Job {

    ABOUT(Args::about, "Get basic info about this application."),
    SETUP(Args::setup, "GET or SET the user specific setup.");

    private static final String NEW_LINE = String.format("%n    ");
    private static final Collector<CharSequence, ?, String> JOINING = Collectors.joining(NEW_LINE);
    private static final String PROBLEM = "Problem:%n%n    Unknown COMMAND: %s%n%n";

    private final Function<Args, Runnable> toRunnable;
    private final String excerpt;

    Job(final Function<Args, Runnable> toRunnable, final String excerpt) {
        this.toRunnable = toRunnable;
        this.excerpt = excerpt;
    }

    static Runnable runnable(final Context context, final String shellCmd, final List<String> args) {
        if (args.isEmpty()) {
            throw newBadRequestException("", shellCmd);
        } else {
            return runnable(new Args(context, shellCmd, args.get(0), args.subList(1, args.size())));
        }
    }

    private static Runnable runnable(final Args args) {
        return Stream.of(values())
                     .filter(job -> job.name().equalsIgnoreCase(args.mainCmd))
                     .findAny()
                     .map(value -> value.toRunnable.apply(args))
                     .orElseThrow(() -> newBadRequestException(String.format(PROBLEM, args.mainCmd), args.shellCmd));
    }

    private static String jobInfos() {
        return Stream.of(values())
                     .map(job -> job.name() + " : " + job.excerpt)
                     .collect(JOINING);
    }

    private static BadRequestException newBadRequestException(final String problem, final String shellCmd) {
        return new BadRequestException(String.format(TextIO.read(Job.class, "job.txt"),
                                                     problem, shellCmd, jobInfos()));
    }

    private record Args(Context context, String shellCmd, String mainCmd, List<String> args) {

        Runnable setup() {
            return Setup.runnable(context, shellCmd, args);
        }

        Runnable about() {
            return () -> context.printf(TextIO.read(Job.class, "about.txt"), shellCmd);
        }
    }
}
