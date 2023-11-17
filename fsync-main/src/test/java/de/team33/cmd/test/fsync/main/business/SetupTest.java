package de.team33.cmd.test.fsync.main.business;

import de.team33.cmd.fsync.main.business.Setup;
import de.team33.cmd.fsync.main.common.BadRequestException;
import de.team33.cmd.fsync.main.common.Config;
import de.team33.cmd.fsync.main.common.Context;
import de.team33.patterns.io.alpha.TextIO;
import de.team33.patterns.testing.io.FileIO;
import de.team33.patterns.testing.titan.io.Redirected;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class SetupTest implements Context {

    private static final Path TEST_PATH = Path.of("target", "testing", SetupTest.class.getSimpleName());

    private final Path configPath;
    private final Path morePath;

    SetupTest() {
        this.configPath = TEST_PATH.resolve(UUID.randomUUID().toString()).resolve("Config.properties");
        this.morePath = TEST_PATH.resolve("more.properties");
        FileIO.copy(getClass(), "more.properties", morePath);
    }

    @ParameterizedTest
    @EnumSource
    void runnable_failing(final FailCase testCase) throws Exception {
        try {
            final Runnable result = Setup.runnable(this, SetupTest.class.getSimpleName(), testCase.args);
            fail("expected to fail - but was " + result);
        } catch (final BadRequestException e) {
            final String expected = TextIO.read(SetupTest.class, testCase.expected);
            assertEquals(expected, e.getMessage());
        }
    }

    @Test
    final void run_get() throws IOException {
        final String expected = String.format("%s%n", config());

        final String result = Redirected.outputOf(() -> Setup.runnable(this,
                                                                       SetupTest.class.getSimpleName(),
                                                                       List.of("get"))
                                                             .run());

        assertEquals(expected, result);
    }

    @Test
    final void run_set() throws IOException {
        final Config expectedConfig = config().read(morePath);
        final String expectedOutput = String.format("%s%n", expectedConfig);

        final String output = Redirected.outputOf(() -> Setup.runnable(this,
                                                                       SetupTest.class.getSimpleName(),
                                                                       List.of("set", morePath.toString()))
                                                             .run());

        assertEquals(expectedOutput, output);
        assertEquals(expectedConfig, config());
    }

    @Test
    final void run_reset() throws IOException {
        final Config preparedConfig = config().read(morePath);
        preparedConfig.write();
        assertEquals(preparedConfig, config());
        final Config expectedConfig = config().reset();
        final String expectedOutput = String.format("%s%n", expectedConfig);

        final String output = Redirected.outputOf(() -> Setup.runnable(this,
                                                                       SetupTest.class.getSimpleName(),
                                                                       List.of("reset"))
                                                             .run());

        assertEquals(expectedOutput, output);
        assertEquals(expectedConfig, config());
    }

    @Override
    public final Config config() {
        return Config.at(configPath);
    }

    @SuppressWarnings("unused")
    enum FailCase {

        NO_ARGS(List.of(), "SetupTest-runnable_noArgs.txt"),
        UNKNOWN_MODE(List.of("unknown"), "SetupTest-runnable_unknown.txt"),
        SET_NO_PATH(List.of("set"), "SetupTest-runnable_setMissingPath.txt"),
        GET_PATH(List.of("get", "path/to/file"), "SetupTest-runnable_tooManyArgs.txt"),
        RESET_PATH(List.of("reset", "path/to/file"), "SetupTest-runnable_tooManyArgs.txt"),
        SET_MULTIPLE_ARGS(List.of("set", "arg", "arg"), "SetupTest-runnable_tooManyArgs.txt"),
        GET_MULTIPLE_ARGS(List.of("get", "arg", "arg"), "SetupTest-runnable_tooManyArgs.txt"),
        RESET_MULTIPLE_ARGS(List.of("reset", "arg", "arg"), "SetupTest-runnable_tooManyArgs.txt"),
        UNKNOWN_MODE_MULTIPLE_ARGS(List.of("unknown", "arg", "arg"), "SetupTest-runnable_unknown.txt");

        final List<String> args;
        final String expected;

        FailCase(final List<String> args, final String expected) {
            this.args = args;
            this.expected = expected;
        }
    }
}