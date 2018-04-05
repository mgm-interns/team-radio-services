package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.PlayList;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.exception.StationNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;


import java.io.IOException;

public interface SongService {
    Flux<SongDTO> getListSongByStationId(String stationId);

    Flux<SongDTO> getAllSongById(List<String> idList);

    Mono<PlayList> getPlayListByStationId(String stationId);

    Mono<SongDTO> upVoteSongInStationPlaylist(String stationId, String songId, String userId);

    Mono<SongDTO> downVoteSongInStationPlaylist(String stationId, String songId, String userId);

    Mono<SongDTO> addSongToStationPlaylist(String stationId, String videoId, String message, String creatorId) ;
}
