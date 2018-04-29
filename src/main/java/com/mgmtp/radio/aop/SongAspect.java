package com.mgmtp.radio.aop;

import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.sdo.EventDataKeys;
import com.mgmtp.radio.sdo.SubscriptionEvents;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class SongAspect {
    private MessageChannel skipRuleChannel;

    public SongAspect(MessageChannel skipRuleChannel) {
        this.skipRuleChannel = skipRuleChannel;
    }

    @AfterReturning(value = "execution(* com.mgmtp.radio.service.station.SongServiceImpl.downVoteSongInStationPlaylist(..))", returning = "monoSongDTO")
    public void checkAndSkipSongIfNeeded(Mono<SongDTO> monoSongDTO) {
        monoSongDTO.subscribe(songDTO -> {
            Map<String, Object> param = new HashMap<>();
            param.put(EventDataKeys.event_id.name(), SubscriptionEvents.skip_rule.name());
            param.put(EventDataKeys.songDto.name(), songDTO);

            skipRuleChannel.send(MessageBuilder.withPayload(param).build());
        });
    }
}
