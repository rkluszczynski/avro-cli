package io.github.rkluszczynski.avro.cli.command;

public interface CliCommand {

    void execute();

    void initialize(String... args);

    String getCommand();
}
