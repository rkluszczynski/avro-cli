package io.github.rkluszczynski.avro.cli.command.kafka.avro;

import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CommandLineSchemaProvider implements SchemaProvider {
    private final List<SchemaWrapper> schemas;

    public CommandLineSchemaProvider(List<SchemaWrapper> schemas) {
        Assert.notEmpty(schemas, "At least one schema should be provided!");

        this.schemas = Collections.unmodifiableList(schemas);
    }

    @Override
    public Iterator<SchemaWrapper> iterator() {
        return schemas.iterator();
    }
}
