package com.mgmtp.radio.respository.conversation;

import com.mgmtp.radio.domain.conversation.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

import java.util.List;

public interface MessageRepository extends ReactiveMongoRepository<Message, String> {

    Flux<Message> findByIdIn(List<String> listId);

    @Tailable
    Flux<Message> findByConversationId(String conversationId);
}
