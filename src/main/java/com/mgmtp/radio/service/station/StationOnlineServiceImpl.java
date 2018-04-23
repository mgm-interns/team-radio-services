package com.mgmtp.radio.service.station;

import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public StationDTO getStationById(String stationId){
        return allStations.get(stationId);
    }

    public Map<String,StationDTO> getAllStation() {
        return sortByStation(allStations);
    }

    private Map<String,StationDTO> sortByStation(Map<String,StationDTO> unsortMap){

        List<Map.Entry<String, StationDTO>> list =
                new LinkedList<Map.Entry<String, StationDTO>>(unsortMap.entrySet());

        Collections.sort(list,new Comparator<Map.Entry<String, StationDTO>>() {
            public int compare(Map.Entry<String, StationDTO> stationDTO1,
                               Map.Entry<String, StationDTO> stationDTO2) {
                return (stationDTO1.getValue()).compareTo(stationDTO2.getValue());
            }
        });

        Map<String, StationDTO> sortedMap = new LinkedHashMap<String, StationDTO>();
        for (Map.Entry<String, StationDTO> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
