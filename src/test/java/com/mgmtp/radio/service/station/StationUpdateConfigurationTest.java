package com.mgmtp.radio.service.station;

import com.mgmtp.radio.RadioApplicationTests;
import com.mgmtp.radio.controller.v1.StationController;
import com.mgmtp.radio.domain.station.SkipRule;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.station.StationConfiguration;
import com.mgmtp.radio.dto.skipRule.SkipRuleDTO;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.mapper.station.StationMapper;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.sdo.SkipRuleType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RadioApplicationTests.class)
public class StationUpdateConfigurationTest {

	WebTestClient webTestClient;

	StationController stationController;

	StationService stationService;

	@Mock
	SongService songService;

	@Mock
	StationRepository stationRepository;

	@Mock
	StationMapper stationMapper = StationMapper.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(stationRepository);
		stationService = new StationServiceImpl(stationMapper, stationRepository, songService);
	}

	/**
	 * Scenario:
	 *  - An exist station has skipRule.typeId=BASIC
	 *  - Try to change it to ADVANCE
	 */
	@Test
	public void updateStationSkipRuleToAdvance() {
		final String id= "123456";
		Station station = new Station();

		when(stationRepository.findById(id)).thenReturn(Mono.just(station));
		when(stationRepository.save(station)).thenReturn(Mono.just(station));

		//Fake input StationConfigurationDto
		SkipRuleDTO skipRuleDTO = new SkipRuleDTO(SkipRuleType.ADVANCE);
		StationConfigurationDTO inputStationConfigurationDTO = new StationConfigurationDTO();
		inputStationConfigurationDTO.setSkipRule(skipRuleDTO);

		//Expected stationConfigurationDto
		StationConfigurationDTO expectedDto = new StationConfigurationDTO();
		expectedDto.setSkipRule(new SkipRuleDTO(SkipRuleType.ADVANCE));

		//Original skipRule
		SkipRule skipRule = new SkipRule(SkipRuleType.BASIC);
		StationConfiguration stationConfiguration = new StationConfiguration();
		stationConfiguration.setSkipRule(skipRule);
		station.setStationConfiguration(stationConfiguration);

		Mono<StationConfigurationDTO> monoStationConfig = stationRepository.findById(id).map(originalStation -> {
			StationConfiguration originalStationConfig = new StationConfiguration();
			when(stationMapper.stationConfigurationDtoToStationConfiguration(inputStationConfigurationDTO)).thenReturn(
				originalStationConfig
			);
			originalStation.setStationConfiguration(stationMapper.stationConfigurationDtoToStationConfiguration(inputStationConfigurationDTO));

			SkipRule mockSkipRule = new SkipRule(SkipRuleType.ADVANCE);
			when(stationMapper.skipRuleDtoToSkipRule(inputStationConfigurationDTO.getSkipRule())).thenReturn(mockSkipRule);

			originalStation.getStationConfiguration().setSkipRule(stationMapper.skipRuleDtoToSkipRule(inputStationConfigurationDTO.getSkipRule()));

			//Save
			when(stationRepository.save(originalStation)).thenReturn(Mono.just(originalStation));
			stationRepository.save(originalStation);

			when(stationMapper.stationConfigurationToStationConfigurationDto(originalStation.getStationConfiguration())).thenReturn(
				expectedDto
			);

			return originalStation.getStationConfiguration();
		}).map(stationMapper::stationConfigurationToStationConfigurationDto);

		final StationConfigurationDTO outputStationConfiguration = monoStationConfig.block();

		assertEquals(inputStationConfigurationDTO.getSkipRule().getSkipRuleType(), outputStationConfiguration.getSkipRule().getSkipRuleType());
	}
}
