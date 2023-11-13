package io.github.calvary.service;

public class EmailNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmailNotFoundException() {
        super("We could not find the email!");
    }
}
