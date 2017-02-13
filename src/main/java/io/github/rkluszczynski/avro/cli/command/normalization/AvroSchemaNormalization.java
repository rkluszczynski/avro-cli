package io.github.rkluszczynski.avro.cli.command.normalization;

import io.github.rkluszczynski.avro.cli.CliMainParameters;
import io.github.rkluszczynski.avro.cli.CommandException;
import io.github.rkluszczynski.avro.cli.command.CliCommand;
import io.github.rkluszczynski.avro.cli.command.CliCommandParameters;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.github.rkluszczynski.avro.cli.command.CommandNames.NORMALIZE;
import static org.apache.avro.SchemaNormalization.toParsingForm;

@Component
public class AvroSchemaNormalization implements CliCommand {
    private final NormalizationParameters normalizationParameters = new NormalizationParameters();

    @Override
    public String execute(CliMainParameters mainParameters) {
        final String parsingForm = toParsingForm(
                normalizationParameters.getSchema()
        );
        final String outputFilePath = normalizationParameters.getOutputFile();

        if ("-".equals(outputFilePath)) {
            return parsingForm;
        }

        try {
            Files.write(Paths.get(outputFilePath), parsingForm.getBytes());
        } catch (IOException e) {
            throw new CommandException("Could not save to file: " + outputFilePath, e);
        }
        return "";
    }

    @Override
    public String getCommandName() {
        return NORMALIZE.getCliCommand();
    }

    @Override
    public CliCommandParameters getParameters() {
        return normalizationParameters;
    }
}
