package com.mgmtp.radio.service.station;

import com.mgmtp.radio.mapper.station.StationMapper;
import com.mgmtp.radio.respository.station.StationRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CreateStationImplTest {
    @Mock
    StationRepository stationRepository;

    StationMapper stationMapper = StationMapper.INSTANCE;

    StationService stationService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        stationService = new StationServiceImpl(stationRepository, stationMapper);
    }

    @Test
    public void createSuccess() {
        //given
//        StationDTO stationDTO = new StationDTO();
//        stationDTO.setId("001");
//        stationDTO.setUserId("001");
//        favoriteSongDTO.setSongId("001");
//
//        FavoriteSong savedFavoriteSong = new FavoriteSong();
//        savedFavoriteSong.setId(favoriteSongDTO.getId());
//        savedFavoriteSong.setUserId(favoriteSongDTO.getUserId());
//        savedFavoriteSong.setSongId(favoriteSongDTO.getSongId());
//
//        when(favoriteSongRepository.save(any(FavoriteSong.class))).thenReturn(Mono.just(savedFavoriteSong));
//        // when
//        Mono<FavoriteSongDTO> result = favoriteSongService.create(favoriteSongDTO.getUserId(), favoriteSongDTO);
//        FavoriteSongDTO expected = result.log().block();
//
//        // then
//        assertEquals(favoriteSongDTO.getId(), expected.getId());
//        assertEquals(favoriteSongDTO.getUserId(), expected.getUserId());
//        assertEquals(favoriteSongDTO.getSongId(), expected.getSongId());
    }

}
