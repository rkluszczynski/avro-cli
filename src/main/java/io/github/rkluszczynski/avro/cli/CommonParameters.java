package io.github.rkluszczynski.avro.cli;

import com.beust.jcommander.Parameter;

class CommonParameters {
    @Parameter(
            names = {"--help", "-h"},
            description = "Show help",
            help = true
    )
    private boolean help = false;

    boolean isHelp() {
        return help;
    }
}
