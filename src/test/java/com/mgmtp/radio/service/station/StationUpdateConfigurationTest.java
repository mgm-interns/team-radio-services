package com.mgmtp.radio.service.station;

import com.mgmtp.radio.RadioApplicationTests;
import com.mgmtp.radio.controller.v1.StationController;
import com.mgmtp.radio.domain.station.SkipRule;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.station.StationConfiguration;
import com.mgmtp.radio.dto.skipRule.SkipRuleDTO;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.mapper.station.StationMapper;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.service.station.StationService;
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
import static org.junit.Assert.assertNotEquals;
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

	@Test
	public void updateStationSkipRuleToAdvance() {
		final String id= "123456";
		Station station = new Station();

		when(stationRepository.findById(id)).thenReturn(Mono.just(station));
		when(stationRepository.save(station)).thenReturn(Mono.just(station));
		//when(stationMapper.stationToStationDTO(station)).thenReturn(new Station);

		//Fake input StationConfigurationDto
		SkipRuleDTO skipRuleDTO = new SkipRuleDTO(SkipRuleDTO.ADVANCE);
		StationConfigurationDTO inputStationConfigurationDTO = new StationConfigurationDTO();
		inputStationConfigurationDTO.setSkipRule(skipRuleDTO);

		//Original skipRule
		SkipRule skipRule = new SkipRule(SkipRule.BASIC);
		StationConfiguration stationConfiguration = new StationConfiguration();
		stationConfiguration.setRule(skipRule);
		station.setStationConfiguration(stationConfiguration);

		Mono<Station> monoStationConfig = stationRepository.findById(id).map(originalStation -> {
			StationConfiguration originalStationConfig = new StationConfiguration();
			when(stationMapper.stationConfigurationDtoToStationConfiguration(inputStationConfigurationDTO)).thenReturn(
				originalStationConfig
			);
			originalStation.setStationConfiguration(stationMapper.stationConfigurationDtoToStationConfiguration(inputStationConfigurationDTO));

			SkipRule mockSkipRule = new SkipRule(SkipRule.ADVANCE);
			when(stationMapper.skipRuleDtoToSkipRule(inputStationConfigurationDTO.getSkipRule())).thenReturn(mockSkipRule);

			originalStation.getStationConfiguration().setRule(stationMapper.skipRuleDtoToSkipRule(inputStationConfigurationDTO.getSkipRule()));

			//Save
			when(stationRepository.save(originalStation)).thenReturn(Mono.just(originalStation));
			stationRepository.save(originalStation);
			return originalStation;
		});

		final Station station1 = monoStationConfig.block();


		assertEquals(inputStationConfigurationDTO.getSkipRule().getTypeId(), station1.getStationConfiguration().getRule().getTypeId());
	}
}
