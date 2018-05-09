package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import reactor.core.publisher.Mono;
import com.mgmtp.radio.dto.station.StationDTO;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.List;
import java.util.Map;

public interface StationService {
    Flux<StationDTO> getAll();
    Map<String,StationDTO> getOrderedStations();
    Mono<StationDTO> findById(String id);
    Mono<StationDTO> create(String userId, StationDTO stationDTO);
    Mono<StationDTO> update(String id, StationDTO stationDTO);
    int getOnlineUsersNumber(StationDTO stationDTO);
    Mono<Station> findStationByIdAndDeletedFalse(String stationId);
    Mono<StationConfigurationDTO> updateConfiguration(String id, StationConfigurationDTO stationConfigurationDTO);
    boolean existsByName(String stationName);
    Flux<StationDTO> getListStationByListStationId(List<String> listStationId);
    void joinStation(String stationId, UserDTO userDto);
    StationDTO removeUserFromStationOnlineList(String stationId, UserDTO userDTO);
    void leaveStation (String stationId, UserDTO userDTO);
    Mono<Station> retrieveByIdOrFriendlyId(String friendlyId);

}
