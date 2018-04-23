package com.mgmtp.radio.service.station;

import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("stationOnlineService")
public class StationOnlineServiceImpl implements StationOnlineService {

    private static List<StationDTO> allStations = new LinkedList<>();

    public void addStationToList(StationDTO stationDTO) {
        allStations.add(stationDTO);
    }

    public void removeStationFromList(String stationId) {
        for(int i = 0; i< allStations.size(); i++) {
            final StationDTO dto = allStations.get(i);
            if(dto.getId().equals(stationId)) {
                allStations.remove(dto);
                break;
            }
        }
    }

    public void addOnlineUser(UserDTO userDTO, String stationId) {
        StationDTO stationDTO = allStations.stream().filter(s->s.getId().equals(stationId)).findFirst().orElse(null);
        stationDTO.getUserList().put(userDTO.getId(),userDTO);
    }

    public void removeOnlineUser(UserDTO userDTO, String stationId){
        StationDTO stationDTO = allStations.stream().filter(station -> station.getId().equals(stationId)).findFirst().orElse(null);
        stationDTO.getUserList().remove(userDTO.getId());
    }

    public StationDTO getStationById(String stationId){
        return allStations.stream().filter(station->station.getId().equals(stationId)).findFirst().orElse(null);
    }

    public List<StationDTO> getAllStation() {
        return sortByStation(allStations);
    }

    private List<StationDTO> sortByStation(List<StationDTO> allStations){
        Collections.sort(allStations,new Comparator< StationDTO>() {
            public int compare( StationDTO stationDTO1, StationDTO stationDTO2) {
                return (stationDTO1.compareTo(stationDTO2));
            }
        });
        return allStations;
    }
}
