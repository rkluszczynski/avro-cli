package io.github.rkluszczynski.avro.cli.command.kafka;

import io.github.rkluszczynski.avro.cli.command.kafka.text.TextMessageListener;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.github.rkluszczynski.avro.cli.CliCommandService.PROGRAM_NAME;

class KafkaMessageConsumer {
    private final KafkaMessageListenerContainer<String, String> listenerContainer;
    private final ExtendedMessageListener messageListener;

    private KafkaMessageConsumer(String bootstrapServers,
                                 String[] topics,
                                 MessageTypeParameter messageType,
                                 OffsetResetParameter offsetReset) {
        final Map<String, Object> consumerConfig =
                consumerConfig(bootstrapServers, UUID.randomUUID().toString(), messageType, offsetReset);
        final ConsumerFactory<String, String> consumerFactory = createConsumerFactory(consumerConfig);

        final ContainerProperties containerProperties = new ContainerProperties(topics);
        listenerContainer = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

        messageListener = createMessageListener(messageType);
        listenerContainer.setupMessageListener(messageListener);
    }

    KafkaMessageListenerContainer<String, String> getListenerContainer() {
        return listenerContainer;
    }

    private ExtendedMessageListener createMessageListener(MessageTypeParameter messageType) {
        return new TextMessageListener();
    }

    private ConsumerFactory<String, String> createConsumerFactory(Map<String, Object> consumerConfig) {
        return new DefaultKafkaConsumerFactory<>(consumerConfig);
    }

    private Map<String, Object> consumerConfig(String bootstrapServers,
                                               String groupId,
                                               MessageTypeParameter messageType,
                                               OffsetResetParameter offsetReset) {
        Map<String, Object> consumerConfig = new HashMap<>();

        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, String.format("%s-%s", PROGRAM_NAME, groupId));

        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        switch (messageType) {
            case TEXT:
                consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                break;
            default:
                throw new RuntimeException("Unknown kafka message type!");
        }
        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset.name().toLowerCase());
        consumerConfig.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");

        return consumerConfig;
    }

    static KafkaMessageConsumer ofConsumeParameters(ConsumeParameters consumeParameters) {
        final List<String> topicsList = consumeParameters.getTopics();
        return new KafkaMessageConsumer(
                consumeParameters.getBootstrapServers(),
                topicsList.toArray(new String[topicsList.size()]),
                consumeParameters.getMessageType(),
                consumeParameters.getOffsetReset()
        );
    }
}
