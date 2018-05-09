package com.mgmtp.radio.support;

import com.mgmtp.radio.domain.station.NowPlaying;
import com.mgmtp.radio.domain.station.PlayList;
import com.mgmtp.radio.dto.station.SongDTO;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StationPlayerHelper {
    public static final int TIME_BUFFER = 5;
    private ConcurrentHashMap<String, Tuple2<NowPlaying, NowPlaying>> stationPlayer = new ConcurrentHashMap<>();

    public Optional<NowPlaying> addNowPlaying(String stationId, SongDTO song, long joinTime) {
        NowPlaying nowPlaying = new NowPlaying();
        nowPlaying.setSongId(song.getId());
        nowPlaying.setTitle(song.getTitle());
        nowPlaying.setDuration(song.getDuration());
        nowPlaying.setStartingTime(joinTime == 0 ? LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() : joinTime);
        nowPlaying.setThumbnail(song.getThumbnail());
        nowPlaying.setUrl(song.getUrl());
        nowPlaying.setMessage(song.getMessage());
        nowPlaying.setEnded(false);

        Optional<Tuple2<NowPlaying, NowPlaying>> currentNowPlaying = Optional.ofNullable(stationPlayer.get(stationId));
        if (currentNowPlaying.isPresent()){
            NowPlaying previousPlay = currentNowPlaying.get().getT1().isEnded() ? currentNowPlaying.get().getT1() : new NowPlaying();
            stationPlayer.put(stationId, Tuples.of(nowPlaying, previousPlay));
        } else {
            stationPlayer.put(stationId, Tuples.of(nowPlaying, new NowPlaying()));
        }

        return Optional.of(stationPlayer.get(stationId).getT1());
    }

    public Optional<NowPlaying> getStationNowPlaying(String stationId) {
        Optional<Tuple2<NowPlaying, NowPlaying>> currentPlayer = Optional.ofNullable(stationPlayer.get(stationId));
        if (currentPlayer.isPresent()) {
            long currentTimestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
            long durationInSecond = currentPlayer.get().getT1().getDuration() / 1000;
            long songEndTime = currentPlayer.get().getT1().getStartingTime() + durationInSecond + TIME_BUFFER;
            if (currentTimestamp > songEndTime) {
                currentPlayer.get().getT1().setEnded(true);
            }

            return Optional.of(currentPlayer.get().getT1());
        }
        return Optional.empty();
    }

    public void clearNowPlayingByStationId(String stationId) {
        stationPlayer.remove(stationId);
    }

    public Optional<NowPlaying> getPreviousPlay(String stationId) {
        Optional<Tuple2<NowPlaying, NowPlaying>> currentPlayer = Optional.ofNullable(stationPlayer.get(stationId));
        if (currentPlayer.isPresent()) {
            return Optional.of(currentPlayer.get().getT2());
        }
        return Optional.empty();
    }
}
