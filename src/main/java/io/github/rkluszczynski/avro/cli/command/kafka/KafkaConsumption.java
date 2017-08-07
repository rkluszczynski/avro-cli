package io.github.rkluszczynski.avro.cli.command.kafka;

import io.github.rkluszczynski.avro.cli.CliMainParameters;
import io.github.rkluszczynski.avro.cli.CommandException;
import io.github.rkluszczynski.avro.cli.command.CliCommand;
import io.github.rkluszczynski.avro.cli.command.CliCommandParameters;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

import static io.github.rkluszczynski.avro.cli.command.CommandNames.KAFKA_CONSUME;
import static io.github.rkluszczynski.avro.cli.command.kafka.KafkaMessageConsumer.ofCommandParameters;
import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
public class KafkaConsumption implements CliCommand {
    private final ConsumeParameters consumeParameters = new ConsumeParameters();
    private final CountDownLatch awaitLatch = new CountDownLatch(1);

    @Override
    public String execute(CliMainParameters mainParameters) {
        final KafkaMessageConsumer messageConsumer = ofCommandParameters(consumeParameters);

        final KafkaMessageListenerContainer<String, String> listenerContainer = messageConsumer.getListenerContainer();
        listenerContainer.start();

        registerContainerShutdownHook(listenerContainer);

        try {
            final Duration consumeDuration = consumeParameters.getDuration();

            if (isNull(consumeDuration)) {
                awaitLatch.await();
            } else {
                awaitLatch.await(consumeDuration.toMillis(), MILLISECONDS);
            }
        } catch (InterruptedException ex) {
            throw new CommandException("Kafka consumer interrupted!", ex);
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
