package com.mintit.incentive.common.exception;

public class CustomException extends RuntimeException {

    public CustomException() {
        super();
    }

    public CustomException(final String message) {
        super(message);
    }

    public CustomException(final String message, Throwable e) {
        super(message, e);
    }
}

