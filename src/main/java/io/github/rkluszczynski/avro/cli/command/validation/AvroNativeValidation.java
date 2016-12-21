package io.github.rkluszczynski.avro.cli.command.validation;

import com.beust.jcommander.JCommander;
import io.github.rkluszczynski.avro.cli.command.CliCommand;
import io.github.rkluszczynski.avro.cli.command.CommandException;
import org.apache.avro.SchemaValidationException;
import org.apache.avro.SchemaValidator;
import org.apache.avro.SchemaValidatorBuilder;
import org.springframework.stereotype.Component;

@Component
public class AvroNativeValidation implements CliCommand {
    private final ValidationParameters validationParameters = new ValidationParameters();

    @Override
    public void execute() {
        final SchemaValidator schemaValidator = createSchemaValidator(validationParameters.getCompatibilityStrategy());
        try {
            schemaValidator.validate(
                    validationParameters.getSchema(),
                    validationParameters.getPreviousSchemaFiles()
            );
        } catch (SchemaValidationException ex) {
            throw new CommandException("Could not validate schema.", ex);
        }
        System.out.println("PASSED");
    }

    @Override
    public void initialize(String... args) {
        new JCommander(validationParameters, args);
    }

    @Override
    public String getCommand() {
        return "validate";
    }

    private SchemaValidator createSchemaValidator(CompatibilityStrategy compatibilityStrategy) {
        final SchemaValidatorBuilder validatorBuilder = new SchemaValidatorBuilder();
        switch (compatibilityStrategy) {
            case BACKWARD:
                return validatorBuilder.canBeReadStrategy()
                        .validateAll();
            case FORWARD:
                return validatorBuilder.canReadStrategy()
                        .validateAll();
            case FULL:
                return validatorBuilder.mutualReadStrategy()
                        .validateAll();
            default:
                throw new CommandException("Unknown compatibility strategy.");
        }
    }
}
