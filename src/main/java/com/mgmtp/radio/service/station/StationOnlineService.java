package com.mgmtp.radio.service.station;

import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;

import java.util.Map;

public interface StationOnlineService {
    /**
     * Add a stationDTO to list all stations
     * - If the stationId is existed, then the old station in list would be replaced by the new station
     * - Else, add a new station to list
     * @param stationDTO
     */
    void addStationToList(StationDTO stationDTO);

    void removeStationFromList(String stationId);

    void addOnlineUser(UserDTO userDTO, String stationId);

    void removeOnlineUser(UserDTO userDTO, String stationId);

    Map<String, StationDTO> getAllStation();

    StationDTO getStationById(String stationId);

	int getNumberOnlineUser(String stationId);
}
