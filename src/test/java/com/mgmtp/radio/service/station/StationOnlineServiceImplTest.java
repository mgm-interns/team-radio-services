package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.sdo.StationPrivacy;
import com.mgmtp.radio.support.StationPlayerHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

public class StationOnlineServiceImplTest {

    StationOnlineService stationOnlineService;

    @Mock
    StationPlayerHelper stationPlayerHelper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        stationOnlineService = new StationOnlineServiceImpl(stationPlayerHelper);
    }
    @Test
    public void multiTestAddRemoveGetStation(){
        List<StationDTO> allStationsTest = new ArrayList<>();
        //given
        StationDTO stationDTO = new StationDTO();
        String stationId ="station1";
        stationDTO.setId(stationId);
        stationDTO.setFriendlyId(stationId);
        stationDTO.setName("ST0");
        stationDTO.setPlaylist(new ArrayList<>());
        stationDTO.setPrivacy(StationPrivacy.station_public);
        stationDTO.getStationConfiguration().setStationFriendlyId(stationId);

        String userId = "user1";
        stationDTO.setOwnerId(userId);

        stationDTO.setCreatedAt(LocalDate.of(2018,06,20));

        UserDTO userDTO = new UserDTO();
        userDTO.setName("ThuyTien");
        Map<String, UserDTO> onlineUsers = new HashMap<>();
        onlineUsers.put("123",userDTO);

        stationDTO.setOnlineUsers(onlineUsers);

        //test addStationToList
        //when
        allStationsTest.add(stationDTO);
        stationOnlineService.addStationToList(stationDTO);

        //then
        assertEquals(allStationsTest.size(),stationOnlineService.getAllStation().size());
        assertThat(allStationsTest.get(0)).isEqualToComparingFieldByField(stationOnlineService.getAllStation().get(stationId));

        //test getStationById
        //when
        StationDTO expectedStationDTO = allStationsTest.get(0);
        StationDTO resultStationDTO = stationOnlineService.getStationById(stationId);
        //then
        assertEquals(expectedStationDTO,resultStationDTO);

        //test removeStationFromList
        //when
        allStationsTest.remove(stationDTO);
        stationOnlineService.removeStationFromList(stationDTO.getId());

        //then
        assertEquals(allStationsTest.size(),stationOnlineService.getAllStation().size());
        if (allStationsTest.size()!=0 && stationOnlineService.getAllStation().size()!=0){
            assertThat(allStationsTest.get(0)).isEqualToComparingFieldByField(stationOnlineService.getAllStation().get(0));
        }
    }

    @Test
    public void multiTestAddRemoveOnlineUser(){
        List<StationDTO> allStationsTest = new ArrayList<>();
        //given
        StationDTO stationDTO = new StationDTO();
        String stationId ="station1";
        stationDTO.setId(stationId);
        stationDTO.setName("ST0");
        stationDTO.setPrivacy(StationPrivacy.station_private);

        String userId = "user1";
        stationDTO.setOwnerId(userId);

        stationDTO.setCreatedAt(LocalDate.of(2018,06,20));

        UserDTO userDTO = new UserDTO();
        String userIdOnline ="userOnline";
        userDTO.setId(userIdOnline);
        userDTO.setName("ThuyTien");
        Map<String, UserDTO> joiningUsers = new HashMap<>();

        //Test addOnlineUser
        //when
        allStationsTest.add(stationDTO);
        StationDTO stationDTOTestAdd = allStationsTest.get(0);
        stationDTOTestAdd.getOnlineUsers().put(userDTO.getId(),userDTO);

        stationOnlineService.addStationToList(stationDTO);
        stationOnlineService.addOnlineUser(userDTO,stationId);

        //then
        assertEquals(allStationsTest.size(),stationOnlineService.getAllStation().size());
        assertEquals(allStationsTest.get(0).getOnlineUsers().size(),stationOnlineService.getAllStation().get(0).getOnlineUsers().size());
        assertThat(allStationsTest.get(0).getOnlineUsers().get(userIdOnline)).isEqualToComparingFieldByField(stationOnlineService.getAllStation().get(0).getOnlineUsers().get(userIdOnline));

        //Test removeOnlineUser
        //when
        StationDTO stationDTOTestRemove = allStationsTest.get(0);
        stationDTOTestRemove.getOnlineUsers().remove(userDTO.getId());

        stationOnlineService.removeOnlineUser(userDTO,stationId);

        //then
        assertEquals(allStationsTest.size(),stationOnlineService.getAllStation().size());
        assertEquals(allStationsTest.get(0).getOnlineUsers().size(),stationOnlineService.getAllStation().get(0).getOnlineUsers().size());
        if (allStationsTest.get(0).getOnlineUsers().size()!=0 && stationOnlineService.getAllStation().get(0).getOnlineUsers().size() !=0){
            assertThat(allStationsTest.get(0).getOnlineUsers().get(userIdOnline)).isEqualToComparingFieldByField(stationOnlineService.getAllStation().get(0).getOnlineUsers().get(userIdOnline));
        }
    }

    @Test
    public void sortByStationCase1() {
        List<StationDTO> allStationsTest = new ArrayList<>();
        // case 1: sort base on has UserOnline
        //given
        StationDTO stationDTO01 = new StationDTO();
        String stationId01 ="station1";
        stationDTO01.setId(stationId01);
        stationDTO01.setName("ST1");
        stationDTO01.setCreatedAt(LocalDate.of(1995,06,20));

        StationDTO stationDTO02 = new StationDTO();
        String stationId02 ="station2";
        stationDTO02.setId(stationId02);
        stationDTO02.setName("ST2");
        stationDTO02.setCreatedAt(LocalDate.now());

        StationDTO stationDTO03 = new StationDTO();
        String stationId03 ="station3";
        stationDTO03.setId(stationId03);
        stationDTO03.setName("ST3");
        stationDTO03.setCreatedAt(LocalDate.of(2007,10,4));

        UserDTO userDTO1 = new UserDTO();
        String userIdOnline ="userOnline1";
        userDTO1.setId(userIdOnline);
        userDTO1.setName("ThuyTien");
        Map<String, UserDTO> userList1 = new HashMap<>();
        userList1.put(userDTO1.getId(),userDTO1);
        stationDTO03.setOnlineUsers(userList1);

        stationOnlineService.addStationToList(stationDTO01);
        stationOnlineService.addStationToList(stationDTO02);
        stationOnlineService.addStationToList(stationDTO03);

        //when
        List<StationDTO> expectedStation = new ArrayList<>();
        expectedStation.add(stationDTO03);
        expectedStation.add(stationDTO02);
        expectedStation.add(stationDTO01);

        List<StationDTO> resultStation = new ArrayList<>(stationOnlineService.getAllStation().values());

        //then
        assertEquals(resultStation.size(),expectedStation.size());
        for(int i = 0; i< resultStation.size(); i++) {
            assertThat(resultStation.get(i)).isEqualToComparingFieldByField(expectedStation.get(i));
        }

    }
    @Test
    public void sortByStationCase2() {
        List<StationDTO> allStationsTest = new ArrayList<>();
        // case 2: sort base on Create date of station

        //given
        StationDTO stationDTO01 = new StationDTO();
        String stationId01 ="station1";
        stationDTO01.setId(stationId01);
        stationDTO01.setName("ST1");
        stationDTO01.setCreatedAt(LocalDate.of(1995,06,20));

        StationDTO stationDTO02 = new StationDTO();
        String stationId02 ="station2";
        stationDTO02.setId(stationId02);
        stationDTO02.setName("ST2");
        stationDTO02.setCreatedAt(LocalDate.now());

        StationDTO stationDTO03 = new StationDTO();
        String stationId03 ="station3";
        stationDTO03.setId(stationId03);
        stationDTO03.setName("ST3");
        stationDTO03.setCreatedAt(LocalDate.of(2007,10,4));

        stationOnlineService.addStationToList(stationDTO01);
        stationOnlineService.addStationToList(stationDTO02);
        stationOnlineService.addStationToList(stationDTO03);

        //when
        List<StationDTO> expectedStation = new ArrayList<>();
        expectedStation.add(stationDTO02);
        expectedStation.add(stationDTO01);
        expectedStation.add(stationDTO03);

        List<StationDTO> resultStation = new ArrayList<>(stationOnlineService.getAllStation().values());

        //then
        assertEquals(resultStation.size(),expectedStation.size());
        for(int i = 0; i< resultStation.size(); i++) {
            assertThat(resultStation.get(i)).isEqualToComparingFieldByField(expectedStation.get(i));
        }
    }
    @Test
    public void sortByStationCase3() {
        List<StationDTO> allStationsTest = new ArrayList<>();
        //case 3 : sort base on number of UserOnline

        //given
        StationDTO stationDTO01 = new StationDTO();
        String stationId01 ="station1";
        stationDTO01.setId(stationId01);
        stationDTO01.setName("ST1");
        stationDTO01.setCreatedAt(LocalDate.of(1995,06,20));

        UserDTO userDTO1 = new UserDTO();
        String userIdOnline ="userOnline1";
        userDTO1.setId(userIdOnline);
        userDTO1.setName("ThuyTien");
        Map<String, UserDTO> userList1 = new HashMap<>();
        userList1.put(userDTO1.getId(),userDTO1);
        stationDTO01.setOnlineUsers(userList1);

        StationDTO stationDTO02 = new StationDTO();
        String stationId02 ="station2";
        stationDTO02.setId(stationId02);
        stationDTO02.setName("ST2");
        stationDTO02.setCreatedAt(LocalDate.now());

        UserDTO userDTO2 = new UserDTO();
        String userIdOnline2 ="userOnline2";
        userDTO2.setId(userIdOnline2);
        userDTO2.setName("ThuyTien2");
        Map<String, UserDTO> userList2 = new HashMap<>();
        userList2.put(userDTO1.getId(),userDTO1);
        userList2.put(userDTO2.getId(),userDTO2);
        stationDTO02.setOnlineUsers(userList2);

        StationDTO stationDTO03 = new StationDTO();
        String stationId03 ="station3";
        stationDTO03.setId(stationId03);
        stationDTO03.setName("ST3");
        stationDTO03.setCreatedAt(LocalDate.of(2007,10,4));

        stationOnlineService.addStationToList(stationDTO01);
        stationOnlineService.addStationToList(stationDTO02);
        stationOnlineService.addStationToList(stationDTO03);

        //when
        List<StationDTO> expectedStation = new ArrayList<>();
        expectedStation.add(stationDTO02);
        expectedStation.add(stationDTO01);
        expectedStation.add(stationDTO03);

        List<StationDTO> resultStation = new ArrayList<>(stationOnlineService.getAllStation().values());

        //then
        assertEquals(resultStation.size(),expectedStation.size());
        for(int i = 0; i< resultStation.size(); i++) {
            assertThat(resultStation.get(i)).isEqualToComparingFieldByField(expectedStation.get(i));
        }
    }
    @Test
    public void sortByStationAllCase() {
        List<StationDTO> allStationsTest = new ArrayList<>();
        //given

        // old station - 1 number of user
        StationDTO stationDTO01 = new StationDTO();
        String stationId01 ="station1";
        stationDTO01.setId(stationId01);
        stationDTO01.setName("ST1");
        stationDTO01.setCreatedAt(LocalDate.of(1995,06,20));

        UserDTO userDTO1 = new UserDTO();
        String userIdOnline ="userOnline1";
        userDTO1.setId(userIdOnline);
        userDTO1.setName("ThuyTien");
        Map<String, UserDTO> userList1 = new HashMap<>();
        userList1.put(userDTO1.getId(),userDTO1);
        stationDTO01.setOnlineUsers(userList1);

        // old station - 2 number of user
        StationDTO stationDTO02 = new StationDTO();
        String stationId02 ="station2";
        stationDTO02.setId(stationId02);
        stationDTO02.setName("ST2");
        stationDTO02.setCreatedAt(LocalDate.of(1992,12,06));

        UserDTO userDTO2 = new UserDTO();
        String userIdOnline2 ="userOnline2";
        userDTO2.setId(userIdOnline2);
        userDTO2.setName("ThuyTien2");
        Map<String, UserDTO> userList2 = new HashMap<>();
        userList2.put(userDTO1.getId(),userDTO1);
        userList2.put(userDTO2.getId(),userDTO2);
        stationDTO02.setOnlineUsers(userList2);

        // old station - 0 number of user
        StationDTO stationDTO03 = new StationDTO();
        String stationId03 ="station3";
        stationDTO03.setId(stationId03);
        stationDTO03.setName("ST3");
        stationDTO03.setCreatedAt(LocalDate.of(2007,10,4));

        // new station - 1 number of user
        StationDTO stationDTO04 = new StationDTO();
        String stationId04 ="station4";
        stationDTO04.setId(stationId04);
        stationDTO04.setName("ST4");
        stationDTO04.setCreatedAt(LocalDate.now());

        UserDTO userDTO4 = new UserDTO();
        String userIdOnline4 ="userOnline4";
        userDTO4.setId(userIdOnline4);
        userDTO4.setName("ThuyTien4");
        Map<String, UserDTO> userList4 = new HashMap<>();
        userList4.put(userDTO4.getId(),userDTO4);
        stationDTO04.setOnlineUsers(userList1);

        // new station - 2 number of user
        StationDTO stationDTO05 = new StationDTO();
        String stationId05 ="station5";
        stationDTO05.setId(stationId05);
        stationDTO05.setName("ST5");
        stationDTO05.setCreatedAt(LocalDate.now());

        UserDTO userDTO5 = new UserDTO();
        String userIdOnline5 ="userOnline5";
        userDTO5.setId(userIdOnline5);
        userDTO5.setName("ThuyTien5");
        Map<String, UserDTO> userList5 = new HashMap<>();
        userList5.put(userDTO1.getId(),userDTO1);
        userList5.put(userDTO5.getId(),userDTO5);
        stationDTO05.setOnlineUsers(userList5);

        // new station - 0 number of user
        StationDTO stationDTO06 = new StationDTO();
        String stationId06 ="station6";
        stationDTO06.setId(stationId06);
        stationDTO06.setName("ST6");
        stationDTO06.setCreatedAt(LocalDate.now());

        stationOnlineService.addStationToList(stationDTO01);
        stationOnlineService.addStationToList(stationDTO02);
        stationOnlineService.addStationToList(stationDTO03);
        stationOnlineService.addStationToList(stationDTO04);
        stationOnlineService.addStationToList(stationDTO05);
        stationOnlineService.addStationToList(stationDTO06);


        //when
        List<StationDTO> expectedStation = new ArrayList<>();
        expectedStation.add(stationDTO05);
        expectedStation.add(stationDTO04);
        expectedStation.add(stationDTO02);
        expectedStation.add(stationDTO01);
        expectedStation.add(stationDTO06);
        expectedStation.add(stationDTO03);

        List<StationDTO> resultStation = new ArrayList(stationOnlineService.getAllStation().values());

        //then
        assertEquals(resultStation.size(),expectedStation.size());
        for(int i = 0; i< resultStation.size(); i++) {
            assertThat(resultStation.get(i)).isEqualToComparingFieldByField(expectedStation.get(i));
        }
    }
}