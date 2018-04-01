package com.mgmtp.radio.respository.conversation;

import com.mgmtp.radio.domain.conversation.Conversation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ConversationRepository extends ReactiveMongoRepository<Conversation, String> {
    Mono<Conversation> findByUid(String uid);
}
