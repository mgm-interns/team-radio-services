package com.mgmtp.radio.event;

import com.mgmtp.radio.domain.station.History;
import com.mgmtp.radio.respository.station.HistoryRepository;
import com.mgmtp.radio.respository.station.SongRepository;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.sdo.EventDataKeys;
import lombok.extern.log4j.Log4j2;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@MessageEndpoint
@Component
public class HistoryChannelEndpoint extends BaseEventMessageEndpoint {
    private final SongRepository songRepository;
    private final StationRepository stationRepository;
    private final HistoryRepository historyRepository;
    private static ConcurrentHashMap<String, String> distinctStationAndSong = new ConcurrentHashMap<>();

    public HistoryChannelEndpoint(SongRepository songRepository, StationRepository stationRepository, HistoryRepository historyRepository) {
        this.songRepository = songRepository;
        this.stationRepository = stationRepository;
        this.historyRepository = historyRepository;
    }

    @ServiceActivator(inputChannel = "historyChannel")
    @Override
    public void receive(Map<String, Object> message) {
        super.receive(message);
    }

    @Override
    boolean canHandleMessage(Map<String, Object> messageData) {
        if (messageData != null &&
                messageData.get(EventDataKeys.event_id.name()) != null &&
                messageData.get(EventDataKeys.stationId.name()) != null &&
                messageData.get(EventDataKeys.songId.name()) != null) {
            if (distinctStationAndSong.get(messageData.get(EventDataKeys.stationId.name())) == null ||
                    !distinctStationAndSong.get(messageData.get(EventDataKeys.stationId.name())).equals(messageData.get(EventDataKeys.songId.name()))){
                return true;
            }
        }
        log.error("Invalid event message data received {}", messageData);
        return false;
    }

    @Override
    protected void process(Map<String, Object> message) {
        String stationId = message.get(EventDataKeys.stationId.name()).toString();
        String songId = message.get(EventDataKeys.songId.name()).toString();
        distinctStationAndSong.put(stationId, songId);
        System.out.println("Station ID: " + stationId + " Song ID: " + songId);

        stationRepository.findById(stationId)
                .map(station -> {
                    List<String> listSongId = station.getPlaylist();
                    listSongId.remove(songId);
                    station.setPlaylist(listSongId);

                    stationRepository.save(station).subscribe();
                    return station;
                }).map(station -> {
                    songRepository.findById(songId).flatMap(song -> {
                        History history = new History();
                        history.setCreatedAt(LocalDate.now());
                        history.setSongId(song.getSongId());
                        history.setStationId(stationId);
                        history.setUrl(song.getUrl());
                        history.setTitle(song.getTitle());
                        history.setThumbnail(song.getThumbnail());
                        history.setDuration(song.getDuration());
                        history.setCreatorId(song.getCreatorId());

                        return historyRepository.save(history);
                    }).subscribe();
                    return station;
                }
        ).subscribe();
    }
}
