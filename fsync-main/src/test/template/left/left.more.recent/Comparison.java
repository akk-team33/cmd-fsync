package de.team33.cmd.fsync.main.business;

import de.team33.cmd.fsync.main.common.BadRequestException;
import de.team33.cmd.fsync.main.common.Context;

import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;

public class Comparison implements Runnable {
    private static final LinkOption[] LINK_OPTIONS = {LinkOption.NOFOLLOW_LINKS};
    private final Context context;
    private final Path left;
    private final Path right;

    private Comparison(final Context context, final Path left, final Path right) {
        this.context = context;
        this.left = left.toAbsolutePath().normalize();
        this.right = right.toAbsolutePath().normalize();
    }

    public static Runnable runnable(final Context context, final String shellCmd, final List<String> args) {
        if (args.size() == 2)
            return new Comparison(context, Path.of(args.get(0)), Path.of(args.get(1)));
        else
            throw new BadRequestException("TODO");
    }

    @Override
    public final void run() {

    }
}
