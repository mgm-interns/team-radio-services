package com.mgmtp.radio.aop;

import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.service.station.SongService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Aspect
@Component
public class UpdateStationConfigAspect {
	private final SongService songService;

	public UpdateStationConfigAspect(SongService songService) {
		this.songService = songService;
	}

	@AfterReturning(value = "execution(* com.mgmtp.radio.service.station.StationServiceImpl.updateConfiguration(..))", returning = "monoStationConfigDto")
	void callHandleSkipRule(Mono<StationConfigurationDTO> monoStationConfigDto) {
		monoStationConfigDto.subscribe(stationConfigDto -> {
			final String stationFriendlyId = stationConfigDto.getStationFriendlyId();
            Flux<SongDTO> stationCurrentListSong = songService.getListSongByStationId(stationFriendlyId);
            stationCurrentListSong.flatMap(songDTO -> {
                songDTO.setStationFriendlyId(stationFriendlyId);
                return songService.handleSkipRule(songDTO);
            }).subscribe();
		});
	}
}
