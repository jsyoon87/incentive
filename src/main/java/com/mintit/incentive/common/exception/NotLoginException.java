package com.mintit.incentive.common.exception;

public class NotLoginException extends RuntimeException {

    public NotLoginException() {
        super();
    }

    public NotLoginException(final String message) {
        super(message);
    }

    public NotLoginException(final String message, Throwable e) {
        super(message, e);
    }
}
