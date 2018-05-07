package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.History;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.station.HistoryDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.mapper.station.HistoryMapper;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.respository.station.HistoryRepository;
import com.mgmtp.radio.respository.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

public class HistoryServiceImplTest {
    @Mock
    HistoryRepository historyRepository;

    @Mock
    HistoryMapper historyMapper;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    HistoryService historyService;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        historyService = new HistoryServiceImpl(historyRepository,historyMapper,userRepository,userMapper);
    }

    @Test
    public void getHistoryByStationIdSuccess(){

        History history_1 = new History();
        history_1.setId("Id001");
        history_1.setStationId("StationId001");
        history_1.setUrl("Url001");
        history_1.setSongId("SongId001");
        history_1.setTitle("Title001");
        history_1.setThumbnail("Thumbnail001");
        history_1.setDuration(1001);
        history_1.setCreatorId("userID001");
        history_1.setCreatedAt(LocalDateTime.of(2018,1,1, 1, 1, 1));

        History history_2 = new History();
        history_2.setId("Id002");
        history_2.setStationId("StationId001");
        history_2.setUrl("Url001");
        history_2.setSongId("SongId001");
        history_2.setTitle("Title001");
        history_2.setThumbnail("Thumbnail001");
        history_2.setDuration(1001);
        history_2.setCreatorId("userID002");
        history_2.setCreatedAt(LocalDateTime.of(2018,1,1,1,1,1));

        HistoryDTO historyDTO_1 = new HistoryDTO();
        historyDTO_1.setId("Id002");
        historyDTO_1.setStationId("StationId001");
        historyDTO_1.setUrl("Url001");
        historyDTO_1.setSongId("SongId001");
        historyDTO_1.setTitle("Title001");
        historyDTO_1.setThumbnail("Thumbnail001");
        historyDTO_1.setDuration(1001);

        User user = new User();
        user.setId("userId001");
        user.setUsername("UserName001");
        user.setAvatarUrl("avatarUrl001");

        UserDTO userDto = new UserDTO();
        userDto.setId("userId001");
        userDto.setUsername("UserName001");
        userDto.setAvatarUrl("avatarUrl001");

        HistoryDTO historyDTO_2 = new HistoryDTO();
        historyDTO_2.setId("Id001");
        historyDTO_2.setUrl("Url001");
        historyDTO_2.setStationId("StationId001");
        historyDTO_2.setSongId("SongId001");
        historyDTO_2.setTitle("Title001");
        historyDTO_2.setThumbnail("Thumbnail001");
        historyDTO_2.setDuration(1001);
        historyDTO_2.setCreator(userDto);

        when(historyRepository.findByStationId("StationId001")).thenReturn(Flux.just(history_1,history_2));
        when(historyMapper.historyToHistoryDto(history_1)).thenReturn(historyDTO_2);
        when(historyMapper.historyToHistoryDto(history_2)).thenReturn(historyDTO_1);
        when(userRepository.findById("userId001")).thenReturn(java.util.Optional.ofNullable(user));
        when(userMapper.userToUserDTO(user)).thenReturn(userDto);

        StepVerifier.create(historyService.getHistoryByStationId("StationId001"))
                .expectNext(historyDTO_2)
                .verifyComplete();
    }
}