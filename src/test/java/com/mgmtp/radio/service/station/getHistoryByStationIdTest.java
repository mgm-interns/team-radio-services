package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.mapper.station.StationMapper;
import com.mgmtp.radio.respository.station.SongRepository;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.support.ActiveStationStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class getHistoryByStationIdTest {
    @Mock
    StationRepository stationRepository;

    StationMapper stationMapper = StationMapper.INSTANCE;

    StationService stationService;

    SongService songService;

    SongRepository songRepository;

    private ActiveStationStore activeStationStore;

    @Before
    public void setUp() throws Exception {
        activeStationStore = new ActiveStationStore();
        MockitoAnnotations.initMocks(this);
        songService = new SongServiceImpl(null,null,songRepository,stationRepository,null,null,null,null,null,null);
        String id = "AAA";
        Station station = new Station();

        when(stationRepository.findById(anyString())).thenReturn(Mono.just(station));
    }

    @Test
    public void getHistoryByStationId() {
        String stationId = "station_id_01";

        Flux<SongDTO> original = null;

        original = songService.getHistoryByStationId(stationId);

        assertNotNull(original);
    }
}