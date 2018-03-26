package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.respository.station.StationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import com.mgmtp.radio.domain.station.Song;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.station.StationMapper;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.respository.station.SongRepository;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.respository.user.UserRepository;
import com.mgmtp.radio.service.station.StationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationServiceImpl implements StationService {

    private final StationMapper stationMapper;
    private final StationRepository stationRepository;

    public StationServiceImpl(StationRepository stationRepository, StationMapper stationMapper) {
        this.stationMapper = stationMapper;
        this.stationRepository = stationRepository;
    }

    @Override
    public Mono<Station> findStationByIdAndDeletedFalse(String stationId) {
        return stationRepository.findByIdAndDeletedFalse(stationId);
    }

    public Flux<StationDTO> getStations() {
        return stationRepository.findAll()
                .map(station -> stationMapper.stationToStationDTO(station));
    }

    public Mono<StationDTO> getStation(String id) {
        return stationRepository.findById(id)
                .map(station -> stationMapper.stationToStationDTO(station));
    }

    public Mono<StationDTO> createStation(String userId, StationDTO stationDTO){
        stationDTO.setOwnerId(userId);
        Station station = stationMapper.stationDtoToStation(stationDTO);
        return stationRepository.save(station).map( item -> stationMapper.stationToStationDTO(item) );
    }

    public Mono<StationDTO>  updateStation(String stationId, StationDTO stationDTO){
        return stationRepository.findById(stationId)
                .flatMap(station -> {
                    station.setName(stationDTO.getName());
                    return stationRepository.save(station);
                })
                .map(station -> stationMapper.stationToStationDTO(station));
    }
}
