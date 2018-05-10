package com.mgmtp.radio.service.user;

import com.mgmtp.radio.domain.user.RecentStation;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.RecentStationDTO;
import com.mgmtp.radio.mapper.user.RecentStationMapper;
import com.mgmtp.radio.respository.user.RecentStationRepository;
import com.mgmtp.radio.sdo.StationPrivacy;
import com.mgmtp.radio.service.station.StationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
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
                recentStationRepository.findByUserIdOrderByCreatedAtDesc(userId).map(recentStationMapper::recentStationToRecentStationDTO);

        List<String> recentStationIdList = recentStationDTOFlux.map(RecentStationDTO::getStationId).toStream().collect(Collectors.toList());
        Flux<StationDTO> stationDTOFlux = stationService.getListStationByListStationId(recentStationIdList);

        List<StationDTO> stationDTOListSorted = new ArrayList<>();
        recentStationIdList.forEach(recentStationId -> {
            Optional<StationDTO> stationDTOOptional = stationDTOFlux.toStream().filter(stationDTO ->
                    stationDTO.getId().equals(recentStationId)
            ).findFirst();
            if(stationDTOOptional.isPresent()) {
                stationDTOListSorted.add(stationDTOOptional.get());
            }
        });
        return Flux.fromIterable(stationDTOListSorted);
    }

    @Override
    public Flux<StationDTO> getRecentStationsByUserIdAndPrivacy(String userId, StationPrivacy privacy) {
        Flux<RecentStationDTO> recentStationDTOFlux =
                recentStationRepository.findByUserIdOrderByCreatedAtDesc(userId).map(recentStationMapper::recentStationToRecentStationDTO);

        List<String> recentStationIdList = recentStationDTOFlux.map(RecentStationDTO::getStationId).toStream().collect(Collectors.toList());
        Flux<StationDTO> stationDTOFlux = stationService.getListStationByListStationIdAndPrivacy(recentStationIdList, StationPrivacy.station_public);

        Optional<Map<String, StationDTO>> listStationsOptional = stationDTOFlux.collectMap(stationDTO -> stationDTO.getId(), stationDTO -> stationDTO).blockOptional();

        List<StationDTO> stationDTOListSorted = new ArrayList<>();
        if (listStationsOptional.isPresent()) {
            Map<String, StationDTO> stations = listStationsOptional.get();
            stationDTOListSorted = recentStationIdList.stream().filter(id -> stations.get(id) != null).map(stations::get).collect(Collectors.toList());
        }

        return Flux.fromIterable(stationDTOListSorted);
    };

    @Override
    public Mono<RecentStationDTO> createRecentStation(String userId, String stationId) {
        RecentStationDTO recentStationDTO = new RecentStationDTO();
        recentStationDTO.setUserId(userId);
        recentStationDTO.setStationId(stationId);
        recentStationDTO.setCreatedAt(LocalDateTime.now());
        RecentStation recentStation = recentStationMapper.recentStationDTOToRecentStation(recentStationDTO);
        return recentStationRepository
                .save(recentStation).map(recentStationMapper::recentStationToRecentStationDTO);
    }

    @Override
    public boolean existsByUserIdAndStationId(String userId, String stationId){
        return recentStationRepository.findByUserIdAndStationId(userId, stationId).blockOptional().isPresent();
    }
}
