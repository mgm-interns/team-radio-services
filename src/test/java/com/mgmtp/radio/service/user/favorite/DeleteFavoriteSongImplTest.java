package com.mgmtp.radio.service.user.favorite;

import com.mgmtp.radio.domain.user.FavoriteSong;
import com.mgmtp.radio.dto.user.FavoriteSongDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.user.FavoriteSongMapper;
import com.mgmtp.radio.respository.user.FavoriteSongRepository;
import com.mgmtp.radio.service.station.SongService;
import com.mgmtp.radio.service.user.FavoriteSongService;
import com.mgmtp.radio.service.user.FavoriteSongServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

public class DeleteFavoriteSongImplTest {

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
    public void deleteSuccess() {
        //given
        FavoriteSongDTO favoriteSongDTO = new FavoriteSongDTO();
        favoriteSongDTO.setId("001");
        favoriteSongDTO.setUserId("001");
        favoriteSongDTO.setSongId("001");

        FavoriteSong favoriteSong = new FavoriteSong();
        favoriteSong.setId(favoriteSongDTO.getId());
        favoriteSong.setUserId(favoriteSongDTO.getUserId());
        favoriteSong.setSongId(favoriteSongDTO.getSongId());

        when(favoriteSongRepository.findByIdAndUserId(anyString(), anyString())).thenReturn(Mono.just(favoriteSong));

        // when
        favoriteSongRepository.delete(favoriteSong);

        //then
        verify(favoriteSongRepository, times(1)).delete(any(FavoriteSong.class));
    }

    @Test(expected = Exception.class)
    public void deleteFailureWithWrongUserId() {
        //given
        FavoriteSongDTO favoriteSongDTO = new FavoriteSongDTO();
        favoriteSongDTO.setId("001");
        favoriteSongDTO.setUserId("001");
        favoriteSongDTO.setSongId("001");

        FavoriteSong favoriteSong = new FavoriteSong();
        favoriteSong.setId(favoriteSongDTO.getId());
        favoriteSong.setUserId(favoriteSongDTO.getUserId());
        favoriteSong.setSongId(favoriteSongDTO.getSongId());


        when(favoriteSongRepository.findByIdAndUserId(anyString(), anyString())).thenThrow(new RadioNotFoundException());
        when(favoriteSongRepository.delete(any(FavoriteSong.class)));

        // when
        favoriteSongService.delete(favoriteSongDTO.getId(), favoriteSongDTO.getUserId());
    }
}
