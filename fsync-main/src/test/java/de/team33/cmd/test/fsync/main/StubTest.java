package de.team33.cmd.test.fsync.main;

import de.team33.cmd.fsync.main.Stub;
import de.team33.cmd.fsync.main.common.Context;
import de.team33.patterns.io.deimos.TextIO;
import de.team33.patterns.testing.titan.io.Redirected;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StubTest implements Context {

    private static final String ARG_0 = StubTest.class.getSimpleName();

    /**
     * Ensures that {@link Stub#main(String...)}, when called without arguments,
     * writes specific information to standard output.
     */
    @Test
    void main_noArgs() throws Exception {
        final String expected = String.format("%s%n%n",
                                              TextIO.read(StubTest.class, "StubTest-main_noArgs.txt"));

        final String result = Redirected.outputOf(Stub::main);
        // System.out.println(result);

        assertEquals(expected, result);
    }

    @Test
    void main_singleArg() throws Exception {
        final String expected = String.format("%s%n%n",
                                              TextIO.read(StubTest.class, "StubTest-main_singleArg.txt"));

        final String result = Redirected.outputOf(() -> Stub.main(ARG_0));
        // System.out.println(result);

        assertEquals(expected, result);
    }

    @Test
    void main_unknownJob() throws Exception {
        final String expected = String.format("%s%n%n",
                                              TextIO.read(StubTest.class, "StubTest-main_unknownJob.txt"));

        final String result = Redirected.outputOf(() -> Stub.main(ARG_0, "unknown"));
        // System.out.println(result);

        assertEquals(expected, result);
    }

    @Test
    void main_about() throws Exception {
        final String expected = String.format("%s%n%n",
                                              TextIO.read(StubTest.class, "StubTest-main_about.txt"));

        final String result = Redirected.outputOf(() -> Stub.main(ARG_0, "about"));
        // System.out.println(result);

        assertEquals(expected, result);
    }
}
