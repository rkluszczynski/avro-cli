package io.github.rkluszczynski.avro.cli.command.kafka.util;

import com.beust.jcommander.IStringConverter;
import io.github.rkluszczynski.avro.cli.command.kafka.avro.SchemaProvider;
import io.github.rkluszczynski.avro.cli.util.SchemaSourceConverter;

public class SchemaWrapperSourceConverter implements IStringConverter<SchemaProvider.SchemaWrapper> {
    private final SchemaSourceConverter schemaSourceConverter = new SchemaSourceConverter();

    @Override
    public SchemaProvider.SchemaWrapper convert(String value) {
        return new SchemaProvider.SchemaWrapper(
                schemaSourceConverter.convert(value),
                value
        );
    }
}
