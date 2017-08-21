package io.github.rkluszczynski.avro.cli.command.kafka.util

import org.apache.avro.Schema
import spock.lang.Specification

class SchemaWrapperSourceConverterTest extends Specification {

    def 'should convert file path to schema wrapper'() {
        when:
        def schemaWrapper = new SchemaWrapperSourceConverter()
                .convert('src/test/resources/schema-no-fields.avsc')

        then:
        schemaWrapper.id == 'src/test/resources/schema-no-fields.avsc'
        checkConvertedSchema(schemaWrapper.schema)
    }

    private checkConvertedSchema(schema) {
        schema.type == Schema.Type.RECORD &&
                schema.name == 'testRecord' &&
                schema.namespace == 'io.github.rkluszczynski.avro.cli' &&
                schema.fields == []
    }
}
