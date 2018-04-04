package com.mgmtp.radio.dto.station;

import com.mgmtp.radio.sdo.StationPrivacy;
import com.mgmtp.radio.service.station.StationServiceImpl;
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
	int numberOfUpvote;
	int numberOfDownvote;

	StationServiceImpl stationService;

	public StationDTO() {
		numberOfUpvote = 0;
		numberOfDownvote = 0;
	}
}
