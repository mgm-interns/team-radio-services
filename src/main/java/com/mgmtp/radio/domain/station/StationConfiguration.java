package com.mgmtp.radio.domain.station;

import lombok.Data;
import org.springframework.data.annotation.Transient;

@Data
public class StationConfiguration {
	private SkipRule skipRule;

	public StationConfiguration() {
		this.skipRule = new SkipRule();
	}

	@Transient
	private String stationFriendlyId;
}
