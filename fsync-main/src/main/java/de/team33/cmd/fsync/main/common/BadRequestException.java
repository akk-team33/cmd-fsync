package de.team33.cmd.fsync.main.common;

public class BadRequestException extends IllegalArgumentException {

    public BadRequestException(final String message) {
        super(message);
    }
}
