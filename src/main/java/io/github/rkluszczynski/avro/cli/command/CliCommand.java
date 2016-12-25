package io.github.rkluszczynski.avro.cli.command;

public interface CliCommand {

    String execute();

    String getCommandName();

    CliCommandParameters getParameters();
}
