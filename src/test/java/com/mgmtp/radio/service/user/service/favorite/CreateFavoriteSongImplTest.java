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
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class CreateFavoriteSongImplTest {

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
    public void createSuccess() {
        //given
        FavoriteSongDTO favoriteSongDTO = new FavoriteSongDTO();
        favoriteSongDTO.setId("001");
        favoriteSongDTO.setUserId("001");
        favoriteSongDTO.setSongId("001");

        FavoriteSong savedFavoriteSong = new FavoriteSong();
        savedFavoriteSong.setId(favoriteSongDTO.getId());
        savedFavoriteSong.setUserId(favoriteSongDTO.getUserId());
        savedFavoriteSong.setSongId(favoriteSongDTO.getSongId());

        when(favoriteSongRepository.save(any(FavoriteSong.class))).thenReturn(Mono.just(savedFavoriteSong));
        // when
        Mono<FavoriteSongDTO> result = favoriteSongService.create(favoriteSongDTO.getUserId(), favoriteSongDTO);
        FavoriteSongDTO expected = result.log().block();

        // then
        assertEquals(favoriteSongDTO.getId(), expected.getId());
        assertEquals(favoriteSongDTO.getUserId(), expected.getUserId());
        assertEquals(favoriteSongDTO.getSongId(), expected.getSongId());
    }

    @Test
    public void createFailure() {
        //given
        FavoriteSongDTO favoriteSongDTO = new FavoriteSongDTO();
        favoriteSongDTO.setId("001");
        favoriteSongDTO.setUserId("001");
        favoriteSongDTO.setSongId("001");

        FavoriteSongDTO failedfavoriteSongDTO = new FavoriteSongDTO();
        failedfavoriteSongDTO.setId("002");
        failedfavoriteSongDTO.setUserId("002");
        failedfavoriteSongDTO.setSongId("002");

        FavoriteSong savedFavoriteSong = new FavoriteSong();
        savedFavoriteSong.setId(favoriteSongDTO.getId());
        savedFavoriteSong.setUserId(favoriteSongDTO.getUserId());
        savedFavoriteSong.setSongId(favoriteSongDTO.getSongId());

        when(favoriteSongRepository.save(any(FavoriteSong.class))).thenReturn(Mono.just(savedFavoriteSong));
        // when
        Mono<FavoriteSongDTO> result = favoriteSongService.create(favoriteSongDTO.getUserId(), favoriteSongDTO);
        FavoriteSongDTO expected = result.log().block();

        // then
        assertNotEquals(failedfavoriteSongDTO.getId(), expected.getId());
        assertNotEquals(failedfavoriteSongDTO.getUserId(), expected.getUserId());
        assertNotEquals(failedfavoriteSongDTO.getSongId(), expected.getSongId());
    }
}
