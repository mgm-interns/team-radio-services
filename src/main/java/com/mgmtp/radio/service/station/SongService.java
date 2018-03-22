package com.mgmtp.radio.service.station;

import com.mgmtp.radio.dto.station.SongDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;


public interface SongService {
    Flux<SongDTO> getListSongByStationId(String stationId);

    Flux<SongDTO> getAllSongById(List<String> idList);

    Mono<SongDTO> addSongToStationPlaylist(String stationId, SongDTO song);

    Mono<SongDTO> upVoteSongInStationPlaylist(String stationId, String songId, String userId);

    Mono<SongDTO> downVoteSongInStationPlaylist(String stationId, String songId, String userId);
}
