package io.github.rkluszczynski.avro.cli.command.validation;

import avro.shaded.com.google.common.collect.Lists;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.EnumConverter;
import io.github.rkluszczynski.avro.cli.command.CliCommandParameters;
import io.github.rkluszczynski.avro.cli.util.SchemaSourceConverter;
import org.apache.avro.Schema;

import java.util.List;

import static io.github.rkluszczynski.avro.cli.command.validation.CompatibilityStrategy.FULL;

@Parameters(
        commandDescription = "Native avro validation."
)
class ValidationParameters extends CliCommandParameters {
    @Parameter(
            names = {"--compatibility", "-c"},
            converter = CompatibilityStrategyConverter.class,
            description = "One of compatibility strategy."
    )
    private CompatibilityStrategy compatibilityStrategy = FULL;

    @Parameter(
            names = {"--schema", "-s"},
            converter = SchemaSourceConverter.class,
            description = "Source of schema to read.",
            required = true
    )
    private Schema schema;

    @Parameter(
            names = {"--previousSchema", "-p"},
            converter = SchemaSourceConverter.class,
            description = "Sources of previous schemas in order of appearance in command line."
    )
    private List<Schema> previousSchemas = Lists.newArrayList();

    @Parameter(
            names = {"--latest", "-l"},
            description = "Use only latest validator."
    )
    private boolean onlyLatestValidator = false;

    public CompatibilityStrategy getCompatibilityStrategy() {
        return compatibilityStrategy;
    }

    public Schema getSchema() {
        return schema;
    }

    public List<Schema> getPreviousSchemas() {
        return previousSchemas;
    }

    public boolean isOnlyLatestValidator() {
        return onlyLatestValidator;
    }

    private static class CompatibilityStrategyConverter extends EnumConverter<CompatibilityStrategy> {
        public CompatibilityStrategyConverter(String optionName, Class<CompatibilityStrategy> clazz) {
            super(optionName, clazz);
        }
    }
}
