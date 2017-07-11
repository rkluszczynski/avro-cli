package io.github.rkluszczynski.avro.cli.command;

public enum CommandNames {
    CONVERT,
    FINGERPRINT,
    KAFKA_CONSUME,
    NORMALIZE,
    VALIDATE;

    public String getCliCommand() {
        return name().toLowerCase().replace('_', '-');
    }
}
