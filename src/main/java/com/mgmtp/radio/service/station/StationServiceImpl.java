package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.respository.station.StationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import com.mgmtp.radio.domain.station.Song;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.mapper.station.SongMapper;
import com.mgmtp.radio.mapper.station.StationMapper;
import com.mgmtp.radio.respository.station.SongRepository;
import com.mgmtp.radio.respository.station.StationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StationServiceImpl implements StationService {

    private final StationMapper stationMapper;
    private final StationRepository stationRepository;
    private final SongRepository songRepository;
    private final SongMapper songMapper;

    public StationServiceImpl(SongRepository songRepository, StationRepository stationRepository, StationMapper stationMapper, SongMapper songMapper) {
        this.stationMapper = stationMapper;
        this.stationRepository = stationRepository;
        this.songRepository = songRepository;
        this.songMapper = songMapper;
    }

    @Override
    public Mono<Station> findStationByIdAndDeletedFalse(String stationId) {
        return stationRepository.findByIdAndDeletedFalse(stationId);
    }

    public Flux<StationDTO> getStations() {
        return stationRepository.findAll()
                .map(station -> {
                    StationDTO result = stationMapper.stationToStationDTO(station);
                    result.setPlaylist(songRepository.findAll().map(song -> songMapper.songToSongDTO(song)).collectList().block());

                    return result;
                });
    }

    public Mono<StationDTO> getStation(String id) {
        return stationRepository.findById(id)
                .map(station -> {
                    StationDTO result = stationMapper.stationToStationDTO(station);
                    songRepository.findAll().collectList().block();
//                    result.setPlaylist(songRepository.findAll().map(song -> songMapper.songToSongDTO(song)).collectList().block());
                    return result;
                });
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
