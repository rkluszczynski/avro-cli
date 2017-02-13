package io.github.rkluszczynski.avro.cli.command

import io.github.rkluszczynski.avro.cli.BaseTestSpecification
import io.github.rkluszczynski.avro.cli.CliCommandService
import io.github.rkluszczynski.avro.cli.command.normalization.AvroSchemaNormalization

class NormalizationCommandTest extends BaseTestSpecification {
    private commandService = new CliCommandService([new AvroSchemaNormalization()])

    def 'should normalize schema to canonical parsing form and print to stdout'() {
        when:
        commandService.executeCommand('normalize', '--schema', prepareSchemaPath('schema-fat.avsc'))

        then:
        trimmedOutput() == '{"name":"io.github.rkluszczynski.avro.cli.testRecord","type":"record","fields":[]}'
    }

    def 'should normalize schema to canonical parsing form and save it to file'() {
        given:
        def tmpFile = File.createTempFile('schema', '.avsc')

        when:
        commandService.executeCommand('normalize',
                '--schema', prepareSchemaPath('schema-fat.avsc'),
                '--outputFile', tmpFile.canonicalPath
        )

        then:
        compareFilesContent(prepareSchemaPath('schema-normalized.avsc'), tmpFile.canonicalPath)
    }

    private prepareSchemaPath(schemaFilename) {
        "src/test/resources/normalization/${schemaFilename}"
    }
}
