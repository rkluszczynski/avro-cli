package io.github.rkluszczynski.avro.cli.command;

import com.beust.jcommander.Parameter;

public abstract class CliCommandParameters {
    @Parameter(
            names = {"--help", "-h"},
            description = "Show help",
            help = true,
            hidden = true
    )
    private boolean help;

    public boolean isHelp() {
        return help;
    }
}
