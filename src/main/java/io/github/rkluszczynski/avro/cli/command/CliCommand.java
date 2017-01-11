package io.github.rkluszczynski.avro.cli.command;

import io.github.rkluszczynski.avro.cli.CliMainParameters;

public interface CliCommand {

    String execute(CliMainParameters mainParameters);

    String getCommandName();

    CliCommandParameters getParameters();
}
