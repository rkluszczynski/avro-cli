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
import spock.lang.Unroll

class KafkaConsumeCommandTest extends BaseTestSpecification {
    private commandService = new CliCommandService([new KafkaConsumption()])

    @ClassRule
    @Shared
    KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, 1, 'testTopic', 'testTopic0-1', 'testTopic0-2', 'testTopic0-3')

    @Unroll
    def 'should consume earliest message from topic with duration string #durationParameter'() {
        given:
        def senderProperties = KafkaTestUtils.senderProps(embeddedKafka.brokersAsString)
        def producerFactory = new DefaultKafkaProducerFactory<Integer, String>(senderProperties)

        new KafkaTemplate<Integer, String>(producerFactory).send(topicName, 'test message')

        when:
        commandService.executeCommand('kafka-consume',
                '-b', embeddedKafka.brokersAsString,
                '-t', topicName,
                '-o', 'earliest',
                '--duration', durationParameter
        )

        then:
        trimmedOutput().endsWith('test message')

        where:
        topicName      | durationParameter
        'testTopic0-1' | 'PT4S'
        'testTopic0-2' | 'T4S'
        'testTopic0-3' | '4S'
    }

    def 'should fail when duration parameter is not parsable'() {
        when:
        commandService.executeCommand('kafka-consume',
                '-b', embeddedKafka.brokersAsString,
                '-t', 'testTopic',
                '--duration', 'NOT-PARSABLE-DURATION-PARAMETER'
        )

        then:
        trimmedOutput() == 'FAILED [java.time.format.DateTimeParseException] Text cannot be parsed to a Duration'
    }
}
