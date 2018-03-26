package com.mgmtp.radio.dto.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.respository.station.SongRepository;
import com.mgmtp.radio.sdo.StationPrivacy;
import com.mgmtp.radio.service.station.SongService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class StationDTO {
	String id;
	String name;
	StationPrivacy privacy;
	String ownerId;
	int startingTime;
	boolean deleted;
	Flux<SongDTO> playlist;
	LocalDate createdAt;
}
