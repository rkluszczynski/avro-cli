package io.github.rkluszczynski.avro.cli.command

import io.github.rkluszczynski.avro.cli.BaseTestSpecification
import io.github.rkluszczynski.avro.cli.CliCommandService
import io.github.rkluszczynski.avro.cli.command.fingerprint.SchemaFingerprint
import spock.lang.Unroll

class FingerprintCommandTest extends BaseTestSpecification {
    private commandService = new CliCommandService([new SchemaFingerprint()])

    @Unroll
    'should calculate schema fingerprint for algorithm #compatibilityStrategy'() {
        when:
        commandService.executeCommand('fingerprint',
                '--algorithm', algorithmName,
                '--schema', prepareSchemaPath('schema-no-fields.avsc')
        )

        then:
        trimmedOutput() == fingerprintValue

        where:
        algorithmName | fingerprintValue
        'CRC-64-AVRO' | '8db714e9ffa989a2'
        'MD5'         | 'f53ea32559669f2659949fe064c091c3'
        'SHA-256'     | 'd8df4d61a004ac76369f63ef7861b908f90a9dddc28ef00d3d65326de50493c9'
    }

    def 'should calculate Rabin fingerprint'() {
        when:
        commandService.executeCommand('fingerprint', '--schema', prepareSchemaPath('schema-for-rabin.avsc'))

        then:
        trimmedOutput() == '0918bd124db26deb'
    }

    def 'should fail for no existing algorithm'() {
        when:
        commandService.executeCommand('fingerprint', '-a', 'NOT-EXISTING-ALGORITHM',
                '--schema', prepareSchemaPath('schema-no-fields.avsc')
        )

        then:
        trimmedOutput() == 'FAILED [java.security.NoSuchAlgorithmException] NOT-EXISTING-ALGORITHM MessageDigest not available'
    }

    private prepareSchemaPath(schemaFilename) {
        "src/test/resources/${schemaFilename}"
    }
}
