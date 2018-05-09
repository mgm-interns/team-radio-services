package com.mgmtp.radio.respository.user;

import com.mgmtp.radio.domain.user.RecentStation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RecentStationRepository extends ReactiveMongoRepository<RecentStation, String> {
    Flux<RecentStation> findByUserIdOrderByJoinedTimeDesc(String userId);
    Mono<RecentStation> findByUserIdAndStationId(String userId, String stationId);
}