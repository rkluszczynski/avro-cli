package io.github.rkluszczynski.avro.cli.command.kafka.avro;

import io.github.rkluszczynski.avro.cli.command.kafka.ExtendedMessageListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Map;

public class AvroMessageListener extends ExtendedMessageListener<String, byte[]> {

    @Override
    public void onMessage(ConsumerRecord<String, byte[]> record) {
        log.info(String.format("{%d} offset == %d, partition == %d", incrementAndGet(), record.offset(), record.partition()));

        System.out.printf("%s%n%n", record.value());
    }

    @Override
    public void applyMessageListenerConfig(Map<String, Object> consumerConfig) {
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
    }

    private Log log = LogFactory.getLog(AvroMessageListener.class);
}
