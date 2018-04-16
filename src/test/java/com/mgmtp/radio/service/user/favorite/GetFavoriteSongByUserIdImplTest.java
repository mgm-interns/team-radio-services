package com.mgmtp.radio.service.user.favorite;

import com.mgmtp.radio.domain.user.FavoriteSong;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.dto.user.FavoriteSongDTO;
import com.mgmtp.radio.mapper.user.FavoriteSongMapper;
import com.mgmtp.radio.respository.user.FavoriteSongRepository;
import com.mgmtp.radio.service.station.SongService;
import com.mgmtp.radio.service.user.FavoriteSongService;
import com.mgmtp.radio.service.user.FavoriteSongServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GetFavoriteSongByUserIdImplTest {

    @Mock
    FavoriteSongRepository favoriteSongRepository;

    @Mock
    SongService songService;

    FavoriteSongMapper favoriteSongMapper = FavoriteSongMapper.INSTANCE;

    FavoriteSongService favoriteSongService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        favoriteSongService = new FavoriteSongServiceImpl(favoriteSongRepository, favoriteSongMapper, songService);
    }

    @Test
    public void getFavoriteListByUserIdSuccess() {
        //given
        FavoriteSongDTO favoriteSongDTO = new FavoriteSongDTO();
        favoriteSongDTO.setId(UUID.randomUUID().toString());
        favoriteSongDTO.setUserId("001");
        favoriteSongDTO.setSongId("001");

        SongDTO songDTO = new SongDTO();
        songDTO.setId(favoriteSongDTO.getId());
        songDTO.setSongId(favoriteSongDTO.getId());
        songDTO.setSource("youtube");

        FavoriteSong favoriteSong = new FavoriteSong();
        favoriteSong.setId(favoriteSongDTO.getId());
        favoriteSong.setUserId(favoriteSongDTO.getUserId());
        favoriteSong.setSongId(favoriteSongDTO.getSongId());

        when(favoriteSongRepository.findByUserId(anyString())).thenReturn(Flux.just(favoriteSong));
        when(songService.getListSongByListSongIdId(anyList())).thenReturn(Flux.just(songDTO));
        when(songService.getById(favoriteSongDTO.getSongId())).thenReturn(Mono.just(songDTO));
        // when
        Flux<FavoriteSongDTO> result = favoriteSongService.findByUserId(favoriteSongDTO.getUserId());
        FavoriteSongDTO expected = result.log().next().block();

        // then
        assertEquals(favoriteSongDTO.getId(), expected.getId());
        assertEquals(favoriteSongDTO.getSongId(), expected.getSongId());
        assertEquals(favoriteSongDTO.getUserId(), expected.getUserId());
    }

    @Test
    public void getEmptyFavoriteListByUserId() {
        //given
        FavoriteSongDTO favoriteSongDTO = new FavoriteSongDTO();
        favoriteSongDTO.setId(UUID.randomUUID().toString());
        favoriteSongDTO.setUserId("001");
        favoriteSongDTO.setSongId("001");

        SongDTO songDTO = new SongDTO();
        songDTO.setId(favoriteSongDTO.getId());
        songDTO.setSongId(favoriteSongDTO.getId());
        songDTO.setSource("youtube");

        when(favoriteSongRepository.findByUserId(anyString())).thenReturn(Flux.empty());
        when(songService.getListSongByListSongIdId(anyList())).thenReturn(Flux.just(songDTO));

        // when
        Flux<FavoriteSongDTO> result = favoriteSongService.findByUserId(favoriteSongDTO.getUserId());

        // then
        assertNull(result.log().next().block());
    }
}
