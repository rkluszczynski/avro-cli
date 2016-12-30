package io.github.rkluszczynski.avro.cli.command.validation

import org.apache.avro.Schema
import spock.lang.Specification

class AvroNativeValidationTest extends Specification {
    private final AvroNativeValidation validationCommand = new AvroNativeValidation()

    def 'should pass validation'() {
        given:
        def validationParams = (ValidationParameters) validationCommand.parameters

        validationParams.schema = nextSchema
        validationParams.previousSchemaFiles =
                [Schema.createRecord('testRecord', null, 'io.github.rkluszczynski.avro.cli', false, [])]

        when:
        def stdout = validationCommand.execute()

        then:
        stdout == 'PASSED'
    }

    private Schema nextSchema = Schema.createRecord('testRecord', null, 'io.github.rkluszczynski.avro.cli', false,
            [new Schema.Field('testField', Schema.create(Schema.Type.STRING), null, 'defaultStringValue')])
}
