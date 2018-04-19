package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import org.aspectj.lang.annotation.Aspect;
import reactor.core.publisher.Mono;
import com.mgmtp.radio.dto.station.StationDTO;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.List;

public interface StationService {
    Flux<StationDTO> getAll();
    Map<String, StationDTO> getAllStationWithArrangement();
    Mono<StationDTO> findById(String id);
    Mono<StationDTO> create(String userId, StationDTO stationDTO);
    Mono<StationDTO> update(String id, StationDTO stationDTO);
    int getOnlineUsersNumber(StationDTO stationDTO);
    Mono<Station> findStationByIdAndDeletedFalse(String stationId);
    Mono<StationConfigurationDTO> updateConfiguration(String id, StationConfigurationDTO stationConfigurationDTO);
    boolean existsByName(String stationName);
    Flux<StationDTO> getListStationByListStationId(List<String> listStationId);
    Mono<StationDTO> joinStation(String stationId, UserDTO userDto);
    StationDTO removeUserFromStationOnlineList(String stationId, UserDTO userDTO);
    Mono<StationDTO> removeUserFromStationOnlineList(String stationId, UserDTO userDTO);
    Mono<Station> retriveByIdOrFriendlyId(String friendlyId);
}
