package com.mgmtp.radio.support;

import com.mgmtp.radio.domain.station.NowPlaying;
import com.mgmtp.radio.dto.station.SongDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StationPlayerHelper {
    private final int TIME_BUFFER = 5000;
    private ConcurrentHashMap<String, NowPlaying> stationPlayer = new ConcurrentHashMap<>();

    public NowPlaying addNowPlaying(String stationId, SongDTO song) {
        NowPlaying nowPlaying = new NowPlaying();
        nowPlaying.setSongId(song.getSongId());
        nowPlaying.setDuration(song.getDuration());
        nowPlaying.setStartingTime(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        nowPlaying.setThumbnail(song.getThumbnail());
        nowPlaying.setUrl(song.getUrl());
        nowPlaying.setEnded(false);

        stationPlayer.put(stationId, nowPlaying);

        return nowPlaying;
    }

    public NowPlaying getStationNowPlaying(String stationId) {
        NowPlaying currentPlayer = stationPlayer.get(stationId);
        if (currentPlayer != null) {
            long currentTimestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
            long songEndTime = currentPlayer.getStartingTime() + currentPlayer.getDuration() + TIME_BUFFER;
            if (currentTimestamp > songEndTime) {
                currentPlayer.setEnded(true);
            }
        }
        return currentPlayer;
    }
}
