package io.github.rkluszczynski.avro.cli.command.kafka

import io.github.rkluszczynski.avro.cli.command.kafka.avro.DeserializationMode
import spock.lang.Specification

class ConsumeParametersTest extends Specification {
    def 'should work message type enum converter'() {
        expect:
        new ConsumeParameters.MessageTypeParameterConverter('messageType', MessageTypeParameter.class)
                .convert('text') == MessageTypeParameter.TEXT
    }

    def 'should work offset reset enum converter'() {
        expect:
        new ConsumeParameters.OffsetResetParameterConverter('offsetReset', OffsetResetParameter.class)
                .convert('latest') == OffsetResetParameter.LATEST
    }

    def 'should work deserialization mode enum converter'() {
        expect:
        new ConsumeParameters.DeserializationModeConverter('deserializationMode', DeserializationMode.class)
                .convert('avro-wire-format') == DeserializationMode.AVRO_WIRE_FORMAT
    }
}
