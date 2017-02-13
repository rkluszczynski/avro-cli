package io.github.rkluszczynski.avro.cli.command.fingerprint;

import io.github.rkluszczynski.avro.cli.CliMainParameters;
import io.github.rkluszczynski.avro.cli.CommandException;
import io.github.rkluszczynski.avro.cli.command.CliCommand;
import io.github.rkluszczynski.avro.cli.command.CliCommandParameters;
import org.apache.avro.Schema;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;

import static org.apache.avro.SchemaNormalization.parsingFingerprint;
import static org.apache.avro.SchemaNormalization.parsingFingerprint64;

@Component
public class SchemaFingerprint implements CliCommand {
    private final FingerprintParameters fingerprintParameters = new FingerprintParameters();

    @Override
    public String execute(CliMainParameters mainParameters) {
        final Schema schema = fingerprintParameters.getSchema();
        final String algorithm = fingerprintParameters.getAlgorithm();

        if (algorithm.isEmpty()) {
            return String.format("%x", parsingFingerprint64(schema));
        }

        try {
            final byte[] bytes = parsingFingerprint(algorithm, schema);

            String fingerprintString = "";
            for (byte b : bytes) {
                fingerprintString += String.format("%x", b);
            }
            return fingerprintString;
        } catch (NoSuchAlgorithmException e) {
            throw new CommandException("Wrong algorithm parameter!", e);
        }
    }

    @Override
    public String getCommandName() {
        return "fingerprint";
    }

    @Override
    public CliCommandParameters getParameters() {
        return fingerprintParameters;
    }
}
