package io.github.rkluszczynski.avro.cli.command.kafka.avro;

import org.apache.avro.generic.GenericRecord;

public interface AvroDeserializer {

    String getSchemaIdentifier();

    GenericRecord getGenericRecord();
}
