package com.mgmtp.radio.dto.station;

import lombok.Data;

@Data
public class ConfigurationDTO {
	private SkipRuleDTO skipRuleDTO;

	public ConfigurationDTO(SkipRuleDTO skipRuleDTO) {
		this.setSkipRuleDTO(skipRuleDTO);
	}
}
