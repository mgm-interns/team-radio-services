package com.mgmtp.radio.service.user;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.station.StationConfiguration;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.mapper.station.StationMapper;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.sdo.StationPrivacy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.Mockito.when;

public class GetAllStationOfSpecificUserById {

    @Mock
    UserService userService;

    @Mock
    StationRepository stationRepository;

    @Mock
    StationMapper stationMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userService = new UserServiceImpl(null,
                null,
                null,
                stationRepository,
                stationMapper);
    }

    @Test
    public void getAllStationOfUserById() {

        Station station_1 = new Station();
        station_1.setId("Id001");
        station_1.setName("stationName1");
        station_1.setOwnerId("User001");
        station_1.setPlaylist(Arrays.asList("Song001","Song002"));
        station_1.setCreatedAt(LocalDate.of(2018, 04, 19));
        station_1.setStationConfiguration(new StationConfiguration());
        station_1.setPrivacy(StationPrivacy.station_public);
        station_1.setFriendlyId("stationName1");

        StationDTO stationDto_1 = new StationDTO();
        stationDto_1.setId("Id001");
        stationDto_1.setName("stationName1");
        stationDto_1.setFriendlyId("stationName1");
        stationDto_1.setPrivacy(StationPrivacy.station_public);
        stationDto_1.setOwnerId("User001");
        stationDto_1.setCreatedAt(LocalDate.of(2018, 04, 19));

        when(stationRepository.findByOwnerIdAndPrivacy("User001", StationPrivacy.station_public)).thenReturn(Flux.just(station_1));
        when(stationMapper.stationToStationDTO(station_1)).thenReturn(stationDto_1);
        StepVerifier.create(userService.getAllStationOfSpecificUserById("User001",StationPrivacy.station_public))
                .expectNext(stationDto_1)
                .verifyComplete();
    }
}