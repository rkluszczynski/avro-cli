package io.github.rkluszczynski.avro.cli.command.normalization;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import io.github.rkluszczynski.avro.cli.command.CliCommandParameters;
import io.github.rkluszczynski.avro.cli.util.SchemaSourceConverter;
import org.apache.avro.Schema;

@Parameters(
        commandDescription = "Normalize schema to canonical parsing form."
)
class NormalizationParameters extends CliCommandParameters {
    @Parameter(
            names = {"--schema", "-s"},
            converter = SchemaSourceConverter.class,
            description = "Source of schema to read.",
            required = true
    )
    private Schema schema;

    @Parameter(
            names = {"--outputFile", "-o"},
            description = "Target file of normalized schema. Default is standard output."
    )
    private String outputFile = "-";

    public Schema getSchema() {
        return schema;
    }

    public String getOutputFile() {
        return outputFile;
    }
}
