package de.team33.cmd.test.fsync.main.business;

import de.team33.cmd.fsync.main.business.Push;
import de.team33.cmd.fsync.main.common.Context;
import de.team33.patterns.testing.titan.io.Redirected;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PushTest implements Context {

    private static final Path TEST_PATH = Path.of("target", "testing", PushTest.class.getSimpleName());

    private Path testPath;
    private Path leftPath;
    private Path rightPath;

    @BeforeEach
    final void init() throws IOException {
        this.testPath = TEST_PATH.resolve(UUID.randomUUID().toString());
        this.leftPath = testPath.resolve("left");
        this.rightPath = testPath.resolve("right");
        Files.createDirectories(leftPath);
        Files.createDirectories(rightPath);
    }

    @Test
    final void run_missing() throws IOException {
        final Path relative = Path.of("missing");
        final Push push = new Push(this, leftPath, rightPath, relative);

        final String result = Redirected.outputOf(push::run);
        printf(result);

        final String expected = String.format("missing: both files are missing -> nothing to do%n");
        assertEquals(expected, result);
        assertFalse(Files.exists(leftPath.resolve(relative)));
        assertFalse(Files.exists(rightPath.resolve(relative)));
    }

    @Test
    final void run_leftOnly() throws IOException {
        final Path relative = Path.of("left.only");
        Files.writeString(leftPath.resolve(relative), UUID.randomUUID().toString(), StandardCharsets.UTF_8);
        final Push push = new Push(this, leftPath, rightPath, relative);

        final String result = Redirected.outputOf(push::run);
        printf(result);

        final String expected = String.format("left.only: left only -> copying ... ok%n");
        assertEquals(expected, result);
        assertTrue(Files.exists(leftPath.resolve(relative)));
        assertTrue(Files.exists(rightPath.resolve(relative)));
    }
}