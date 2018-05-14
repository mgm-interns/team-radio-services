package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.mapper.station.StationMapper;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.sdo.StationPrivacy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class GetAllStationImplTest {

    @Mock
    StationRepository stationRepository;

    StationMapper stationMapper = StationMapper.INSTANCE;

    StationService stationService;

    StationOnlineService stationOnlineService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        stationService = new StationServiceImpl(stationMapper, stationRepository, stationOnlineService);
    }

    @Test
    public void createSuccess() {
        StationDTO stationDTO1 = new StationDTO();
        stationDTO1.setId("001");
        stationDTO1.setName("001");
        stationDTO1.setPrivacy(StationPrivacy.station_public);
        stationDTO1.setOwnerId("user1");

        Station station1 = new Station();
        station1.setId(stationDTO1.getId());
        station1.setName(stationDTO1.getName());
        station1.setPrivacy(stationDTO1.getPrivacy());
        station1.setOwnerId(stationDTO1.getOwnerId());

        StationDTO stationDTO2 = new StationDTO();
        stationDTO2.setId("002");
        stationDTO2.setName("002");
        stationDTO2.setPrivacy(StationPrivacy.station_public);
        stationDTO2.setOwnerId("user2");

        List<StationDTO> stationDTOList = new ArrayList<>();
        stationDTOList.add(stationDTO1);
        stationDTOList.add(stationDTO2);

        Station station2 = new Station();
        station2.setId(stationDTO2.getId());
        station2.setName(stationDTO2.getName());
        station2.setPrivacy(stationDTO2.getPrivacy());
        station2.setOwnerId(stationDTO2.getOwnerId());

        when(stationRepository.findAll()).thenReturn(Flux.just(station1, station2));
        // when
        Flux<StationDTO> result = stationService.getAll();
        List<StationDTO> expected = result.log().collectList().block();

        // then
        assertEquals(stationDTOList, expected);
    }
}