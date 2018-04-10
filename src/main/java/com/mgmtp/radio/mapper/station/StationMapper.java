package com.mgmtp.radio.mapper.station;

import com.mgmtp.radio.domain.station.SkipRule;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.station.StationConfiguration;
import com.mgmtp.radio.dto.skipRule.SkipRuleDTO;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.dto.station.StationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StationMapper {
	StationMapper INSTANCE = Mappers.getMapper(StationMapper.class);
	@Mapping(target = "playlist", ignore = true)
	StationDTO stationToStationDTO(Station station);

	@Mapping(target = "playlist", ignore = true)
	Station stationDTOToStation(StationDTO stationDTO);

	SkipRuleDTO skipRuleToSkipRuleDTO(SkipRule skipRule);

	SkipRule skipRuleDtoToSkipRule(SkipRuleDTO skipRuleDTO);

	StationConfigurationDTO stationConfigurationToStationConfigurationDto(StationConfiguration stationConfiguration);

	StationConfiguration stationConfigurationDtoToStationConfiguration(StationConfigurationDTO stationConfigurationDTO);
}
