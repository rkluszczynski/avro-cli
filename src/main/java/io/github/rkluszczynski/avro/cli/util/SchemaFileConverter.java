package io.github.rkluszczynski.avro.cli.util;

import com.beust.jcommander.IStringConverter;
import io.github.rkluszczynski.avro.cli.CommandException;
import org.apache.avro.Schema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SchemaFileConverter implements IStringConverter<Schema> {
    @Override
    public Schema convert(String value) {
        try {
            return new Schema.Parser().parse(
                    new String(Files.readAllBytes(Paths.get(value))));
        } catch (IOException e) {
            throw new CommandException("Could not parse schema.", e);
        }
    }
}
