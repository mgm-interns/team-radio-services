package com.mgmtp.radio.domain.station;

import com.mgmtp.radio.dto.station.SkipRuleDTO.RuleTypeDTO;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@NoArgsConstructor
public class SkipRule {
	private RuleType ruleType;
	private String typeName;
	private String typeDescription;
	private HashMap<Integer, SkipRule> mapAllRules;
	private enum RuleType {
		BASIC(0),
		ADVANCE(1),
		;
		private int typeId;

		RuleType(int typeId) {
			this.typeId = typeId;
		}

		public int getTypeId() {
			return typeId;
		}
	}
	public SkipRule(RuleType ruleType) {
		this.ruleType = ruleType;
		switch (ruleType) {
			case BASIC:
				this.typeName = "Basic";
				this.typeDescription = "SkipRule: More than 50% downvotes can skip the song";
				break;
			case ADVANCE:
				this.typeName = "Advance";
				this.typeDescription = "SkipRule: Only you can skip the song";
			default:
				break;
		}
	}
	public SkipRule getInstance(RuleType ruleType) throws InvalidRuleTypeException {
		if(mapAllRules == null) {
			mapAllRules = new HashMap<>();
		}
		if(ruleType != null) {
			SkipRule skipRule = mapAllRules.get(ruleType.getTypeId());
			if(skipRule == null)
			{
				skipRule = new SkipRule(ruleType);
				mapAllRules.put(ruleType.getTypeId(), skipRule);
			}
			return skipRule;
		}
		else {
			throw new InvalidRuleTypeException();
		}
	}

	public boolean isBasic() {
		return ruleType.equals(RuleTypeDTO.BASIC);
	}

	public class InvalidRuleTypeException extends Exception {
		@Override
		public String getMessage() {
			return "The input RuleType is invalid or empty";
		}
	}
}
