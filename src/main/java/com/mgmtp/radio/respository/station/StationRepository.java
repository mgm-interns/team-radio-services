package com.mgmtp.radio.respository.station;

import com.mgmtp.radio.domain.station.Station;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StationRepository extends MongoRepository<Station, String> {

}
