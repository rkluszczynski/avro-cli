package io.github.rkluszczynski.avro.cli.util;

import com.beust.jcommander.IStringConverter;

import java.time.Duration;
import java.time.format.DateTimeParseException;

public class DurationGuessConverter implements IStringConverter<Duration> {
    @Override
    public Duration convert(String value) {
        try {
            return Duration.parse(value);
        } catch (DateTimeParseException ex) {
            parseException = ex;

            return tryGuessPeriodPrefix(value);
        }
    }

    private Duration tryGuessPeriodPrefix(String value) {
        try {
            return Duration.parse("P" + value);
        } catch (DateTimeParseException ex) {
            return tryGuessPeriodTimePrefix(value);
        }
    }

    private Duration tryGuessPeriodTimePrefix(String value) {
        try {
            return Duration.parse("PT" + value);
        } catch (DateTimeParseException ex) {
            throw parseException;
        }
    }

    private DateTimeParseException parseException = null;
}
