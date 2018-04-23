package com.mgmtp.radio.service.station;

import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.sdo.StationPrivacy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class StationOnlineServiceImplTest {
    private static Map<String, StationDTO> allStationsTest = new HashMap<>();

    @Mock
    StationOnlineService stationOnlineService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        //given
        StationDTO stationDTO = new StationDTO();
        stationDTO.setId("station1");
        stationDTO.setName("ST0");
        stationDTO.setPrivacy(StationPrivacy.station_private);

        String userId = "user1";
        stationDTO.setOwnerId(userId);

        stationDTO.setCreatedAt(LocalDate.of(2018,06,20));

        UserDTO userDTO = new UserDTO();
        userDTO.setName("ThuyTien");
        Map<String, UserDTO> userList = new HashMap<>();
        userList.put("123",userDTO);

        stationDTO.setUserList(userList);
    }
    @Test
    public void addStationToList(){
        //given
        StationDTO stationDTO = new StationDTO();
        String idStation ="station1";
        stationDTO.setId(idStation);
        stationDTO.setName("ST0");
        stationDTO.setPrivacy(StationPrivacy.station_private);

        String userId = "user1";
        stationDTO.setOwnerId(userId);

        stationDTO.setCreatedAt(LocalDate.of(2018,06,20));

        UserDTO userDTO = new UserDTO();
        userDTO.setName("ThuyTien");
        Map<String, UserDTO> userList = new HashMap<>();
        userList.put("123",userDTO);

        stationDTO.setUserList(userList);

        allStationsTest.put(idStation,stationDTO);
        stationOnlineService.addStationToList(stationDTO);

        assertEquals(allStationsTest,stationOnlineService.getAllStation());
    }

    @Test
    public void removeStationFromList(){

    }
    @Test
    public void getStationById(){

    }

    @Test
    public void addOnlineUser(){

    }
    @Test
    public void removeOnlineUser(){

    }

    @Test
    public void getAllStation() {

    }
}