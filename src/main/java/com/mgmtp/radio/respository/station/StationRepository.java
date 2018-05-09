package com.mgmtp.radio.respository.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.sdo.StationPrivacy;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StationRepository extends ReactiveMongoRepository<Station, String> {
    @Query("{ $or: [ { '_id': ?#{[0]} }, { 'friendlyId': ?#{[0]} } ] }")
    Mono<Station> retrieveByIdOrFriendlyId(String friendlyId);
    Flux<Station> findByOwnerId(String Id);
	Mono<Station> findFirstByName(String stationId);
	Flux<Station> findByOwnerIdAndPrivacy(String ownerId, StationPrivacy privacy);
    Flux<Station> findByIdIn(List<String> listStationId);
}
