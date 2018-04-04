package com.mgmtp.radio.dto.station;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mgmtp.radio.sdo.StationPrivacy;
import com.mgmtp.radio.service.station.StationServiceImpl;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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
	int numberOfUpvote;
	int numberOfDownvote;

	StationServiceImpl stationService;

	public StationDTO() {
		numberOfUpvote = 0;
		numberOfDownvote = 0;
	}
}
