package com.mgmtp.radio.exception;

import lombok.Data;

@Data
public class RadioDuplicateNameException extends Exception {

	public RadioDuplicateNameException(String stationName) {
		super(String.format("A station with name %s has existed!", stationName));
	}
}
