package com.mgmtp.radio.service.conversation;

import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.conversation.MessageDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageService {

    Mono<MessageDTO> create(String stationId, User user, MessageDTO messageDTO);

    Flux<MessageDTO> findByStationId(String conversationId);

    Mono<Boolean> existsById(String id);
}
