package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.station.StationConfiguration;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.exception.StationNotFoundException;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.mapper.station.StationMapper;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.respository.station.StationRepository;
import org.aspectj.lang.annotation.Aspect;
import com.mgmtp.radio.sdo.SkipRuleType;
import lombok.Data;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.*;

@Service
public class StationServiceImpl implements StationService {

	private final StationRepository stationRepository;
	private final StationMapper stationMapper;
	private final StationOnlineServiceImpl stationOnlineService;


    @Override
	public int getOnlineUsersNumber(StationDTO stationDTO) {
		//TODO Get number of online users id here
		return 0;
	}

    public StationServiceImpl(StationMapper stationMapper, StationRepository stationRepository, StationOnlineServiceImpl stationOnlineService) {
        this.stationMapper = stationMapper;
        this.stationRepository = stationRepository;
        this.stationOnlineService = stationOnlineService;
    }

    @Override
    public Mono<Station> findStationByIdAndDeletedFalse(String stationId) {
        return stationRepository.retriveByIdOrFriendlyId(stationId);
    }



    public Map<String, StationDTO> getAllStationWithArrangement(){
	    Map<String, StationDTO> result = stationOnlineService.getAllStation();
	    if (result.isEmpty()) {
            getAll().map(stationDTO -> {
                stationOnlineService.addStationToList(stationDTO);
                return stationDTO;
            }).subscribe();
        }
        return stationOnlineService.getAllStation();
	}

	public Flux<StationDTO> getAll() {
        return stationRepository.findAll()
                .map(stationMapper::stationToStationDTO);
    }

    @Override
    public Mono<StationDTO> findById(String id) {
        return stationRepository.retriveByIdOrFriendlyId(id).map(stationMapper::stationToStationDTO);
    }

    @Override
    public Mono<StationDTO> create(String userId, StationDTO stationDTO) {
        stationDTO.setOwnerId(userId);
        stationDTO.setCreatedAt(LocalDate.now());
        String friendlyId = createFriendlyIdFromStationName(stationDTO.getName());
        stationDTO.setFriendlyId(friendlyId);
        Station station = stationMapper.stationDTOToStation(stationDTO);

        station.setStationConfiguration(stationMapper.stationConfigurationDtoToStationConfiguration(stationDTO.getStationConfiguration()));
	    station.getStationConfiguration().setSkipRule(stationMapper.skipRuleDtoToSkipRule(stationDTO.getStationConfiguration().getSkipRule()));
        return stationRepository.save(station).map(stationMapper::stationToStationDTO).doOnSuccess(stationSaveSuccess -> stationOnlineService.addStationToList(stationSaveSuccess));;
    }

    private String createFriendlyIdFromStationName(String stationName) {
	    String friendlyId = Normalizer.normalize(stationName, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	    friendlyId = friendlyId.replaceAll("đ", "d").replaceAll("Đ", "D");
        friendlyId = friendlyId.replaceAll("\\s+", "-");
        Optional<Station> station = stationRepository.retriveByIdOrFriendlyId(friendlyId).blockOptional();
        if(station.isPresent()) {
            Long now = LocalDate.now().toEpochDay();
            friendlyId += "-" + now.toString();
        }
        return friendlyId;
    }

    @Override
    public Mono<StationDTO> update(String stationId, StationDTO stationDTO){
        return stationRepository.retriveByIdOrFriendlyId(stationId)
                .switchIfEmpty(Mono.error(new RadioNotFoundException("Station id is not found.")))
                .flatMap(station -> {
                    station.setName(stationDTO.getName());
                    return stationRepository.save(station);
                })
                .map(stationMapper::stationToStationDTO);
    }

    @Override
	public Mono<StationConfigurationDTO> updateConfiguration(String id, StationConfigurationDTO stationConfigurationDTO) {
		return stationRepository.retriveByIdOrFriendlyId(id)
			.map(station -> {
				final StationConfiguration stationConfiguration =
					stationMapper.stationConfigurationDtoToStationConfiguration(stationConfigurationDTO);
					station.setStationConfiguration(stationConfiguration);
				station.setStationConfiguration(stationMapper.stationConfigurationDtoToStationConfiguration(stationConfigurationDTO));
					stationRepository.save(station).subscribe();
					return stationConfiguration;
			})
			.map(stationMapper::stationConfigurationToStationConfigurationDto);
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
    public Mono<Station> retriveByIdOrFriendlyId(String friendlyId) {
        int[] count  = {0};
        return stationRepository.retriveByIdOrFriendlyId(friendlyId)
                .delayElement(Duration.ofMillis(100))
                .doOnNext(station -> count[0]++)
                .filter(station -> count[0] == 1)
                .switchIfEmpty(Mono.error(new StationNotFoundException(friendlyId)));
    }
    @Override
    public Mono<StationDTO> joinStation(String stationId, UserDTO userDto) {
        final Mono<StationDTO> monoStationDto = findById(stationId);
        addUserToStationOnlineList(monoStationDto,userDto);
        return monoStationDto;
        final Mono<StationDTO> monoStationDto = findById(stationId);
        return monoStationDto
                .doOnNext(stationDTO -> addUserToStationOnlineList(stationDTO,userDto));
    }

    public void addUserToStationOnlineList(StationDTO stationDTO, UserDTO userDto) {
        //TODO Call this method after user join station
        stationOnlineService.addOnlineUser(userDto,stationDTO.getId());
    }


    public UserDTO leaveStation (String stationId, String userId){
        //TODO Call this method after user left station
        stationOnlineService.addOnlineUser(userDto,stationDTO.getId());
    }

    public StationDTO removeUserFromStationOnlineList(String stationId, UserDTO userDTO) {
        stationOnlineService.removeOnlineUser(userDTO,stationId);
        return stationOnlineService.getStationById(stationId);
    }
}
