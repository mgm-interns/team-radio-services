package com.mgmtp.radio.service.user.service.favorite;

import com.mgmtp.radio.domain.user.FavoriteSong;
import com.mgmtp.radio.dto.user.FavoriteSongDTO;
import com.mgmtp.radio.mapper.user.FavoriteSongMapper;
import com.mgmtp.radio.respository.user.FavoriteSongRepository;
import com.mgmtp.radio.service.user.FavoriteSongService;
import com.mgmtp.radio.service.user.FavoriteSongServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GetFavoriteSongByUserIdImplTest {

    @Mock
    FavoriteSongRepository favoriteSongRepository;

    FavoriteSongMapper favoriteSongMapper = FavoriteSongMapper.INSTANCE;

    FavoriteSongService favoriteSongService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        favoriteSongService = new FavoriteSongServiceImpl(favoriteSongRepository, favoriteSongMapper);
    }

    @Test
    public void getFavoriteListByUserIdSuccess() {
        //given
        FavoriteSongDTO favoriteSongDTO = new FavoriteSongDTO();
        favoriteSongDTO.setId(UUID.randomUUID().toString());
        favoriteSongDTO.setUserId("001");
        favoriteSongDTO.setSongId("001");

        FavoriteSong favoriteSong = new FavoriteSong();
        favoriteSong.setId(favoriteSongDTO.getId());
        favoriteSong.setUserId(favoriteSongDTO.getUserId());
        favoriteSong.setSongId(favoriteSongDTO.getSongId());

        when(favoriteSongRepository.findByUserId(anyString())).thenReturn(Flux.just(favoriteSong));

        // when
        Flux<FavoriteSongDTO> result = favoriteSongService.findByUserId(favoriteSongDTO.getUserId());
        FavoriteSongDTO expected = result.log().next().block();

        // then
        assertEquals(favoriteSongDTO, expected);
    }

    @Test
    public void getEmptyFavoriteListByUserId() {
        //given
        FavoriteSongDTO favoriteSongDTO = new FavoriteSongDTO();
        favoriteSongDTO.setId(UUID.randomUUID().toString());
        favoriteSongDTO.setUserId("001");
        favoriteSongDTO.setSongId("001");

        when(favoriteSongRepository.findByUserId(anyString())).thenReturn(Flux.empty());

        // when
        Flux<FavoriteSongDTO> result = favoriteSongService.findByUserId(favoriteSongDTO.getUserId());

        // then
        assertNull(result.log().next().block());
    }
}
