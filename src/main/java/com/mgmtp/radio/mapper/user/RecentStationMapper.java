package com.mgmtp.radio.mapper.user;

import com.mgmtp.radio.domain.user.RecentStation;
import com.mgmtp.radio.dto.user.RecentStationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RecentStationMapper {
    RecentStationMapper INSTANCE = Mappers.getMapper(RecentStationMapper.class);

    RecentStationDTO recentStationToRecentStationDTO(RecentStation recentStation);

    RecentStation recentStationDTOToRecentStation(RecentStationDTO recentStationDTO);

}
