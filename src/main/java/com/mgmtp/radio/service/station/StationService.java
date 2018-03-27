package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import reactor.core.publisher.Mono;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StationService {
    Flux<StationDTO> getAll();
    Mono<StationDTO> getOne(String id) throws RadioNotFoundException;;
    Mono<StationDTO> create(String userId, StationDTO stationDTO);
    Mono<StationDTO> update(String id, StationDTO stationDTO);
    Mono<Station> findStationByIdAndDeletedFalse(String stationId);
}
