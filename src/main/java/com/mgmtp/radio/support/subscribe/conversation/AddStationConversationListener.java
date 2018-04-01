package com.mgmtp.radio.support.subscribe.conversation;

import com.mgmtp.radio.dto.conversation.ConversationDTO;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.service.conversation.ConversationService;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.BaseSubscriber;

@Component
@Log4j2
public class AddStationConversationListener<T> extends BaseSubscriber {

    @Autowired
    private ConversationService conversationService;

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        log.info("Add station conversation listener");
        request(1);
    }

    @Override
    protected void hookOnNext(Object value) {
        StationDTO stationDTO = (StationDTO) value;
        ConversationDTO conversationDTO = new ConversationDTO();
        conversationDTO.setUid(stationDTO.getId());
        conversationService.create(conversationDTO).block();
    }

    @Override
    protected void hookOnError(Throwable throwable) {
        log.info(throwable);
    }
}
