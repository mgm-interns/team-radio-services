package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.SkipRule;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.station.StationConfiguration;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.mapper.station.StationMapper;
import com.mgmtp.radio.mapper.stationConfiguration.StationConfigurationMapper;
import com.mgmtp.radio.respository.station.StationConfigurationRepository;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.respository.station.StationRepository;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service("stationService")
@Aspect
public class StationServiceImpl implements StationService {

	private final StationMapper stationMapper;
	private final StationConfigurationMapper stationConfigurationMapper;
	private static final double ONE_HUNDRED_PERCENT = 1;
	private static final double DOWN_VOTE_THRES_PERCENT = 0.5;

	@Override
	public int getOnlineUsersNumber(StationDTO stationDTO) {
		//TODO Get number of online users id here
		return 0;
	}

	private void skipSong(StationDTO stationDTO, int songIndex) {
		stationDTO.getPlaylist().get(songIndex).setSkipped(true);
	}

	private double calcCurrentSongDislikePercent(SongDTO songDTO, StationDTO station) {
		final int numberOnline = getOnlineUsersNumber(station);
		double currentSongDislikePercent = 0;
		if (numberOnline > 0) {
			currentSongDislikePercent = songDTO.getDownVoteCount() / (float) numberOnline;
		}
		return currentSongDislikePercent;
	}

	private boolean isOwnerDownvote(Station station, SongDTO songDTO) {
		for (UserDTO user: songDTO.getDownvoteUserList()) {
			if (station.getOwnerId().equals(user.getId())) {
				return true;
			}
		}
		return false;
	}

	@AfterReturning(value = "execution(* com.mgmtp.radio.service.station.SongService.downVoteSongInStationPlaylist(..))", returning = "monoSongDTO")
	public Mono<SongDTO> checkAndSkipSongIfNeeded(Mono<SongDTO> monoSongDTO) {
		Mono<SongDTO> ms = monoSongDTO.map(songDTO -> {
			final Station station = stationRepository.findById(songDTO.getStationId()).block();
			final StationConfiguration stationConfiguration = stationConfigurationRepository.findById(songDTO.getStationId()).block();
			boolean isSkipped = false;

			if (stationConfiguration.getRule().getTypeId() == SkipRule.ADVANCE) {
				if (isOwnerDownvote(station, songDTO)) {
					isSkipped = true;
				}
			} else {
				double downvotePercent = 0;
				downvotePercent = calcCurrentSongDislikePercent(songDTO, new StationDTO());
				if (downvotePercent > DOWN_VOTE_THRES_PERCENT) {
					isSkipped = true;
				}
			}
			songDTO.setSkipped(isSkipped);
			return songDTO;
		});
		return ms;
	}
    private final StationRepository stationRepository;
    private final SongService songService;

    public StationServiceImpl(StationMapper stationMapper,
                              StationRepository stationRepository,
                              SongService songService) {
        this.stationMapper = stationMapper;
        this.stationConfigurationMapper= stationConfigurationMapper;
        this.stationRepository = stationRepository;
        this.songService = songService;
    }

    @Override
    public Mono<Station> findStationByIdAndDeletedFalse(String stationId) {
        return stationRepository.findById(stationId);
    }

    public Flux<StationDTO> getAll() {
        return stationRepository.findAll()
                .map(stationMapper::stationToStationDTO);
    }

    @Override
    public Mono<StationDTO> findById(String id) {
        Mono<Station> stationMono = stationRepository.findById(id);
        Station station = stationMono.block();
        StationDTO stationDTO = stationMono.map(stationMapper::stationToStationDTO).block();
        List<String> playlistIdList = station.getPlaylist();
        if (playlistIdList == null || playlistIdList.isEmpty()){
            return Mono.just(stationDTO);
        }
        return songService.getAllSongById(playlistIdList).collectList().map(songs -> {
            stationDTO.setPlaylist(songs);
            return stationDTO;
        });
    }

    @Override
    public Mono<StationDTO> create(String userId, StationDTO stationDTO) {
        stationDTO.setOwnerId(userId);
        stationDTO.setCreatedAt(LocalDate.now());
        String friendlyId = createFriendlyIdFromStationName(stationDTO.getName());
        stationDTO.setFriendlyId(friendlyId);
        Station station = stationMapper.stationDTOToStation(stationDTO);
        return stationRepository.save(station).map( stationMapper::stationToStationDTO);
    }

    private String createFriendlyIdFromStationName(String stationName) {
        String friendlyId = stationName.replaceAll("\\s+", "-");
        Optional<Station> station = stationRepository.findByFriendlyId(friendlyId).blockOptional();
        if(station.isPresent()) {
            Long now = LocalDate.now().toEpochDay();
            friendlyId += "-" + now.toString();
        }
        return friendlyId;
    }

    @Override
    public Mono<StationDTO> update(String stationId, StationDTO stationDTO){
        return stationRepository.findById(stationId)
                .switchIfEmpty(Mono.error(new RadioNotFoundException("Station id is not found.")))
                .flatMap(station -> {
                    station.setName(stationDTO.getName());
                    return stationRepository.save(station);
                })
                .map(stationMapper::stationToStationDTO);
    }

	@Override
	public Mono<StationConfigurationDTO> updateConfiguration(String stationId, StationConfigurationDTO stationConfigurationDTO) {
		return stationConfigurationRepository.findById(stationId).flatMap(stationConfiguration -> {
			stationConfiguration.setSkipRule(skipRuleMapper.skipRuleDTOToSkipRule(stationConfigurationDTO.getSkipRuleDTO()));
			return stationConfigurationRepository.save(stationConfiguration);
		}).map(stationConfigurationMapper::stationConfigurationToStationConfigurationDto);
	}
}
