package com.mgmtp.radio.service.station;

import com.mgmtp.radio.RadioApplicationTests;
import com.mgmtp.radio.config.YouTubeConfig;
import com.mgmtp.radio.domain.station.Song;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
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
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RadioApplicationTests.class)
public class UpVoteAndDownVoteSongServiceImplTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private UserRepository userRepository;

    @Autowired
    @Qualifier("songMapperImpl")
    private SongMapper songMapper;

    private UserMapper userMapper = UserMapper.INSTANCE;

    private SongServiceImpl songService;

    private YouTubeHelper youTubeHelper;

    private TransferHelper transferHelper;

    private DateHelper dateHelper;

    @Autowired
    YouTubeConfig youTubeConfig;

    private User user;

    private Station station;

    private Song song;

    @Autowired
    private StationPlayerHelper stationPlayerHelper;

    MessageChannel historyChannel;

    @Mock
    StationSongSkipHelper stationSongSkipHelper;

    @Mock
    StationService stationService;

    MessageChannel shiftSongChannel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        youTubeHelper = new YouTubeHelper();

        transferHelper = new TransferHelper();

        dateHelper = new DateHelper();

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

        //given
        user = new User();
        user.setId("pc");
        user.setUsername("pc");
        user.setEmail("john.doe@yopmail.com");

        when(userRepository.save(any(User.class))).thenReturn(user);

        List<User> userList = new ArrayList<>();
        userList.add(user);
        when(userRepository.findByIdIn(any(List.class))).thenReturn(userList);

        // Init new station
        station = new Station();
        station.setId("station");
        station.setPlaylist(new ArrayList<>());
        station.setName("Test station");
        station.setOwnerId(user.getId());

        when(stationRepository.save(any(Station.class))).thenReturn(Mono.just(station));
        when(stationRepository.retriveByIdOrFriendlyId(any(String.class))).thenReturn(Mono.just(station));

        // Init new song
        song = new Song();
        song.setId("shape-of-me");
        song.setTitle("Shape of me");
        song.setStatus(SongStatus.not_play_yet);
        song.setSkipped(false);
        song.setDuration(1230);
        song.setDownVoteUserIdList(new ArrayList<>());
        song.setUpVoteUserIdList(new ArrayList<>());
        song.setCreatorId(user.getId());

        when(songRepository.findById(any(String.class))).thenReturn(Mono.just(song));

        // Add song to station playlist
        station.getPlaylist().add(song.getId());

    }

    @Test
    public void testUpVoteSongInStationPlaylist() {
        // Change creator to someone else
        song.setCreatorId("12083712");

        Song savedSong = new Song();
        savedSong.setId(song.getId());
        savedSong.setDownVoteUserIdList(new ArrayList<>());
        savedSong.setUpVoteUserIdList(new ArrayList<>());
        savedSong.getUpVoteUserIdList().add(user.getId());

        when(songRepository.save(any(Song.class))).thenReturn(Mono.just(savedSong));
        when(stationService.retriveByIdOrFriendlyId(station.getId())).thenReturn(Mono.just(station));

        SongDTO savedSongDTO = songService.upVoteSongInStationPlaylist(
                station.getId(),
                song.getId(),
                user.getId()
        ).block();

        // user in up vote list is match with userId
        assertEquals(savedSongDTO.getUpvoteUserList().get(0).getId(), user.getId());
    }

    @Test(expected = RadioBadRequestException.class)
    public void testUpVoteSongInStationPlaylistWithUpVoteOwnSong() {
        Song savedSong = new Song();
        savedSong.setId(song.getId());
        savedSong.setDownVoteUserIdList(new ArrayList<>());
        savedSong.setUpVoteUserIdList(new ArrayList<>());
        savedSong.getUpVoteUserIdList().add(user.getId());
        savedSong.setCreatorId(user.getId());

        when(songRepository.findById(song.getId())).thenReturn(Mono.just(savedSong));
        when(stationService.retriveByIdOrFriendlyId(station.getId())).thenReturn(Mono.just(station));

        // Service will thrown bad request exception
        songService.upVoteSongInStationPlaylist(
                station.getId(),
                song.getId(),
                user.getId()
        ).block();
    }

    @Test
    public void testDownVoteSongInStationPlaylist() {
        // Change creator to someone else
        song.setCreatorId("12083712");

        Song savedSong = new Song();
        savedSong.setId(song.getId());
        savedSong.setUpVoteUserIdList(new ArrayList<>());
        savedSong.setDownVoteUserIdList(new ArrayList<>());
        savedSong.getDownVoteUserIdList().add(user.getId());

        when(songRepository.save(any(Song.class))).thenReturn(Mono.just(savedSong));
        when(stationService.retriveByIdOrFriendlyId(station.getId())).thenReturn(Mono.just(station));

        SongDTO savedSongDTO = songService.downVoteSongInStationPlaylist(
                station.getId(),
                song.getId(),
                user.getId()
        ).block();

        // user in up vote list is match with userId
        assertEquals(savedSongDTO.getDownvoteUserList().get(0).getId(), user.getId());
    }
}
