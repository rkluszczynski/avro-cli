package io.github.rkluszczynski.avro.cli.command

import io.github.rkluszczynski.avro.cli.CliCommandService
import io.github.rkluszczynski.avro.cli.command.conversion.AvroConversion
import io.github.rkluszczynski.avro.cli.command.conversion.ConversionService
import org.junit.Rule
import org.springframework.boot.test.rule.OutputCapture
import spock.lang.Specification

class ConversionCommandTest extends Specification {
    private commandService = new CliCommandService([new AvroConversion(new ConversionService())])

    @Rule
    OutputCapture capture = new OutputCapture()

    def 'should pass conversion from json to avro'() {
        given:
        def tmpFile = File.createTempFile('message', '.avro')

        when:
        commandService.executeCommand('convert', '--toAvro',
                '--schemaFile', prepareFilePath('schema-enum.avsc'),
                '--inputFile', prepareFilePath('message-priority.json'),
                '--outputFile', tmpFile.canonicalPath
        )

        then:
        trimmedOutput() == 'DONE'
        compareFiles(prepareFilePath('message-priority.avro'), tmpFile.canonicalPath)
    }

    def 'should pass conversion from avro to json'() {
        given:
        def tmpFile = File.createTempFile('message', '.json')

        when:
        commandService.executeCommand('convert', '--toJson',
                '--schemaFile', prepareFilePath('schema-enum.avsc'),
                '--inputFile', prepareFilePath('message-priority.avro'),
                '--outputFile', tmpFile.canonicalPath
        )

        then:
        trimmedOutput() == 'DONE'
        compareFiles(prepareFilePath('message-priority.json'), tmpFile.canonicalPath)
    }

    def 'should fails when no target format is indicated'() {
        when:
        commandService.executeCommand('convert', '--schemaFile', prepareFilePath('schema-enum.avsc'))

        then:
        trimmedOutput() == 'FAILED [io.github.rkluszczynski.avro.cli.CommandException] Exactly one of target format should be indicated (Avro or JSON).'
    }

    private trimmedOutput() {
        capture.toString().trim()
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
