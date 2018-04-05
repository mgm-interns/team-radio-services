package com.mgmtp.radio.controller.response;

public class RadioResponse {

    private boolean success;

    public RadioResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
