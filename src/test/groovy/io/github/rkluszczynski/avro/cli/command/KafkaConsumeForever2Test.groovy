package io.github.rkluszczynski.avro.cli.command

import io.github.rkluszczynski.avro.cli.CliCommandService
import io.github.rkluszczynski.avro.cli.command.kafka.KafkaConsumption
import org.junit.ClassRule
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.rule.OutputCapture
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration
@SpringBootTest
class KafkaConsumeForever2Test extends Specification {
    @Rule
    OutputCapture capture = new OutputCapture()

    @ClassRule
    @Shared
    KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, 1, 'testTopic')

    @Autowired
    CliCommandService commandService

    @Autowired
    KafkaConsumption kafkaConsumeCommand

    def 'should end'() {
        setup:
        def th = runInThread {
            commandService.executeCommand('kafka-consume',
                    '-b', embeddedKafka.brokersAsString,
                    '-t', 'testTopic'
            )
        }
        capture.flush()
        capture.reset()

        when:
        th.start()
        sleep(7000)
        kafkaConsumeCommand.awaitLatch.countDown()
        th.join()

        then:
        capture.toString().trim() == ''
    }

    private Thread runInThread(closure) {
        new Thread() {
            @Override
            void run() {
                closure()
            }
        }
    }
}
