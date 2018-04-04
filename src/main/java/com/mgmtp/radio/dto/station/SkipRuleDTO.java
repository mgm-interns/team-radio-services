package com.mgmtp.radio.dto.station;

import lombok.NoArgsConstructor;

import java.util.HashMap;

@NoArgsConstructor
public class SkipRuleDTO {

	private RuleTypeDTO ruleTypeDTO;
    private String typeName;
    private String typeDescription;
    private HashMap<Integer, SkipRuleDTO> mapAllRules;

    public enum RuleTypeDTO {
    	BASIC(0),
	    ADVANCE(1),
	    ;
    	private int typeId;

    	RuleTypeDTO(int typeId) {
    		this.typeId = typeId;
	    }

	    public int getTypeId() {
    		return typeId;
	    }
    }
	public SkipRuleDTO(RuleTypeDTO ruleTypeDTO) {
		this.ruleTypeDTO = ruleTypeDTO;
		switch (ruleTypeDTO) {
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

	public SkipRuleDTO getInstance(RuleTypeDTO ruleTypeDTO) throws InvalidRuleTypeDtoException {
    	if(mapAllRules == null) {
    		mapAllRules = new HashMap<>();
	    }
		if(ruleTypeDTO != null) {
			SkipRuleDTO skipRule = mapAllRules.get(ruleTypeDTO.getTypeId());
			if(skipRule == null)
			{
				skipRule = new SkipRuleDTO(ruleTypeDTO);
				mapAllRules.put(ruleTypeDTO.getTypeId(), skipRule);
			}
			return skipRule;
		}
		else {
			throw new InvalidRuleTypeDtoException();
		}
	}

	public boolean isBasic() {
    	return ruleTypeDTO.equals(RuleTypeDTO.BASIC);
	}

	public class InvalidRuleTypeDtoException extends Exception {
		@Override
		public String getMessage() {
			return "The input RuleTypeDTO is invalid or empty";
		}
	}
}
