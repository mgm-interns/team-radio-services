package com.mgmtp.radio.exception;

public class StationNotFoundException extends RadioNotFoundException {

    public StationNotFoundException() {
        super("Can not find this station.");
    }

    public StationNotFoundException(String stationId) {
        super("Can not find station " + stationId + ".");
    }
}
