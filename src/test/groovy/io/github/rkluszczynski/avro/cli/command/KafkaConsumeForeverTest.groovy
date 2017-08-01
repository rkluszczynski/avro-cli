package io.github.rkluszczynski.avro.cli.command

import io.github.rkluszczynski.avro.cli.CliMainParameters
import io.github.rkluszczynski.avro.cli.command.kafka.KafkaConsumption
import org.junit.ClassRule
import org.junit.Rule
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.rule.OutputCapture
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

import java.util.function.Predicate
import java.util.stream.Collectors

@ContextConfiguration
@SpringBootTest
class KafkaConsumeForeverTest extends Specification {
    @Rule
    OutputCapture capture = new OutputCapture()
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
        def commandOutput = ''
        def commandThread = runInThread {
            commandOutput = kafkaConsumeCommand.execute(new CliMainParameters())
        }
        capture.flush()
        capture.reset()

        when:
        commandThread.start()
        sleep(3000)

        commandThread.interrupt()
        commandThread.join()

        then:
        capture.toString().trim() == ''
        commandOutput == ''
    }

    def 'should end'() {
        setup:
        def commandOutput = ''
        def commandThread = runInThread {
            commandOutput = kafkaConsumeCommand.execute(new CliMainParameters())
        }

        capture.flush()
        capture.reset()

        when:
        commandThread.start()
        sleep(3000)

        kafkaConsumeCommand.awaitLatch.countDown()
        commandThread.join()

        then:
        capture.toString().trim() == ''
        commandOutput == ''
    }

    private String warnOrWorse(String output) {
        output
                .tokenize(System.lineSeparator())
                .stream()
                .filter(new Predicate<String>() {
            @Override
            boolean test(String s) {
                return !s.contains(" INFO ") && !s.contains(" DEBUG ") && !s.contains(" = ")
            }
        })
                .collect(Collectors.toList())
                .join(System.lineSeparator())
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
