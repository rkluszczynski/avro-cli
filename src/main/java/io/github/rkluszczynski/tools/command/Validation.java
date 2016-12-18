package io.github.rkluszczynski.tools.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

class Validation {
    @Parameter(names = {"--length", "-l"})
    int length;
    @Parameter(names = {"--pattern", "-p"})
    int pattern;

    public static void main(String... args) {
        Validation main = new Validation();
        new JCommander(main, args);
        main.run();
    }

    public void run() {
        System.out.printf("%d %d", length, pattern);
    }
}
