package io.github.rkluszczynski.avro.cli.command.validation;

import io.github.rkluszczynski.avro.cli.CliMainParameters;
import io.github.rkluszczynski.avro.cli.CommandException;
import io.github.rkluszczynski.avro.cli.command.CliCommand;
import io.github.rkluszczynski.avro.cli.command.CliCommandParameters;
import org.apache.avro.SchemaValidationException;
import org.apache.avro.SchemaValidator;
import org.apache.avro.SchemaValidatorBuilder;
import org.springframework.stereotype.Component;

@Component
public class AvroNativeValidation implements CliCommand {
    private final ValidationParameters validationParameters = new ValidationParameters();

    @Override
    public String execute(CliMainParameters mainParameters) {
        final SchemaValidator schemaValidator = createSchemaValidator(
                validationParameters.getCompatibilityStrategy(),
                validationParameters.isOnlyLatestValidator()
        );

        try {
            schemaValidator.validate(
                    validationParameters.getSchema(),
                    validationParameters.getPreviousSchemaFiles()
            );
        } catch (SchemaValidationException ex) {
            throw new CommandException("Could not validate schema.", ex);
        }
        return "PASSED";
    }

    @Override
    public String getCommandName() {
        return "validate";
    }

    @Override
    public CliCommandParameters getParameters() {
        return validationParameters;
    }

    private SchemaValidator createSchemaValidator(CompatibilityStrategy compatibilityStrategy,
                                                  boolean onlyLatestValidator) {
        final SchemaValidatorBuilder validatorBuilder = new SchemaValidatorBuilder();
        switch (compatibilityStrategy) {
            case BACKWARD:
                return createLatestOrAllValidator(validatorBuilder.canReadStrategy(), onlyLatestValidator);
            case FORWARD:
                return createLatestOrAllValidator(validatorBuilder.canBeReadStrategy(), onlyLatestValidator);
            default:
                return createLatestOrAllValidator(validatorBuilder.mutualReadStrategy(), onlyLatestValidator);
        }
    }

    private SchemaValidator createLatestOrAllValidator(SchemaValidatorBuilder validatorBuilder, boolean onlyLatest) {
        return onlyLatest ? validatorBuilder.validateLatest() : validatorBuilder.validateAll();
    }
}
