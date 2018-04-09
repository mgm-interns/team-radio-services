package com.mgmtp.radio.dto.station;

import com.mgmtp.radio.dto.skipRule.SkipRuleDTO;
import com.mgmtp.radio.sdo.StationPrivacy;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StationDTO {

	String id;

	@NonNull
	String name;

	String friendlyId;

	StationPrivacy privacy = StationPrivacy.station_public;

	String ownerId;

	List<SongDTO> playlist;

	LocalDate createdAt;
	StationConfigurationDTO stationConfigurationDTO;

	public StationDTO() {
		stationConfigurationDTO = new StationConfigurationDTO();
		stationConfigurationDTO.setSkipRule(new SkipRuleDTO(SkipRuleDTO.BASIC));
	}
}
