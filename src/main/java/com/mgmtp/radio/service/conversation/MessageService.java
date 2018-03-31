package com.mgmtp.radio.service.conversation;

import com.mgmtp.radio.dto.conversation.MessageDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MessageService {
    Flux<MessageDTO> findByListId(List<String> listId);

    Mono<MessageDTO> create(MessageDTO messageDTO);
}
