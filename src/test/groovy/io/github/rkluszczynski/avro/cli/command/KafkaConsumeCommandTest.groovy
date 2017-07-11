package io.github.rkluszczynski.avro.cli.command

import io.github.rkluszczynski.avro.cli.BaseTestSpecification
import io.github.rkluszczynski.avro.cli.CliCommandService
import io.github.rkluszczynski.avro.cli.command.kafka.KafkaConsumption
import org.junit.ClassRule
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.kafka.test.utils.KafkaTestUtils
import spock.lang.Shared

class KafkaConsumeCommandTest extends BaseTestSpecification {
    private commandService = new CliCommandService([new KafkaConsumption()])

    @ClassRule
    @Shared
    KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, 1, 'testTopic0')

    def 'should'() {
        given:
        Map<String, Object> senderProps = KafkaTestUtils.senderProps(embeddedKafka.getBrokersAsString());
        ProducerFactory<Integer, String> pf = new DefaultKafkaProducerFactory<Integer, String>(senderProps);
        KafkaTemplate<Integer, String> template = new KafkaTemplate<>(pf);

        template.send('testTopic0', 'test message')

        when:
        commandService.executeCommand('kafka-consume',
                '-b', embeddedKafka.brokersAsString,
                '-l', '1',
                '-t', 'testTopic0',
                '-o', 'earliest'
        )

        then:
        trimmedOutput().endsWith('test message')
    }
}
