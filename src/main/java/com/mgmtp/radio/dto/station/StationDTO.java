package com.mgmtp.radio.dto.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.respository.station.SongRepository;
import com.mgmtp.radio.sdo.StationPrivacy;
import com.mgmtp.radio.service.station.StationServiceImpl;
import com.mgmtp.radio.service.station.SongService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.ArrayList;
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
	ConfigurationDTO configurationDTO;
	int numberOfUpvote;
	int numberOfDownvote;

	StationServiceImpl stationService;

	public StationDTO() {
		numberOfUpvote = 0;
		numberOfDownvote = 0;
	}
}
