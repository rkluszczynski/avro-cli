package io.github.rkluszczynski.avro.cli.command.kafka;

import io.github.rkluszczynski.avro.cli.command.kafka.avro.BatchAvroMessageListener;
import io.github.rkluszczynski.avro.cli.command.kafka.text.BatchTextMessageListener;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.github.rkluszczynski.avro.cli.command.kafka.MessageTypeParameter.AVRO;

class KafkaMessageConsumer {
    private final KafkaMessageListenerContainer<String, String> listenerContainer;
    private final ExtendedMessageListener messageListener;

    public KafkaMessageConsumer(String bootstrapServers,
                                String[] topics,
                                MessageTypeParameter messageType,
                                OffsetResetParameter offsetReset) {
        final ContainerProperties containerProperties = new ContainerProperties(topics);

        final ConsumerFactory<String, String> consumerFactory =
                createConsumerFactory(bootstrapServers, messageType, offsetReset);
        listenerContainer = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

        messageListener = createMessageListener(messageType);
        listenerContainer.setupMessageListener(messageListener);
    }


    private ExtendedMessageListener createMessageListener(MessageTypeParameter messageType) {
        if (AVRO.equals(messageType)) {
            return new BatchAvroMessageListener();
        }
        return new BatchTextMessageListener();
    }

    public KafkaMessageListenerContainer<String, String> getListenerContainer() {
        return listenerContainer;
    }

    public ExtendedMessageListener getMessageListener() {
        return messageListener;
    }

    private ConsumerFactory<String, String> createConsumerFactory(String bootstrapServers,
                                                                  MessageTypeParameter messageType,
                                                                  OffsetResetParameter offsetReset) {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(bootstrapServers, messageType, offsetReset));
    }

    private Map<String, Object> consumerConfigs(String bootstrapServers,
                                                MessageTypeParameter messageType,
                                                OffsetResetParameter offsetReset) {
        Map<String, Object> consumerConfig = new HashMap<>();

        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());

        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        switch (messageType) {
            case TEXT:
                consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                break;
            case AVRO:
                consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
                break;
            default:
                throw new RuntimeException("Unknown kafka message type!");
        }
        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset.name().toLowerCase());
        consumerConfig.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");

        return consumerConfig;
    }

    static KafkaMessageConsumerFactory builder() {
        return new KafkaMessageConsumerFactory();
    }

    static class KafkaMessageConsumerFactory {
        private String bootstrapServers;
        private String[] topics;
        private OffsetResetParameter offsetReset;
        private MessageTypeParameter messageType;

        private KafkaMessageConsumerFactory() {
        }

        KafkaMessageConsumerFactory withBootstrapServers(String bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
            return this;
        }

        KafkaMessageConsumerFactory withTopics(String... topics) {
            this.topics = topics;
            return this;
        }

        KafkaMessageConsumerFactory withMessageType(MessageTypeParameter messageType) {
            this.messageType = messageType;
            return this;
        }

        KafkaMessageConsumerFactory withOffsetReset(OffsetResetParameter offsetReset) {
            this.offsetReset = offsetReset;
            return this;
        }

        KafkaMessageConsumer build() {
            Assert.notNull(bootstrapServers, "Bootstrap servers should be provided!");
            Assert.notNull(topics, "Topics should be provided!");
            Assert.notNull(messageType, "Message type should be provided!");
            Assert.notNull(offsetReset, "Offset reset value should be provided!");

            return new KafkaMessageConsumer(bootstrapServers, topics, messageType, offsetReset);
        }
    }
}
