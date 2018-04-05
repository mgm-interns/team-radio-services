package com.mgmtp.radio.dto.station;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class StationConfigurationDTO {
	private String id;
	private SkipRuleDTO skipRule;
}
