package com.mgmtp.radio.service.station;

import com.google.api.services.youtube.model.*;
import com.mgmtp.radio.RadioApplicationTests;
import com.mgmtp.radio.config.YouTubeConfig;
import com.mgmtp.radio.domain.station.Song;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.mapper.station.SongMapper;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.respository.station.SongRepository;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.respository.user.UserRepository;
import com.mgmtp.radio.sdo.SongStatus;
import com.mgmtp.radio.support.*;
import lombok.extern.log4j.Log4j2;
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

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@Log4j2
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RadioApplicationTests.class)
public class AddSongServiceImplTest {
    @Mock
    StationRepository stationRepository;

    @Mock
    SongRepository songRepository;

    @Mock
    UserRepository userRepository;

    @Autowired
    @Qualifier("songMapperImpl")
    SongMapper songMapper = SongMapper.INSTANCE;

    private UserMapper userMapper = UserMapper.INSTANCE;

    @Mock
    private YouTubeHelper youTubeHelper;

    private TransferHelper transferHelper;

    private DateHelper dateHelper;

    @Autowired
    YouTubeConfig youTubeConfig;

    SongService songService;

    User user;
    Station station;

    @Autowired
    private StationPlayerHelper stationPlayerHelper;

    MessageChannel historyChannel;

    @Mock
    StationSongSkipHelper stationSongSkipHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

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
                stationSongSkipHelper);

        // Init new user
        user = new User();
        user.setId("dungID");
        user.setName("dung");
        user.setEmail("test@gmail.com");

        log.info(user.toString());

        when(userRepository.save(any(User.class))).thenReturn(user);

        List<User> userList = new ArrayList<>();
        userList.add(user);
        when(userRepository.findByIdIn(any(List.class))).thenReturn(userList);

        // Init new station
        station = new Station();
        station.setId("station");
        station.setName("Dung's Station");
        station.setPlaylist(new ArrayList<>());
        station.setOwnerId(user.getId());

        when(stationRepository.save(any(Station.class))).thenReturn(Mono.just(station));
        when(stationRepository.retriveByIdOrFriendlyId(any(String.class))).thenReturn(Mono.just(station));
    }

    @Test
    public void addSongToStationPlaylistTest() {
        SongDTO songDTO = new SongDTO();

        songDTO.setSongId("mNh6MCoMPis");
        songDTO.setStatus(SongStatus.not_play_yet);
        songDTO.setSkipped(false);
        songDTO.setUrl("https://www.youtube.com/watch?v=mNh6MCoMPis");
        songDTO.setTitle("Chelsea - What Do You Want From Me (The Voice Kids 2013: The Blind Auditions)");
        songDTO.setDuration(50000);
        songDTO.setCreator(userMapper.userToUserDTO(user));

        Song savedSong = new Song();
        savedSong.setStatus(songDTO.getStatus());
        savedSong.setSkipped(songDTO.isSkipped());
        savedSong.setUrl(songDTO.getUrl());
        savedSong.setTitle(songDTO.getTitle());
        savedSong.setDuration(songDTO.getDuration());
        savedSong.setCreatorId(songDTO.getCreator().getId());

        Video video = new Video();
        VideoSnippet videoSnippet = new VideoSnippet();
        ThumbnailDetails thumbnailDetails = new ThumbnailDetails();
        Thumbnail thumbnail = new Thumbnail();
        VideoContentDetails videoContentDetails = new VideoContentDetails();

        thumbnail.setUrl(songDTO.getUrl());
        thumbnailDetails.setDefault(thumbnail);

        videoSnippet.setTitle(songDTO.getTitle());
        videoSnippet.setThumbnails(thumbnailDetails);

        videoContentDetails.setDuration("PT1H27M22S");

        video.setId(songDTO.getSongId());
        video.setSnippet(videoSnippet);
        video.setKind("youtube#video");
        video.setContentDetails(videoContentDetails);

        when(songRepository.save(any(Song.class))).thenReturn(Mono.just(savedSong));
        when(youTubeHelper.getYouTubeVideoById("mNh6MCoMPis")).thenReturn(video);

        SongDTO savedSongDTO = songService.addSongToStationPlaylist(
                station.getId(), "mNh6MCoMPis", "Nhac Audition", user.getId()
        ).block();

        assertEquals(songDTO.getTitle(), savedSongDTO.getTitle());
        assertEquals(songDTO.getUrl(), savedSongDTO.getUrl());
    }
}
