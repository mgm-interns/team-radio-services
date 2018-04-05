package com.mgmtp.radio.mapper.stationConfiguration;

import com.mgmtp.radio.domain.station.SkipRule;
import com.mgmtp.radio.domain.station.StationConfiguration;
import com.mgmtp.radio.dto.station.SkipRuleDTO;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StationConfigurationMapper {
	StationConfigurationMapper INSTANCE = Mappers.getMapper(StationConfigurationMapper.class);

	SkipRuleDTO skipRuleToSkipRuleDTO(SkipRule skipRule);

	SkipRule skipRuleDtoToSkipRule(SkipRuleDTO skipRuleDTO);

	StationConfigurationDTO stationConfigurationToStationConfigurationDto(StationConfiguration stationConfiguration);

	StationConfiguration stationConfigurationDtoToStationConfiguration(StationConfigurationDTO stationConfigurationDTO);
}
