package com.mgmtp.radio.dto.station;

public class ConfigurationDTO {
	private SkipRuleDTO skipRuleDTO;

	public ConfigurationDTO(SkipRuleDTO skipRuleDTO) {
		this.setSkipRuleDTO(skipRuleDTO);
	}

	public SkipRuleDTO getSkipRuleDTO() {
		return skipRuleDTO;
	}

	public void setSkipRuleDTO(SkipRuleDTO skipRuleDTO) {
		this.skipRuleDTO = skipRuleDTO;
	}
}
