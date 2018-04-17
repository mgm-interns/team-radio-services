package com.mgmtp.radio.respository.station;

import com.mgmtp.radio.domain.station.History;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface HistoryRepository extends ReactiveMongoRepository<History, String> {
    Flux<History> findByStationId(String stationId);
}
