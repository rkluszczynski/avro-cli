package io.github.rkluszczynski.avro.cli;

import avro.shaded.com.google.common.collect.Sets;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import io.github.rkluszczynski.avro.cli.command.CliCommand;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
public class CliCommandService {
    private final Map<String, CliCommand> cliCommands;
    private final CliMainParameters cliMainParameters;
    private final JCommander commander;

    @Autowired
    CliCommandService(List<CliCommand> cliCommands) {
        this.cliMainParameters = new CliMainParameters();
        this.commander = createCommander(cliCommands, cliMainParameters);
        this.cliCommands = cliCommands.stream()
                .collect(Collectors.toMap(CliCommand::getCommandName, Function.identity()));
    }

    /**
     * This method executes command based on passed arguments.
     *
     * @param args Commandline arguments
     */
    public void executeCommand(String... args) {
        try {
            commander.parse(stripSpringBootArguments(args));

            if (cliMainParameters.isHelp() || args.length == 0) {
                commander.usage();
                return;
            }

            final String parsedCommand = commander.getParsedCommand();
            final CliCommand cliCommand = cliCommands.get(parsedCommand);

            if (cliCommand.getParameters().isHelp()) {
                commander.usage(parsedCommand);
                return;
            }
            final String stdoutMessage = cliCommand.execute(cliMainParameters);
            System.out.println(stdoutMessage);
        } catch (CommandException | ParameterException ex) {
            handleCommandException(ex);
        }
    }

    private String[] stripSpringBootArguments(String[] args) {
        final HashSet<String> excludeArgs = Sets.newHashSet("--trace", "--debug", "--info", "--warn", "--error");

        final List<String> filteredArgs = Arrays.stream(args)
                .filter(s -> !excludeArgs.contains(s))
                .collect(Collectors.toList());

        return filteredArgs.toArray(new String[filteredArgs.size()]);
    }

    private void handleCommandException(RuntimeException ex) {
        String stderrMessage = String.format("FAILED [%s] %s",
                isNull(ex.getCause()) ? ex.getClass().getCanonicalName() : ex.getCause().getClass().getCanonicalName(),
                isNull(ex.getCause()) ? ex.getLocalizedMessage() : ex.getCause().getLocalizedMessage()
        );
        System.err.println(stderrMessage);

//        if (log.isDebugEnabled()) {
//            log.error(ex.getMessage(), ex);
//        }
        if (cliMainParameters.isVerbose()) {
            ex.printStackTrace(System.err);
        }
    }

    private JCommander createCommander(List<CliCommand> cliCommands, CliMainParameters cliMainParameters) {
        JCommander commander = new JCommander(cliMainParameters);
        commander.setProgramName(PROGRAM_NAME);
        commander.setCaseSensitiveOptions(true);
        commander.setAllowAbbreviatedOptions(true);
        commander.setExpandAtSign(true);
        cliCommands.stream()
                .forEach(cliCommand -> commander.addCommand(cliCommand.getCommandName(), cliCommand.getParameters()));
        return commander;
    }

    private Log log = LogFactory.getLog(CliCommandService.class);

    private static final String PROGRAM_NAME = "avro-cli";
}
