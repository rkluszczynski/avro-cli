package io.github.rkluszczynski.avro.cli;

import com.beust.jcommander.Parameter;

public class CliMainParameters {
    @Parameter(
            names = {"--help", "-h"},
            description = "Show help.",
            help = true
    )
    private boolean help = false;

    @Parameter(
            names = {"--verbose", "-v"},
            description = "Use verbose mode."
    )
    private boolean verbose = false;

    boolean isHelp() {
        return help;
    }

    public boolean isVerbose() {
        return verbose;
    }
}
