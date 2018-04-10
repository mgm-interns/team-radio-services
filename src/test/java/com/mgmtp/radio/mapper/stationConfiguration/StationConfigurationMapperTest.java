//package com.mgmtp.radio.mapper.stationConfiguration;
//
//import com.mgmtp.radio.domain.station.SkipRule;
//import com.mgmtp.radio.dto.skipRule.SkipRuleDTO;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//public class StationConfigurationMapperTest {
//
//	StationConfigurationMapper stationConfigurationMapper = StationConfigurationMapper.INSTANCE;
//
//	@Test
//	public void skipRuleToSkipRuleDTO() {
//		//given
//		SkipRule skipRuleBasic = new SkipRule();
//		skipRuleBasic.setTypeId(SkipRule.BASIC);
//
//		SkipRule skipRuleAdvance = new SkipRule();
//		skipRuleAdvance.setTypeId(SkipRule.ADVANCE);
//
//		//when
//		SkipRuleDTO skipRuleDtoBasic = stationConfigurationMapper.skipRuleToSkipRuleDTO(skipRuleBasic);
//		SkipRuleDTO skipRuleDtoAdvance = stationConfigurationMapper.skipRuleToSkipRuleDTO(skipRuleAdvance);
//
//		//then
//		assertEquals(skipRuleBasic.getTypeId(),skipRuleDtoBasic.getTypeId());
//		assertEquals(skipRuleAdvance.getTypeId(), skipRuleDtoAdvance.getTypeId());
//	}
//
//	@Test
//	public void skipRuleDtoToSkipRule() {
//	}
//
//	@Test
//	public void stationConfigurationToStationConfigurationDto() {
//	}
//
//	@Test
//	public void stationConfigurationDtoToStationConfiguration() {
//	}
//}