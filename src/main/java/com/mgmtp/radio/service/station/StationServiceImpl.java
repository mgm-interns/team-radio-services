package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.respository.station.StationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class StationServiceImpl implements StationService {
    private final StationRepository stationRepository;

    public StationServiceImpl(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Override
    public Mono<Station> findStationByIdAndDeletedFalse(String stationId) {
        return stationRepository.findByIdAndDeletedFalse(stationId);
    }
}
