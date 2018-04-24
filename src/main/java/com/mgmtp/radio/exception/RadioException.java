package com.mgmtp.radio.exception;

public class RadioException extends RuntimeException {
    public RadioException() {

    }

    public RadioException(String message) {
        super(message);
    }

    public RadioException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
