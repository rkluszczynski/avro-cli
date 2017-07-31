package io.github.rkluszczynski.avro.cli.command

import io.github.rkluszczynski.avro.cli.CliCommandService
import org.junit.ClassRule
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
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
    KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, 1, 'testTopic')

    @Autowired
    CliCommandService commandService


    def 'should end without output when interrupting infinite consumption'() {
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
