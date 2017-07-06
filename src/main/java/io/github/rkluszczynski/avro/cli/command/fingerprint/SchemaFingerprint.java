package io.github.rkluszczynski.avro.cli.command.fingerprint;

import io.github.rkluszczynski.avro.cli.CliMainParameters;
import io.github.rkluszczynski.avro.cli.CommandException;
import io.github.rkluszczynski.avro.cli.command.CliCommand;
import io.github.rkluszczynski.avro.cli.command.CliCommandParameters;
import org.apache.avro.Schema;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;

import static io.github.rkluszczynski.avro.cli.command.CommandNames.FINGERPRINT;
import static java.lang.Long.toHexString;
import static org.apache.avro.SchemaNormalization.parsingFingerprint;
import static org.apache.avro.SchemaNormalization.parsingFingerprint64;

@Component
public class SchemaFingerprint implements CliCommand {
    private final FingerprintParameters fingerprintParameters = new FingerprintParameters();

    @Override
    public String execute(CliMainParameters mainParameters) {
        final Schema schema = fingerprintParameters.getSchema();
        final String algorithm = fingerprintParameters.getAlgorithm();

        if (algorithm == null || algorithm.isEmpty()) {
            return String.format(FINGERPRINT64_HEX_FORMAT, toHexString(parsingFingerprint64(schema)))
                    .replace(" ", "0");
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
        return FINGERPRINT.getCliCommand();
    }

    @Override
    public CliCommandParameters getParameters() {
        return fingerprintParameters;
    }

    private static final String FINGERPRINT64_HEX_FORMAT = "%" + String.valueOf(Long.SIZE >> 2) + "s";
}
