package com.mgmtp.radio.controller.response;

public class RadioErrorResponse extends RadioResponse {

    private String error;

    public RadioErrorResponse(String message) {
        super(false);
        this.error = message;
    }

    public String getError() {
        return error;
    }
}
