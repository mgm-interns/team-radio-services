package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.mapper.station.StationMapper;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.sdo.StationPrivacy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class CreateStationImplTest {

    @Mock
    StationRepository stationRepository;

    StationMapper stationMapper = StationMapper.INSTANCE;

    StationService stationService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        stationService = new StationServiceImpl(stationMapper,  stationRepository);
    }

    @Test
    public void createSuccess() {
        //given
        StationDTO stationDTO = new StationDTO();
        stationDTO.setId("station1");
        stationDTO.setName("ST0");
        stationDTO.setPrivacy(StationPrivacy.station_private);
        String userId = "user1";

        Station savedStation = new Station();
        savedStation.setId(stationDTO.getId());
        savedStation.setName(stationDTO.getName());
        savedStation.setPrivacy(stationDTO.getPrivacy());
        savedStation.setOwnerId(userId);

        Mockito.when(stationRepository.save(any(Station.class))).thenReturn(Mono.just(savedStation));
        Mockito.when(stationRepository.retriveByIdOrFriendlyId(any(String.class))).thenReturn(Mono.empty());

        // when
        Mono<StationDTO> result = stationService.create(userId, stationDTO);
        StationDTO expected = result.log().block();

        // then
        assertEquals(stationDTO.getName(), expected.getName());
        assertEquals(stationDTO.getPrivacy(), expected.getPrivacy());
        assertEquals(userId, expected.getOwnerId());
    }

}