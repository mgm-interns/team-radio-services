package com.mgmtp.radio.service.station;

import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("stationOnlineService")
public class StationOnlineServiceImpl implements StationOnlineService {
    private static Map<String, StationDTO> allStations = new HashMap<>();

    public void addStationToList(StationDTO stationDTO) {
        allStations.put(stationDTO.getId(), stationDTO);
    }

    public void removeStationFromList(String stationId) {
        allStations.remove(stationId);
    }

    public void addOnlineUser(UserDTO userDTO, String stationId) {
        StationDTO stationDTO = allStations.get(stationId);
        stationDTO.getUserList().put(userDTO.getId(),userDTO);
    }

    public void removeOnlineUser(UserDTO userDTO, String stationId){
        StationDTO stationDTO = allStations.get(stationId);
        stationDTO.getUserList().remove(userDTO.getId());
    }

    public Map<String,StationDTO> getAllStation() {
        return allStations;
    }
}
