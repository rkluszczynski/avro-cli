package io.github.rkluszczynski.avro.cli.command.validation;

import com.beust.jcommander.Parameter;
import org.apache.avro.Schema;

import java.util.List;

import static io.github.rkluszczynski.avro.cli.command.validation.CompatibilityStrategy.FULL;

class ValidationParameters {
    @Parameter(names = {"--compatibility", "-c"}, converter = CompatibilityConverter.class)
    private CompatibilityStrategy compatibilityStrategy = FULL;

    @Parameter(names = {"--schemaFile", "-f"}, converter = SchemaFileConverter.class)
    private Schema schema;

    @Parameter(names = {"--previousSchemaFile", "-p"}, converter = SchemaFileConverter.class)
    private List<Schema> previousSchemaFiles;

    public CompatibilityStrategy getCompatibilityStrategy() {
        return compatibilityStrategy;
    }

    Schema getSchema() {
        return schema;
    }

    List<Schema> getPreviousSchemaFiles() {
        return previousSchemaFiles;
    }
}
