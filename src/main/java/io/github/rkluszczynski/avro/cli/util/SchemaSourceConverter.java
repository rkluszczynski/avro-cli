package io.github.rkluszczynski.avro.cli.util;

import com.beust.jcommander.IStringConverter;
import io.github.rkluszczynski.avro.cli.CommandException;
import org.apache.avro.Schema;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

public class SchemaSourceConverter implements IStringConverter<Schema> {
    @Override
    public Schema convert(String value) {
        Optional<URL> schemaUrl = prepareUrlIfApplicable(value);
        if (!schemaUrl.isPresent()) {
            schemaUrl = prepareUrlAsLocalPath(value);
        }

        return schemaUrl.map(url -> {
                    try (InputStream schemaStream = url.openStream()) {
                        return new Schema.Parser().parse(schemaStream);
                    } catch (IOException e) {
                        throw new CommandException("Could not parse schema from: " + value, e);
                    }
                }
        ).orElseThrow(() -> new CommandException("Could not get URL location of: " + value, urlException));
    }

    private Optional<URL> prepareUrlAsLocalPath(String filePath) {
        try {
            return Optional.of(Paths.get(filePath).toUri().toURL());
        } catch (MalformedURLException e) {
            urlException = e;
        }
        return Optional.empty();
    }

    private Optional<URL> prepareUrlIfApplicable(String urlSpec) {
        try {
            return Optional.of(new URL(urlSpec));
        } catch (MalformedURLException e) {
            urlException = e;
        }
        return Optional.empty();
    }

    private MalformedURLException urlException = null;
}
