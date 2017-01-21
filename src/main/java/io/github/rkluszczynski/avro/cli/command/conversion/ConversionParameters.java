package io.github.rkluszczynski.avro.cli.command.conversion;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import io.github.rkluszczynski.avro.cli.command.CliCommandParameters;
import io.github.rkluszczynski.avro.cli.util.SchemaSourceConverter;
import org.apache.avro.Schema;

@Parameters(
        commandDescription = "Avro <-> JSON conversion (without schema included)."
)
class ConversionParameters extends CliCommandParameters {
    @Parameter(
            names = {"--schemaFile", "-s"},
            converter = SchemaSourceConverter.class,
            description = "Source of schema to read.",
            required = true
    )
    private Schema schema;

    @Parameter(
            names = {"--toAvro", "-a"},
            description = "Convert from JSON to Avro."
    )
    private boolean toAvro = false;

    @Parameter(
            names = {"--toJson", "-j"},
            description = "Convert from Avro to JSON."
    )
    private boolean toJson = false;

    @Parameter(
            names = {"--inputFile", "-i"},
            description = "Source file with message."
    )
    private String inputFile;

    @Parameter(
            names = {"--outputFile", "-o"},
            description = "Target file of converted message."
    )
    private String outputFile;

    public Schema getSchema() {
        return schema;
    }

    public boolean isToAvro() {
        return toAvro;
    }

    public boolean isToJson() {
        return toJson;
    }

    public String getInputFile() {
        return inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }
}
