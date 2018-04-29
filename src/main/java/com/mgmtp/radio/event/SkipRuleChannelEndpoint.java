package com.mgmtp.radio.event;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.station.StationConfiguration;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.respository.station.SongRepository;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.sdo.EventDataKeys;
import com.mgmtp.radio.sdo.SkipRuleType;
import lombok.extern.log4j.Log4j2;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Log4j2
@MessageEndpoint
@Component
public class SkipRuleChannelEndpoint extends BaseEventMessageEndpoint {
    private static final double DOWN_VOTE_THRES_PERCENT = 0.5;
    private final StationRepository stationRepository;
    private final SongRepository songRepository;

    public SkipRuleChannelEndpoint(StationRepository stationRepository, SongRepository songRepository) {
        this.stationRepository = stationRepository;
        this.songRepository = songRepository;
    }

    @Override
    boolean canHandleMessage(Map<String, Object> messageData) {
        if (messageData != null
                && messageData.get(EventDataKeys.songDto.name()) != null
                && messageData.get(EventDataKeys.event_id.name()) != null) {
            return true;
        }

        log.error("Invalid event message data received {}", messageData);
        return false;
    }

    @ServiceActivator(inputChannel = "skipRuleChannel")
    @Override
    public void receive(Map<String, Object> message) {
        super.receive(message);
    }

    @Override
    protected void process(Map<String, Object> message) {
        SongDTO songDTO = (SongDTO) message.get(EventDataKeys.songDto.name());

        stationRepository.retriveByIdOrFriendlyId(songDTO.getStationId()).map(tempStation ->{
            final StationConfiguration stationConfiguration = tempStation.getStationConfiguration();
            boolean isSkipped = false;

            if (stationConfiguration.getSkipRule().getSkipRuleType() == SkipRuleType.ADVANCE) {
                if (isOwnerDownvote(tempStation, songDTO)) {
                    isSkipped = true;
                }
            } else {
                double downvotePercent = calcCurrentSongDislikePercent(songDTO, new StationDTO());
                if (downvotePercent > DOWN_VOTE_THRES_PERCENT) {
                    isSkipped = true;
                }
            }
            songDTO.setSkipped(isSkipped);
            songRepository.findById(songDTO.getId())
                    .map(song -> {
                        song.setSkipped(songDTO.isSkipped());
                        songRepository.save(song).subscribe();
                        return song;
                    }).subscribe();
            return tempStation;
        }).subscribe();
    }

    private double calcCurrentSongDislikePercent(SongDTO songDTO, StationDTO station) {
        final int numberOnline = getOnlineUsersNumber(station);
        double currentSongDislikePercent = 0;
        if (numberOnline > 0) {
            currentSongDislikePercent = songDTO.getDownVoteCount() / (double) numberOnline;
        }
        return currentSongDislikePercent;
    }

    private boolean isOwnerDownvote(Station station, SongDTO songDTO) {
        return songDTO.getDownvoteUserList().stream().anyMatch(userDTO -> userDTO.getId().equals(station.getOwnerId()));
    }

    private int getOnlineUsersNumber(StationDTO stationDTO) {
        //TODO Get number of online users id here
        return 1;
    }
}
