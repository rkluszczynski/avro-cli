package io.github.rkluszczynski.avro.cli.command

import io.github.rkluszczynski.avro.cli.BaseTestSpecification
import io.github.rkluszczynski.avro.cli.CliCommandService
import io.github.rkluszczynski.avro.cli.command.kafka.KafkaConsumption
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.junit.ClassRule
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.kafka.test.utils.KafkaTestUtils
import spock.lang.Shared

import static groovy.json.JsonOutput.toJson
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG

class KafkaConsumeCommandTest extends BaseTestSpecification {
    private commandService = new CliCommandService([new KafkaConsumption()])

    @ClassRule
    @Shared
    KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, 1, 'testTopic', 'testTopic0', 'avroTopic')

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
                '--duration', 'PT4S'
        )

        then:
        trimmedOutput().endsWith('test message')
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

    def 'should consume earliest avro message from topic'() {
        given:
        def senderProperties = KafkaTestUtils.senderProps(embeddedKafka.brokersAsString)
        senderProperties.put(VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer)
        def producerFactory = new DefaultKafkaProducerFactory<Integer, byte[]>(senderProperties)

        byte[] kafkaMessageBytes = readFileAsBytes('test-0.avro')
        new KafkaTemplate<Integer, byte[]>(producerFactory).send('avroTopic', kafkaMessageBytes)

        when:
        commandService.executeCommand('kafka-consume',
                '-b', embeddedKafka.brokersAsString,
                '-m', 'avro',
                '-t', 'avroTopic',
                '-o', 'earliest',
                '-d', 'RAW_DATA_FORMAT',
                '-s', prepareFilePath('schema-message-with-timestamp.avsc'),
                '--duration', 'PT4S'
        )

        then:
        trimmedOutput().endsWith(toJson([message: 'test', timestamp: 0]))
    }

    private readFileAsBytes(filename) {
        java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(prepareFilePath(filename)))
    }

    private prepareFilePath(filename) {
        "src/test/resources/kafka-consumption/${filename}"
    }
}
