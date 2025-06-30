package com.mintit.incentive.common.exception;

public class UnauthorizationException extends RuntimeException {

    public UnauthorizationException() {
        super();
    }

    public UnauthorizationException(final String message) {
        super(message);
    }

    public UnauthorizationException(final String message, Throwable e) {
        super(message, e);
    }
}
