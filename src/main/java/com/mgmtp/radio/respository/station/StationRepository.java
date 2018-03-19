package com.mgmtp.radio.respository.station;

import com.mgmtp.radio.domain.station.Station;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface StationRepository extends ReactiveMongoRepository<Station, String> {

}
