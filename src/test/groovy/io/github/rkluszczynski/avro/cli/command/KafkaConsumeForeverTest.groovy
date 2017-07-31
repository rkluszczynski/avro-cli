package io.github.rkluszczynski.avro.cli.command

import io.github.rkluszczynski.avro.cli.BaseTestSpecification
import io.github.rkluszczynski.avro.cli.CliMainParameters
import io.github.rkluszczynski.avro.cli.command.kafka.KafkaConsumption
import org.junit.ClassRule
import org.springframework.kafka.test.rule.KafkaEmbedded
import spock.lang.Shared

import java.util.function.Predicate
import java.util.stream.Collectors

//@ContextConfiguration
//@SpringBootTest
class KafkaConsumeForeverTest extends BaseTestSpecification {
    @ClassRule
    @Shared
    KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, 1, 'testTopic')

    def 'should end without output when interrupting infinite consumption'() {
        setup:
        def kafkaConsumeCommand = new KafkaConsumption()
        def cmdOutput = ''
        def th = runInThread {
            kafkaConsumeCommand.consumeParameters.bootstrapServers = embeddedKafka.brokersAsString
            kafkaConsumeCommand.consumeParameters.topics = ['testTopic']

            cmdOutput = kafkaConsumeCommand.execute(new CliMainParameters())
        }
        capture.flush()
        capture.reset()

        when:
        th.start()
        sleep(7000)
        th.interrupt()
        th.join()

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

        then:
        capture.toString().trim() == ''
        cmdOutput == ''
    }
//
//    def 'should stop'() {
//        setup:
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//
//        when:
//        executor.submit(new Runnable() {
//            @Override
//            void run() {
//                commandService.executeCommand('kafka-consume',
//                        '-b', embeddedKafka.brokersAsString,
//                        '-t', 'testTopic'
//                )
//            }
//        })
//        sleep(3000)
//        def q = executor.shutdownNow()
//        def s = warnOrWorse(trimmedOutput())
//
//        then:
//        q.empty
//        s == ''
//    }

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
