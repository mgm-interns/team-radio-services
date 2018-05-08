package com.mgmtp.radio.service.station;

import com.mgmtp.radio.RadioApplicationTests;
import com.mgmtp.radio.domain.station.NowPlaying;
import com.mgmtp.radio.domain.station.PlayList;
import com.mgmtp.radio.config.YouTubeConfig;
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
import com.mgmtp.radio.sdo.SongStatus;
import com.mgmtp.radio.support.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.MessageChannel;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
    StationPlayerHelper stationPlayerHelper;

    @Autowired
    @Qualifier("songMapperImpl")
    SongMapper songMapper;

    UserMapper userMapper = UserMapper.INSTANCE;

    SongService songService;

    private static final String STATION_ID = "5ab0c35c04a97f2c08954fa6";
    private static final List<String> playListCreatorId = Arrays.asList("5ab0c35c04a97f2c08954fa6", "5ab0c2eb04a97f2c08954fa5", "5ab0c2eb04a97f2c08954fa7", "5ab0c2eb04a97f2c08954fa8");

    YouTubeHelper youTubeHelper;

    TransferHelper transferHelper;

    DateHelper dateHelper;

    @Autowired
    YouTubeConfig youTubeConfig;

    MessageChannel historyChannel;

    @Mock
    StationSongSkipHelper stationSongSkipHelper;

    @Mock
    StationService stationService;

    MessageChannel shiftSongChannel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        songService = new SongServiceImpl(
                songMapper,
                userMapper,
                songRepository,
                stationRepository,
                userRepository,
                youTubeHelper,
                transferHelper,
                dateHelper,
                youTubeConfig,
                stationPlayerHelper,
                historyChannel,
                stationSongSkipHelper,
                stationService,
                shiftSongChannel);
    }

    @Test
    public void getListSong() {
        //given this station
        Station station = new Station();
        station.setId(STATION_ID);
        station.setName("test station");
        station.setOwnerId(STATION_ID);
        station.setPlaylist(playListCreatorId);

        when(stationService.retriveByIdOrFriendlyId(station.getId())).thenReturn(Mono.just(station));

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
        Flux<SongDTO> result = songService.getListSongByStationId(STATION_ID);

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

    @Test
    public void getPlayListSong() {
        //given this station
        Station station = new Station();
        station.setId(STATION_ID);
        station.setName("test station");
        station.setOwnerId(STATION_ID);
        station.setPlaylist(playListCreatorId);

        when(stationService.retriveByIdOrFriendlyId(station.getId())).thenReturn(Mono.just(station));

        //given these song
        Song song1 = new Song();
        song1.setId(playListCreatorId.get(0));
        song1.setCreatorId(playListCreatorId.get(0));
        song1.setCreatedAt(LocalDate.now());
        song1.setUpVoteUserIdList(Collections.emptyList());
        song1.setDownVoteUserIdList(Collections.emptyList());
        song1.setUrl("url1");
        song1.setSongId("1");
        song1.setStatus(SongStatus.not_play_yet);

        Song song2 = new Song();
        song2.setId(playListCreatorId.get(1));
        song2.setCreatorId(playListCreatorId.get(1));
        song2.setCreatedAt(LocalDate.now());
        song2.setUpVoteUserIdList(Collections.emptyList());
        song2.setDownVoteUserIdList(Collections.emptyList());
        song2.setUrl("url2");
        song2.setSongId("2");
        song2.setStatus(SongStatus.not_play_yet);

        Song song3 = new Song();
        song3.setId(playListCreatorId.get(2));
        song3.setCreatorId(playListCreatorId.get(2));
        song3.setCreatedAt(LocalDate.now());
        song3.setUpVoteUserIdList(Collections.emptyList());
        song3.setDownVoteUserIdList(Collections.emptyList());
        song3.setUrl("url3");
        song3.setSongId("3");
        song3.setStatus(SongStatus.not_play_yet);
        song3.setSkipped(true);

        Song song4 = new Song();
        song4.setId(playListCreatorId.get(3));
        song4.setCreatorId(playListCreatorId.get(3));
        song4.setCreatedAt(LocalDate.now());
        song4.setUpVoteUserIdList(Collections.emptyList());
        song4.setDownVoteUserIdList(Collections.emptyList());
        song4.setUrl("url4");
        song4.setSongId("4");
        song4.setStatus(SongStatus.not_play_yet);

        Flux<Song> resultSearchSong = Flux.just(song1, song2, song3, song4);

        //when song repository call
        when(songRepository.findByIdIn(playListCreatorId)).thenReturn(resultSearchSong);
        when(songRepository.findById(playListCreatorId.get(0))).thenReturn(Mono.just(song1));
        when(songRepository.findById(playListCreatorId.get(1))).thenReturn(Mono.just(song2));
        when(songRepository.findById(playListCreatorId.get(2))).thenReturn(Mono.just(song3));
        when(songRepository.findById(playListCreatorId.get(3))).thenReturn(Mono.just(song4));

        song1.setStatus(SongStatus.playing);
        song1.setMessage("");

        when(songRepository.save(song1)).thenReturn(Mono.just(song1));

        //given these user
        User user1 = new User();
        user1.setName("test User 1");

        User user2 = new User();
        user2.setName("test User 2");

        //when user repository call
        when(userRepository.findById(playListCreatorId.get(0))).thenReturn(Optional.of(user1));
        when(userRepository.findById(playListCreatorId.get(1))).thenReturn(Optional.of(user2));
        when(userRepository.findById(playListCreatorId.get(2))).thenReturn(Optional.of(user1));
        when(userRepository.findById(playListCreatorId.get(3))).thenReturn(Optional.of(user2));

        //then
        Mono<PlayList> result = songService.getPlayListByStationId(STATION_ID, 0);

        PlayList playList = result.log().block();

        assertEquals(4, playList.getListSong().size());

        Optional<NowPlaying> nowPlaying = stationPlayerHelper.getStationNowPlaying(STATION_ID);

        assertThat(nowPlaying.get()).isEqualToComparingFieldByField(playList.getNowPlaying());

        assertThat(songMapper.songToSongDTO(song1)).isEqualToIgnoringGivenFields(playList.getListSong().get(0), "creator");
        assertThat(songMapper.songToSongDTO(song2)).isEqualToIgnoringGivenFields(playList.getListSong().get(1), "creator");
        assertThat(songMapper.songToSongDTO(song3)).isEqualToIgnoringGivenFields(playList.getListSong().get(2), "creator");
        assertThat(songMapper.songToSongDTO(song4)).isEqualToIgnoringGivenFields(playList.getListSong().get(3), "creator");
    }

    @Test
    public void getPlayListTest_changePlayingSong() throws InterruptedException {
        //given this station
        Station station = new Station();
        station.setId(STATION_ID);
        station.setName("test station");
        station.setOwnerId(STATION_ID);
        station.setPlaylist(playListCreatorId);

        when(stationService.retriveByIdOrFriendlyId(station.getId())).thenReturn(Mono.just(station));

        //given these song
        Song song1 = new Song();
        song1.setId(playListCreatorId.get(0));
        song1.setCreatorId(playListCreatorId.get(0));
        song1.setCreatedAt(LocalDate.now());
        song1.setUpVoteUserIdList(Collections.emptyList());
        song1.setDownVoteUserIdList(Collections.emptyList());
        song1.setUrl("url1");
        song1.setSongId("1");
        song1.setStatus(SongStatus.not_play_yet);

        Song song2 = new Song();
        song2.setId(playListCreatorId.get(1));
        song2.setCreatorId(playListCreatorId.get(1));
        song2.setCreatedAt(LocalDate.now());
        song2.setUpVoteUserIdList(Collections.emptyList());
        song2.setDownVoteUserIdList(Collections.emptyList());
        song2.setUrl("url2");
        song2.setSongId("2");
        song2.setStatus(SongStatus.not_play_yet);

        Song song3 = new Song();
        song3.setId(playListCreatorId.get(2));
        song3.setCreatorId(playListCreatorId.get(2));
        song3.setCreatedAt(LocalDate.now());
        song3.setUpVoteUserIdList(Collections.emptyList());
        song3.setDownVoteUserIdList(Collections.emptyList());
        song3.setUrl("url3");
        song3.setSongId("3");
        song3.setStatus(SongStatus.not_play_yet);
        song3.setSkipped(true);

        Song song4 = new Song();
        song4.setId(playListCreatorId.get(3));
        song4.setCreatorId(playListCreatorId.get(3));
        song4.setCreatedAt(LocalDate.now());
        song4.setUpVoteUserIdList(Collections.emptyList());
        song4.setDownVoteUserIdList(Collections.emptyList());
        song4.setUrl("url4");
        song4.setSongId("4");
        song4.setStatus(SongStatus.not_play_yet);

        Flux<Song> resultSearchSong = Flux.just(song1, song2, song3, song4);

        //when song repository call
        when(songRepository.findByIdIn(playListCreatorId)).thenReturn(resultSearchSong);
        when(songRepository.findById(playListCreatorId.get(0))).thenReturn(Mono.just(song1));
        when(songRepository.findById(playListCreatorId.get(1))).thenReturn(Mono.just(song2));
        when(songRepository.findById(playListCreatorId.get(2))).thenReturn(Mono.just(song3));
        when(songRepository.findById(playListCreatorId.get(3))).thenReturn(Mono.just(song4));

        song1.setStatus(SongStatus.played);
        song1.setMessage("");
        when(songRepository.save(song1)).thenReturn(Mono.just(song1));

        song2.setStatus(SongStatus.playing);
        song2.setMessage("");
        when(songRepository.save(song2)).thenReturn(Mono.just(song2));

        song3.setStatus(SongStatus.not_play_yet);
        song3.setMessage("");
        when(songRepository.save(song3)).thenReturn(Mono.just(song3));

        //given these user
        User user1 = new User();
        user1.setName("test User 1");

        User user2 = new User();
        user2.setName("test User 2");

        //when user repository call
        when(userRepository.findById(playListCreatorId.get(0))).thenReturn(Optional.of(user1));
        when(userRepository.findById(playListCreatorId.get(1))).thenReturn(Optional.of(user2));
        when(userRepository.findById(playListCreatorId.get(2))).thenReturn(Optional.of(user1));
        when(userRepository.findById(playListCreatorId.get(3))).thenReturn(Optional.of(user2));

        stationPlayerHelper.addNowPlaying(STATION_ID, songMapper.songToSongDTO(song1), 0);

        Thread.sleep((StationPlayerHelper.TIME_BUFFER + 1) *1000L);

        //then test
        Mono<PlayList> result = songService.getPlayListByStationId(STATION_ID, 0);

        PlayList playList = result.log().block();
        Optional<NowPlaying> playing = stationPlayerHelper.getStationNowPlaying(STATION_ID);

        assertNotNull(playing);
        assertFalse(playing.get().isEnded());
        assertEquals(song2.getId(), playing.get().getSongId());
        assertEquals(0, playing.get().getDuration());
        assertEquals(song2.getUrl(), playing.get().getUrl());

        assertEquals(3, playList.getListSong().size());
        assertThat(playing).get().isEqualToComparingFieldByField(playList.getNowPlaying());

        assertThat(songMapper.songToSongDTO(song2)).isEqualToIgnoringGivenFields(playList.getListSong().get(0), "creator");
        assertThat(songMapper.songToSongDTO(song3)).isEqualToIgnoringGivenFields(playList.getListSong().get(1), "creator");
        assertThat(songMapper.songToSongDTO(song4)).isEqualToIgnoringGivenFields(playList.getListSong().get(2), "creator");
    }

    @Test
    public void getPlayListTest_skipPlayingSong(){
        //given this station
        Station station = new Station();
        station.setId(STATION_ID);
        station.setName("test station");
        station.setOwnerId(STATION_ID);
        station.setPlaylist(playListCreatorId);

        when(stationService.retriveByIdOrFriendlyId(station.getId())).thenReturn(Mono.just(station));

        //given these song
        Song song3 = new Song();
        song3.setId(playListCreatorId.get(2));
        song3.setCreatorId(playListCreatorId.get(2));
        song3.setCreatedAt(LocalDate.now());
        song3.setUpVoteUserIdList(Collections.emptyList());
        song3.setDownVoteUserIdList(Collections.emptyList());
        song3.setUrl("url3");
        song3.setSongId("3");
        song3.setStatus(SongStatus.not_play_yet);
        song3.setSkipped(true);

        Song song4 = new Song();
        song4.setId(playListCreatorId.get(3));
        song4.setCreatorId(playListCreatorId.get(3));
        song4.setCreatedAt(LocalDate.now());
        song4.setUpVoteUserIdList(Collections.emptyList());
        song4.setDownVoteUserIdList(Collections.emptyList());
        song4.setUrl("url4");
        song4.setSongId("4");
        song4.setStatus(SongStatus.not_play_yet);

        Flux<Song> resultSearchSong = Flux.just(song3, song4);

        //when song repository call
        when(songRepository.findByIdIn(playListCreatorId)).thenReturn(resultSearchSong);
        when(songRepository.findById(playListCreatorId.get(2))).thenReturn(Mono.just(song3));
        when(songRepository.findById(playListCreatorId.get(3))).thenReturn(Mono.just(song4));

        Song song3save = new Song();
        song3save.setId(playListCreatorId.get(2));
        song3save.setCreatorId(playListCreatorId.get(2));
        song3save.setCreatedAt(LocalDate.now());
        song3save.setUpVoteUserIdList(Collections.emptyList());
        song3save.setDownVoteUserIdList(Collections.emptyList());
        song3save.setUrl("url3");
        song3save.setSongId("3");
        song3save.setStatus(SongStatus.played);
        song3save.setSkipped(true);
        song3save.setMessage("");
        when(songRepository.save(song3)).thenReturn(Mono.just(song3save));

        Song song4save = new Song();
        song4save.setId(playListCreatorId.get(3));
        song4save.setCreatorId(playListCreatorId.get(3));
        song4save.setCreatedAt(LocalDate.now());
        song4save.setUpVoteUserIdList(Collections.emptyList());
        song4save.setDownVoteUserIdList(Collections.emptyList());
        song4save.setUrl("url4");
        song4save.setSongId("4");
        song4save.setStatus(SongStatus.playing);
        song4save.setMessage("");
        when(songRepository.save(song4)).thenReturn(Mono.just(song4save));

        //given these user
        User user1 = new User();
        user1.setName("test User 1");

        User user2 = new User();
        user2.setName("test User 2");

        //when user repository call
        when(userRepository.findById(playListCreatorId.get(0))).thenReturn(Optional.of(user1));
        when(userRepository.findById(playListCreatorId.get(1))).thenReturn(Optional.of(user2));
        when(userRepository.findById(playListCreatorId.get(2))).thenReturn(Optional.of(user1));
        when(userRepository.findById(playListCreatorId.get(3))).thenReturn(Optional.of(user2));

        stationPlayerHelper.addNowPlaying(STATION_ID, songMapper.songToSongDTO(song3), 0);

        //then test
        Mono<PlayList> result = songService.getPlayListByStationId(STATION_ID, 0);

        PlayList playList = result.log().block();
        Optional<NowPlaying> playing = stationPlayerHelper.getStationNowPlaying(STATION_ID);

        assertNotNull(playing);
        assertFalse(playing.get().isEnded());
        assertEquals(song3.getId(), playing.get().getSongId());
        assertEquals(0, playing.get().getDuration());
        assertEquals(song3.getUrl(), playing.get().getUrl());

        assertEquals(2, playList.getListSong().size());
        assertThat(playing.get()).isEqualToComparingFieldByField(playList.getNowPlaying());

        song3.setStatus(SongStatus.not_play_yet);
        song3.setMessage(null);
        assertThat(songMapper.songToSongDTO(song3)).isEqualToIgnoringGivenFields(playList.getListSong().get(0), "creator");
    }

    @Test
    public void getPlayListTest_emptyListSong(){
        //given this station
        Station station = new Station();
        station.setId(STATION_ID);
        station.setName("test station");
        station.setOwnerId(STATION_ID);
        station.setPlaylist(playListCreatorId);

        when(stationService.retriveByIdOrFriendlyId(station.getId())).thenReturn(Mono.just(station));

        //given these song
        Song song4 = new Song();
        song4.setId(playListCreatorId.get(3));
        song4.setCreatorId(playListCreatorId.get(3));
        song4.setCreatedAt(LocalDate.now());
        song4.setUpVoteUserIdList(Collections.emptyList());
        song4.setDownVoteUserIdList(Collections.emptyList());
        song4.setUrl("url4");
        song4.setSongId("4");
        song4.setStatus(SongStatus.played);

        Flux<Song> resultSearchSong = Flux.just(song4);

        //when song repository call
        when(songRepository.findByIdIn(playListCreatorId)).thenReturn(resultSearchSong);

        stationPlayerHelper.addNowPlaying(STATION_ID, songMapper.songToSongDTO(song4), 0);

        //then test
        Mono<PlayList> result = songService.getPlayListByStationId(STATION_ID, 0);

        PlayList playList = result.log().block();

        assertThat(playList).isEqualToComparingFieldByField(PlayList.EMPTY_PLAYLIST);
    }
}