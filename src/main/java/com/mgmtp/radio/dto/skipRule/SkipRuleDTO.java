package com.mgmtp.radio.dto.skipRule;

import com.mgmtp.radio.sdo.SkipRuleType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class SkipRuleDTO {

	SkipRuleType skipRuleType;

    public SkipRuleDTO() {
    	this.skipRuleType = SkipRuleType.BASIC;
    }

	public SkipRuleDTO(final SkipRuleType skipRuleType) {
		if (skipRuleType != SkipRuleType.BASIC && skipRuleType != SkipRuleType.ADVANCE) {
			this.skipRuleType = SkipRuleType.BASIC;
		} else {
			this.skipRuleType = skipRuleType;
		}
	}

	@Setter(AccessLevel.NONE)
    String description;

	public String getDescription() {
		switch (skipRuleType) {
			case BASIC:
				description = "Rule: More than 50% downvotes can skip the song";
				break;
			case ADVANCE:
				description = "Rule: Only you can skip the song";
				break;
			default:
				description = "Invalid typeId";
				break;
		}
		return description;
	}
}

