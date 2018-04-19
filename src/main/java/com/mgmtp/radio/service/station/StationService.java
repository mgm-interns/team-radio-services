package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.dto.station.StationDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StationService {
    Flux<StationDTO> getAll();
    Mono<StationDTO> findById(String id);
    Mono<StationDTO> create(String userId, StationDTO stationDTO);
    Mono<StationDTO> update(String id, StationDTO stationDTO);
    int getOnlineUsersNumber(StationDTO stationDTO);
    Mono<Station> findStationByIdAndDeletedFalse(String stationId);
    Mono<StationConfigurationDTO> updateConfiguration(String id, StationConfigurationDTO stationConfigurationDTO);
}
