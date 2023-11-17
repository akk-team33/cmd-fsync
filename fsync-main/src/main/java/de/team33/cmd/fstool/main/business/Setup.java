package de.team33.cmd.fstool.main.business;

import de.team33.cmd.fstool.main.common.BadRequestException;
import de.team33.cmd.fstool.main.common.Config;
import de.team33.cmd.fstool.main.common.Context;
import de.team33.patterns.io.alpha.TextIO;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class Setup {

    private static final String TOO_MANY_ARGUMENTS = "Too many arguments.";
    private static final String UNKNOWN_MODE = "Unknown MODE: ";

    private final Context context;
    private final Path path;

    private Setup(final Context context, final Path path) {
        this.context = context;
        this.path = path;
    }

    public static Runnable runnable(final Context context, final String shellCmd, final List<String> args) {
        if (args.isEmpty()) {
            throw newBadRequestException("", shellCmd);
        } else {
            return Mode.runnable(context, shellCmd, args.get(0), args.subList(1, args.size()));
        }
    }

    private static String problem(final String message) {
        return String.format("Problem:%n%n    %s%n%n", message);
    }

    private static BadRequestException newBadRequestException(final String problem, final String shellCmd) {
        return new BadRequestException(String.format(TextIO.read(Setup.class, "Setup.txt"),
                                                     problem, shellCmd));
    }

    private static Runnable resetting(final Args args) {
        return new Setup(args.context, null)::reset;
    }

    private static Runnable setting(final Args args) {
        return new Setup(args.context, Path.of(args.args.get(0)).toAbsolutePath().normalize())::set;
    }

    private static Runnable getting(final Args args) {
        return new Setup(args.context, null)::get;
    }

    private void get() {
        context.printf("%s%n", context.config());
    }

    private void set() {
        final Config config = context.config().read(path);
        config.write(context.config().path());
        context.printf("%s%n", config);
    }

    private void reset() {
        final Config config = context.config().reset();
        config.write(context.config().path());
        context.printf("%s%n", config);
    }

    private enum Mode {

        GET(Setup::getting, 0),
        SET(Setup::setting, 1),
        RESET(Setup::resetting, 0);

        private final Function<Args, Runnable> toRunnable;
        private final int argsSize;

        Mode(final Function<Args, Runnable> toRunnable, final int argsSize) {
            this.toRunnable = toRunnable;
            this.argsSize = argsSize;
        }

        static Runnable runnable(final Context context, final String shellCmd,
                                 final String modeName, final List<String> args) {
            return Stream.of(values())
                         .filter(mode -> mode.name().equalsIgnoreCase(modeName))
                         .findAny()
                         .map(mode -> mode.runnable(new Args(context, shellCmd, args)))
                         .orElseThrow(() -> newBadRequestException(problem(UNKNOWN_MODE + modeName), shellCmd));
        }

        private Runnable runnable(final Args args) {
            if (args.args.size() < argsSize) {
                throw newBadRequestException(problem("Missing argument: PATH"), args.shellCmd);
            }
            if (args.args.size() > argsSize) {
                throw newBadRequestException(problem(TOO_MANY_ARGUMENTS), args.shellCmd);
            }
            return toRunnable.apply(args);
        }
    }

    private record Args(Context context, String shellCmd, List<String> args) {
    }
}
