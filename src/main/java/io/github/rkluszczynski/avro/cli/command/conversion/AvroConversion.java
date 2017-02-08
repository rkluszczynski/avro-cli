package io.github.rkluszczynski.avro.cli.command.conversion;

import io.github.rkluszczynski.avro.cli.CliMainParameters;
import io.github.rkluszczynski.avro.cli.CommandException;
import io.github.rkluszczynski.avro.cli.command.CliCommand;
import io.github.rkluszczynski.avro.cli.command.CliCommandParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AvroConversion implements CliCommand {
    private final ConversionParameters conversionParameters = new ConversionParameters();

    private final ConversionService conversionService;

    @Autowired
    public AvroConversion(@Qualifier("AvroCliConversionService") ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public String execute(CliMainParameters mainParameters) {
        if (conversionParameters.isToAvro() == conversionParameters.isToJson()) {
            throw new CommandException("Exactly one of target format should be indicated (Avro or JSON).");
        }

        try {
            if (conversionParameters.isToAvro()) {
                conversionService.convertJsonToAvro(
                        conversionParameters.getInputFile(),
                        conversionParameters.getOutputFile(),
                        conversionParameters.getSchema(),
                        conversionParameters.isRawAvroConversion()
                );
            } else if (conversionParameters.isToJson()) {
                conversionService.convertAvroToJson(
                        conversionParameters.getInputFile(),
                        conversionParameters.getOutputFile(),
                        conversionParameters.getSchema(),
                        conversionParameters.isRawAvroConversion()
                );
            } else {
                throw new IllegalStateException("This should never happens!");
            }
        } catch (IOException | IllegalStateException ex) {
            throw new CommandException("Could not convert!", ex);
        }
        return "DONE";
    }

    @Override
    public String getCommandName() {
        return "convert";
    }

    @Override
    public CliCommandParameters getParameters() {
        return conversionParameters;
    }
}
