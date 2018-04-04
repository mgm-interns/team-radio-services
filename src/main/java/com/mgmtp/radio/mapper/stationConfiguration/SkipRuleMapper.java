package com.mgmtp.radio.mapper.stationConfiguration;

import com.mgmtp.radio.domain.station.SkipRule;
import com.mgmtp.radio.dto.station.SkipRuleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SkipRuleMapper {
	SkipRuleMapper INSTANCE = Mappers.getMapper(SkipRuleMapper.class);

	SkipRuleDTO skipRuleToSkipRuleDTO(SkipRule skipRule);

	SkipRule skipRuleDTOToSkipRule(SkipRuleDTO skipRuleDTO);
}
