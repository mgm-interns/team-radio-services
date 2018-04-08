package com.mgmtp.radio.aop;

import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.sdo.StationEventDataKeys;
import com.mgmtp.radio.sdo.SubscriptionEvents;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class StationAspect {

	MessageChannel registerChannel;

	public StationAspect(MessageChannel registerChannel) {
		this.registerChannel = registerChannel;
	}

	@AfterReturning(value = "execution(* com.mgmtp.radio.service.station.StationServiceImpl.checkAndSkipSongIfNeeded(..))", returning = "monoSongDTO")
	public void updatePlaylist(Mono<SongDTO> monoSongDTO) {
		monoSongDTO.map(m -> {
			if(m.isSkipped()) {
				Map<String, Object> updatePram = new HashMap<>();
				updatePram.put(StationEventDataKeys.station_id.name(), m.getStationId());
				updatePram.put(StationEventDataKeys.event_id.name(), SubscriptionEvents.updatePlaylist.name());

				registerChannel.send(new GenericMessage<>(updatePram));
			}
			return m;
		});

	}
}
