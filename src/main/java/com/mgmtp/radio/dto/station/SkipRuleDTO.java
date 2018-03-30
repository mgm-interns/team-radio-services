package com.mgmtp.radio.dto.station;

import java.util.HashMap;

public class SkipRuleDTO {

	private RuleType ruleType;
    private String typeName;
    private String typeDescription;
    private HashMap<Integer, SkipRuleDTO> mapAllRules;

    public enum RuleType{
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
	private SkipRuleDTO(RuleType ruleType) {
		this.ruleType = ruleType;
		switch (ruleType) {
			case BASIC:
				this.typeName = "Basic";
				this.typeDescription = "SkipRuleDTO: More than 50% downvotes can skip the song";
				break;
			case ADVANCE:
				this.typeName = "Advance";
				this.typeDescription = "SkipRuleDTO: Only you can skip the song";
			default:
				break;
		}
	}

	public String getTypeName() {
		return typeName;
	}

	public String getTypeDescription() {
    	return typeDescription;
	}

	public SkipRuleDTO getInstance(RuleType ruleType) throws InvalidRuleTypeException {
    	if(mapAllRules == null) {
    		mapAllRules = new HashMap<>();
	    }
		if(ruleType != null) {
			SkipRuleDTO skipRule = mapAllRules.get(ruleType.getTypeId());
			if(skipRule == null)
			{
				skipRule = new SkipRuleDTO(ruleType);
				mapAllRules.put(ruleType.getTypeId(), skipRule);
			}
			return skipRule;
		}
		else {
			throw new InvalidRuleTypeException();
		}
	}

	public boolean isBasic() {
    	return ruleType.equals(RuleType.BASIC);
	}

	public class InvalidRuleTypeException extends Exception {
		@Override
		public String getMessage() {
			return "The input RuleType is invalid or empty";
		}
	}
}
