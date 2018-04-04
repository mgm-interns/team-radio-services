package com.mgmtp.radio.mapper.stationConfiguration;

import com.mgmtp.radio.domain.station.SkipRule;
import com.mgmtp.radio.dto.station.SkipRuleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SkipRuleTypeMapper {
	SkipRuleMapper INSTANCE = Mappers.getMapper(SkipRuleMapper.class);

	SkipRuleDTO skipRuleToSkipRuleDto(SkipRule skipRule);

	SkipRule skipRuleDtoToSkipRule(SkipRuleDTO skipRuleDTO);
}
