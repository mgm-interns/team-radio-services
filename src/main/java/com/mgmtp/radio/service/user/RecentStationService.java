package com.mgmtp.radio.service.user;

import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.RecentStationDTO;
import com.mgmtp.radio.sdo.StationPrivacy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RecentStationService {
    Flux<StationDTO> getRecentStation(String userId);

    Mono<RecentStationDTO> createRecentStation(String userId, String stationId);

    boolean existsByUserIdAndStationId(String userId, String songId);

    Flux<StationDTO> getRecentStationsByUserIdAndPrivacy(String userId, StationPrivacy privacy);
}
