package com.mgmtp.radio.respository.conversation;

import com.mgmtp.radio.domain.conversation.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface MessageRepository extends ReactiveMongoRepository<Message, String> {

    @Tailable
    Flux<Message> findByStationId(String conversationId);

    Flux<Message> findByFrom_Id(String userId);
}
