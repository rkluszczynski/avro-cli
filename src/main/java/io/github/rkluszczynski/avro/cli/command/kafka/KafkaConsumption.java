package io.github.rkluszczynski.avro.cli.command.kafka;

import io.github.rkluszczynski.avro.cli.CliMainParameters;
import io.github.rkluszczynski.avro.cli.command.CliCommand;
import io.github.rkluszczynski.avro.cli.command.CliCommandParameters;
import org.awaitility.core.ConditionFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.github.rkluszczynski.avro.cli.command.CommandNames.KAFKA_CONSUME;
import static java.util.Objects.nonNull;
import static org.awaitility.Awaitility.await;

@Component
public class KafkaConsumption implements CliCommand {
    private final ConsumerParameters consumerParameters = new ConsumerParameters();

    @Override
    public String execute(CliMainParameters mainParameters) {
        final List<String> topics = consumerParameters.getTopics();
        final KafkaMessageConsumer messageConsumer = KafkaMessageConsumer.builder()
                .withBootstrapServers(consumerParameters.getBootstrapServers())
                .withTopics(topics.toArray(new String[topics.size()]))
                .withMessageType(consumerParameters.getMessageType())
                .withOffsetReset(consumerParameters.getOffsetReset())
                .build();

        final KafkaMessageListenerContainer<String, String> listenerContainer = messageConsumer.getListenerContainer();
        listenerContainer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (listenerContainer.isRunning()) {
                listenerContainer.stop();

                await().forever().until(() -> listenerContainer.isRunning());
            }
        }));

        final ConditionFactory forever = await()
//                .pollDelay(1L, TimeUnit.SECONDS)
                .forever();

        final Long limit = consumerParameters.getLimit();
        if (isValidLimitProvided(limit)) {
            forever.until(() -> messageConsumer.getMessageListener().getCount() >= limit);
            listenerContainer.stop();
        } else {
            forever.until(() -> !listenerContainer.isRunning());
        }
        return "";
    }

    private boolean isValidLimitProvided(Long limit) {
        return nonNull(limit) && limit > 0L;
    }

    @Override
    public String getCommandName() {
        return KAFKA_CONSUME.getCliCommand();
    }

    @Override
    public CliCommandParameters getParameters() {
        return consumerParameters;
    }
}
