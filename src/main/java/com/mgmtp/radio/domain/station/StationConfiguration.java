package com.mgmtp.radio.domain.station;

import lombok.Data;

@Data
public class StationConfiguration {
	private SkipRule skipRule;

	public StationConfiguration() {
		this.skipRule = new SkipRule();
	}
}
