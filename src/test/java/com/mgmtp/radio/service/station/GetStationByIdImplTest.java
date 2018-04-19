package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.mapper.station.StationMapper;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.sdo.StationPrivacy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GetStationByIdImplTest {

    @Mock
    StationRepository stationRepository;

    StationMapper stationMapper = StationMapper.INSTANCE;

    StationService stationService;

    @Mock
    SongService songService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        stationService = new StationServiceImpl(stationMapper,  stationRepository, songService);
    }

    @Test
    public void GetStationByIdSuccess() {
        //given
        SongDTO songDTO = new SongDTO();
        songDTO.setId("song1");
        songDTO.setDownVoteCount(1);
        songDTO.setUpVoteCount(1);
        songDTO.setDuration(100);
        songDTO.setSkipped(false);
        songDTO.setSource("source");
        songDTO.setThumbnail("thumnail1");
        songDTO.setTitle("title1");
        songDTO.setUrl("url");

        List<String> listSongString = new ArrayList<String>();
        listSongString.add(songDTO.getId());

        List<SongDTO> listSongDTO = new ArrayList<SongDTO>();
        listSongDTO.add(songDTO);

        StationDTO stationDTO = new StationDTO();
        stationDTO.setId("001");
        stationDTO.setName("001");
        stationDTO.setPrivacy(StationPrivacy.station_public);
        stationDTO.setOwnerId("user1");
        stationDTO.setPlaylist(listSongDTO);

        Station station = new Station();
        station.setId(stationDTO.getId());
        station.setName(stationDTO.getName());
        station.setPrivacy(stationDTO.getPrivacy());
        station.setOwnerId(stationDTO.getOwnerId());
        station.setPlaylist(listSongString);

        when(stationRepository.retriveByIdOrFriendlyId(anyString())).thenReturn(Mono.just(station));
        when(songService.getAllSongById(anyList())).thenReturn(Flux.just(songDTO));

        // when
        Mono<StationDTO> result = stationService.findById(stationDTO.getId());
        StationDTO expected = result.log().block();

        // then
        assertEquals(stationDTO, expected);
    }

}