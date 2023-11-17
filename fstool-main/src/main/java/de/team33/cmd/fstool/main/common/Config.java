package de.team33.cmd.fstool.main.common;

import de.team33.patterns.lazy.narvi.Lazy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class Config {

    private static final String NEW_LINE = String.format("%n");

    private final Path path;
    private final Properties properties = new Properties();
    private final transient Lazy<String> stringValue = Lazy.init(this::newString);

    private Config(final Config origin) {
        this(origin, origin.path);
    }

    private Config(final Config origin, final Path path) {
        this(path, origin.properties);
    }

    private Config(final Path path, final Properties properties) {
        this.path = path.toAbsolutePath().normalize();
        this.properties.putAll(properties);
    }

    public static Config at(final Path path) {
        final Config result = new Config(path, new Properties()).reset();
        return Files.isRegularFile(path) ? result.read(path) : result;
    }

    public final Path path() {
        return path;
    }

    public final Config read(final Class<?> refClass, final String rsrcName) {
        try (final InputStream in = refClass.getResourceAsStream(rsrcName)) {
            return read(in);
        } catch (final IOException | NullPointerException e) {
            throw new IllegalStateException("Could not read \"" + rsrcName + "\"", e);
        }
    }

    public final Config read(final Path path) {
        try (final InputStream in = Files.newInputStream(path)) {
            return read(in);
        } catch (final IOException e) {
            throw new IllegalStateException("Could not read <" + path + ">", e);
        }
    }

    public final Config reset() {
        return read(getClass(), "Config.properties");
    }

    public final void write() {
        write(path);
    }

    public final void write(final Path path) {
        try {
            tryWrite(path);
        } catch (final IOException e) {
            throw new IllegalStateException("Could not write <" + path + ">", e);
        }
    }

    private Config read(final InputStream in) throws IOException {
        final Config result = new Config(this);
        final Properties stage = new Properties();
        try (final Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            stage.load(reader);
            result.properties.putAll(stage);
        }

        return result;
    }

    private void tryWrite(final Path path) throws IOException {
        Files.createDirectories(path.getParent());
        try (final OutputStream out = Files.newOutputStream(path, CREATE, TRUNCATE_EXISTING);
             final Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
            properties.store(writer, "");
        }
    }

    private String newString() {
        try (final StringWriter result = new StringWriter()) {
            properties.store(result, null);
            return result.toString()
                         .lines()
                         .filter(Predicate.not(line -> line.startsWith("#")))
                         .sorted()
                         .collect(Collectors.joining(NEW_LINE));
        } catch (IOException e) {
            throw new IllegalStateException("Should not happen at all", e);
        }
    }

    private List<Object> toList() {
        return List.of(path, properties);
    }

    @Override
    public final int hashCode() {
        return toList().hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof Config other) && toList().equals(other.toList()));
    }

    @Override
    public final String toString() {
        return stringValue.get();
    }
}
