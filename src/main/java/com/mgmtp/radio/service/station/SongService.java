package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.PlayList;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.station.SongDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SongService {
    Flux<SongDTO> getListSongByStationId(String stationId);

    Flux<SongDTO> getAllSongById(List<String> idList);

    Mono<PlayList> getPlayListByStationId(String stationId, long joinTime);

    Mono<SongDTO> upVoteSongInStationPlaylist(String stationId, String songId, String userId);

    Mono<SongDTO> downVoteSongInStationPlaylist(String stationId, String songId, String userId);

    Mono<SongDTO> addSongToStationPlaylist(String stationId, String videoId, String message, User creator) ;

    Mono<Boolean> existsById(String id);

    Mono<SongDTO> getById(String id);

    Flux<SongDTO> getListSongByListSongId(List<String> listSongId);

	Mono<SongDTO> updateSongSkippedStatusToDb(Mono<SongDTO> songDTOMono);

    Flux<SongDTO> getListSongByListSongIdId(List<String> listSongId);

    boolean existsBySongId(String songId);

    Mono<SongDTO> handleSkipRule (SongDTO songDTO);
}
