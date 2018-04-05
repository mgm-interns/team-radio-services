package com.mgmtp.radio.domain.station;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.HashMap;

@Data
public class SkipRule {
	public static final int BASIC = 0;
	public static final int ADVANCE = 1;

	int typeId;

	public SkipRule() {
		this.typeId = BASIC;
	}

	public SkipRule(int typeId) throws InvalidRuleTypeDtoException {
		if(typeId == BASIC || typeId == ADVANCE) {
			this.typeId = typeId;
		}
		else {
			throw new InvalidRuleTypeDtoException();
		}
	}

	public class InvalidRuleTypeDtoException extends Exception {
		@Override
		public String getMessage() {
			return "The input RuleTypeDTO is invalid or empty";
		}
	}
}
