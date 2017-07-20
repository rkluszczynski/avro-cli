package io.github.rkluszczynski.avro.cli.command.kafka.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.util.Utf8;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AvroRecordExtractor {

    private static Logger log = Logger.getLogger(AvroRecordExtractor.class);

    private final Map<Integer, Schema> schemas;
    private final DecoderFactory decoderFactory;
    private final List<Integer> versions;

    public AvroRecordExtractor(String topicName, Map<Integer, Schema> schemas, DecoderFactory decoderFactory,
                               List<Integer> versions) {
        log.info(String.format("Creating extractor for %s with %d schemas of versions %s", topicName,
                schemas.size(), versions.toString()));
        this.schemas = schemas;
        this.decoderFactory = decoderFactory;
        this.versions = versions;
    }

    public GenericRecordWrapper wrap(byte[] originalMessage) {
        GenericRecordWrapper recordWrapper = extractGenericRecord(originalMessage);
        recordWrapper.setTimestamp(getTimestampFromRecord(recordWrapper.getRecord()));
        return recordWrapper;
    }

    @SuppressWarnings("unchecked")
    private long getTimestampFromRecord(GenericRecord record) {
        long timestamp = System.currentTimeMillis();
        if (record.get("__timestamp") != null) {
            timestamp = (Long) record.get("__timestamp");
        } else if (record.get("__metadata") != null && ((Map<Utf8, Utf8>) record.get("__metadata")).get(new Utf8("timestamp")) != null) {
            timestamp = Long.valueOf(((Map<Utf8, Utf8>) record.get("__metadata")).get(new Utf8("timestamp")).toString());
        }
        return timestamp;
    }

    private GenericRecordWrapper extractGenericRecord(byte[] message) {
        return decodePayloadUsingItsSchema(message)
                .orElseGet(() -> decodeUsingSchemasFromLatestToOldest(message)
                        .orElseThrow(() -> new IllegalStateException("Could not decode message using any schema")));
    }

    private Optional<GenericRecordWrapper> decodeUsingSchemasFromLatestToOldest(byte[] message) {
        for (int i : versions) {
            final Optional<GenericRecord> genericRecord = noSchemaDecoding(message, schemas.get(i));
            if (genericRecord.isPresent()) {
                return Optional.of(new GenericRecordWrapper(genericRecord.get(), schemas.get(i), "guessed-" + i));
            }
        }
        return Optional.empty();
    }

    private Optional<GenericRecord> noSchemaDecoding(byte[] message, Schema schema) {
        try {
            return Optional.of(decodePayloadWithGivenSchema(message, schema));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private GenericRecord decodePayloadWithGivenSchema(byte[] message, Schema schema) throws IOException {
        GenericDatumReader<GenericRecord> reader = new GenericDatumReader<>(schema,
                schemas.get(versions.get(0)));
        return reader.read(null, decoderFactory.binaryDecoder(message, null));
    }

    private Optional<GenericRecordWrapper> decodePayloadUsingItsSchema(byte[] message) {
        try {
            AvroDeserializer deserializer = new AvroDeserializer(message);
            final int schemaVersion = deserializer.getWriterSchemaVersion();
            Schema schema = schemas.get(schemaVersion);

            DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
            BinaryDecoder binaryDecoder = decoderFactory.binaryDecoder(
                    deserializer.getPayload(),
                    deserializer.getAvroBinaryOffset(),
                    deserializer.getAvroBinaryLength(),
                    null);

            return Optional.of(new GenericRecordWrapper(datumReader.read(null, binaryDecoder), schema, "0-" + schemaVersion));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
