package com.mgmtp.radio.domain.station;

import com.mgmtp.radio.sdo.SkipRuleType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.HashMap;

@Data
@ToString
public class SkipRule {

	SkipRuleType skipRuleType;

	public SkipRule() {
		this.skipRuleType = SkipRuleType.BASIC;
	}

	public SkipRule(SkipRuleType skipRuleType) {
		if(skipRuleType != SkipRuleType.BASIC && skipRuleType != SkipRuleType.ADVANCE) {
			skipRuleType = SkipRuleType.BASIC;
		}
		this.skipRuleType = skipRuleType;
	}
}
