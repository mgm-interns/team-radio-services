package com.mgmtp.radio.service.station;

import com.mgmtp.radio.RadioApplicationTests;
import com.mgmtp.radio.domain.station.Song;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.mapper.station.SongMapper;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.respository.station.SongRepository;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.respository.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RadioApplicationTests.class)
public class SongServiceImplTest {

    @Mock
    SongRepository songRepository;

    @Mock
    StationRepository stationRepository;

    @Mock
    UserRepository userRepository;

    @Autowired
    @Qualifier("songMapperImpl")
    SongMapper songMapper;

    UserMapper userMapper = UserMapper.INSTANCE;

    SongService songService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        songService = new SongServiceImpl(stationRepository, songRepository, userRepository, songMapper, userMapper);
    }

    @Test
    public void getListSong() {
        final String STATION_ID = "5ab0c35c04a97f2c08954fa6";
        final List<String> playListCreatorId = Arrays.asList("5ab0c35c04a97f2c08954fa6", "5ab0c2eb04a97f2c08954fa5");

        //given this station
        Station station = new Station();
        station.setName("test station");
        station.setOwnerId(STATION_ID);
        station.setDeleted(false);
        station.setPlaylist(playListCreatorId);

        //when station repository call
        when(stationRepository.findByIdAndDeletedFalse(STATION_ID)).thenReturn(Mono.just(station));

        //given these song
        Song song1  = new Song();
        song1.setCreatorId(playListCreatorId.get(0));
        song1.setCreatedAt(LocalDate.now());
        song1.setUpVoteUserIdList(Collections.emptyList());
        song1.setDownVoteUserIdList(Collections.emptyList());
        song1.setUrl("url1");
        song1.setSongId("1");

        Song song2 = new Song();
        song2.setCreatorId(playListCreatorId.get(1));
        song2.setCreatedAt(LocalDate.now());
        song2.setUpVoteUserIdList(Collections.emptyList());
        song2.setDownVoteUserIdList(Collections.emptyList());
        song2.setUrl("url2");
        song2.setSongId("2");

        Flux<Song> resultSearchSong = Flux.just(song1, song2);

        //when song repository call
        when(songRepository.findByIdIn(playListCreatorId)).thenReturn(resultSearchSong);

        //given these user
        User user1 = new User();
        user1.setName("test User 1");

        User user2 = new User();
        user2.setName("test User 2");

        //when user repository call
        when(userRepository.findById(playListCreatorId.get(0))).thenReturn(Optional.of(user1));
        when(userRepository.findById(playListCreatorId.get(1))).thenReturn(Optional.of(user2));

        //test
        Flux<SongDTO> result = songService.getListSongBy(STATION_ID);

        //then
        List<SongDTO> convertResult = result.log().collectList().block();

        SongDTO compareSong1 = songMapper.songToSongDTO(song1);
        SongDTO compareSong2 = songMapper.songToSongDTO(song2);

        UserDTO compareUser1 = userMapper.userToUserDTO(user1);
        UserDTO compareUser2 = userMapper.userToUserDTO(user2);

        assertEquals(2, convertResult.size());
        assertThat(convertResult.get(0)).isEqualToIgnoringGivenFields(compareSong1,"creator");

        assertThat(compareUser1).isEqualToComparingFieldByField(convertResult.get(0).getCreator());
        assertThat(compareUser2).isEqualToComparingFieldByField(convertResult.get(1).getCreator());
    }
}