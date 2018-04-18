package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.RadioApplicationTests;
import com.mgmtp.radio.domain.station.Song;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.mapper.station.SongMapper;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.sdo.SongStatus;
import com.mgmtp.radio.service.station.SongService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RadioApplicationTests.class)
public class SongControllerTest {

    WebTestClient webTestClient;

    SongController songController;

    @Mock
    SongService songService;

    @Autowired
    @Qualifier("songMapperImpl")
    SongMapper songMapper;

    UserMapper userMapper = UserMapper.INSTANCE;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        songController = new SongController(songService);
        webTestClient = WebTestClient.bindToController(songController).build();
    }

    @Test
    public void testGetListSongHistory() {
        final String STATION_ID = "5ab0c35c04a97f2c08954fa6";
        final List<String> playListCreatorId = Arrays.asList("5ab0c35c04a97f2c08954fa6", "5ab0c2eb04a97f2c08954fa5");

        //given these song
        Song song1 = new Song();
        song1.setCreatorId(playListCreatorId.get(0));
        song1.setCreatedAt(LocalDate.of(2018, 03, 27));
        song1.setUpVoteUserIdList(Collections.emptyList());
        song1.setDownVoteUserIdList(Collections.emptyList());
        song1.setUrl("url1");
        song1.setSongId("1");

        Song song2 = new Song();
        song2.setCreatorId(playListCreatorId.get(1));
        song2.setCreatedAt(LocalDate.of(2018, 03, 28));
        song2.setUpVoteUserIdList(Collections.emptyList());
        song2.setDownVoteUserIdList(Collections.emptyList());
        song2.setUrl("url2");
        song2.setSongId("2");

        Song song3 = new Song();
        song3.setCreatorId(playListCreatorId.get(0));
        song3.setCreatedAt(LocalDate.of(2018, 03, 28));
        song3.setUpVoteUserIdList(Collections.emptyList());
        song3.setDownVoteUserIdList(Collections.emptyList());
        song3.setUrl("url1");
        song3.setSongId("3");

        Song song4 = new Song();
        song4.setCreatorId(playListCreatorId.get(1));
        song4.setCreatedAt(LocalDate.of(2018, 03, 28));
        song4.setUpVoteUserIdList(Collections.emptyList());
        song4.setDownVoteUserIdList(Collections.emptyList());
        song4.setUrl("url1");
        song4.setSongId("4");

        SongDTO songDTO1 = songMapper.songToSongDTO(song1);
        SongDTO songDTO2 = songMapper.songToSongDTO(song2);
        SongDTO songDTO3 = songMapper.songToSongDTO(song3);
        SongDTO songDTO4 = songMapper.songToSongDTO(song4);

        Flux<SongDTO> resultSearchSong = Flux.just(songDTO1, songDTO2, songDTO3, songDTO4);

        //when song service call
        when(songService.getListSongByStationId(STATION_ID)).thenReturn(resultSearchSong);

        //test
        webTestClient.get()
                .uri(URI.create("/api/v1/songs/sse/listHistorySong?stationId=" + STATION_ID + "&limit=2"))
                .header("Content-Type", MediaType.TEXT_EVENT_STREAM_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ServerSentEvent.class)
                .getResponseBody()
                .subscribe(data -> {
                    String event = data.event();
                    if (event != null) {
                        assertEquals("fetch", data.event());
                    }
                    String id = data.id();
                    if (id != null) {
                        assertEquals("0", id);
                    }
                    Object responseData = data.data();
                    if (((LinkedHashMap) responseData) != null) {
                        assertTrue((boolean) ((LinkedHashMap) responseData).get("success"));
                        ArrayList<LinkedHashMap> dataMap = (ArrayList<LinkedHashMap>) ((LinkedHashMap) responseData).get("data");

                        assertEquals(2, dataMap.size());

                        //assert song 2
                        assertEquals("url2", dataMap.get(0).get("url"));
                        assertEquals("2", dataMap.get(0).get("songId"));

                        //assert song 3
                        assertEquals("url1", dataMap.get(1).get("url"));
                        assertEquals("3", dataMap.get(1).get("songId"));
                    }
                });
    }

    @Test
    public void testGetListSongHistory_notExistStationId() {
        final String STATION_ID = "5ab0c35c04a97f2c08954fa6";

        //given this Flux SongDTO
        when(songService.getListSongByStationId(STATION_ID)).thenReturn(Flux.empty());

        //test
        webTestClient.get()
                .uri(URI.create("/api/v1/songs/sse/listHistorySong?stationId=" + STATION_ID + "&limit=2"))
                .header("Content-Type", MediaType.TEXT_EVENT_STREAM_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ServerSentEvent.class)
                .getResponseBody()
                .subscribe(assertDataEmpty);
    }

    private Consumer<ServerSentEvent> assertDataEmpty = data -> {
        String event = data.event();
        if (event != null) {
            assertEquals("fetch", data.event());
        }

        Object responseData = data.data();
        if (((LinkedHashMap) responseData) != null) {
            assertTrue((boolean) ((LinkedHashMap) responseData).get("success"));
            ArrayList<LinkedHashMap> dataMap = (ArrayList<LinkedHashMap>) ((LinkedHashMap) responseData).get("data");
            assertEquals(0, dataMap.size());
        }
    };
}