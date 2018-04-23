package com.mgmtp.radio.dto.station;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mgmtp.radio.dto.skipRule.SkipRuleDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.sdo.Days_subtracted;
import com.mgmtp.radio.sdo.SkipRuleType;
import com.mgmtp.radio.sdo.StationPrivacy;
import lombok.Data;
import lombok.NonNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StationDTO implements  Comparable<StationDTO> {

	String id;

	@NonNull
	String name;

	String friendlyId;

	StationPrivacy privacy = StationPrivacy.station_public;

	String ownerId;

	List<SongDTO> playlist;

	LocalDate createdAt;

	StationConfigurationDTO stationConfiguration;

	Map<String, UserDTO> userList;


	public StationDTO() {
        stationConfiguration = new StationConfigurationDTO();
        stationConfiguration.setSkipRule(new SkipRuleDTO(SkipRuleType.BASIC));
		userList = new HashMap<>();
	}

	public int getNumberOnline() {
		return userList.size();
	}

	private boolean hasUser() {
		return userList.size() > 0;
	}

	@Override
	public int compareTo(StationDTO otherStationDto) {
		int LARGER = -1;
		int SMALLER = 1;
		int EQUAL = 0;

		if(this.hasUser() && !otherStationDto.hasUser())
			return LARGER;
		if(!this.hasUser() && otherStationDto.hasUser() )
			return SMALLER;
		if(this.isNewStation() && !otherStationDto.isNewStation())
			return LARGER;
		if(!this.isNewStation() && otherStationDto.isNewStation())
			return SMALLER;
		if(this.getNumberOnline() > otherStationDto.getNumberOnline())
			return LARGER;
		if(this.getNumberOnline() < otherStationDto.getNumberOnline())
			return SMALLER;
		return EQUAL;
	}

	public boolean isNewStation() {
		return getCreatedAt().isAfter(LocalDate.now().minusDays(Days_subtracted.DAYS_SUBTRACTED.getDays_limited()));
	}
}
