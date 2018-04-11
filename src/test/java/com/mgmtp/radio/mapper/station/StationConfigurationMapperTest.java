package com.mgmtp.radio.mapper.station;

import com.mgmtp.radio.domain.station.SkipRule;
import com.mgmtp.radio.domain.station.StationConfiguration;
import com.mgmtp.radio.dto.skipRule.SkipRuleDTO;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.sdo.SkipRuleType;
import org.junit.Test;

import static org.junit.Assert.*;

public class StationConfigurationMapperTest {
    StationMapper stationMapper = StationMapper.INSTANCE;

    @Test
    public void skipRuleToSkipRuleDTO() {
        //given
        SkipRule skipRuleBasic = new SkipRule();
        skipRuleBasic.setSkipRuleType(SkipRuleType.BASIC);

        SkipRule skipRuleAdvance = new SkipRule();
        skipRuleAdvance.setSkipRuleType(SkipRuleType.ADVANCE);

        //when
        SkipRuleDTO skipRuleDtoBasic = stationMapper.skipRuleToSkipRuleDTO(skipRuleBasic);
        SkipRuleDTO skipRuleDtoAdvance = stationMapper.skipRuleToSkipRuleDTO(skipRuleAdvance);

        //then
        assertEquals(skipRuleBasic.getSkipRuleType(),skipRuleDtoBasic.getSkipRuleType());
        assertEquals(skipRuleAdvance.getSkipRuleType(), skipRuleDtoAdvance.getSkipRuleType());
    }

    @Test
    public void skipRuleDtoToSkipRule() {
        //given
        SkipRuleDTO skipRuleDTOBasic = new SkipRuleDTO();
        skipRuleDTOBasic.setSkipRuleType(SkipRuleType.BASIC);

        SkipRuleDTO skipRuleDTOAdvance = new SkipRuleDTO();
        skipRuleDTOAdvance.setSkipRuleType(SkipRuleType.ADVANCE);

        //when
        SkipRule skipRuleBasic = stationMapper.skipRuleDtoToSkipRule(skipRuleDTOBasic);
        SkipRule skipRuleAdvance = stationMapper.skipRuleDtoToSkipRule(skipRuleDTOAdvance);

        //then
        assertEquals(skipRuleDTOBasic.getSkipRuleType(),skipRuleBasic.getSkipRuleType());
        assertEquals(skipRuleDTOAdvance.getSkipRuleType(),skipRuleAdvance.getSkipRuleType());
    }

    @Test
    public void stationConfigurationToStationConfigurationDto() {
        //given
        StationConfiguration stationConfigurationBasic = new StationConfiguration();
        SkipRule skipRuleBasic = new SkipRule();
        skipRuleBasic.setSkipRuleType(SkipRuleType.BASIC);
        stationConfigurationBasic.setSkipRule(skipRuleBasic);

        //when
        StationConfigurationDTO stationConfigurationBasicDTO = stationMapper.stationConfigurationToStationConfigurationDto(stationConfigurationBasic);

        //then
        assertEquals(stationConfigurationBasic.getSkipRule().getSkipRuleType(),stationConfigurationBasicDTO.getSkipRule().getSkipRuleType());


        //given
        StationConfiguration stationConfigurationAdvance = new StationConfiguration();
        SkipRule skipRuleAdvance = new SkipRule();
        skipRuleAdvance.setSkipRuleType(SkipRuleType.ADVANCE);
        stationConfigurationAdvance.setSkipRule(skipRuleAdvance);

        //when
        StationConfigurationDTO stationConfigurationAdvanceDTO = stationMapper.stationConfigurationToStationConfigurationDto(stationConfigurationAdvance);

        //then
        assertEquals(stationConfigurationAdvance.getSkipRule().getSkipRuleType(),stationConfigurationAdvanceDTO.getSkipRule().getSkipRuleType());

    }

    @Test
    public void stationConfigurationDtoToStationConfiguration() {
        //given
        StationConfigurationDTO stationConfigurationDTOBasic = new StationConfigurationDTO();
        SkipRuleDTO skipRuleDTOBasic = new SkipRuleDTO();
        skipRuleDTOBasic.setSkipRuleType(SkipRuleType.BASIC);
        stationConfigurationDTOBasic.setSkipRule(skipRuleDTOBasic);

        //when
        StationConfiguration stationConfigurationBasic = stationMapper.stationConfigurationDtoToStationConfiguration(stationConfigurationDTOBasic);

        //then
        assertEquals(stationConfigurationDTOBasic.getSkipRule().getSkipRuleType(),stationConfigurationBasic.getSkipRule().getSkipRuleType());

        //given
        StationConfigurationDTO stationConfigurationDTOAdvance = new StationConfigurationDTO();
        SkipRuleDTO skipRuleDTOAdvance = new SkipRuleDTO();
        skipRuleDTOAdvance.setSkipRuleType(SkipRuleType.ADVANCE);
        stationConfigurationDTOAdvance.setSkipRule(skipRuleDTOAdvance);

        //when
        StationConfiguration stationConfigurationAdvance = stationMapper.stationConfigurationDtoToStationConfiguration(stationConfigurationDTOAdvance);

        //then
        assertEquals(stationConfigurationDTOAdvance.getSkipRule().getSkipRuleType(),stationConfigurationAdvance.getSkipRule().getSkipRuleType());

    }
}