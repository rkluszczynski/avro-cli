package io.github.rkluszczynski.avro.cli.command.kafka.avro;

import io.github.rkluszczynski.avro.cli.command.kafka.ExtendedMessageListener;
import io.github.rkluszczynski.avro.cli.util.NativeAvroProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

public class AvroMessageListener extends ExtendedMessageListener<String, byte[]> {
    private final SchemaProvider schemaProvider;

    public AvroMessageListener(SchemaProvider schemaProvider) {
        this.schemaProvider = schemaProvider;
    }

    @Override
    public void onMessage(ConsumerRecord<String, byte[]> record) {
        log.info(String.format("{%d} offset == %d, partition == %d", incrementAndGet(), record.offset(), record.partition()));

//        System.out.printf("%s%n%n", record.value());
        try {
            NativeAvroProcessor.convertAvroToJson(new ByteArrayInputStream(record.value()),
                    System.out,
                    schemaProvider.getSchema(),
                    schemaProvider.getSchema(),
                    false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void applyMessageListenerConfig(Map<String, Object> consumerConfig) {
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
    }

    private Log log = LogFactory.getLog(AvroMessageListener.class);
}
