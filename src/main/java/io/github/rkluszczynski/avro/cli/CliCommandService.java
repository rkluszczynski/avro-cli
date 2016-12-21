package io.github.rkluszczynski.avro.cli;

import io.github.rkluszczynski.avro.cli.command.CliCommand;
import io.github.rkluszczynski.avro.cli.command.CommandException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
class CliCommandService {
    private final List<CliCommand> cliCommands;

    @Autowired
    CliCommandService(List<CliCommand> cliCommands) {
        this.cliCommands = cliCommands;
    }

    public void executeCommand(String... args) {
        try {
            if (args.length == 0) {
                new CommandException("Missing command argument!");
            }

            final String command = args[0];
            final CliCommand cliCommand = cliCommands.stream()
                    .filter(cmd -> cmd.getCommand().equals(command))
                    .findAny()
                    .orElseThrow(() -> new CommandException("No such command: " + command));

            cliCommand.initialize(
                    Arrays.copyOfRange(args, 1, args.length)
            );
            cliCommand.execute();
        } catch (CommandException ex) {
            if (log.isDebugEnabled()) {
                log.error(ex.getMessage(), ex);
            } else {
                log.error(ex.getMessage());
            }
        }
    }

    private Log log = LogFactory.getLog(CliCommandService.class);
}
