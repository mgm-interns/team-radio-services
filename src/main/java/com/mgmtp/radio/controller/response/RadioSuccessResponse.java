package com.mgmtp.radio.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RadioSuccessResponse<T> extends RadioResponse {
    private T data;

    public RadioSuccessResponse() {
        super(true);
    }

    public RadioSuccessResponse(T data) {
        super(true);
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
