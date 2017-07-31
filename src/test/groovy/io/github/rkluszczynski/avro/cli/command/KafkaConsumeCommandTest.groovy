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

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Predicate
import java.util.stream.Collectors

class KafkaConsumeCommandTest extends BaseTestSpecification {
    private commandService = new CliCommandService([new KafkaConsumption()])

    @ClassRule
    @Shared
    KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, 1,
            'testTopic', 'testTopic0-1', 'testTopic0-2', 'testTopic0-3')

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
        'testTopic0-1' | 'PT2S'
        'testTopic0-2' | 'T2S'
        'testTopic0-3' | '2S'
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

//    def 'should end without output when interrupting infinite consumption'() {
//        setup:
//        def th = runInThread {
//            commandService.executeCommand('kafka-consume',
//                    '-b', embeddedKafka.brokersAsString,
//                    '-t', 'testTopic'
//            )
//        }
//
//        when:
//        th.start()
//        th.interrupt()
//        th.join()
//
//        def output = Arrays.stream(
//                trimmedOutput()
//            .split(System.lineSeparator())
//        )
//        .filter(new Predicate<String>() {
//            @Override
//            boolean test(String s) {
//                return !s.contains(" INFO ") && !s.contains(" DEBUG ")
//            }
//        })
//        .collect(Collectors.toList())
//        .join(System.lineSeparator())
//
//        then:
//        output == ''
//    }

    def 'should stop'() {
        setup:
        ExecutorService executor = Executors.newSingleThreadExecutor();

        when:
        executor.submit(new Runnable() {
            @Override
            void run() {
                commandService.executeCommand('kafka-consume',
                        '-b', embeddedKafka.brokersAsString,
                        '-t', 'testTopic'
                )
            }
        })
        sleep(3000)
        def q = executor.shutdownNow()
        def s = warnOrWorse(trimmedOutput())

        then:
        q.empty
        s == ''
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
