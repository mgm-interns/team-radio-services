package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.station.StationConfiguration;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.exception.StationNotFoundException;
import com.mgmtp.radio.mapper.station.StationMapper;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.sdo.StationPrivacy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StationServiceImpl implements StationService {

	private final StationRepository stationRepository;
	private final StationMapper stationMapper;
	private final StationOnlineService stationOnlineService;

    public StationServiceImpl(StationMapper stationMapper, StationRepository stationRepository, StationOnlineService stationOnlineService) {
        this.stationMapper = stationMapper;
        this.stationRepository = stationRepository;
        this.stationOnlineService = stationOnlineService;
    }

    public Map<String, StationDTO> getOrderedStations() {
        Map<String, StationDTO> result = stationOnlineService.getAllStation();
        if (result.isEmpty()) {
            getAll().subscribe(stationDTO -> stationOnlineService.addStationToList(stationDTO));
        }
        return stationOnlineService.getAllStation();
    }

	public Flux<StationDTO> getAll() {
        return stationRepository.findAll()
                .map(stationMapper::stationToStationDTO);
    }

    @Override
    public Mono<StationDTO> findById(String id) {
        return retrieveByIdOrFriendlyId(id).map(stationMapper::stationToStationDTO);
    }

    @Override
    public Mono<StationDTO> create(String userId, StationDTO stationDTO) {
        stationDTO.setName(stationDTO.getName().trim());
        stationDTO.setOwnerId(userId);
        stationDTO.setPrivacy(StationPrivacy.station_public);
        stationDTO.setCreatedAt(LocalDate.now());
        String friendlyId = createFriendlyIdFromStationName(stationDTO.getName());
        stationDTO.setFriendlyId(friendlyId);
        Station station = stationMapper.stationDTOToStation(stationDTO);

        station.setStationConfiguration(stationMapper.stationConfigurationDtoToStationConfiguration(stationDTO.getStationConfiguration()));
	    station.getStationConfiguration().setSkipRule(stationMapper.skipRuleDtoToSkipRule(stationDTO.getStationConfiguration().getSkipRule()));

        return stationRepository
                .save(station)
                .map(stationMapper::stationToStationDTO)
                .doOnSuccess(stationSaveSuccess -> stationOnlineService.addStationToList(stationSaveSuccess));
    }

    private String createFriendlyIdFromStationName(String stationName) {
	    String friendlyId = Normalizer.normalize(stationName, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	    friendlyId = friendlyId.replaceAll("đ", "d").replaceAll("Đ", "D");
        friendlyId = friendlyId.replaceAll("\\s+", "-");
        Optional<Station> station = stationRepository.retrieveByIdOrFriendlyId(friendlyId).blockOptional();
        if(station.isPresent()) {
            Long now = LocalDate.now().toEpochDay();
            friendlyId += "-" + now.toString();
        }
        return friendlyId;
    }

    @Override
    public Mono<StationDTO> update(String stationId, StationDTO stationDTO){
        return stationRepository.retrieveByIdOrFriendlyId(stationId)
                .switchIfEmpty(Mono.error(new RadioNotFoundException("Station id is not found.")))
                .flatMap(station -> {
                    station.setName(stationDTO.getName().trim());
                    stationOnlineService.addStationToList(stationDTO);
                    return stationRepository.save(station);
                })
                .map(stationMapper::stationToStationDTO);
    }

	@Override
	public Mono<StationConfigurationDTO> updateConfiguration(String stationId, StationConfigurationDTO stationConfigurationDTO) {
        StationDTO currentStation = stationOnlineService.getStationById(stationId);
        currentStation.setStationConfiguration(stationConfigurationDTO);
        stationRepository.retrieveByIdOrFriendlyId(stationId).flatMap(station -> {
            station.setStationConfiguration(stationMapper.stationConfigurationDtoToStationConfiguration(stationConfigurationDTO));
            return stationRepository.save(station);
        }).subscribe();
        stationOnlineService.addStationToList(currentStation);
        stationConfigurationDTO.setStationFriendlyId(currentStation.getFriendlyId());
        return Mono.just(stationConfigurationDTO);
	}

	@Override
	public boolean existsByName(String name) {
		return stationRepository.findFirstByName(name)
			.blockOptional().isPresent();
	}

    @Override
    public Flux<StationDTO> getListStationByListStationId(List<String> listStationId) {
        return stationRepository.findByIdIn(listStationId).map(stationMapper::stationToStationDTO);
    }

    @Override
    public Flux<StationDTO> getListStationByListStationIdAndPrivacy(List<String> listStationId, StationPrivacy privacy) {
        return stationRepository.findByIdInAndPrivacy(listStationId, privacy).map(stationMapper::stationToStationDTO);
    }

    @Override
    public Mono<Station> retrieveByIdOrFriendlyId(String friendlyId) {
        int[] count  = {0};
        return stationRepository.retrieveByIdOrFriendlyId(friendlyId)
                .delayElement(Duration.ofMillis(100))
                .doOnNext(station -> count[0]++)
                .filter(station -> count[0] == 1)
                .switchIfEmpty(Mono.error(new StationNotFoundException(friendlyId)));
    }
    @Override
    public void joinStation(String stationId, UserDTO userDto) {
        addUserToStationOnlineList(stationId, userDto);
    }

    private void addUserToStationOnlineList(String stationId, UserDTO userDto) {
        stationOnlineService.addOnlineUser(userDto, stationId);
    }

    @Override
    public Map<String, Object> getAllStationInfo() {
        return stationOnlineService.getStationInfo();
    }

    @Override
    public void clearJoinUserInfo(String stationId) {
        stationOnlineService.clearJoinUserInfo(stationId);
    }

    @Override
    public void clearLeaveUserInfo(String stationId) {
        stationOnlineService.clearLeaveUserInfo(stationId);
    }

    @Override
    public StationDTO getStationById(String stationId) {
        return stationOnlineService.getStationById(stationId);
    }
}
