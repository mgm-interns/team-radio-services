package com.mgmtp.radio.respository.station;

import com.mgmtp.radio.domain.station.StationConfiguration;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;


public interface StationConfigurationRepository extends ReactiveMongoRepository<StationConfiguration, String> {
//	Mono<StationConfiguration> findByIdAndDeletedFalse(String id);
}
