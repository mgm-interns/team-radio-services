package com.mgmtp.radio.domain.station;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.HashMap;

@Data
@ToString
public class SkipRule {
	public static final int BASIC = 0;
	public static final int ADVANCE = 1;

	int typeId;

	public SkipRule() {
		this.typeId = BASIC;
	}

	public SkipRule(int typeId) {
		if(typeId != BASIC && typeId != ADVANCE) {
			typeId = BASIC;
		}
		this.typeId = typeId;
	}
}
