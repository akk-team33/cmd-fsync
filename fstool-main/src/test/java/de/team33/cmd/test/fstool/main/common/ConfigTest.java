package de.team33.cmd.test.fstool.main.common;

import de.team33.cmd.fstool.main.common.Config;
import de.team33.patterns.testing.io.FileIO;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ConfigTest {

    private static final Path TEST_PATH = Path.of("target", "testing", ConfigTest.class.getSimpleName());

    private final Path configPath;
    private final Path morePath;
    private final Path missingPath;

    ConfigTest() {
        this.configPath = TEST_PATH.resolve(UUID.randomUUID().toString()).resolve("Config.properties");
        this.morePath = TEST_PATH.resolve("more.properties");
        this.missingPath = TEST_PATH.resolve(UUID.randomUUID().toString());
        FileIO.copy(getClass(), "more.properties", morePath);
    }

    @Test
    void read_from_resource() {
        final String rsrcName = UUID.randomUUID().toString();
        try {
            final Config result = Config.at(configPath).read(getClass(), rsrcName);
            fail("expected to fail - but was " + result);
        } catch (final IllegalStateException e) {
            assertTrue(e.getMessage().contains(rsrcName));
            assertInstanceOf(NullPointerException.class, e.getCause());
        }
    }

    @Test
    void read_from_file() {
        final Config result = Config.at(configPath).read(morePath);
        result.write();
        assertEquals(Config.at(configPath), result);
    }

    @Test
    void read_from_missing_file() {
        try {
            final Config result = Config.at(configPath).read(missingPath);
            fail("expected to fail - but was " + result);
        } catch (final IllegalStateException e) {
            assertTrue(e.getMessage().contains(missingPath.toString()));
            assertInstanceOf(IOException.class, e.getCause());
        }
    }

    @Test
    void write_to_improper_file() {
        try {
            Config.at(configPath).write(TEST_PATH);
            fail("expected to fail - but was " + Config.at(TEST_PATH));
        } catch (final IllegalStateException e) {
            assertTrue(e.getMessage().contains(TEST_PATH.toString()));
            assertInstanceOf(IOException.class, e.getCause());
        }
    }

    @Test
    void testEquals() {
        final Config first = Config.at(configPath);
        final Config second = Config.at(configPath);
        assertNotSame(first, second);
        assertEquals(first, second);
    }

    @Test
    void testHashCode() {
        final Config first = Config.at(configPath);
        final Config second = Config.at(configPath);
        assertNotSame(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}