package io.github.rkluszczynski.avro.cli.command.kafka;

import io.github.rkluszczynski.avro.cli.CliMainParameters;
import io.github.rkluszczynski.avro.cli.command.CliCommand;
import io.github.rkluszczynski.avro.cli.command.CliCommandParameters;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

import static io.github.rkluszczynski.avro.cli.command.CommandNames.KAFKA_CONSUME;
import static io.github.rkluszczynski.avro.cli.command.kafka.KafkaMessageConsumer.ofConsumeParameters;
import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
public class KafkaConsumption implements CliCommand {
    private final ConsumeParameters consumeParameters = new ConsumeParameters();

    @Override
    public String execute(CliMainParameters mainParameters) {
        final KafkaMessageConsumer messageConsumer = ofConsumeParameters(consumeParameters);

        final KafkaMessageListenerContainer<String, String> listenerContainer = messageConsumer.getListenerContainer();
        listenerContainer.start();

        registerContainerShutdownHook(listenerContainer);

        try {
            final Duration consumeDuration = consumeParameters.getDuration();
            final CountDownLatch awaitLatch = new CountDownLatch(1);

            if (isNull(consumeDuration)) {
                awaitLatch.await();
            } else {
                awaitLatch.await(consumeDuration.toMillis(), MILLISECONDS);
            }
        } catch (InterruptedException ex) {
            System.out.print(""); // FIXME
        } finally {
            listenerContainer.stop();
        }
        return "";
    }

    private void registerContainerShutdownHook(KafkaMessageListenerContainer<String, String> listenerContainer) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (listenerContainer.isRunning()) {
                listenerContainer.stop();
            }
        }));
    }

    @Override
    public String getCommandName() {
        return KAFKA_CONSUME.getCliCommand();
    }

    @Override
    public CliCommandParameters getParameters() {
        return consumeParameters;
    }
}
