package io.github.rkluszczynski.avro.cli.command.validation

import spock.lang.Specification

class ValidationParametersTest extends Specification {
    def 'should works compatibility strategy enum converter'() {
        expect:
        new ValidationParameters.CompatibilityStrategyConverter("compatibilityStrategy", CompatibilityStrategy.class)
                .convert('full') == CompatibilityStrategy.FULL
    }
}
