package io.github.rkluszczynski.avro.cli.command.kafka.avro;

import org.apache.avro.Schema;

public interface SchemaProvider {

    Schema getSchema();
}
