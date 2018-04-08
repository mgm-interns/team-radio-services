package com.mgmtp.radio.dto.station;

import com.mgmtp.radio.dto.station.SkipRuleDTO.InvalidRuleTypeDtoException;
import com.mgmtp.radio.sdo.StationPrivacy;
import com.mgmtp.radio.service.station.StationServiceImpl;
import lombok.Data;
import lombok.NoArgsConstructor;

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

	public StationDTO () throws InvalidRuleTypeDtoException {
		stationConfigurationDTO = new StationConfigurationDTO();
		stationConfigurationDTO.setSkipRule(new SkipRuleDTO(SkipRuleDTO.BASIC));
	}
}
