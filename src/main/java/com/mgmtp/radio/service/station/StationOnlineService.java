package com.mgmtp.radio.service.station;

import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;

import java.util.Map;

public interface StationOnlineService {
    void addStationToList(StationDTO stationDTO);
    void removeStationFromList(String stationId);
    void addOnlineUser(UserDTO userDTO, String stationId);
    void removeOnlineUser(UserDTO userDTO, String stationId);
    Map<String,StationDTO> getAllStation();
}
