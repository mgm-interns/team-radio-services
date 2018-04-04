package com.mgmtp.radio.respository.station;

import com.mgmtp.radio.domain.station.StationConfiguration;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import javax.security.auth.login.Configuration;

public interface StationConfigurationRepository extends ReactiveMongoRepository<StationConfiguration, String> {
	Mono<Configuration> findByIdAndDeletedFalse(String stationId);
}
