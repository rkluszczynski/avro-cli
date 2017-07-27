package io.github.rkluszczynski.avro.cli.command

import io.github.rkluszczynski.avro.cli.BaseTestSpecification
import io.github.rkluszczynski.avro.cli.CliCommandService
import io.github.rkluszczynski.avro.cli.EmbeddedKafkaInstance
import io.github.rkluszczynski.avro.cli.EmbeddedKafkaTrait
import io.github.rkluszczynski.avro.cli.command.kafka.KafkaConsumption
import org.junit.ClassRule
import spock.lang.Ignore
import spock.lang.Shared
import spock.util.concurrent.PollingConditions

import java.util.concurrent.CountDownLatch

class KafkaConsumeCommandTest extends BaseTestSpecification implements EmbeddedKafkaTrait {
    private commandService = new CliCommandService([new KafkaConsumption()])

    @ClassRule
    @Shared
    EmbeddedKafkaInstance embeddedKafka = new EmbeddedKafkaInstance('testTopic0')

    def 'should consume earliest message from topic'() {
        given:
        embeddedKafka.send('testTopic0', 'test message')

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

    @Ignore
    def 'should consumer ignore earlier messages when offset reset is set to latest'() {
        setup:
        def conditions = new PollingConditions(timeout: 5, delay: 0.2)

        def latestEmbeddedKafka = startEmbeddedKafka('testTopic')

        def consumerStartLatch = new CountDownLatch(1)
        def secondMessageLatch = new CountDownLatch(1)

        def producer = runInThread {
            latestEmbeddedKafka.send('testTopic', 'test message 1')
            consumerStartLatch.countDown()
            secondMessageLatch.await()
            latestEmbeddedKafka.send('testTopic', 'test message 2')
        }
        def consumer = runInThread {
            commandService.executeCommand('kafka-consume',
                    '-b', latestEmbeddedKafka.brokersAsString,
                    '-l', '1',
                    '-t', 'testTopic',
                    '-o', 'latest'
            )
        }

        when:
        producer.start()
        consumerStartLatch.await()
        consumer.start()

        then:
        conditions.eventually {
            assert latestEmbeddedKafka.hasAnyConsumerEver()
        }

        when:
        secondMessageLatch.countDown()
        producer.join()
        consumer.join()

        then:
        !trimmedOutput().contains('test message 1')
        trimmedOutput().endsWith('test message 2')

        cleanup:
        latestEmbeddedKafka.shutdown()
    }

    Thread runInThread(closure) {
        new Thread() {
            @Override
            void run() {
                closure()
            }
        }
    }
}
