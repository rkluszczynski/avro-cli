package io.github.rkluszczynski.avro.cli.command;

public enum CommandNames {
    CONVERT,
    FINGERPRINT,
    NORMALIZE,
    VALIDATE;

    public String getCliCommand() {
        return name().toLowerCase();
    }
}
