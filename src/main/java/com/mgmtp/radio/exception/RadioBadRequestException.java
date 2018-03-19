package com.mgmtp.radio.exception;

public class RadioBadRequestException extends RadioException {

    public RadioBadRequestException(String message) {
        super(message);
    }

    public RadioBadRequestException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
