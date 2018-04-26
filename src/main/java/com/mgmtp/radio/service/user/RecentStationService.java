package com.mgmtp.radio.service.user;

import com.mgmtp.radio.dto.station.StationDTO;
import reactor.core.publisher.Flux;

public interface RecentStationService {
    Flux<StationDTO> getRecentStation(String userId);

    void createRecentStation();

    void updateRecentStation();
}
