package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.NowPlaying;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.sdo.StationPrivacy;
import com.mgmtp.radio.support.StationPlayerHelper;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service("stationOnlineService")
public class StationOnlineServiceImpl implements StationOnlineService {

    private static Map<String, StationDTO> allStations = new LinkedHashMap<>();
    private static Map<String, Map<String, String>> joinUser = new HashMap<>();
    private static Map<String, Map<String, String>> leaveUser = new HashMap<>();

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
        System.out.println("Add online " + userDTO);
        stationDTO.getOnlineUsers().put(userDTO.getId(), userDTO);
        Map<String, String> joinUserMap = joinUser.get(stationId);
        if (joinUserMap == null) {
            joinUserMap = new HashMap<>();
            joinUserMap.put(userDTO.getId(), userDTO.getName());
            joinUser.put(stationId, joinUserMap);
        } else {
            joinUserMap.put(userDTO.getId(), userDTO.getName());
        }
    }

    public void removeOnlineUser(UserDTO userDTO, String stationId) {
        StationDTO stationDTO = allStations.get(stationId);
        System.out.println("HIT remove " + stationId + " user " + userDTO.getId());
        System.out.println("Join user " + joinUser.get(stationId));
        if (joinUser.get(stationId) == null || !joinUser.get(stationId).containsKey(userDTO.getId())) {
            System.out.println("Remove online " + userDTO);
            stationDTO.getOnlineUsers().remove(userDTO.getId());
        }
        Map<String, String> leaveUserMap = leaveUser.get(stationId);
        if (leaveUserMap != null) {
            leaveUserMap.put(userDTO.getId(), userDTO.getName());
        } else {
            leaveUserMap = new HashMap<>();
            leaveUserMap.put(userDTO.getId(), userDTO.getName());
            leaveUser.put(stationId, leaveUserMap);
        }
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
            if(nowPlaying.isPresent()) {
                stationDTO.setPicture(nowPlaying.get().getThumbnail());
            } else {
                stationDTO.setPicture("");
            }
        }));
        result.entrySet().removeIf(entry -> entry.getValue().getPrivacy() == StationPrivacy.station_private);
        return result;
    }

    private Map<String, StationDTO> sortByStation(Map<String, StationDTO> unSortMap) {
        return unSortMap.entrySet().stream().sorted(comparatorStation)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    @Override
    public int getNumberOnlineUser(String friendlyId) {
        return getStationByFriendlyId(friendlyId).getNumberOnline();
    }

    private StationDTO getStationByFriendlyId(String friendlyId) {
        return allStations.get(friendlyId);
    }

    @Override
    public Map<String, Object> getStationInfo() {
        Map<String, Object> result = new HashMap<>();
        allStations.forEach((stationFriendlyId, stationDTO) -> {
            Map<String, Object> stationInfo = new HashMap<>();
            stationInfo.put("stationInfo", stationDTO);

            Map<String, String> joinUserInfo = joinUser.get(stationFriendlyId);
            if (joinUserInfo != null) {
                stationInfo.put("joinUser", new ArrayList<>(joinUserInfo.values()));
            } else {
                stationInfo.put("joinUser", Collections.EMPTY_LIST);
            }

            Map<String, String> leaveUserInfo = leaveUser.get(stationFriendlyId);
            if (leaveUserInfo != null) {
                stationInfo.put("leaveUser", new ArrayList<>(leaveUserInfo.values()));
            } else {
                stationInfo.put("leaveUser", Collections.EMPTY_LIST);
            }

            result.put(stationFriendlyId, stationInfo);
        });

        return result;
    }

    @Override
    public void clearJoinUserInfo(String stationId){
        joinUser.remove(stationId);
    }

    @Override
    public void clearLeaveUserInfo(String stationId){
        leaveUser.remove(stationId);
    }
}
