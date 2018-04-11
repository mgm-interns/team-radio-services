package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.ActiveStation;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.support.ActiveStationStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.mapper.station.StationMapper;
import reactor.core.publisher.Flux;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StationServiceImpl implements StationService {

    private final StationMapper stationMapper;
    private final StationRepository stationRepository;
    private final SongService songService;
    private final ActiveStationStore activeStationStore;

    public StationServiceImpl(StationMapper stationMapper,
                              StationRepository stationRepository,
                              SongService songService, ActiveStationStore activeStationStore) {
        this.stationMapper = stationMapper;
        this.stationRepository = stationRepository;
        this.songService = songService;
        this.activeStationStore = activeStationStore;
    }

    @Override
    public Mono<Station> findStationByIdAndDeletedFalse(String stationId) {
        return stationRepository.findById(stationId);
    }

    public Flux<StationDTO> getAll() {
        return stationRepository.findAll()
                .map(stationMapper::stationToStationDTO);
    }

    @Override
    public Mono<StationDTO> findById(String id) {
        Mono<Station> stationMono = stationRepository.findById(id);
        Station station = stationMono.block();
        StationDTO stationDTO = stationMono.map(stationMapper::stationToStationDTO).block();
        List<String> playlistIdList = station.getPlaylist();
        if (playlistIdList == null || playlistIdList.isEmpty()){
            return Mono.just(stationDTO);
        }
        return songService.getAllSongById(playlistIdList).collectList().map(songs -> {
            stationDTO.setPlaylist(songs);
            return stationDTO;
        });
    }

    @Override
    public Mono<StationDTO> create(String userId, StationDTO stationDTO) {
        stationDTO.setOwnerId(userId);
        stationDTO.setCreatedAt(LocalDate.now());
        String friendlyId = createFriendlyIdFromStationName(stationDTO.getName());
        stationDTO.setFriendlyId(friendlyId);
        Station station = stationMapper.stationDTOToStation(stationDTO);
        return stationRepository.save(station).map( stationMapper::stationToStationDTO);
    }

    private String createFriendlyIdFromStationName(String stationName) {
        String friendlyId = stationName.replaceAll("\\s+", "-");
        Optional<Station> station = stationRepository.findByFriendlyId(friendlyId).blockOptional();
        if(station.isPresent()) {
            Long now = LocalDate.now().toEpochDay();
            friendlyId += "-" + now.toString();
        }
        return friendlyId;
    }

    @Override
    public Mono<StationDTO> update(String stationId, StationDTO stationDTO){
        return stationRepository.findById(stationId)
                .switchIfEmpty(Mono.error(new RadioNotFoundException("Station id is not found.")))
                .flatMap(station -> {
                    station.setName(stationDTO.getName());
                    return stationRepository.save(station);
                })
                .map(stationMapper::stationToStationDTO);
    }

    @Override
    public Mono<ActiveStation> findByStationId(UserDTO userDTO, String id) {

        return findById(id).map(stationDTO -> {
            activeStationStore.getActiveStations().putIfAbsent(id, new ActiveStation());
            ActiveStation activeStation = activeStationStore.getActiveStations().get(id);

            if (!activeStation.getUsers().contains(userDTO)) {
                activeStation.getUsers().add(userDTO);
            }
            activeStation.setStationDTO(stationDTO);
            activeStationStore.getActiveStations().put(stationDTO.getId(), activeStation);

            return activeStation;
        }).switchIfEmpty(Mono.error(new RadioNotFoundException()));
    }
}
