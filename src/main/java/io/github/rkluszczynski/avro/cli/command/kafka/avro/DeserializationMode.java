package io.github.rkluszczynski.avro.cli.command.kafka.avro;

public enum DeserializationMode {
    AVRO_WIRE_FORMAT,
    MAGIC_BYTE_FORMAT,
    RAW_DATA_FORMAT,
    HEURISTIC
}
