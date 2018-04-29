package com.mgmtp.radio.aop;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.station.StationConfiguration;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.respository.station.SongRepository;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.sdo.SkipRuleType;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Aspect
@Component
public class SongAspect {
    private static final double DOWN_VOTE_THRES_PERCENT = 0.5;

    private final StationRepository stationRepository;
    private final SongRepository songRepository;

    public SongAspect(StationRepository stationRepository, SongRepository songRepository) {
        this.stationRepository = stationRepository;
        this.songRepository = songRepository;
    }

    @AfterReturning(value = "execution(* com.mgmtp.radio.controller.v1.SongController.downVoteSong(..))", returning = "monoSongDTO")
    public void checkAndSkipSongIfNeeded(Mono<SongDTO> monoSongDTO) {
        monoSongDTO.map(songDTO -> {
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
                        .flatMap(song -> {
                            song.setSkipped(songDTO.isSkipped());
                            return songRepository.save(song);
                        }).subscribe();
                return tempStation;
            }).subscribe();
            return songDTO;
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
