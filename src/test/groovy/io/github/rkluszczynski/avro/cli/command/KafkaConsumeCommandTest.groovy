package io.github.rkluszczynski.avro.cli.command

import io.github.rkluszczynski.avro.cli.BaseTestSpecification
import io.github.rkluszczynski.avro.cli.CliCommandService
import io.github.rkluszczynski.avro.cli.command.kafka.KafkaConsumption
import org.junit.ClassRule
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.kafka.test.utils.KafkaTestUtils
import spock.lang.Shared

class KafkaConsumeCommandTest extends BaseTestSpecification {
    private commandService = new CliCommandService([new KafkaConsumption()])

    @ClassRule
    @Shared
    KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, 1, 'testTopic0')

    def 'should consume earliest message from topic'() {
        given:
        def senderProperties = KafkaTestUtils.senderProps(embeddedKafka.brokersAsString)
        def producerFactory = new DefaultKafkaProducerFactory<Integer, String>(senderProperties)

        new KafkaTemplate<Integer, String>(producerFactory).send('testTopic0', 'test message')

        when:
        commandService.executeCommand('kafka-consume',
                '-b', embeddedKafka.brokersAsString,
                '-t', 'testTopic0',
                '-o', 'earliest',
                '--duration', '10s'
        )

        then:
        trimmedOutput().endsWith('test message')
    }
}
