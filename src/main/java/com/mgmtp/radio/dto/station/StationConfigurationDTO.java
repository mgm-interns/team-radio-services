package com.mgmtp.radio.dto.station;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mgmtp.radio.dto.skipRule.SkipRuleDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonIgnoreProperties(value = { "stationFriendlyId" })
public class StationConfigurationDTO {
	private SkipRuleDTO skipRule;
	private String stationFriendlyId;
}
