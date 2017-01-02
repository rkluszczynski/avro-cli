package io.github.rkluszczynski.avro.cli.command

import io.github.rkluszczynski.avro.cli.CliCommandService
import io.github.rkluszczynski.avro.cli.command.validation.AvroNativeValidation
import org.junit.Rule
import org.springframework.boot.test.rule.OutputCapture
import spock.lang.Specification
import spock.lang.Unroll

class ValidationCommandTest extends Specification {
    private commandService = new CliCommandService([new AvroNativeValidation()])

    @Rule
    OutputCapture capture = new OutputCapture()

    @Unroll
    'should pass validation for #compatibilityStrategy'() {
        when:
        commandService.executeCommand('validate',
                '--compatibility', compatibilityStrategy,
                '--schemaFile', prepareSchemaValidationPath(nextSchemaFilename),
                '--previousSchemaFile', prepareSchemaValidationPath(previousSchemaFilename)
        )

        then:
        trimmedOutput() == 'PASSED'

        where:
        compatibilityStrategy | nextSchemaFilename               | previousSchemaFilename
        'backward'            | 'schema2-string-null-field.json' | 'schema1-string-field.json'
        'forward'             | 'schema1-string-field.json'      | 'schema2-string-null-field.json'
        'full'                | 'schema1-string-field.json'      | 'schema0-no-fields.json'
    }

    def 'should fail when native validation fails'() {
        when:
        commandService.executeCommand('validate', '--compatibility', 'FORWARD',
                '--schemaFile', prepareSchemaValidationPath('schema0-no-fields.json'),
                '--previousSchemaFile', prepareSchemaValidationPath('schema3-int-field.json')
        )

        then:
        trimmedOutput().startsWith('FAILED [org.apache.avro.SchemaValidationException] Unable to read schema:')
    }

    def 'should fail when no file found'() {
        when:
        commandService.executeCommand('validate', '--schemaFile', 'not-existing-file.avsc')

        then:
        trimmedOutput() == 'FAILED [java.nio.file.NoSuchFileException] not-existing-file.avsc'
    }

    private trimmedOutput() {
        capture.toString().trim()
    }

    private prepareSchemaValidationPath(schemaFilename) {
        "src/test/resources/validation/${schemaFilename}"
    }
}
