package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.respository.station.StationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import com.mgmtp.radio.domain.station.Song;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.mapper.station.StationMapper;
import com.mgmtp.radio.respository.station.StationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
public class StationServiceImpl implements StationService {

    private final StationMapper stationMapper;
    private final StationRepository stationRepository;
    private final SongService songService;

    public StationServiceImpl(StationMapper stationMapper, StationRepository stationRepository, SongService songService) {
        this.stationMapper = stationMapper;
        this.stationRepository = stationRepository;
        this.songService = songService;
    }

    @Override
    public Mono<Station> findStationByIdAndDeletedFalse(String stationId) {
        return stationRepository.findByIdAndDeletedFalse(stationId);
    }

    public Flux<StationDTO> getAll() {
        return stationRepository.findAll()
                .map(station -> stationMapper.stationToStationDTO(station));
    }

    @Override
    public Mono<StationDTO> findById(String id) {
        Mono<Station> stationMono = stationRepository.findById(id);
        Station station = stationMono.block();
        StationDTO stationDTO = stationMono.map(stationMapper::stationToStationDTO).block();
        List<String> playlistIdList = station.getPlaylist();
        if (playlistIdList == null || playlistIdList.isEmpty()) return Mono.just(stationDTO);
        return songService.getAllSongById(playlistIdList).collectList().map(songs -> {
            stationDTO.setPlaylist(songs);
            return stationDTO;
        });
    }

    @Override
    public Mono<StationDTO> create(String userId, StationDTO stationDTO){
        stationDTO.setOwnerId(userId);
        stationDTO.setCreatedAt(LocalDate.now());
        Station station = stationMapper.stationDtoToStation(stationDTO);
        return stationRepository.save(station).map( item -> stationMapper.stationToStationDTO(item) );
    }

    @Override
    public Mono<StationDTO>  update(String stationId, StationDTO stationDTO){
        return stationRepository.findById(stationId)
                .flatMap(station -> {
                    station.setName(stationDTO.getName());
                    return stationRepository.save(station);
                })
                .map(station -> stationMapper.stationToStationDTO(station));
    }
}
