package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.SkipRule;
import com.mgmtp.radio.dto.station.SkipRuleDTO;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.mapper.stationConfiguration.SkipRuleMapper;
import com.mgmtp.radio.mapper.stationConfiguration.StationConfigurationMapper;
import com.mgmtp.radio.respository.station.StationConfigurationRepository;
import com.mgmtp.radio.respository.station.StationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.mgmtp.radio.mapper.station.StationMapper;
import reactor.core.publisher.Flux;
import java.time.LocalDate;
import java.util.List;

@Service("stationService")
public class StationServiceImpl implements StationService {

	private final StationMapper stationMapper;
	private final SkipRuleMapper skipRuleMapper;
	private static final double ONE_HUNDRED_PERCENT = 1;
	private static final double DOWN_VOTE_THRES_PERCENT = 0.5;

	@Override
	public int getOnlineUsersNumber(StationDTO stationDTO) {
		//TODO Get number of online users id here
		return 0;
	}

	@Override
	public void skipCurrentSong(StationDTO stationDTO) {
		skipSong(stationDTO, 0);
	}

	private void skipSong(StationDTO stationDTO, int songIndex) {
		stationDTO.getPlaylist().get(songIndex).setSkipped(true);
	}

	private double calcCurrentSongDislikePercent(StationDTO stationDTO, String userId) {
		if (!stationDTO.getStationConfigurationDTO().getSkipRuleDTO().isBasic()
			&& stationDTO.getOwnerId().equals(userId)) {
			return ONE_HUNDRED_PERCENT;
		} else {
			final int numberOnline = getOnlineUsersNumber(stationDTO);
			double currentSongDislikePercent = 0;
			if(numberOnline > 0) {
				currentSongDislikePercent = (stationDTO.getNumberOfUpvote() - stationDTO.getNumberOfUpvote())
					/ (float) numberOnline;
			}
			return currentSongDislikePercent;
		}
	}

	//TODO Do it after updvotes/downvotes happen
	public void checkAndSkipSongIfNeeded(StationDTO stationDTO, String userId) {
		if(calcCurrentSongDislikePercent(stationDTO, userId) > DOWN_VOTE_THRES_PERCENT){
			skipCurrentSong(stationDTO);
		}
	}
    private final StationRepository stationRepository;
    private final SongService songService;
    private final StationConfigurationRepository stationConfigurationRepository;

    public StationServiceImpl(StationMapper stationMapper, SkipRuleMapper configurationMapper,
                              StationRepository stationRepository, SongService songService,
                              StationConfigurationRepository stationConfigurationRepository) {
        this.stationMapper = stationMapper;
        this.skipRuleMapper = configurationMapper;
        this.stationRepository = stationRepository;
        this.songService = songService;
        this.stationConfigurationRepository = stationConfigurationRepository;
    }

    @Override
    public Mono<Station> findStationByIdAndDeletedFalse(String stationId) {
        return stationRepository.findByIdAndDeletedFalse(stationId);
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
    public Mono<StationDTO> create(String userId, StationDTO stationDTO){
        stationDTO.setOwnerId(userId);
        stationDTO.setCreatedAt(LocalDate.now());
        Station station = stationMapper.stationDTOToStation(stationDTO);
        return stationRepository.save(station).map( stationMapper::stationToStationDTO);
    }

    @Override
    public Mono<StationDTO> update(String stationId, StationDTO stationDTO){
        return stationRepository.findById(stationId)
                .flatMap(station -> {
                    station.setName(stationDTO.getName());
                    return stationRepository.save(station);
                })
                .map(stationMapper::stationToStationDTO);
    }

	@Override
	public Mono<StationConfigurationDTO> updateConfiguration(String stationId, StationConfigurationDTO stationConfigurationDTO) {
		return stationConfigurationRepository.findById(stationId).flatMap(station -> {
			final SkipRule skipRule = stationConfigurationMapper.stationConfigurationDtoToStationConfiguration(stationConfigurationDTO);
			station.setSkipRule(
				);
			return stationConfigurationRepository.save(station);
		}).map(stationConfigurationMapper::stationConfigurationToStationConfigurationDto);
	}
}
