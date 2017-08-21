package io.github.rkluszczynski.avro.cli.command.kafka.avro;

import io.github.rkluszczynski.avro.cli.command.kafka.ExtendedMessageListener;
import org.apache.avro.Schema;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static io.github.rkluszczynski.avro.cli.util.NativeAvroProcessor.convertAvroToJson;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Instant.ofEpochMilli;

public class AvroMessageListener extends ExtendedMessageListener<String, byte[]> {
    private final SchemaProvider schemaProvider;
    private final Collection<AvroDeserializer> deserializers;

    public AvroMessageListener(SchemaProvider schemaProvider, Collection<AvroDeserializer> deserializers) {
        this.schemaProvider = schemaProvider;
        this.deserializers = Collections.unmodifiableCollection(deserializers);
    }

    @Override
    public void onMessage(ConsumerRecord<String, byte[]> record) {
        final long messageNumber = incrementAndGet();
        for (AvroDeserializer deserializer : deserializers) {
            final Optional<String> jsonMessageString = deserializeIfPossible(messageNumber, deserializer, record);

            if (jsonMessageString.isPresent()) {
                System.out.printf("%s%n%n", jsonMessageString.get());
                break;
            }
        }
    }

    private Optional<String> deserializeIfPossible(long messageNumber,
                                                   AvroDeserializer deserializer,
                                                   ConsumerRecord<String, byte[]> record) {
        for (SchemaProvider.SchemaWrapper schemaWrapper : schemaProvider) {
            final Schema schema = schemaWrapper.getSchema();

            try {
                final String jsonRecordString = convertToJsonString(record.value(), schema);

                log.info(String.format("{%d} offset == %d, partition == %d, timestamp == %s, schema == %s",
                        messageNumber, record.offset(), record.partition(), ofEpochMilli(record.timestamp()),
                        schemaWrapper.getId()));
                return Optional.of(jsonRecordString);
            } catch (Exception e) {
                log.warn(String.format("{%d} offset == %d, partition == %d, timestamp == %s: "
                                + "Could not deserialize message with schema %s!",
                        messageNumber, record.offset(), record.partition(), ofEpochMilli(record.timestamp()),
                        schemaWrapper.getId()), e);
            }
        }
        return Optional.empty();
    }

    @Override
    public void applyMessageListenerConfig(Map<String, Object> consumerConfig) {
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
    }

    private String convertToJsonString(byte[] recordBytes, Schema schema) throws IOException {
        final InputStream inputStream = new ByteArrayInputStream(recordBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        convertAvroToJson(inputStream, outputStream, schema, schema, false);
        return outputStream.toString(UTF_8.name());
    }

    private Log log = LogFactory.getLog(AvroMessageListener.class);
}
