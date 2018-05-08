package com.mgmtp.radio.event;

import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.respository.station.SongRepository;
import com.mgmtp.radio.sdo.EventDataKeys;
import com.mgmtp.radio.sdo.SongStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Log4j2
@MessageEndpoint
@Component
public class UpdateShiftSongChannelEndpoint  extends BaseEventMessageEndpoint{
    private final SongRepository songRepository;

    public UpdateShiftSongChannelEndpoint(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    boolean canHandleMessage(Map<String, Object> messageData) {
        if (messageData != null && messageData.get(EventDataKeys.event_id.name()) != null && messageData.get(EventDataKeys.list_shift_song.name()) != null){
            return true;
        }
        log.error("Invalid event message data received {}", messageData);
        return false;
    }

    @ServiceActivator(inputChannel = "shiftSongChannel")
    @Override
    public void receive(Map<String, Object> message) {
        super.receive(message);
    }

    @Override
    protected void process(Map<String, Object> message) {
        List<SongDTO> listShiftSong = (List<SongDTO>) message.get(EventDataKeys.list_shift_song.name());

        listShiftSong.forEach(currentSong ->{
            songRepository.findById(currentSong.getId())
                .flatMap(song -> {
                    song.setStatus(SongStatus.played);
                    return songRepository.save(song);
                }).subscribe();
        });
    }
}
