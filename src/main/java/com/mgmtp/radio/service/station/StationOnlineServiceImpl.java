package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.NowPlaying;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.support.StationPlayerHelper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("stationOnlineService")
public class StationOnlineServiceImpl implements StationOnlineService {

    private static Map<String, StationDTO> allStations = new LinkedHashMap<>();
    private static Map<String, String> userManager = new HashMap<>();

    private static final int LARGER = -1;
    private static final int SMALLER = 1;
    private static final int EQUAL = 0;

    private final StationPlayerHelper stationPlayerHelper;

    public StationOnlineServiceImpl(StationPlayerHelper stationPlayerHelper) {
        this.stationPlayerHelper = stationPlayerHelper;
    }

    public void addStationToList(StationDTO stationDTO) {
        allStations.put(stationDTO.getFriendlyId(), stationDTO);
    }

    public void removeStationFromList(String stationId) {
        allStations.remove(stationId);
    }

    public void addOnlineUser(UserDTO userDTO, String stationId) {
        StationDTO stationDTO = allStations.get(stationId);
        if (userManager.get(userDTO.getId()) != null){
            removeOnlineUser(userDTO, userManager.get(userDTO.getId()));
        }
        userManager.put(userDTO.getId(), stationId);
        stationDTO.getOnlineUsers().put(userDTO.getId(), userDTO);
    }

    public void removeOnlineUser(UserDTO userDTO, String stationId) {
        StationDTO stationDTO = allStations.get(stationId);
        stationDTO.getOnlineUsers().remove(userDTO.getId());
    }

    public StationDTO getStationById(String stationId) {
        return allStations.get(stationId);
    }


    private Comparator<Map.Entry<String, StationDTO>> comparatorStation = (Map.Entry<String, StationDTO> entry1, Map.Entry<String, StationDTO> entry2) -> {
        StationDTO stationDTO1 = entry1.getValue();
        StationDTO stationDTO2 = entry2.getValue();

        if (stationDTO1.hasUser() && !stationDTO2.hasUser())
            return LARGER;
        if (!stationDTO1.hasUser() && stationDTO2.hasUser())
            return SMALLER;
        if (stationDTO1.isNewStation() && !stationDTO2.isNewStation())
            return LARGER;
        if (!stationDTO1.isNewStation() && stationDTO2.isNewStation())
            return SMALLER;
        if (stationDTO1.getNumberOnline() > stationDTO2.getNumberOnline())
            return LARGER;
        if (stationDTO1.getNumberOnline() < stationDTO2.getNumberOnline())
            return SMALLER;
        return EQUAL;
    };

    public Map<String, StationDTO> getAllStation() {
        Map<String, StationDTO> result = sortByStation(allStations);
        result.forEach(((stationId, stationDTO) -> {
            Optional<NowPlaying> nowPlaying = stationPlayerHelper.getStationNowPlaying(stationId);
            nowPlaying.ifPresent(currentPlaying -> stationDTO.setPicture(currentPlaying.getThumbnail()));
        }));
        return result;
    }

    private Map<String, StationDTO> sortByStation(Map<String, StationDTO> unSortMap) {
        return unSortMap.entrySet().stream().sorted(comparatorStation)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
}
