package io.github.rkluszczynski.avro.cli

import org.junit.Rule
import org.springframework.boot.test.rule.OutputCapture
import spock.lang.Specification

abstract class BaseTestSpecification extends Specification {
    @Rule
    OutputCapture capture = new OutputCapture()

    protected trimmedOutput() {
        capture.toString().trim()
    }
}