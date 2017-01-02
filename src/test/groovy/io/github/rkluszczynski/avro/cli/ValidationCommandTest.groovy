package io.github.rkluszczynski.avro.cli

import io.github.rkluszczynski.avro.cli.command.validation.AvroNativeValidation
import org.junit.Rule
import org.springframework.boot.test.rule.OutputCapture
import spock.lang.Specification

class ValidationCommandTest extends Specification {
    private commandService = new CliCommandService([new AvroNativeValidation()])

    @Rule
    OutputCapture capture = new OutputCapture()

    def 'should pass validation'() {
        when:
        commandService.executeCommand('validate',
                '-c', 'backward',
                '-f', 'src/test/resources/schema0.json',
                '-p', 'src/test/resources/schema1.json')

        then:
        trimmedOutput() == 'PASSED'
    }

    private trimmedOutput() {
        capture.toString().trim()
    }
}
