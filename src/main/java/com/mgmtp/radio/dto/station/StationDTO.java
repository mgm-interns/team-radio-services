package com.mgmtp.radio.dto.station;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mgmtp.radio.dto.skipRule.SkipRuleDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.sdo.DaySubtracted;
import com.mgmtp.radio.sdo.SkipRuleType;
import com.mgmtp.radio.sdo.StationPrivacy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
public class StationDTO {

	String id;

	@NonNull
	String name;

	String friendlyId;

	StationPrivacy privacy = StationPrivacy.station_public;

	String ownerId;

	List<SongDTO> playlist;

	LocalDate createdAt;

	StationConfigurationDTO stationConfiguration;

	Map<String, UserDTO> onlineUsers;

	String picture;

	public StationDTO() {
		stationConfiguration = new StationConfigurationDTO();
		stationConfiguration.setStationFriendlyId(friendlyId);
		stationConfiguration.setSkipRule(new SkipRuleDTO(SkipRuleType.BASIC));
		onlineUsers = new HashMap<>();
	}

	public int getNumberOnline() {
		return getOnlineUsers().size();
	}

	public boolean hasUser() {
		return getOnlineUsers().size() > 0;
	}

	public boolean isNewStation() {
		return getCreatedAt().isAfter(LocalDate.now().minusDays(DaySubtracted.DAYS_SUBTRACTED.getDAYS_LIMITED()));
	}
}
