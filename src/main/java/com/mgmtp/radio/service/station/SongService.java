package com.mgmtp.radio.service.station;

import com.mgmtp.radio.dto.station.SongDTO;
import reactor.core.publisher.Flux;
import java.util.List;


public interface SongService {
    Flux<SongDTO> getListSongByStationId(String stationId);
    Flux<SongDTO> getAllSongById(List<String> idList);
}
