package io.github.rkluszczynski.avro.cli.command.fingerprint;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import io.github.rkluszczynski.avro.cli.command.CliCommandParameters;
import io.github.rkluszczynski.avro.cli.util.SchemaSourceConverter;
import org.apache.avro.Schema;

@Parameters(
        commandDescription = "Prints fingerprint of schema canonical form."
)
class FingerprintParameters extends CliCommandParameters {
    @Parameter(
            names = {"--schema", "-s"},
            converter = SchemaSourceConverter.class,
            description = "Source of schema to read.",
            required = true
    )
    private Schema schema;

    @Parameter(
            names = {"--algorithm", "-a"},
            description = "Algorithm code for fingerprint (i.e. CRC-64-AVRO, MD5, SHA-256).\n" +
                    "Default is Rabin fingerprint."
    )
    private String algorithm = "";

    public Schema getSchema() {
        return schema;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}
