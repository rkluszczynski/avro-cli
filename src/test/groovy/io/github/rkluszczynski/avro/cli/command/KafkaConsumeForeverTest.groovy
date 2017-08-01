package io.github.rkluszczynski.avro.cli.command

import io.github.rkluszczynski.avro.cli.CliMainParameters
import io.github.rkluszczynski.avro.cli.CommandException
import io.github.rkluszczynski.avro.cli.command.kafka.KafkaConsumption
import org.junit.ClassRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@ContextConfiguration
@SpringBootTest
class KafkaConsumeForeverTest extends Specification {
    @ClassRule
    @Shared
    private KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, 1, 'foreverTopic')

    @Shared
    protected condition = new PollingConditions(timeout: 5, delay: 0.2)

    @Autowired
    private KafkaConsumption kafkaConsumeCommand

    def setup() {
        kafkaConsumeCommand.consumeParameters.bootstrapServers = embeddedKafka.brokersAsString
        kafkaConsumeCommand.consumeParameters.topics = ['foreverTopic']
    }

    def 'should infinite consumption throw exception when interrupted'() {
        setup:
        def commandException = null
        def commandThread = runInThread {
            try {
                kafkaConsumeCommand.execute(new CliMainParameters())
            }
            catch (Throwable cause) {
                commandException = cause
            }
        }

        when:
        commandThread.start()

        and:
        condition.eventually {
            assert commandThread.state == Thread.State.WAITING
        }

        and:
        commandThread.interrupt()
        commandThread.join()

        then:
        commandException instanceof CommandException
        commandException.message == 'Kafka consumer interrupted!'
        commandException.cause instanceof InterruptedException
    }

    def 'should infinite consumption end without output when await latch is disposed'() {
        setup:
        def commandOutput = null
        def commandThread = runInThread {
            commandOutput = kafkaConsumeCommand.execute(new CliMainParameters())
        }

        when:
        commandThread.start()

        and:
        condition.eventually {
            assert commandThread.state == Thread.State.WAITING
        }

        and:
        kafkaConsumeCommand.awaitLatch.countDown()
        commandThread.join()

        then:
        commandOutput == ''
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
