package com.mgmtp.radio.domain.station;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class StationConfiguration {
	private SkipRule rule;

	public StationConfiguration() {
		this.rule = new SkipRule();
	}
}
