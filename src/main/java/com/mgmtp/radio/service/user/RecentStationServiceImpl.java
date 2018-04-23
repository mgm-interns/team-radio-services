package com.mgmtp.radio.service.user;

import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.RecentStationDTO;
import com.mgmtp.radio.mapper.user.RecentStationMapper;
import com.mgmtp.radio.respository.user.RecentStationRepository;
import com.mgmtp.radio.service.station.StationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecentStationServiceImpl implements RecentStationService {

    private final RecentStationRepository recentStationRepository;
    private final StationService stationService;
    private final RecentStationMapper recentStationMapper;

    public RecentStationServiceImpl(RecentStationRepository recentStationRepository, StationService stationService, RecentStationMapper recentStationMapper) {
        this.recentStationRepository = recentStationRepository;
        this.stationService = stationService;
        this.recentStationMapper = recentStationMapper;
    }

    @Override
    public Flux<StationDTO> getRecentStation(String userId) {
        Flux<RecentStationDTO> recentStationDTOFlux =
                recentStationRepository.findByUserIdOrderByJoinedTimeDesc(userId).map(recentStationMapper::recentStationToRecentStationDTO);

        //Get StationDTO by list recent station id
        List<String> recentStationIdList = recentStationDTOFlux.map(RecentStationDTO::getStationId).toStream().collect(Collectors.toList());
        Flux<StationDTO> stationDTOFlux = stationService.getListStationByListStationId(recentStationIdList);

        //Sort list StationDTO
        List<StationDTO> StationDTOListSorted = new ArrayList<>();
        recentStationIdList.stream().forEach(recentStationId -> {
            StationDTO stationDTOSorted = stationDTOFlux.toStream().filter(stationDTO ->
                    stationDTO.getId().equals(recentStationId)
            ).findFirst().get();
            StationDTOListSorted.add(stationDTOSorted);
        });
        return Flux.fromIterable(StationDTOListSorted);
    }

    @Override
    public void createRecentStation() {
    }

    @Override
    public void updateRecentStation() {
    }
}
