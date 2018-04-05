package com.mgmtp.radio.exception;

public class RadioServiceException extends RadioException {

    public RadioServiceException(String message) {
        super(message);
    }

    public RadioServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
