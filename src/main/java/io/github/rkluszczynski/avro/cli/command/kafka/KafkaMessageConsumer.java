package io.github.rkluszczynski.avro.cli.command.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.github.rkluszczynski.avro.cli.CliCommandService.PROGRAM_NAME;
import static io.github.rkluszczynski.avro.cli.command.kafka.ExtendedMessageListener.ofConsumeParameters;

class KafkaMessageConsumer {
    private final KafkaMessageListenerContainer<String, String> listenerContainer;

    private KafkaMessageConsumer(String bootstrapServers,
                                 String[] topics,
                                 OffsetResetParameter offsetReset,
                                 ExtendedMessageListener messageListener) {
        final String groupId = UUID.randomUUID().toString();

        final Map<String, Object> consumerConfig = consumerConfig(bootstrapServers, groupId, offsetReset);
        messageListener.applyMessageListenerConfig(consumerConfig);
        final ConsumerFactory<String, String> consumerFactory = createConsumerFactory(consumerConfig);

        final ContainerProperties containerProperties = new ContainerProperties(topics);
        listenerContainer = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

        listenerContainer.setupMessageListener(messageListener);
    }

    KafkaMessageListenerContainer<String, String> getListenerContainer() {
        return listenerContainer;
    }

    private ConsumerFactory<String, String> createConsumerFactory(Map<String, Object> consumerConfig) {
        return new DefaultKafkaConsumerFactory<>(consumerConfig);
    }

    private Map<String, Object> consumerConfig(String bootstrapServers,
                                               String groupId,
                                               OffsetResetParameter offsetReset) {
        Map<String, Object> consumerConfig = new HashMap<>();
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, String.format("%s-%s", PROGRAM_NAME, groupId));

        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset.name().toLowerCase());
        consumerConfig.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
        return consumerConfig;
    }

    static KafkaMessageConsumer ofCommandParameters(ConsumeParameters consumeParameters) {
        final ExtendedMessageListener messageListener = ofConsumeParameters(consumeParameters);

        final List<String> topicsList = consumeParameters.getTopics();
        return new KafkaMessageConsumer(
                consumeParameters.getBootstrapServers(),
                topicsList.toArray(new String[topicsList.size()]),
                consumeParameters.getOffsetReset(),
                messageListener
        );
    }
}
