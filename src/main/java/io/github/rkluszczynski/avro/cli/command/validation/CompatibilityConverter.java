package io.github.rkluszczynski.avro.cli.command.validation;

import com.beust.jcommander.IStringConverter;

class CompatibilityConverter implements IStringConverter<CompatibilityStrategy> {
    @Override
    public CompatibilityStrategy convert(String value) {
        return CompatibilityStrategy.valueOf(value.toUpperCase());
    }
}
