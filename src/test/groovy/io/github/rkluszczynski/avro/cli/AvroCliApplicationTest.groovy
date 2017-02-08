package io.github.rkluszczynski.avro.cli

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

import static java.util.Objects.nonNull

@ContextConfiguration
@SpringBootTest
class AvroCliApplicationTest extends BaseTestSpecification {
    @Autowired
    ApplicationContext context
    @Autowired
    AvroCliApplication application

    def 'should boot up without errors'() {
        expect:
        nonNull(context)
    }

    def 'should show exception when verbose main parameter exists'() {
        when:
        application.run('-v', 'validate')

        then:
        trimmedOutput().startsWith('FAILED [com.beust.jcommander.ParameterException] ')
        trimmedOutput().contains('\ncom.beust.jcommander.ParameterException: ')
    }

    @Unroll
    'should show help for command: #command'() {
        when:
        application.run(command, '-h')

        then:
        trimmedOutput().contains('\nUsage: ' + command + ' [options]\n  Options:\n')

        where:
        command << ['convert', 'validate']
    }
}