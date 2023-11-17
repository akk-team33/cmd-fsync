package de.team33.cmd.fsync.main;

import de.team33.cmd.fsync.main.common.BadRequestException;
import de.team33.cmd.fsync.main.common.Context;
import de.team33.patterns.io.alpha.TextIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JobTest implements Context {

    @Test
    void runnable_insufficient() {
        try {
            final Runnable result = Job.runnable(this, JobTest.class.getSimpleName(), List.of("setup"));
            fail("expected to fail - but was " + result);
        } catch (final BadRequestException e) {
            // e.printStackTrace();
            // as expected!
        }
    }

    @ParameterizedTest
    @EnumSource
    void runnable_failing(final Case testCase) throws Exception {
        try {
            final Runnable result = Job.runnable(this, JobTest.class.getSimpleName(), testCase.args);
            fail("expected to fail - but was " + result);
        } catch (final BadRequestException e) {
            final String expected = TextIO.read(JobTest.class, testCase.expected);
            assertEquals(expected, e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    enum Case {

        NO_ARGS(List.of(), "JobTest-runnable_noArgs.txt"),
        UNKNOWN_JOB(List.of("unknown"), "JobTest-runnable_unknown.txt"),
        UNKNOWN_JOB_MULTIPLE_ARGS(List.of("unknown", "arg", "arg"), "JobTest-runnable_unknown.txt");

        final List<String> args;
        final String expected;

        Case(final List<String> args, final String expected) {
            this.args = args;
            this.expected = expected;
        }
    }
}