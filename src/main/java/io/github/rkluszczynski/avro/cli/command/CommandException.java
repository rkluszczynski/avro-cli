package io.github.rkluszczynski.avro.cli.command;

public class CommandException extends RuntimeException {
    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
