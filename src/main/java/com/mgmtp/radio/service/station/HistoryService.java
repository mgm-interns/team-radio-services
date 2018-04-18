package com.mgmtp.radio.service.station;

import com.mgmtp.radio.dto.station.HistoryDTO;
import com.mgmtp.radio.dto.station.SongDTO;
import reactor.core.publisher.Flux;

public interface HistoryService {
    Flux<HistoryDTO> getHistoryByStationId(String stationId);
}
