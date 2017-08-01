package io.github.rkluszczynski.avro.cli.command.kafka

import spock.lang.Specification

class ConsumeParametersTest extends Specification {
    def 'should works message type enum converter'() {
        expect:
        new ConsumeParameters.MessageTypeParameterConverter("messageType", MessageTypeParameter.class)
                .convert('text') == MessageTypeParameter.TEXT
    }

    def 'should works offset reset enum converter'() {
        expect:
        new ConsumeParameters.OffsetResetParameterConverter("offsetReset", OffsetResetParameter.class)
                .convert('latest') == OffsetResetParameter.LATEST
    }
}
