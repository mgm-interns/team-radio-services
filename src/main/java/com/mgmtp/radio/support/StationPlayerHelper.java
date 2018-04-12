package com.mgmtp.radio.support;

import com.mgmtp.radio.domain.station.NowPlaying;
import com.mgmtp.radio.dto.station.SongDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StationPlayerHelper {
    public static final int TIME_BUFFER = 5;
    private ConcurrentHashMap<String, NowPlaying> stationPlayer = new ConcurrentHashMap<>();

    public Optional<NowPlaying> addNowPlaying(String stationId, SongDTO song) {
        NowPlaying nowPlaying = new NowPlaying();
        nowPlaying.setSongId(song.getId());
        nowPlaying.setDuration(song.getDuration());
        nowPlaying.setStartingTime(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        nowPlaying.setThumbnail(song.getThumbnail());
        nowPlaying.setUrl(song.getUrl());
        nowPlaying.setMessages(song.getMessage());
        nowPlaying.setEnded(false);

        stationPlayer.put(stationId, nowPlaying);

        return Optional.of(nowPlaying);
    }

    public Optional<NowPlaying> getStationNowPlaying(String stationId) {
        Optional<NowPlaying> currentPlayer = Optional.ofNullable(stationPlayer.get(stationId));
        if (currentPlayer.isPresent()) {
            long currentTimestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
            long durationInSecond = currentPlayer.get().getDuration() / 1000;
            long songEndTime = currentPlayer.get().getStartingTime() + durationInSecond + TIME_BUFFER;
            if (currentTimestamp > songEndTime) {
                currentPlayer.get().setEnded(true);
            }
        }
        return currentPlayer;
    }

    public void clearNowPlayingByStationId(String stationId) {
        stationPlayer.remove(stationId);
    }
}
