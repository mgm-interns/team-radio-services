package com.mgmtp.radio.service.conversation;

import com.mgmtp.radio.dto.conversation.ConversationDTO;
import reactor.core.publisher.Mono;

public interface ConversationService {

    Mono<ConversationDTO> create(ConversationDTO conversationDTO);

    Mono<ConversationDTO> findByStationId(String uid);

    Mono<ConversationDTO> findById(String id);

    Mono<Boolean> existsById(String id);

    Mono<Boolean> existsByUid(String uid);

}
