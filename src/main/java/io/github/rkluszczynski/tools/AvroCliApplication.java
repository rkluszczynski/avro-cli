package io.github.rkluszczynski.tools;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AvroCliApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AvroCliApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(args[0]);
    }
}
