package com.mgmtp.radio.service.station;

import com.mgmtp.radio.dto.station.SongDTO;
import reactor.core.publisher.Flux;

public interface SongService {
    Flux<SongDTO> getListSongIn(String stationId);
}
