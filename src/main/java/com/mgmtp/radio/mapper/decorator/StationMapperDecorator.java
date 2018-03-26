package com.mgmtp.radio.mapper.decorator;

import com.mgmtp.radio.domain.station.Song;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.mapper.station.SongMapper;
import com.mgmtp.radio.mapper.station.StationMapper;
import com.mgmtp.radio.service.station.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.publisher.Flux;

import java.util.List;

public abstract class StationMapperDecorator implements StationMapper {

    @Autowired
    @Qualifier("delegate")
    private StationMapper delegate;

    @Autowired
    private SongService songService;

//    @Override
//    public StationDTO stationToStationDTO(Station station){
//        StationDTO stationDTO = delegate.stationToStationDTO(station);
//
//        Flux<SongDTO> playlist = songService.getAllSongById(station.getPlaylist());
//
//        stationDTO.setPlaylist(playlist.collectList().block());
//        return stationDTO;
//    };
}
