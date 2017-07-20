package io.github.rkluszczynski.avro.cli.command.kafka.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AvroRecordProcessor {
    private final AvroRecordExtractor recordExtractor;

    public AvroRecordProcessor(String topicName, Map<Integer, Schema> schemas) {
        final List<Integer> versions = schemas.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        recordExtractor = new AvroRecordExtractor(topicName, schemas, DecoderFactory.get(), versions);
    }

    public void processConsumerRecord(ConsumerRecord<String, byte[]> record) {
        final GenericRecordWrapper recordWrapper = recordExtractor.wrap(record.value());

        logRecordMetadata(record, recordWrapper);
        printGenericRecordAsJsonToStdout(recordWrapper.getRecord());
    }

    private void logRecordMetadata(ConsumerRecord<String, byte[]> record, GenericRecordWrapper recordWrapper) {
        final Instant timestampInstant = Instant.ofEpochMilli(recordWrapper.getTimestamp());
        log.info(String.format("offset == %d, partition == %d, schema-version == %s, timestamp == %s, recordInstant == %s",
                record.offset(),
                record.partition(),
                recordWrapper.getVersionCode(),
                Instant.ofEpochMilli(record.timestamp()),
                timestampInstant
        ));
    }

    private void printGenericRecordAsJsonToStdout(GenericRecord genericRecord) {
        final Schema schema = genericRecord.getSchema();

        final GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
        final JsonEncoder encoder;
        try {
            encoder = EncoderFactory.get().jsonEncoder(schema, System.out);
            writer.write(genericRecord, encoder);
            encoder.flush();
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            throw new IllegalStateException(e);
        }
        System.out.println();
    }

    private Log log = LogFactory.getLog(AvroRecordProcessor.class);
}
