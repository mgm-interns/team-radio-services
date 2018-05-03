package com.mgmtp.radio.support;

import com.mgmtp.radio.domain.station.Song;
import com.mgmtp.radio.dto.station.SongDTO;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class StationSongSkipHelper {
    private Map<String, Set<SongDTO>> stationSongSkip = new ConcurrentHashMap<>();

    public void addSkipSong(String stationId, SongDTO songDTO){
        Optional<Set<SongDTO>> stationListSong = Optional.ofNullable(stationSongSkip.get(stationId));
        if (stationListSong.isPresent()){
            stationListSong.get().add(songDTO);
        } else {
            Set<SongDTO> initListSong = new HashSet<>();
            initListSong.add(songDTO);
            stationSongSkip.put(stationId, initListSong);
        }
    }

    public void removeSkipSong(String stationId, SongDTO songDto) {
        Optional<Set<SongDTO>> stationListSong = Optional.ofNullable(stationSongSkip.get(stationId));
        if (stationListSong.isPresent()){
            Set<SongDTO> currentList = stationListSong.get();
            Optional<SongDTO> removeSong = currentList.stream()
                    .filter(currentSong -> currentSong.getId().equals(songDto.getId()))
                    .findFirst();
            if (removeSong.isPresent()){
                stationSongSkip.get(stationId).remove(removeSong.get());
            }
        }
    }

    public Optional<Set<SongDTO>> getListSkipSong(String stationId){
        return Optional.ofNullable(stationSongSkip.get(stationId));
    }
}
