package com.mgmtp.radio.dto.station;

import com.mgmtp.radio.dto.skipRule.SkipRuleDTO;
import com.mgmtp.radio.sdo.StationPrivacy;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class StationDTO {
	String id;
	String name;
	StationPrivacy privacy;
	String ownerId;
	int startingTime;
	boolean deleted;
	List<SongDTO> playlist;
	LocalDate createdAt;
	StationConfigurationDTO stationConfigurationDTO;

	public StationDTO() {
		stationConfigurationDTO = new StationConfigurationDTO();
		stationConfigurationDTO.setSkipRule(new SkipRuleDTO(SkipRuleDTO.BASIC));
	}
}
