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

    public Map<String,StationDTO> getAllStation() {
        return sortByStation(allStations);
    }

    public StationDTO getStationById(String stationId){
        return allStations.get(stationId);
    }

    private Map<String,StationDTO> sortByStation(Map<String,StationDTO> unsortMap){

        List<Map.Entry<String, StationDTO>> list =
                new LinkedList<Map.Entry<String, StationDTO>>(unsortMap.entrySet());

        Collections.sort(list,new Comparator<Map.Entry<String, StationDTO>>() {
            public int compare(Map.Entry<String, StationDTO> o1,
                               Map.Entry<String, StationDTO> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<String, StationDTO> sortedMap = new LinkedHashMap<String, StationDTO>();
        for (Map.Entry<String, StationDTO> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
