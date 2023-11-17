package de.team33.cmd.fstool.main.common;

public interface Context {

    default Config config() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    default void printf(final String format, final Object... args) {
        System.out.printf(format, args);
    }
}
