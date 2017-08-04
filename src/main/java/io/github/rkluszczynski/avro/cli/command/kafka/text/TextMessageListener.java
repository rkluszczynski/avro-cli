package io.github.rkluszczynski.avro.cli.command.kafka.text;

import io.github.rkluszczynski.avro.cli.command.kafka.ExtendedMessageListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Map;

import static java.time.Instant.ofEpochMilli;

public class TextMessageListener extends ExtendedMessageListener<String, String> {

    @Override
    public void onMessage(ConsumerRecord<String, String> record) {
        log.info(String.format("{%d} offset == %d, partition == %d, timestamp == %s",
                incrementAndGet(), record.offset(), record.partition(), ofEpochMilli(record.timestamp())));

        System.out.printf("%s%n%n", record.value());
    }

    @Override
    public void applyMessageListenerConfig(Map<String, Object> consumerConfig) {
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    }

    private Log log = LogFactory.getLog(TextMessageListener.class);
}