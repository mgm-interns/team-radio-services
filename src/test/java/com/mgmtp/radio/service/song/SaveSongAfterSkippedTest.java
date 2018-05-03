package com.mgmtp.radio.service.song;

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
import com.mgmtp.radio.service.station.SongService;
import com.mgmtp.radio.service.station.SongServiceImpl;
import com.mgmtp.radio.service.station.StationService;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Log4j2
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RadioApplicationTests.class)
public class SaveSongAfterSkippedTest {

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

	final String songId = "5ab3800f04a97f59986e92f61";

	MessageChannel historyChannel;

	@Mock
	StationSongSkipHelper stationSongSkipHelper;

	StationService stationService;

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
            stationSongSkipHelper,
            stationService);

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

		//Fake the song from DB
		Song song = new Song();
		song.setId(songId);
		song.setSongId(songId);
		song.setCreatorId("Hanna");
		song.setSkipped(false);
		song.setDownVoteUserIdList(new ArrayList<>());
		song.setUpVoteUserIdList(new ArrayList<>());

		when(songRepository.findById(anyString())).thenReturn(Mono.just(song));

		when(songRepository.save(song)).thenReturn(Mono.just(song));
	}

	@Test
	public void updateSongAfterSkippedTest() {

		SongDTO songDTO = new SongDTO();
		songDTO.setId("1");
		songDTO.setSongId("1");
		songDTO.setSkipped(false);
		Mono<SongDTO> monoSongDto = Mono.just(songDTO);

		Mono<SongDTO> monoSongDTO = songService.updateSongSkippedStatusToDb(monoSongDto);

		SongDTO updatedSong = monoSongDTO.block();

		assertEquals(true, updatedSong.isSkipped());
	}
}
