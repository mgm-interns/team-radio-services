package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import reactor.core.publisher.Mono;

public interface StationService {
    Mono<Station> findStationByIdAndDeletedFalse(String stationId);
}
