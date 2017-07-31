package io.github.rkluszczynski.avro.cli;

import io.github.rkluszczynski.avro.cli.command.kafka.KafkaConsumption;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaConsumeForeverTest {
    private CliCommandService commandService = new CliCommandService(Lists.newArrayList(new KafkaConsumption()));

    @Rule
    public OutputCapture capture = new OutputCapture();

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, 1, "testTopic");

    @Test
    public void exampleTest() throws InterruptedException {
        Thread thread = new Thread(() ->
                commandService.executeCommand("kafka-consume", "-b", embeddedKafka.getBrokersAsString(), "-t", "testTopic"));

        thread.start();
        Thread.sleep(3000);
        thread.interrupt();
        thread.join();

        final String output = capture.toString().trim();

        Assertions.assertThat(output).isEqualToIgnoringCase("");
    }
}
