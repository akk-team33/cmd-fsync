package de.team33.cmd.fstool.main.common;

public class BadRequestException extends IllegalArgumentException {

    public BadRequestException(final String message) {
        super(message);
    }
}
