package io.github.rkluszczynski.avro.cli.command

import io.github.rkluszczynski.avro.cli.CliMainParameters
import io.github.rkluszczynski.avro.cli.CommandException
import io.github.rkluszczynski.avro.cli.command.kafka.KafkaConsumption
import org.junit.ClassRule
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration
@SpringBootTest
class KafkaConsumeForeverTest extends Specification {
//    @Rule
//    OutputCapture capture = new OutputCapture()
    @ClassRule
    @Shared
    private KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, 1, 'testTopic')
    @Shared
    private KafkaConsumption kafkaConsumeCommand = new KafkaConsumption()

    def setupSpec() {
        kafkaConsumeCommand.consumeParameters.bootstrapServers = embeddedKafka.brokersAsString
        kafkaConsumeCommand.consumeParameters.topics = ['testTopic']
    }

    def 'should end without output when interrupting infinite consumption'() {
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
        sleep(3000)

        commandThread.interrupt()
        commandThread.join()

        then:
        commandException instanceof CommandException
        commandException.message == 'Kafka consumer interrupted!'
        commandException.cause instanceof InterruptedException
    }

    def 'should end'() {
        setup:
        def commandOutput = null
        def commandThread = runInThread {
            commandOutput = kafkaConsumeCommand.execute(new CliMainParameters())
        }

        when:
        commandThread.start()
        sleep(3000)

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
