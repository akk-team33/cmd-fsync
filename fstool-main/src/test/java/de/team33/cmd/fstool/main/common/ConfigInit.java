package de.team33.cmd.fstool.main.common;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class ConfigInit {

    private static final Path PATH = Path.of("fstool-main/src/main/resources/de/team33/cmd/fstool/main/common/Config.properties")
                                         .toAbsolutePath()
                                         .normalize();

    public static void main(String[] args) throws IOException {
        final Properties props = new Properties();
        props.put("file.processing.ignore.name", "abc,def,ghi");
        props.put("hash.algorithm", "SHA-1");
        props.put("line.width", "80");
        try (final OutputStream out = Files.newOutputStream(PATH, CREATE, TRUNCATE_EXISTING);
             final Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
            props.store(writer, null);
        }
    }
}