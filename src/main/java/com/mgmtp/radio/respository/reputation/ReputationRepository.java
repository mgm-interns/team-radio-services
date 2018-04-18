package com.mgmtp.radio.respository.reputation;

import com.mgmtp.radio.domain.reputation.Reputation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ReputationRepository extends ReactiveMongoRepository<Reputation, String> {
    Mono<Reputation> findByUserId(String userId);
}
