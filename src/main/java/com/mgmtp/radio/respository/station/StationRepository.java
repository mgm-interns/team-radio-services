package com.mgmtp.radio.respository.station;

import com.mgmtp.radio.domain.station.Station;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface StationRepository extends ReactiveMongoRepository<Station, String> {
    Mono<Station> findByFriendlyId(String friendlyId);
}
