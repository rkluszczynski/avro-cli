package io.github.rkluszczynski.avro.cli.command

import io.github.rkluszczynski.avro.cli.BaseTestSpecification
import io.github.rkluszczynski.avro.cli.CliCommandService
import io.github.rkluszczynski.avro.cli.command.conversion.AvroConversion
import io.github.rkluszczynski.avro.cli.command.conversion.ConversionService

class ConversionCommandTest extends BaseTestSpecification {
    private commandService = new CliCommandService([new AvroConversion(new ConversionService())])

    def 'should pass friendly conversion from json to avro'() {
        given:
        def tmpFile = File.createTempFile('message', '.avro')

        when:
        commandService.executeCommand('convert', '--toAvro',
                '--schema', prepareFilePath('schema-friendly-union.avsc'),
                '--inputFile', prepareFilePath('message-friendly-union.json'),
                '--outputFile', tmpFile.canonicalPath
        )

        then:
        trimmedOutput() == 'DONE'
        compareFiles(prepareFilePath('message-friendly-union.avro'), tmpFile.canonicalPath)
    }

    def 'should pass friendly conversion from avro to json'() {
        given:
        def tmpFile = File.createTempFile('message', '.json')

        when:
        commandService.executeCommand('convert', '--toJson',
                '--schema', prepareFilePath('schema-friendly-union.avsc'),
                '--inputFile', prepareFilePath('message-friendly-union.avro'),
                '--outputFile', tmpFile.canonicalPath
        )

        then:
        trimmedOutput() == 'DONE'
        compareFiles(prepareFilePath('message-friendly-union.json'), tmpFile.canonicalPath)
    }

    def 'should pass raw conversion from json to avro'() {
        given:
        def tmpFile = File.createTempFile('message', '.avro')

        when:
        commandService.executeCommand('convert', '--toAvro', '--rawAvroConversion',
                '--schema', prepareFilePath('schema-raw-enum.avsc'),
                '--inputFile', prepareFilePath('message-raw-enum.json'),
                '--outputFile', tmpFile.canonicalPath
        )

        then:
        trimmedOutput() == 'DONE'
        compareFiles(prepareFilePath('message-raw-enum.avro'), tmpFile.canonicalPath)
    }

    def 'should pass raw conversion from avro to json'() {
        given:
        def tmpFile = File.createTempFile('message', '.json')

        when:
        commandService.executeCommand('convert', '--toJson', '--rawAvroConversion',
                '--schema', prepareFilePath('schema-raw-enum.avsc'),
                '--inputFile', prepareFilePath('message-raw-enum.avro'),
                '--outputFile', tmpFile.canonicalPath
        )

        then:
        trimmedOutput() == 'DONE'
        compareFiles(prepareFilePath('message-raw-enum.json'), tmpFile.canonicalPath)
    }

    def 'should fails when no target format is indicated'() {
        when:
        commandService.executeCommand('convert', '--schema', prepareFilePath('schema-raw-enum.avsc'))

        then:
        trimmedOutput() == 'FAILED [io.github.rkluszczynski.avro.cli.CommandException] Exactly one of target format should be indicated (Avro or JSON).'
    }

    private prepareFilePath(filename) {
        "src/test/resources/conversion/${filename}"
    }

    private compareFiles(filePath1, filePath2) {
        def contentText1 = new File(filePath1).text
        def contentText2 = new File(filePath2).text

        contentText1 == contentText2
    }
}
