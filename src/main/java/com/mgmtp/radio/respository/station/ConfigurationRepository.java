package com.mgmtp.radio.respository.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.station.StationConfiguration;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ConfigurationRepository extends ReactiveMongoRepository<StationConfiguration, String> {

}
