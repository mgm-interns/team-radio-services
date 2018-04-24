package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("stationOnlineService")
public class StationOnlineServiceImpl implements StationOnlineService {

    private static Map<String,StationDTO> allStations = new HashMap<>();

    private static final int LARGER = -1;
    private static final int SMALLER = 1;
    private static final int EQUAL = 0;

    public void addStationToList(StationDTO stationDTO) {
        allStations.put(stationDTO.getId(),stationDTO);
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


    private Comparator<Map.Entry<String,StationDTO>> comparatorStation = ( Map.Entry<String,StationDTO> entry1, Map.Entry<String,StationDTO> entry2) -> {
        StationDTO stationDTO1 = entry1.getValue();
        StationDTO stationDTO2 = entry2.getValue();

        if(stationDTO1.hasUser() && !stationDTO2.hasUser())
            return LARGER;
        if(!stationDTO1.hasUser() && stationDTO2.hasUser() )
            return SMALLER;
        if(stationDTO1.isNewStation() && !stationDTO2.isNewStation())
            return LARGER;
        if(!stationDTO1.isNewStation() && stationDTO2.isNewStation())
            return SMALLER;
        if(stationDTO1.getNumberOnline() > stationDTO2.getNumberOnline())
            return LARGER;
        if(stationDTO1.getNumberOnline() < stationDTO2.getNumberOnline())
            return SMALLER;
        return EQUAL;
    };

    public Map<String,StationDTO> getAllStation() {
        return sortByStation(allStations);
    }

    private Map<String,StationDTO> sortByStation(Map<String,StationDTO> unsortMap){
        return unsortMap.entrySet().stream().sorted(comparatorStation)
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(oldValue,newValue) -> oldValue, LinkedHashMap::new));
    }
}
