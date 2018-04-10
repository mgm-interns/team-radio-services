package com.mgmtp.radio.support;

import com.mgmtp.radio.domain.station.ActiveStation;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Data
public class ActiveStationStore {
    private ConcurrentHashMap<String, ActiveStation> activeStations =  new ConcurrentHashMap<>();
}
