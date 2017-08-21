package io.github.rkluszczynski.avro.cli.command.kafka.avro.deserialization;

import io.github.rkluszczynski.avro.cli.command.kafka.avro.AvroDeserializer;
import org.apache.avro.generic.GenericRecord;

class FingerprintDeserializer implements AvroDeserializer {
    @Override
    public String getSchemaIdentifier() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public GenericRecord getGenericRecord() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
