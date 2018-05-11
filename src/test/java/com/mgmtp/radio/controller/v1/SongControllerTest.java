package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.RadioApplicationTests;
import com.mgmtp.radio.dto.station.HistoryDTO;
import com.mgmtp.radio.mapper.station.SongMapper;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.service.station.HistoryService;
import com.mgmtp.radio.service.station.SongService;
import com.mgmtp.radio.service.station.StationOnlineService;
import com.mgmtp.radio.service.station.StationService;
import com.mgmtp.radio.service.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RadioApplicationTests.class)
public class SongControllerTest {

    WebTestClient webTestClient;

    SongController songController;

    @Mock
    SongService songService;

    @Mock
    StationService stationService;

    @Autowired
    @Qualifier("songMapperImpl")
    SongMapper songMapper;

    @Autowired
    @Qualifier("userMapperImpl")
    UserMapper userMapper;

    @Mock
    HistoryService historyService;

    @Mock
    StationOnlineService stationOnlineService;

    @Mock
    UserService userService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        songController = new SongController(songService,historyService, stationOnlineService, userMapper, userService);
        webTestClient = WebTestClient.bindToController(songController).build();
    }

    @Test
    public void testGetListSongHistory() {
        final String STATION_ID = "5ab0c35c04a97f2c08954fa6";
        final List<String> playListCreatorId = Arrays.asList("5ab0c35c04a97f2c08954fa6", "5ab0c2eb04a97f2c08954fa5");

        HistoryDTO history1 = new HistoryDTO();
        history1.setStationId(STATION_ID);
        history1.setSongId(playListCreatorId.get(0));
        history1.setUrl("url1");
        history1.setTitle("title1");
        history1.setThumbnail("thumbnail1");
        history1.setDuration(1000);

        //when call history service
        when(historyService.getHistoryByStationId(STATION_ID)).thenReturn(Flux.just(history1));

        //test
        webTestClient.get()
                .uri(URI.create("/api/v1/station/" + STATION_ID + "/history?limit=2"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(HistoryDTO.class)
                .consumeWith(response -> {
                   List<HistoryDTO> compare = response.getResponseBody();

                   assertEquals(1, compare.size());

                   assertEquals(STATION_ID, compare.get(0).getStationId());
                   assertEquals(playListCreatorId.get(0), compare.get(0).getSongId());
                   assertEquals(history1.getUrl(), compare.get(0).getUrl());
                   assertEquals(history1.getThumbnail(), compare.get(0).getThumbnail());
                   assertEquals(history1.getTitle(), compare.get(0).getTitle());
                   assertEquals(history1.getDuration(), compare.get(0).getDuration());
                });
    }
}