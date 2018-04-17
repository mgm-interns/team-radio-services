package com.mgmtp.radio.exception;

public class StationDuplicateNameException extends Exception {
	public StationDuplicateNameException() {
		super("This station name is existed.");
	}

	public StationDuplicateNameException(String stationName) {
		super("This station name is existed: " + stationName + ".");
	}
}
