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
	public static final SkipRuleType BASIC = SkipRuleType.BASIC;
	public static final SkipRuleType ADVANCE = SkipRuleType.ADVANCE;

//	int typeId;
	SkipRuleType skipRuleType = SkipRuleType.BASIC;

	public SkipRule() {
		this.skipRuleType = BASIC;
	}

	public SkipRule(SkipRuleType skipRuleType) {
		if(skipRuleType != BASIC && skipRuleType != ADVANCE) {
			skipRuleType = BASIC;
		}
		this.skipRuleType = skipRuleType;
	}
}
