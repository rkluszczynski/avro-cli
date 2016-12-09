package io.github.rkluszczynski.avro.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

class Main {
    @Parameter(names = {"--length", "-l"})
    int length;
    @Parameter(names = {"--pattern", "-p"})
    int pattern;

    public static void main(String... args) {
        Main main = new Main();
        new JCommander(main, args);
        main.run();
    }

    public void run() {
        System.out.printf("%d %d", length, pattern);
    }
}
