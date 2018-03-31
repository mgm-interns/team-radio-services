package com.mgmtp.radio.service.conversation;

import com.mgmtp.radio.dto.conversation.ConversationDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConversationService {

    Mono<ConversationDTO> create(ConversationDTO conversationDTO);

    Flux<ConversationDTO> findByUid(String uid);

}
