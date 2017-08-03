package io.github.rkluszczynski.avro.cli.util

import io.github.rkluszczynski.avro.cli.CommandException
import spock.lang.Specification
import spock.lang.Unroll

import java.time.format.DateTimeParseException

import static java.time.Duration.ofSeconds

class DurationGuessConverterTest extends Specification {
    def converter = new DurationGuessConverter()

    @Unroll
    def 'should convert text #durationText to duration'() {
        expect:
        converter.convert(durationText) == ofSeconds(3)

        where:
        durationText << ['3s', 't3s', 'pt3s']
    }

    def 'should throw exception when duration text is not parsable'() {
        when:
        converter.convert('NOT-PARSABLE-DURATION')

        then:
        def exception = thrown(CommandException)
        exception.cause instanceof DateTimeParseException
    }
}
