package com.mgmtp.radio.mapper.stationConfiguration;

import com.mgmtp.radio.domain.station.StationConfiguration;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StationConfigurationMapper {
	StationConfigurationMapper INSTANCE = Mappers.getMapper(StationConfigurationMapper.class);

	StationConfigurationDTO stationConfigurationToStationConfigurationDto(StationConfiguration stationConfiguration);

	StationConfiguration stationConfigurationDtoToStationConfiguration(StationConfigurationDTO stationConfigurationDTO);
}
