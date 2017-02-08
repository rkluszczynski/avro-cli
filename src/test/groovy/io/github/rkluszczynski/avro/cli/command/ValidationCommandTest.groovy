package io.github.rkluszczynski.avro.cli.command

import io.github.rkluszczynski.avro.cli.BaseTestSpecification
import io.github.rkluszczynski.avro.cli.CliCommandService
import io.github.rkluszczynski.avro.cli.command.validation.AvroNativeValidation
import spock.lang.Unroll

class ValidationCommandTest extends BaseTestSpecification {
    private commandService = new CliCommandService([new AvroNativeValidation()])

    @Unroll
    'should pass validation for #compatibilityStrategy'() {
        when:
        commandService.executeCommand('validate',
                '--compatibility', compatibilityStrategy,
                '--schema', prepareSchemaValidationPath(nextSchemaFilename),
                '--previousSchema', prepareSchemaValidationPath(previousSchemaFilename)
        )

        then:
        trimmedOutput() == 'PASSED'

        where:
        compatibilityStrategy | nextSchemaFilename               | previousSchemaFilename
        'backward'            | 'schema2-string-null-field.json' | 'schema1-string-field.json'
        'forward'             | 'schema1-string-field.json'      | 'schema2-string-null-field.json'
        'full'                | 'schema1-string-field.json'      | '../schema-no-fields.avsc'
    }

    def 'should fail when native validation fails'() {
        when:
        commandService.executeCommand('validate', '--compatibility', 'FORWARD',
                '--schema', prepareSchemaValidationPath('../schema-no-fields.avsc'),
                '--previousSchema', prepareSchemaValidationPath('schema3-int-field.json')
        )

        then:
        trimmedOutput().startsWith('FAILED [org.apache.avro.SchemaValidationException] Unable to read schema:')
    }

    def 'should fail when no file found'() {
        when:
        commandService.executeCommand('validate', '--schema', 'not-existing-file.avsc')

        then:
        trimmedOutput().startsWith('FAILED [java.io.FileNotFoundException] ')
        trimmedOutput().endsWith('not-existing-file.avsc (No such file or directory)')
    }

    private prepareSchemaValidationPath(schemaFilename) {
        "src/test/resources/validation/${schemaFilename}"
    }
}
