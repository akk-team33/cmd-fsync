package de.team33.cmd.test.fsync.main.business;

import de.team33.cmd.fsync.main.business.Comparison;
import de.team33.cmd.fsync.main.common.Context;
import de.team33.patterns.io.deimos.TextIO;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static de.team33.patterns.testing.titan.io.Redirected.outputOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ComparisonTest implements Context {

    @Test
    void run_both_are_missing() throws IOException {
        final Path left = Path.of("left", "is", "missing");
        final Path right = Path.of("right", "is", "missing");
        final String expected = String.format(TextIO.read(getClass(), "ComparisonTest.both_are_missing.txt"),
                                              left, right);

        final String result = outputOf(() -> Comparison.runnable(this,
                                                                 getClass().getSimpleName(),
                                                                 List.of(left.toString(), right.toString()))
                                                       .run());

        assertEquals(expected, result);
    }
}