package io.github.rkluszczynski.avro.cli.command.kafka.avro;

import org.apache.avro.Schema;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

public class CommandLineSchemaProvider implements SchemaProvider {
    private final List<Schema> schemas;

    public CommandLineSchemaProvider(List<Schema> schemas) {
        Assert.notEmpty(schemas, "At least one schema should be provided!");

        this.schemas = Collections.unmodifiableList(schemas);
    }

    @Override
    public Schema getSchema() {
        return schemas.get(0);
    }
}
