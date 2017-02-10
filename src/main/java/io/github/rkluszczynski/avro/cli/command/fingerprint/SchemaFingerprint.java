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
            return String.format("%X", parsingFingerprint64(schema));
        }

        try {
            final byte[] bytes = parsingFingerprint(algorithm, schema);

            String fingerprintString = "";
            for (byte b : bytes) {
                fingerprintString += String.format("%X", b);
            }
            return fingerprintString;
        } catch (NoSuchAlgorithmException e) {
            throw new CommandException("Wrong algorithm parameter!", e);
        }
    }

//    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
//        final Schema schema = new Schema.Parser().parse(new File("src/test/resources/schema-no-fields.avsc"));
//
//        System.out.println(SchemaNormalization.toParsingForm(schema));
//
//        System.out.println(String.format("%x", parsingFingerprint64(schema)));
//
//        print("CRC-64-AVRO", schema);
//        print("MD5", schema);
//        print("SHA-256", schema);
//    }
//
//    private static void print(String alg, Schema schema) throws NoSuchAlgorithmException {
//        final byte[] bytes = parsingFingerprint(alg, schema);
//        System.out.println("=> " + bytes.length);
//        for (byte b : bytes) {
//            System.out.print(String.format("%x", b));
//        }
//        System.out.println();
//    }

    @Override
    public String getCommandName() {
        return "fingerprint";
    }

    @Override
    public CliCommandParameters getParameters() {
        return fingerprintParameters;
    }
}
