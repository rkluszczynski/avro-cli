package io.github.rkluszczynski.avro.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AvroCliApplication implements CommandLineRunner {
    private final CliCommandService commandService;

    @Autowired
    public AvroCliApplication(CliCommandService commandService) {
        this.commandService = commandService;
    }

    @Override
    public void run(String... args) throws Exception {
        commandService.executeCommand(args);
    }

    public static void main(String[] args) {
        SpringApplication.run(AvroCliApplication.class, args);
    }
}
