package io.github.rkluszczynski.avro.cli.command.kafka.avro.deserialization;

import io.github.rkluszczynski.avro.cli.command.kafka.avro.AvroDeserializer;
import org.apache.avro.generic.GenericRecord;

class RawDataDeserializer implements AvroDeserializer {
    @Override
    public String getSchemaIdentifier() {
        return null;
    }

    @Override
    public GenericRecord getGenericRecord() {
        return null;
    }
}
