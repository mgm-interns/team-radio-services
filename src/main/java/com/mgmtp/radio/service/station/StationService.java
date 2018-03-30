package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import reactor.core.publisher.Mono;
import com.mgmtp.radio.dto.station.StationDTO;
import reactor.core.publisher.Flux;

import com.mgmtp.radio.dto.station.StationDTO;

public interface StationService {
	int getOnlineUsersNumber(StationDTO stationDTO);
	void skipCurrentSong(StationDTO stationDTO);
	Flux<StationDTO> getAll();
	Mono<StationDTO> findById(String id);
	Mono<StationDTO> create(String userId, StationDTO stationDTO);
	Mono<StationDTO> update(String id, StationDTO stationDTO);
    Mono<Station> findStationByIdAndDeletedFalse(String stationId);
}
