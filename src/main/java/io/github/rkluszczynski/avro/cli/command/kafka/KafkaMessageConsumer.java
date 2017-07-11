package io.github.rkluszczynski.avro.cli.command.kafka;

import io.github.rkluszczynski.avro.cli.command.kafka.text.BatchTextMessageListener;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class KafkaMessageConsumer {
    private final KafkaMessageListenerContainer<String, String> listenerContainer;
    private final BatchTextMessageListener textMessageListener;

    public KafkaMessageConsumer(String bootstrapServers, String[] topics, OffsetResetParameter offsetReset) {
        final ContainerProperties containerProperties = new ContainerProperties(topics);

        final ConsumerFactory<String, String> consumerFactory = consumerFactory(bootstrapServers, offsetReset);
        listenerContainer = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

        textMessageListener = new BatchTextMessageListener();
        listenerContainer.setupMessageListener(textMessageListener);
    }

    public KafkaMessageListenerContainer<String, String> getListenerContainer() {
        return listenerContainer;
    }

    public BatchTextMessageListener getTextMessageListener() {
        return textMessageListener;
    }

    private ConsumerFactory<String, String> consumerFactory(String bootstrapServers, OffsetResetParameter offsetReset) {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(bootstrapServers, offsetReset));
    }

    private Map<String, Object> consumerConfigs(String bootstrapServers, OffsetResetParameter offsetReset) {
        Map<String, Object> consumerConfig = new HashMap<>();

        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());

        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

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

        KafkaMessageConsumerFactory withOffsetReset(OffsetResetParameter offsetReset) {
            this.offsetReset = offsetReset;
            return this;
        }

        KafkaMessageConsumer build() {
            Assert.notNull(bootstrapServers, "Bootstrap servers should be provided!");
            Assert.notNull(topics, "Topics should be provided!");
            Assert.notNull(offsetReset, "Offset reset value should be provided!");

            return new KafkaMessageConsumer(bootstrapServers, topics, offsetReset);
        }
    }
}
