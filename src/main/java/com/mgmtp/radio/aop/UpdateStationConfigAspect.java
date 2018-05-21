package com.mgmtp.radio.aop;

import com.mgmtp.radio.domain.station.NowPlaying;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.service.station.SongService;
import com.mgmtp.radio.support.StationPlayerHelper;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Aspect
@Component
public class UpdateStationConfigAspect {
	private final SongService songService;
	private final StationPlayerHelper stationPlayerHelper;

	public UpdateStationConfigAspect(SongService songService, StationPlayerHelper stationPlayerHelper) {
		this.songService = songService;
		this.stationPlayerHelper = stationPlayerHelper;
	}

	@AfterReturning(value = "execution(* com.mgmtp.radio.service.station.StationServiceImpl.updateConfiguration(..))", returning = "monoStationConfigDto")
	void callHandleSkipRule(Mono<StationConfigurationDTO> monoStationConfigDto) {
		monoStationConfigDto.subscribe(stationConfigDto -> {
			final String stationFriendlyId = stationConfigDto.getStationFriendlyId();
			if(stationPlayerHelper.getStationNowPlaying(stationFriendlyId).isPresent()) {
				final NowPlaying nowPlaying = stationPlayerHelper.getStationNowPlaying(stationFriendlyId).get();
				Mono<SongDTO> currentSongDto = songService.getById(nowPlaying.getSongId());
				currentSongDto.flatMap(songDTO -> {
				    songDTO.setStationFriendlyId(stationFriendlyId);
                    return songService.handleSkipRule(songDTO);
                }).subscribe();
			}
		});

	}
}
