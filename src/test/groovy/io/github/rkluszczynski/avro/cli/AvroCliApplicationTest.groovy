package io.github.rkluszczynski.avro.cli

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
@SpringBootTest
class AvroCliApplicationTest extends Specification {

    @Autowired
    ApplicationContext context

    def 'should boot up without errors'() {
        expect:
        context != null
    }
}