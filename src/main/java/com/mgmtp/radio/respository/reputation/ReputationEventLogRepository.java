package com.mgmtp.radio.respository.reputation;

import com.mgmtp.radio.domain.reputation.ReputationEventLog;
import com.mgmtp.radio.sdo.ReputationEventKeys;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReputationEventLogRepository extends ReactiveMongoRepository<ReputationEventLog, String> {
    Mono<ReputationEventLog> findFirstByUserIdAndEvent(String userId, ReputationEventKeys event);
    Flux<ReputationEventLog> findByUserId(String userId);
}
